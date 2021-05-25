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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;
import static com.google.cloud.pubsublite.internal.wire.ApiServiceUtils.backgroundResourceAsApiService;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Monitor;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.concurrent.GuardedBy;

public class SubscriberImpl extends ProxyService
    implements Subscriber, RetryingConnectionObserver<Response> {
  @VisibleForTesting static final long FLOW_REQUESTS_FLUSH_INTERVAL_MS = 100;

  private final Consumer<ImmutableList<SequencedMessage>> messageConsumer;

  private final CloseableMonitor monitor = new CloseableMonitor();

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
      StreamFactory<SubscribeRequest, SubscribeResponse> streamFactory,
      ConnectedSubscriberFactory factory,
      InitialSubscribeRequest initialRequest,
      Consumer<ImmutableList<SequencedMessage>> messageConsumer)
      throws ApiException {
    this.messageConsumer = messageConsumer;
    this.connection =
        new RetryingConnectionImpl<>(
            streamFactory,
            factory,
            InitialRequestProvider.of(
                SubscribeRequest.newBuilder().setInitial(initialRequest).build()),
            this,
            ResetHandler::noop);
    addServices(this.connection);
  }

  public SubscriberImpl(
      SubscriberServiceClient client,
      InitialSubscribeRequest initialRequest,
      Consumer<ImmutableList<SequencedMessage>> messageConsumer)
      throws ApiException {
    this(
        stream -> client.subscribeCallable().splitCall(stream),
        new ConnectedSubscriberImpl.Factory(),
        initialRequest,
        messageConsumer);
    addServices(backgroundResourceAsApiService(client));
  }

  // ProxyService implementation.
  @Override
  protected void handlePermanentError(CheckedApiException error) {
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
          SystemExecutors.getAlarmExecutor()
              .scheduleWithFixedDelay(
                  this::processBatchFlowRequest,
                  FLOW_REQUESTS_FLUSH_INTERVAL_MS,
                  FLOW_REQUESTS_FLUSH_INTERVAL_MS,
                  TimeUnit.MILLISECONDS);
    }
  }

  @Override
  protected void stop() {
    alarmFuture.cancel(false /* mayInterruptIfRunning */);
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      inFlightSeek.ifPresent(
          inFlight ->
              inFlight.seekFuture.setException(
                  new CheckedApiException("Client stopped while seek in flight.", Code.ABORTED)));
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
    } catch (CheckedApiException e) {
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
  public void allowFlow(FlowControlRequest clientRequest) throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      flowControlBatcher.onClientFlowRequest(clientRequest);
      if (flowControlBatcher.shouldExpediteBatchRequest()) {
        connection.modifyConnection(
            connectedSubscriber ->
                connectedSubscriber.ifPresent(subscriber -> flushBatchFlowRequest(subscriber)));
      }
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
                .ifPresent(request -> connectedSubscriber.get().allowFlow(request));
          });
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  @Override
  public void onClientResponse(Response value) throws CheckedApiException {
    switch (value.getKind()) {
      case MESSAGES:
        onMessageResponse(value.messages());
        return;
      case SEEK_OFFSET:
        onSeekResponse(value.seekOffset());
        return;
    }
    throw new CheckedApiException(
        "Invalid switch case: " + value.getKind(), Code.FAILED_PRECONDITION);
  }

  private void onMessageResponse(ImmutableList<SequencedMessage> messages)
      throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      nextOffsetTracker.onMessages(messages);
      flowControlBatcher.onMessages(messages);
    }
    messageConsumer.accept(messages);
  }

  private void onSeekResponse(Offset seekOffset) throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      if (internalSeekInFlight) {
        internalSeekInFlight = false;
        return;
      }
      checkState(inFlightSeek.isPresent(), "No in flight seek, but received a seek response.");
      nextOffsetTracker.onClientSeek(seekOffset);
      inFlightSeek.get().seekFuture.set(seekOffset);
      inFlightSeek = Optional.empty();
    }
  }

  @VisibleForTesting
  void processBatchFlowRequest() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      connection.modifyConnection(
          connectedSubscriber ->
              connectedSubscriber.ifPresent(subscriber -> flushBatchFlowRequest(subscriber)));
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  private void flushBatchFlowRequest(ConnectedSubscriber subscriber) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      flowControlBatcher
          .releasePendingRequest()
          .ifPresent(request -> subscriber.allowFlow(request));
    }
  }
}
