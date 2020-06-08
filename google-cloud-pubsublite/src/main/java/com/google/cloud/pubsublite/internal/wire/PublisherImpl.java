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

import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.SerialBatcher.UnbatchedMessage;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Monitor;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.concurrent.GuardedBy;

public final class PublisherImpl extends ProxyService
    implements Publisher<Offset>, RetryingConnectionObserver<Offset> {
  @GuardedBy("monitor.monitor")
  private final RetryingConnection<BatchPublisher> connection;

  private final ScheduledExecutorService executorService;
  private final BatchingSettings batchingSettings;
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
  private boolean shutdown = false;

  @GuardedBy("monitor.monitor")
  private Optional<Offset> lastSentOffset = Optional.empty();

  @GuardedBy("monitor.monitor")
  private final SerialBatcher batcher;

  private static class InFlightBatch {
    final List<PubSubMessage> messages;
    final List<SettableApiFuture<Offset>> messageFutures;

    InFlightBatch(Collection<UnbatchedMessage> toBatch) {
      messages =
          toBatch.stream()
              .map(SerialBatcher.UnbatchedMessage::message)
              .collect(Collectors.toList());
      messageFutures =
          toBatch.stream().map(SerialBatcher.UnbatchedMessage::future).collect(Collectors.toList());
    }
  }

  // An ordered list of batches in flight.
  @GuardedBy("monitor.monitor")
  private final Queue<InFlightBatch> batchesInFlight = new ArrayDeque<>();

  @VisibleForTesting
  PublisherImpl(
      PublisherServiceGrpc.PublisherServiceStub stub,
      BatchPublisherFactory publisherFactory,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws StatusException {
    Preconditions.checkNotNull(batchingSettings.getDelayThreshold());
    Preconditions.checkNotNull(batchingSettings.getRequestByteThreshold());
    Preconditions.checkNotNull(batchingSettings.getElementCountThreshold());
    this.connection =
        new RetryingConnectionImpl<>(
            stub::publish,
            publisherFactory,
            PublishRequest.newBuilder().setInitialRequest(initialRequest).build(),
            this);
    this.batcher =
        new SerialBatcher(
            batchingSettings.getRequestByteThreshold(),
            batchingSettings.getElementCountThreshold());
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.batchingSettings = batchingSettings;
    addServices(connection);
  }

  public PublisherImpl(
      PublisherServiceGrpc.PublisherServiceStub stub,
      InitialPublishRequest initialRequest,
      BatchingSettings batchingSettings)
      throws StatusException {
    this(stub, new BatchPublisherImpl.Factory(), initialRequest, batchingSettings);
  }

  @Override
  public void triggerReinitialize() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      connection.reinitialize();
      Collection<InFlightBatch> batches = batchesInFlight;
      connection.modifyConnection(
          connectionOr -> {
            if (!connectionOr.isPresent()) return;
            batches.forEach(batch -> connectionOr.get().publish(batch.messages));
          });
    } catch (StatusException e) {
      onPermanentError(e);
    }
  }

  @Override
  protected void handlePermanentError(StatusException error) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      batchesInFlight.forEach(
          batch -> batch.messageFutures.forEach(future -> future.setException(error)));
      batcher.flush().forEach(m -> m.future().setException(error));
      batchesInFlight.clear();
    }
  }

  @Override
  protected void start() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      // After initialize, the stream can have an error and try to cancel the future, but the
      // future can call into the stream, so this needs to be created under lock.
      this.alarmFuture =
          this.executorService.scheduleWithFixedDelay(
              this::flushToStream,
              batchingSettings.getDelayThreshold().toNanos(),
              batchingSettings.getDelayThreshold().toNanos(),
              TimeUnit.NANOSECONDS);
    }
  }

  @Override
  protected void stop() {
    alarmFuture.cancel(false /* mayInterruptIfRunning */);
    executorService.shutdown();
    flush(); // Flush any outstanding messages that were batched.
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
    }
    flush(); // Flush again in case messages were added since shutdown was set.
  }

  @GuardedBy("monitor.monitor")
  private void processBatch(Collection<UnbatchedMessage> batch) throws StatusException {
    if (batch.isEmpty()) return;
    InFlightBatch inFlightBatch = new InFlightBatch(batch);
    batchesInFlight.add(inFlightBatch);
    connection.modifyConnection(
        connectionOr -> {
          checkState(connectionOr.isPresent(), "Published after the stream shut down.");
          connectionOr.get().publish(inFlightBatch.messages);
        });
  }

  @Override
  public ApiFuture<Offset> publish(Message message) {
    PubSubMessage proto = message.toProto();
    if (proto.getSerializedSize() > Constants.MAX_PUBLISH_MESSAGE_BYTES) {
      Status error =
          Status.FAILED_PRECONDITION.withDescription(
              String.format(
                  "Tried to send message with serialized size %s larger than limit %s on the"
                      + " stream.",
                  proto.getSerializedSize(), Constants.MAX_PUBLISH_MESSAGE_BYTES));
      try (CloseableMonitor.Hold h = monitor.enter()) {
        if (!shutdown) {
          onPermanentError(error.asException());
        }
      }
      return ApiFutures.immediateFailedFuture(error.asException());
    }
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
    } catch (StatusException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @VisibleForTesting
  void flushToStream() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      processBatch(batcher.flush());
    } catch (StatusException e) {
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
  public Status onClientResponse(Offset value) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (batchesInFlight.isEmpty()) {
        return Status.FAILED_PRECONDITION.withDescription(
            "Received publish response with no batches in flight.");
      }
      if (lastSentOffset.isPresent() && lastSentOffset.get().value() >= value.value()) {
        return Status.FAILED_PRECONDITION.withDescription(
            String.format(
                "Received publish response with offset %s that is inconsistent with previous"
                    + " responses max %s",
                value, lastSentOffset.get()));
      }
      if (batchesInFlight.isEmpty()) {
        return Status.FAILED_PRECONDITION.withDescription("Received empty batch on stream.");
      }
      InFlightBatch batch = batchesInFlight.remove();
      lastSentOffset = Optional.of(Offset.of(value.value() + batch.messages.size() - 1));
      for (int i = 0; i < batch.messageFutures.size(); i++) {
        Offset offset = Offset.of(value.value() + i);
        batch.messageFutures.get(i).set(offset);
      }
      return Status.OK;
    }
  }
}
