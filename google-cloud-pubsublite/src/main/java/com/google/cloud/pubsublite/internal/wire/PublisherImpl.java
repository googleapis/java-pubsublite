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
import static com.google.cloud.pubsublite.internal.wire.ApiServiceUtils.backgroundResourceAsApiService;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.SerialBatcher.UnbatchedMessage;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Monitor;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.concurrent.GuardedBy;

public final class PublisherImpl extends ProxyService
    implements Publisher<Offset>, RetryingConnectionObserver<Offset> {
  private final BatchingSettings batchingSettings;
  private final PublishRequest initialRequest;
  private Future<?> alarmFuture;

  private final CloseableMonitor monitor = new CloseableMonitor();
  private final Monitor.Guard noneInFlight =
      new Monitor.Guard(monitor.monitor) {
        @Override
        public boolean isSatisfied() {
          return batchesInFlight.isEmpty() || shutdown;
        }
      };

  @GuardedBy("monitor.monitor")
  private final RetryingConnection<PublishRequest, BatchPublisher> connection;

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  @GuardedBy("monitor.monitor")
  private Optional<Offset> lastSentOffset = Optional.empty();

  @GuardedBy("monitor.monitor")
  private final SerialBatcher batcher;

  private static class InFlightBatch {
    final List<PubSubMessage> messages;
    final List<SettableApiFuture<Offset>> messageFutures;

    InFlightBatch(Collection<UnbatchedMessage> toBatch) {
      messages = toBatch.stream().map(UnbatchedMessage::message).collect(Collectors.toList());
      messageFutures = toBatch.stream().map(UnbatchedMessage::future).collect(Collectors.toList());
    }
  }

  // An ordered list of batches in flight.
  @GuardedBy("monitor.monitor")
  private final Queue<InFlightBatch> batchesInFlight = new ArrayDeque<>();

  @VisibleForTesting
  PublisherImpl(
      StreamFactory<PublishRequest, PublishResponse> streamFactory,
      BatchPublisherFactory publisherFactory,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws ApiException {
    Preconditions.checkNotNull(batchingSettings.getDelayThreshold());
    Preconditions.checkNotNull(batchingSettings.getRequestByteThreshold());
    Preconditions.checkNotNull(batchingSettings.getElementCountThreshold());
    this.batchingSettings = batchingSettings;
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
      PublisherServiceClient client,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws ApiException {
    this(
        responseStream -> client.publishCallable().splitCall(responseStream),
        new BatchPublisherImpl.Factory(),
        initialRequest,
        batchingSettings);
    addServices(backgroundResourceAsApiService(client));
  }

  @GuardedBy("monitor.monitor")
  private void rebatchForRestart() {
    Queue<UnbatchedMessage> messages = new ArrayDeque<>();
    for (InFlightBatch batch : batchesInFlight) {
      for (int i = 0; i < batch.messages.size(); ++i) {
        messages.add(UnbatchedMessage.of(batch.messages.get(i), batch.messageFutures.get(i)));
      }
    }
    long size = 0;
    int count = 0;
    Queue<UnbatchedMessage> currentBatch = new ArrayDeque<>();
    batchesInFlight.clear();
    for (UnbatchedMessage message : messages) {
      long messageSize = message.message().getSerializedSize();
      if (size + messageSize > Constants.MAX_PUBLISH_BATCH_BYTES
          || count + 1 > Constants.MAX_PUBLISH_BATCH_COUNT) {
        if (!currentBatch.isEmpty()) {
          batchesInFlight.add(new InFlightBatch(currentBatch));
          currentBatch = new ArrayDeque<>();
          count = 0;
          size = 0;
        }
      }
      currentBatch.add(message);
      size = size + messageSize;
      count += 1;
    }
    if (!currentBatch.isEmpty()) {
      batchesInFlight.add(new InFlightBatch(currentBatch));
    }
  }

  @Override
  public void triggerReinitialize(CheckedApiException streamError) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      connection.reinitialize(initialRequest);
      rebatchForRestart();
      Collection<InFlightBatch> batches = batchesInFlight;
      connection.modifyConnection(
          connectionOr -> {
            if (!connectionOr.isPresent()) return;
            batches.forEach(batch -> connectionOr.get().publish(batch.messages));
          });
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      terminateOutstandingPublishes(error);
    }
  }

  @Override
  protected void start() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      // After initialize, the stream can have an error and try to cancel the future, but the
      // future can call into the stream, so this needs to be created under lock.
      this.alarmFuture =
          SystemExecutors.getAlarmExecutor()
              .scheduleWithFixedDelay(
                  this::flushToStream,
                  batchingSettings.getDelayThreshold().toNanos(),
                  batchingSettings.getDelayThreshold().toNanos(),
                  TimeUnit.NANOSECONDS);
    }
  }

  @Override
  protected void stop() {
    alarmFuture.cancel(false /* mayInterruptIfRunning */);
    flush(); // Flush any outstanding messages that were batched.
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
    }
    flush(); // Flush again in case messages were added since shutdown was set.
  }

  @GuardedBy("monitor.monitor")
  private void processBatch(Collection<UnbatchedMessage> batch) throws CheckedApiException {
    if (batch.isEmpty()) return;
    InFlightBatch inFlightBatch = new InFlightBatch(batch);
    batchesInFlight.add(inFlightBatch);
    connection.modifyConnection(
        connectionOr -> {
          checkState(connectionOr.isPresent(), "Published after the stream shut down.");
          connectionOr.get().publish(inFlightBatch.messages);
        });
  }

  @GuardedBy("monitor.monitor")
  private void terminateOutstandingPublishes(CheckedApiException e) {
    batchesInFlight.forEach(
        batch -> batch.messageFutures.forEach(future -> future.setException(e)));
    batcher.flush().forEach(m -> m.future().setException(e));
    batchesInFlight.clear();
  }

  @Override
  public ApiFuture<Offset> publish(Message message) {
    PubSubMessage proto = message.toProto();
    try (CloseableMonitor.Hold h = monitor.enter()) {
      ApiService.State currentState = state();
      checkState(
          currentState == ApiService.State.RUNNING,
          String.format("Cannot publish when Publisher state is %s.", currentState.name()));
      checkState(!shutdown, "Published after the stream shut down.");
      ApiFuture<Offset> messageFuture = batcher.add(proto);
      if (batcher.shouldFlush()) {
        processBatch(batcher.flush());
      }
      return messageFuture;
    } catch (CheckedApiException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void cancelOutstandingPublishes() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      terminateOutstandingPublishes(
          new CheckedApiException("Cancelled by client.", Code.CANCELLED));
    }
  }

  @VisibleForTesting
  void flushToStream() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      processBatch(batcher.flush());
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  // Flushable implementation
  @Override
  public void flush() {
    flushToStream();
    try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(noneInFlight)) {}
  }

  @Override
  public void onClientResponse(Offset value) throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkState(
          !batchesInFlight.isEmpty(), "Received publish response with no batches in flight.");
      if (lastSentOffset.isPresent() && lastSentOffset.get().value() >= value.value()) {
        throw new CheckedApiException(
            String.format(
                "Received publish response with offset %s that is inconsistent with previous"
                    + " responses max %s",
                value, lastSentOffset.get()),
            Code.FAILED_PRECONDITION);
      }
      InFlightBatch batch = batchesInFlight.remove();
      lastSentOffset = Optional.of(Offset.of(value.value() + batch.messages.size() - 1));
      for (int i = 0; i < batch.messageFutures.size(); i++) {
        Offset offset = Offset.of(value.value() + i);
        batch.messageFutures.get(i).set(offset);
      }
    }
  }
}
