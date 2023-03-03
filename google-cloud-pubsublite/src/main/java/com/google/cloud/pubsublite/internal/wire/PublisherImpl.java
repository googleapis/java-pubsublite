/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal.wire;

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.PublishSequenceNumber;
import com.google.cloud.pubsublite.internal.SequencedPublisher;
import com.google.cloud.pubsublite.internal.wire.SerialBatcher.UnbatchedMessage;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.MessagePublishResponse;
import com.google.cloud.pubsublite.proto.MessagePublishResponse.CursorRange;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.Monitor;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Future;
import javax.annotation.concurrent.GuardedBy;

public final class PublisherImpl extends ProxyService
    implements SequencedPublisher<Offset>, RetryingConnectionObserver<MessagePublishResponse> {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private final AlarmFactory alarmFactory;
  private final PublishRequest initialRequest;

  private final Monitor monitor = new Monitor();
  private final Monitor.Guard noneInFlight =
      new Monitor.Guard(monitor) {
        @Override
        public boolean isSatisfied() {
          return batchesInFlight.isEmpty() || shutdown;
        }
      };

  @GuardedBy("monitor")
  private Optional<Future<?>> alarmFuture = Optional.empty();

  @GuardedBy("monitor")
  private final RetryingConnection<PublishRequest, BatchPublisher> connection;

  @GuardedBy("monitor")
  private boolean shutdown = false;

  @GuardedBy("monitor")
  private Offset lastSentOffset = Offset.of(-1);

  private final SerialBatcher batcher;

  private static class InFlightBatch {
    final List<UnbatchedMessage> messages;

    InFlightBatch(List<UnbatchedMessage> toBatch) {
      this.messages = toBatch;
    }

    List<PubSubMessage> messagesToSend() {
      return messages.stream().map(UnbatchedMessage::message).collect(toImmutableList());
    }

    PublishSequenceNumber firstSequenceNumber() {
      return messages.get(0).sequenceNumber();
    }

    void failBatch(int startIdx, CheckedApiException e) {
      for (int i = startIdx; i < messages.size(); i++) {
        messages.get(i).future().setException(e);
      }
    }
  }

  // An ordered list of batches in flight.
  @GuardedBy("monitor")
  private final Queue<InFlightBatch> batchesInFlight = new ArrayDeque<>();

  @VisibleForTesting
  PublisherImpl(
      PublishStreamFactory streamFactory,
      BatchPublisherFactory publisherFactory,
      AlarmFactory alarmFactory,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws ApiException {
    this.alarmFactory = alarmFactory;
    Preconditions.checkNotNull(batchingSettings.getRequestByteThreshold());
    Preconditions.checkNotNull(batchingSettings.getElementCountThreshold());
    this.initialRequest = PublishRequest.newBuilder().setInitialRequest(initialRequest).build();
    this.connection =
        new RetryingConnectionImpl<>(streamFactory, publisherFactory, this, this.initialRequest);
    this.batcher =
        new SerialBatcher(
            batchingSettings.getRequestByteThreshold(),
            batchingSettings.getElementCountThreshold());
    addServices(connection);
  }

  public PublisherImpl(
      PublishStreamFactory streamFactory,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws ApiException {
    this(
        streamFactory,
        new BatchPublisherImpl.Factory(),
        AlarmFactory.create(
            Duration.ofNanos(
                Objects.requireNonNull(batchingSettings.getDelayThreshold()).toNanos())),
        initialRequest,
        batchingSettings);
  }

  @GuardedBy("monitor")
  private void rebatchForRestart() {
    Queue<UnbatchedMessage> messages =
        batchesInFlight.stream()
            .flatMap(b -> b.messages.stream())
            .collect(toCollection(ArrayDeque::new));
    logger.atFiner().log(
        "Re-publishing %s messages after reconnection for partition %s",
        messages.size(), initialRequest.getInitialRequest().getPartition());
    long size = 0;
    int count = 0;
    Queue<UnbatchedMessage> currentBatch = new ArrayDeque<>();
    batchesInFlight.clear();
    for (UnbatchedMessage message : messages) {
      long messageSize = message.message().getSerializedSize();
      if (size + messageSize > Constants.MAX_PUBLISH_BATCH_BYTES
          || count + 1 > Constants.MAX_PUBLISH_BATCH_COUNT) {
        if (!currentBatch.isEmpty()) {
          batchesInFlight.add(new InFlightBatch(ImmutableList.copyOf(currentBatch)));
          currentBatch.clear();
          count = 0;
          size = 0;
        }
      }
      currentBatch.add(message);
      size = size + messageSize;
      count += 1;
    }
    if (!currentBatch.isEmpty()) {
      batchesInFlight.add(new InFlightBatch(ImmutableList.copyOf(currentBatch)));
    }
  }

  @Override
  public void triggerReinitialize(CheckedApiException streamError) {
    monitor.enter();
    try {
      connection.reinitialize(initialRequest);
      rebatchForRestart();
      Collection<InFlightBatch> batches = batchesInFlight;
      connection.modifyConnection(
          connectionOr -> {
            if (!connectionOr.isPresent()) return;
            batches.forEach(
                batch ->
                    connectionOr
                        .get()
                        .publish(batch.messagesToSend(), batch.firstSequenceNumber()));
          });
    } catch (CheckedApiException e) {
      onPermanentError(e);
    } finally {
      monitor.leave();
    }
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    monitor.enter();
    try {
      shutdown = true;
      this.alarmFuture.ifPresent(future -> future.cancel(false));
      this.alarmFuture = Optional.empty();
      terminateOutstandingPublishes(error);
    } finally {
      monitor.leave();
    }
  }

  @Override
  protected void start() {
    monitor.enter();
    try {
      this.alarmFuture = Optional.of(alarmFactory.newAlarm(this::flushToStream));
    } finally {
      monitor.leave();
    }
  }

  @Override
  protected void stop() {
    flush(); // Flush any outstanding messages that were batched.
    monitor.enter();
    try {
      shutdown = true;
      this.alarmFuture.ifPresent(future -> future.cancel(false));
      this.alarmFuture = Optional.empty();
    } finally {
      monitor.leave();
    }
    flush(); // Flush again in case messages were added since shutdown was set.
  }

  @GuardedBy("monitor")
  private void terminateOutstandingPublishes(CheckedApiException e) {
    batchesInFlight.forEach(
        batch -> batch.messages.forEach(message -> message.future().setException(e)));
    batcher.flush().forEach(batch -> batch.forEach(m -> m.future().setException(e)));
    batchesInFlight.clear();
  }

  @Override
  public ApiFuture<Offset> publish(PubSubMessage message, PublishSequenceNumber sequenceNumber) {
    try {
      ApiService.State currentState = state();
      switch (currentState) {
        case FAILED:
          throw new CheckedApiException(
              "Cannot publish when publisher has failed.",
              failureCause(),
              Code.FAILED_PRECONDITION);
        case STARTING:
        case RUNNING:
          return batcher.add(message, sequenceNumber);
        default:
          throw new CheckedApiException(
              "Cannot publish when Publisher state is " + currentState.name(),
              Code.FAILED_PRECONDITION);
      }
    } catch (CheckedApiException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void cancelOutstandingPublishes() {
    monitor.enter();
    try {
      terminateOutstandingPublishes(
          new CheckedApiException("Cancelled by client.", Code.CANCELLED));
    } finally {
      monitor.leave();
    }
  }

  private void flushToStream() {
    monitor.enter();
    try {
      if (shutdown) return;
      for (List<UnbatchedMessage> batch : batcher.flush()) {
        processBatch(batch);
      }
    } catch (CheckedApiException e) {
      onPermanentError(e);
    } finally {
      monitor.leave();
    }
  }

  @GuardedBy("monitor")
  private void processBatch(List<UnbatchedMessage> batch) throws CheckedApiException {
    if (batch.isEmpty()) return;
    InFlightBatch inFlightBatch = new InFlightBatch(batch);
    batchesInFlight.add(inFlightBatch);
    connection.modifyConnection(
        connectionOr -> {
          checkState(connectionOr.isPresent(), "Published after the stream shut down.");
          connectionOr
              .get()
              .publish(inFlightBatch.messagesToSend(), inFlightBatch.firstSequenceNumber());
        });
  }

  // Flushable implementation
  @Override
  public void flush() {
    flushToStream();
    monitor.enterWhenUninterruptibly(noneInFlight);
    monitor.leave();
  }

  @Override
  public void onClientResponse(MessagePublishResponse publishResponse) throws CheckedApiException {
    // Ensure cursor ranges are sorted by increasing message batch index.
    ImmutableList<CursorRange> ranges =
        ImmutableList.sortedCopyOf(
            comparing(CursorRange::getStartIndex), publishResponse.getCursorRangesList());
    monitor.enter();
    try {
      checkState(
          !batchesInFlight.isEmpty(), "Received publish response with no batches in flight.");
      InFlightBatch batch = batchesInFlight.remove();
      int rangeIndex = 0;
      for (int messageIndex = 0; messageIndex < batch.messages.size(); messageIndex++) {
        UnbatchedMessage message = batch.messages.get(messageIndex);
        try {
          if (rangeIndex < ranges.size() && ranges.get(rangeIndex).getEndIndex() <= messageIndex) {
            rangeIndex++;
            if (rangeIndex < ranges.size()
                && ranges.get(rangeIndex).getStartIndex()
                    < ranges.get(rangeIndex - 1).getEndIndex()) {
              throw new CheckedApiException(
                  String.format(
                      "Server sent invalid cursor ranges in message publish response: %s",
                      publishResponse),
                  Code.FAILED_PRECONDITION);
            }
          }
          if (rangeIndex < ranges.size()
              && messageIndex >= ranges.get(rangeIndex).getStartIndex()
              && messageIndex < ranges.get(rangeIndex).getEndIndex()) {
            CursorRange range = ranges.get(rangeIndex);
            Offset offset =
                Offset.of(
                    range.getStartCursor().getOffset() + messageIndex - range.getStartIndex());
            if (lastSentOffset.value() >= offset.value()) {
              throw new CheckedApiException(
                  String.format(
                      "Received publish response with offset %s that is inconsistent with"
                          + " previous offset %s",
                      offset, lastSentOffset),
                  Code.FAILED_PRECONDITION);
            }
            message.future().set(offset);
            lastSentOffset = offset;
          } else {
            message.future().set(Offset.of(-1));
          }
        } catch (CheckedApiException e) {
          batch.failBatch(messageIndex, e);
          throw e;
        }
      }
    } finally {
      monitor.leave();
    }
  }
}
