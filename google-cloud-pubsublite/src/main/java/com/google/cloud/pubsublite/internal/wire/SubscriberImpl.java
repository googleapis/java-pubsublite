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
import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.SerialExecutor;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.SubscribeStreamFactory;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.annotation.concurrent.GuardedBy;

public class SubscriberImpl extends ProxyService
    implements Subscriber, RetryingConnectionObserver<List<SequencedMessage>> {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private static final Duration FLOW_REQUESTS_FLUSH_INTERVAL = Duration.ofMillis(100);

  private final AlarmFactory alarmFactory;

  private final Consumer<List<SequencedMessage>> messageConsumer;

  private final SubscriberResetHandler resetHandler;

  private final InitialSubscribeRequest baseInitialRequest;

  // Whether to retry `DUPLICATE_SUBSCRIBER_CONNECTIONS` errors.
  private final boolean retryStreamRaces;

  // Used to ensure messages are delivered to consumers in order.
  private final SerialExecutor messageDeliveryExecutor;

  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private Optional<Future<?>> alarmFuture = Optional.empty();

  @GuardedBy("monitor.monitor")
  private final RetryingConnection<SubscribeRequest, ConnectedSubscriber> connection;

  @GuardedBy("monitor.monitor")
  private final NextOffsetTracker nextOffsetTracker = new NextOffsetTracker();

  @GuardedBy("monitor.monitor")
  private final FlowControlBatcher flowControlBatcher = new FlowControlBatcher();

  @GuardedBy("monitor.monitor")
  private SeekRequest initialLocation;

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  @VisibleForTesting
  SubscriberImpl(
      SubscribeStreamFactory streamFactory,
      ConnectedSubscriberFactory factory,
      AlarmFactory alarmFactory,
      InitialSubscribeRequest baseInitialRequest,
      SeekRequest initialLocation,
      Consumer<List<SequencedMessage>> messageConsumer,
      SubscriberResetHandler resetHandler,
      boolean retryStreamRaces)
      throws ApiException {
    this.alarmFactory = alarmFactory;
    this.messageConsumer = messageConsumer;
    this.resetHandler = resetHandler;
    this.baseInitialRequest = baseInitialRequest;
    this.retryStreamRaces = retryStreamRaces;
    this.messageDeliveryExecutor = new SerialExecutor(SystemExecutors.getFuturesExecutor());
    this.initialLocation = initialLocation;
    this.connection =
        new RetryingConnectionImpl<>(streamFactory, factory, this, getInitialRequest());
    addServices(this.connection);
  }

  public SubscriberImpl(
      SubscribeStreamFactory streamFactory,
      InitialSubscribeRequest baseInitialRequest,
      SeekRequest initialLocation,
      Consumer<List<SequencedMessage>> messageConsumer,
      SubscriberResetHandler resetHandler,
      boolean retryStreamRaces)
      throws ApiException {
    this(
        streamFactory,
        new ConnectedSubscriberImpl.Factory(),
        AlarmFactory.create(FLOW_REQUESTS_FLUSH_INTERVAL),
        baseInitialRequest,
        initialLocation,
        messageConsumer,
        resetHandler,
        retryStreamRaces);
  }

  // ProxyService implementation.
  @Override
  protected void start() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      alarmFuture = Optional.of(alarmFactory.newAlarm(this::processBatchFlowRequest));
    }
  }

  @Override
  protected void stop() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      this.alarmFuture.ifPresent(future -> future.cancel(false));
      this.alarmFuture = Optional.empty();
      messageDeliveryExecutor.close();
    }
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    stop();
  }

  @Override
  public void allowFlow(FlowControlRequest clientRequest) throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      flowControlBatcher.onClientFlowRequest(clientRequest);
      if (flowControlBatcher.shouldExpediteBatchRequest()) {
        connection.modifyConnection(
            connectedSubscriber -> connectedSubscriber.ifPresent(this::flushBatchFlowRequest));
      }
    }
  }

  private SubscribeRequest getInitialRequest() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      return SubscribeRequest.newBuilder()
          .setInitial(
              baseInitialRequest
                  .toBuilder()
                  .setInitialLocation(
                      nextOffsetTracker.requestForRestart().orElse(initialLocation)))
          .build();
    }
  }

  public void reset() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      nextOffsetTracker.reset();
      initialLocation =
          SeekRequest.newBuilder().setNamedTarget(NamedTarget.COMMITTED_CURSOR).build();
    }
  }

  @Override
  @SuppressWarnings("GuardedBy")
  public void triggerReinitialize(CheckedApiException streamError) {
    if (!retryStreamRaces
        && ExtractStatus.getErrorInfoReason(streamError)
            .equals("DUPLICATE_SUBSCRIBER_CONNECTIONS")) {
      onPermanentError(streamError);
      return;
    }
    if (ResetSignal.isResetSignal(streamError)) {
      try {
        // Flush pre-seek messages.
        messageDeliveryExecutor.waitUntilInactive();
        if (resetHandler.handleReset()) {
          // Wait for cursor commit.
          reset();
        }
      } catch (CheckedApiException e) {
        onPermanentError(e);
        return;
      }
    }

    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      connection.reinitialize(getInitialRequest());
      connection.modifyConnection(
          connectedSubscriber -> {
            checkArgument(monitor.monitor.isOccupiedByCurrentThread());
            checkArgument(connectedSubscriber.isPresent());
            flowControlBatcher
                .requestForRestart()
                .ifPresent(request -> connectedSubscriber.get().allowFlow(request));
          });
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  @Override
  public void onClientResponse(List<SequencedMessage> messages) throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      nextOffsetTracker.onMessages(messages);
      flowControlBatcher.onMessages(messages);
      messageDeliveryExecutor.execute(
          () -> {
            try {
              messageConsumer.accept(messages);
            } catch (Throwable t) {
              logger.atWarning().withCause(t).log(
                  "Consumer threw an exception- failing subscriber. %s", baseInitialRequest);
              onPermanentError(toCanonical(t));
            }
          });
    }
  }

  private void processBatchFlowRequest() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) return;
      connection.modifyConnection(
          connectedSubscriber -> connectedSubscriber.ifPresent(this::flushBatchFlowRequest));
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  private void flushBatchFlowRequest(ConnectedSubscriber subscriber) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      flowControlBatcher.releasePendingRequest().ifPresent(subscriber::allowFlow);
    }
  }
}
