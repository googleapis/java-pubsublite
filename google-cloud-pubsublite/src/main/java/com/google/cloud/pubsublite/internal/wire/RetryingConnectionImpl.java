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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.api.core.AbstractApiService;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.concurrent.GuardedBy;

/**
 * A connection which recreates an underlying stream on retryable errors.
 *
 * @param <StreamRequestT> The type of request sent on the stream.
 * @param <StreamResponseT> The type of response sent by the stream.
 * @param <ClientResponseT> The type of response sent to the client.
 * @param <ConnectionT> The type of the underlying connection that is retried.
 */
class RetryingConnectionImpl<
        StreamRequestT, StreamResponseT, ClientResponseT, ConnectionT extends AutoCloseable>
    extends AbstractApiService
    implements RetryingConnection<StreamRequestT, ConnectionT>, ResponseObserver<ClientResponseT> {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private static final Duration INITIAL_RECONNECT_BACKOFF_TIME = Duration.ofMillis(10);
  private static final Duration MAX_RECONNECT_BACKOFF_TIME = Duration.ofSeconds(10);

  private final StreamFactory<StreamRequestT, StreamResponseT> streamFactory;
  private final SingleConnectionFactory<
          StreamRequestT, StreamResponseT, ClientResponseT, ConnectionT>
      connectionFactory;
  private final RetryingConnectionObserver<ClientResponseT> observer;

  // connectionMonitor will not be held in any upcalls.
  private final CloseableMonitor connectionMonitor = new CloseableMonitor();

  @GuardedBy("connectionMonitor.monitor")
  private long nextRetryBackoffDuration = INITIAL_RECONNECT_BACKOFF_TIME.toMillis();

  @GuardedBy("connectionMonitor.monitor")
  private StreamRequestT lastInitialRequest;

  @GuardedBy("connectionMonitor.monitor")
  private ConnectionT currentConnection;

  @GuardedBy("connectionMonitor.monitor")
  private boolean completed = false;

  RetryingConnectionImpl(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      SingleConnectionFactory<StreamRequestT, StreamResponseT, ClientResponseT, ConnectionT>
          connectionFactory,
      RetryingConnectionObserver<ClientResponseT> observer,
      StreamRequestT initialRequest) {
    this.streamFactory = streamFactory;
    this.connectionFactory = connectionFactory;
    this.observer = observer;
    this.lastInitialRequest = initialRequest;
  }

  @Override
  protected void doStart() {
    SystemExecutors.getAlarmExecutor()
        .execute(
            () -> {
              reinitialize(lastInitialRequest);
              notifyStarted();
            });
  }

  // Reinitialize the stream. Must be called in a downcall to prevent deadlock.
  @Override
  public void reinitialize(StreamRequestT initialRequest) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      lastInitialRequest = initialRequest;
      currentConnection = connectionFactory.New(streamFactory, this, lastInitialRequest);
    }
  }

  @Override
  protected void doStop() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
      logger.atFine().log(
          String.format("Terminating connection with initial request %s.", streamDescription()));
      currentConnection.close();
    } catch (Throwable t) {
      logger.atWarning().withCause(t).log(
          String.format(
              "Failed while terminating connection with initial request %s.", streamDescription()));
      notifyFailed(t);
      return;
    }
    logger.atFine().log(
        String.format("Terminated connection with initial request %s.", streamDescription()));
    notifyStopped();
  }

  // Run modification on the current connection or empty if not connected.
  @Override
  public void modifyConnection(Modifier<ConnectionT> modifier) throws CheckedApiException {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) {
        modifier.modify(Optional.empty());
      } else {
        modifier.modify(Optional.of(currentConnection));
      }
    }
  }

  void setPermanentError(Throwable error) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    notifyFailed(error);
  }

  @Override
  public void onStart(StreamController controller) {
    controller.disableAutoInboundFlowControl();
    controller.request(Integer.MAX_VALUE);
  }

  // ResponseObserver implementation
  @Override
  public final void onResponse(ClientResponseT value) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      nextRetryBackoffDuration = INITIAL_RECONNECT_BACKOFF_TIME.toMillis();
    }
    try {
      observer.onClientResponse(value);
    } catch (Throwable t) {
      setPermanentError(t);
    }
  }

  @Override
  public final void onError(Throwable t) {
    // This is an error code sent from the server.
    Optional<CheckedApiException> statusOr = ExtractStatus.extract(t);
    if (!statusOr.isPresent()) {
      setPermanentError(t);
      return;
    }
    if (!ErrorCodes.IsRetryableForStreams(statusOr.get().code())) {
      setPermanentError(statusOr.get());
      return;
    }
    Optional<Throwable> throwable = Optional.empty();
    long backoffTime = 0;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      currentConnection.close();
      backoffTime = nextRetryBackoffDuration;
      nextRetryBackoffDuration = Math.min(backoffTime * 2, MAX_RECONNECT_BACKOFF_TIME.toMillis());
    } catch (Throwable t2) {
      throwable = Optional.of(t2);
    }
    if (throwable.isPresent()) {
      setPermanentError(
          new CheckedApiException(
              "Failed to close preexisting stream after error.",
              throwable.get(),
              Code.FAILED_PRECONDITION));
      return;
    }
    logger.atFine().withCause(t).log(
        "Stream disconnected attempting retry, after %s milliseconds", backoffTime);
    ScheduledFuture<?> retry =
        SystemExecutors.getAlarmExecutor()
            .schedule(
                () -> {
                  try {
                    observer.triggerReinitialize(statusOr.get());
                  } catch (Throwable t2) {
                    logger.atWarning().withCause(t2).log("Error occurred in triggerReinitialize.");
                    onError(t2);
                  }
                },
                backoffTime,
                MILLISECONDS);
  }

  @Override
  public final void onComplete() {
    logger.atFine().log("Stream completed for %s.", streamDescription());
    boolean expectedCompletion;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      expectedCompletion = completed;
    }
    if (!expectedCompletion) {
      setPermanentError(
          new CheckedApiException("Server unexpectedly closed stream.", Code.FAILED_PRECONDITION));
    }
  }

  private String streamDescription() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      return lastInitialRequest.toString();
    }
  }
}
