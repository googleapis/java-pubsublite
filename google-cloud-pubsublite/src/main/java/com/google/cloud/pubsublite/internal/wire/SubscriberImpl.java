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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc.SubscriberServiceStub;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Monitor;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.concurrent.GuardedBy;

public class SubscriberImpl extends ProxyService
    implements Subscriber, RetryingConnectionObserver<Response> {
  private static final long BATCH_FLOW_REQUESTS_INTERVAL_MS = 100;

  private final Consumer<ImmutableList<SequencedMessage>> messageConsumer;

  private final CloseableMonitor monitor = new CloseableMonitor();

  private final ScheduledExecutorService executorService;
  private Future<?> alarmFuture;

  @GuardedBy("monitor.monitor")
  private final RetryingConnection<ConnectedSubscriber> connection;

  @GuardedBy("monitor.monitor")
  private final NextOffsetTracker nextOffsetTracker = new NextOffsetTracker();

  @GuardedBy("monitor.monitor")
  private final FlowControlBatcher flowControlBatcher = new FlowControlBatcher();

  @GuardedBy("monitor.monitor")
  private Optional<InFlightSeek> inFlightSeek = Optional.empty();

  @GuardedBy("monitor.monitor")
  private boolean internalSeekInFlight = false;

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  private static class InFlightSeek {
    final SeekRequest seekRequest;
    final SettableApiFuture<Offset> seekFuture;

    InFlightSeek(SeekRequest request, SettableApiFuture<Offset> future) {
      seekRequest = request;
      seekFuture = future;
    }
  }

  @VisibleForTesting
  SubscriberImpl(
      SubscriberServiceStub stub,
      ConnectedSubscriberFactory factory,
      InitialSubscribeRequest initialRequest,
      Consumer<ImmutableList<SequencedMessage>> messageConsumer)
      throws StatusException {
    this.messageConsumer = messageConsumer;
    this.connection =
        new RetryingConnectionImpl<>(
            stub::subscribe,
            factory,
            SubscribeRequest.newBuilder().setInitial(initialRequest).build(),
            this);
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    addServices(this.connection);
  }

  public SubscriberImpl(
      SubscriberServiceStub stub,
      InitialSubscribeRequest initialRequest,
      Consumer<ImmutableList<SequencedMessage>> messageConsumer)
      throws StatusException {
    this(stub, new ConnectedSubscriberImpl.Factory(), initialRequest, messageConsumer);
  }

  // ProxyService implementation.
  @Override
  protected void handlePermanentError(StatusException error) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      inFlightSeek.ifPresent(inFlight -> inFlight.seekFuture.setException(error));
      inFlightSeek = Optional.empty();
      onPermanentError(error);
    }
  }

  @Override
  protected void start() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      alarmFuture =
          executorService.scheduleWithFixedDelay(
              this::processBatchFlowRequest,
              BATCH_FLOW_REQUESTS_INTERVAL_MS,
              BATCH_FLOW_REQUESTS_INTERVAL_MS,
              TimeUnit.MILLISECONDS);
    }
  }

  @Override
  protected void stop() {
    alarmFuture.cancel(false /* mayInterruptIfRunning */);
    executorService.shutdown();
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      inFlightSeek.ifPresent(
          inFlight ->
              inFlight.seekFuture.setException(
                  Status.ABORTED
                      .withDescription("Client stopped while seek in flight.")
                      .asException()));
    }
  }

  @Override
  public ApiFuture<Offset> seek(SeekRequest request) {
    try (CloseableMonitor.Hold h =
        monitor.enterWhenUninterruptibly(
            new Monitor.Guard(monitor.monitor) {
              @Override
              public boolean isSatisfied() {
                return !internalSeekInFlight || shutdown;
              }
            })) {
      checkArgument(
          Predicates.isValidSeekRequest(request), "Sent SeekRequest with no location set.");
      checkState(!shutdown, "Seeked after the stream shut down.");
      checkState(!inFlightSeek.isPresent(), "Seeked while seek is already in flight.");
      SettableApiFuture<Offset> future = SettableApiFuture.create();
      inFlightSeek = Optional.of(new InFlightSeek(request, future));
      flowControlBatcher.onClientSeek();
      connection.modifyConnection(
          connectedSubscriber ->
              connectedSubscriber.ifPresent(subscriber -> subscriber.seek(request)));
      return future;
    } catch (StatusException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public boolean seekInFlight() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      return inFlightSeek.isPresent();
    }
  }

  @Override
  public void allowFlow(FlowControlRequest clientRequest) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      flowControlBatcher.onClientFlowRequest(clientRequest);
      if (flowControlBatcher.shouldExpediteBatchRequest()) {
        connection.modifyConnection(
            connectedSubscriber ->
                connectedSubscriber.ifPresent(subscriber -> flushBatchFlowRequest(subscriber)));
      }
    } catch (StatusException e) {
      onPermanentError(e);
      throw e.getStatus().asRuntimeException();
    }
  }

  @Override
  @SuppressWarnings("GuardedBy")
  public void triggerReinitialize() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      connection.reinitialize();
      connection.modifyConnection(
          connectedSubscriber -> {
            checkArgument(monitor.monitor.isOccupiedByCurrentThread());
            checkArgument(connectedSubscriber.isPresent());
            if (inFlightSeek.isPresent()) {
              connectedSubscriber.get().seek(inFlightSeek.get().seekRequest);
            } else {
              nextOffsetTracker
                  .requestForRestart()
                  .ifPresent(
                      request -> {
                        internalSeekInFlight = true;
                        connectedSubscriber.get().seek(request);
                      });
            }
            flowControlBatcher
                .requestForRestart()
                .ifPresent(
                    request -> {
                      connectedSubscriber.get().allowFlow(request);
                      flowControlBatcher.onFlowRequestDispatched();
                    });
          });
    } catch (StatusException e) {
      onPermanentError(e);
    }
  }

  @Override
  public Status onClientResponse(Response value) {
    switch (value.getKind()) {
      case MESSAGES:
        return onMessageResponse(value.messages());
      case SEEK_OFFSET:
        return onSeekResponse(value.seekOffset());
    }
    return Status.FAILED_PRECONDITION.withDescription("Invalid switch case: " + value.getKind());
  }

  private Status onMessageResponse(ImmutableList<SequencedMessage> messages) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) {
        return Status.OK;
      }
      nextOffsetTracker.onMessages(messages);
      flowControlBatcher.onMessages(messages);
    } catch (StatusException e) {

      onPermanentError(e);
      return e.getStatus();
    }
    messageConsumer.accept(messages);
    return Status.OK;
  }

  private Status onSeekResponse(Offset seekOffset) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) {
        return Status.OK;
      }
      if (internalSeekInFlight) {
        internalSeekInFlight = false;
        return Status.OK;
      }
      checkState(inFlightSeek.isPresent(), "No in flight seek, but received a seek response.");
      nextOffsetTracker.onClientSeek(seekOffset);
      inFlightSeek.get().seekFuture.set(seekOffset);
      inFlightSeek = Optional.empty();
      return Status.OK;
    } catch (StatusException e) {
      onPermanentError(e);
      return e.getStatus();
    }
  }

  @VisibleForTesting
  void processBatchFlowRequest() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      connection.modifyConnection(
          connectedSubscriber ->
              connectedSubscriber.ifPresent(subscriber -> flushBatchFlowRequest(subscriber)));
    } catch (StatusException e) {
      onPermanentError(e);
    }
  }

  private void flushBatchFlowRequest(ConnectedSubscriber subscriber) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      flowControlBatcher
          .pendingBatchRequest()
          .ifPresent(
              flowControlRequest -> {
                subscriber.allowFlow(flowControlRequest);
                flowControlBatcher.onFlowRequestDispatched();
              });
    }
  }
}
