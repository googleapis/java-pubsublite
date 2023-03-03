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

  // "this" will not be held in any upcalls.

  @GuardedBy("this")
  private long nextRetryBackoffDuration = INITIAL_RECONNECT_BACKOFF_TIME.toMillis();

  @GuardedBy("this")
  private StreamRequestT lastInitialRequest;

  @GuardedBy("this")
  private ConnectionT currentConnection;

  @GuardedBy("this")
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
  protected synchronized void doStart() {
    StreamRequestT initialInitialRequest = lastInitialRequest;
    SystemExecutors.getFuturesExecutor()
        .execute(
            () -> {
              reinitialize(initialInitialRequest);
              notifyStarted();
            });
  }

  // Reinitialize the stream. Must be called in a downcall to prevent deadlock.
  @Override
  public synchronized void reinitialize(StreamRequestT initialRequest) {
    if (completed) return;
    lastInitialRequest = initialRequest;
    logger.atFiner().log("Start initializing connection for %s", streamDescription());
    currentConnection = connectionFactory.New(streamFactory, this, lastInitialRequest);
    logger.atFiner().log("Finished initializing connection for %s", streamDescription());
  }

  @Override
  protected synchronized void doStop() {
    try {
      if (completed) return;
      completed = true;
      logger.atFine().log("Terminating connection for %s", streamDescription());
      if (currentConnection != null) {
        currentConnection.close();
      }
    } catch (Throwable t) {
      logger.atInfo().withCause(t).log(
          "Failed while terminating connection for %s", streamDescription());
      notifyFailed(t);
      return;
    }
    notifyStopped();
  }

  // Run modification on the current connection or empty if not connected.
  @Override
  public synchronized void modifyConnection(Modifier<ConnectionT> modifier)
      throws CheckedApiException {
    if (completed) {
      modifier.modify(Optional.empty());
    } else {
      modifier.modify(Optional.of(currentConnection));
    }
  }

  synchronized void setPermanentError(Throwable error) {
    if (completed) return;
    completed = true;
    logger.atInfo().withCause(error).log("Permanent error occurred for %s", streamDescription());
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
    synchronized (this) {
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
    try {
      synchronized (this) {
        if (currentConnection != null) {
          currentConnection.close();
        }
        backoffTime = nextRetryBackoffDuration;
        nextRetryBackoffDuration = Math.min(backoffTime * 2, MAX_RECONNECT_BACKOFF_TIME.toMillis());
      }
    } catch (Throwable t2) {
      setPermanentError(
          new CheckedApiException(
              "Failed to close preexisting stream after error.",
              throwable.get(),
              Code.FAILED_PRECONDITION));
      return;
    }
    logger.atFine().withCause(t).log(
        "Stream disconnected attempting retry, after %s milliseconds for %s",
        backoffTime, streamDescription());
    ScheduledFuture<?> unusedFuture =
        SystemExecutors.getAlarmExecutor()
            .schedule(() -> triggerReinitialize(statusOr.get()), backoffTime, MILLISECONDS);
  }

  private void triggerReinitialize(CheckedApiException streamError) {
    // Reinitialize in an unbounded executor to avoid starving tasks using the bounded alarm
    // executor.
    SystemExecutors.getFuturesExecutor()
        .execute(
            () -> {
              try {
                observer.triggerReinitialize(streamError);
              } catch (Throwable t) {
                logger.atInfo().withCause(t).log("Error occurred in triggerReinitialize.");
                onError(t);
              }
            });
  }

  @Override
  public final void onComplete() {
    logger.atFine().log("Stream completed for %s", streamDescription());
    boolean expectedCompletion;
    synchronized (this) {
      expectedCompletion = completed;
    }
    if (!expectedCompletion) {
      setPermanentError(
          new CheckedApiException("Server unexpectedly closed stream.", Code.FAILED_PRECONDITION));
    }
  }

  private synchronized String streamDescription() {
    return lastInitialRequest.getClass().getSimpleName() + ": " + lastInitialRequest.toString();
  }
}
