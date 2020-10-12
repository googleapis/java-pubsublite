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
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.common.flogger.GoogleLogger;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.concurrent.GuardedBy;
import org.threeten.bp.Duration;

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
    implements RetryingConnection<ConnectionT>, StreamObserver<ClientResponseT> {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private static final Duration INITIAL_RECONNECT_BACKOFF_TIME = Duration.ofMillis(10);
  private static final Duration MAX_RECONNECT_BACKOFF_TIME = Duration.ofSeconds(10);

  private final StreamFactory<StreamRequestT, StreamResponseT> streamFactory;
  private final SingleConnectionFactory<
          StreamRequestT, StreamResponseT, ClientResponseT, ConnectionT>
      connectionFactory;
  private final StreamRequestT initialRequest;
  private final RetryingConnectionObserver<ClientResponseT> observer;
  private final ScheduledExecutorService systemExecutor;

  // connectionMonitor will not be held in any upcalls.
  private final CloseableMonitor connectionMonitor = new CloseableMonitor();

  @GuardedBy("connectionMonitor.monitor")
  private long nextRetryBackoffDuration = INITIAL_RECONNECT_BACKOFF_TIME.toMillis();

  @GuardedBy("connectionMonitor.monitor")
  private ConnectionT currentConnection;

  @GuardedBy("connectionMonitor.monitor")
  private boolean completed = false;

  RetryingConnectionImpl(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      SingleConnectionFactory<StreamRequestT, StreamResponseT, ClientResponseT, ConnectionT>
          connectionFactory,
      StreamRequestT initialRequest,
      RetryingConnectionObserver<ClientResponseT> observer) {
    this.streamFactory = streamFactory;
    this.connectionFactory = connectionFactory;
    this.initialRequest = initialRequest;
    this.observer = observer;
    this.systemExecutor = Executors.newSingleThreadScheduledExecutor();
  }

  @Override
  protected void doStart() {
    this.systemExecutor.execute(
        () -> {
          reinitialize();
          notifyStarted();
        });
  }

  // Reinitialize the stream. Must be called in a downcall to prevent deadlock.
  @Override
  public void reinitialize() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      currentConnection = connectionFactory.New(streamFactory, this, initialRequest);
    }
  }

  @Override
  protected void doStop() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      completed = true;
      currentConnection.close();
    } catch (Exception e) {
      notifyFailed(e);
      return;
    }
    systemExecutor.shutdownNow();
    notifyStopped();
  }

  // Run modification on the current connection or empty if not connected.
  @Override
  public void modifyConnection(Modifier<ConnectionT> modifier) throws StatusException {
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

  // StreamObserver implementation
  @Override
  public final void onNext(ClientResponseT value) {
    Status status;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      nextRetryBackoffDuration = INITIAL_RECONNECT_BACKOFF_TIME.toMillis();
    }
    status = observer.onClientResponse(value);
    if (!status.isOk()) {
      setPermanentError(status.asRuntimeException());
    }
  }

  @Override
  public final void onError(Throwable t) {
    // This is an error code sent from the server.
    Optional<Status> statusOr = ExtractStatus.extract(t);
    if (!statusOr.isPresent()) {
      setPermanentError(t);
      return;
    }
    if (!ErrorCodes.IsRetryableForStreams(statusOr.get().getCode())) {
      setPermanentError(statusOr.get().asRuntimeException());
      return;
    }
    Optional<Throwable> throwable = Optional.empty();
    long backoffTime = 0;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      currentConnection.close();
      backoffTime = nextRetryBackoffDuration;
      nextRetryBackoffDuration = Math.min(backoffTime * 2, MAX_RECONNECT_BACKOFF_TIME.toMillis());
    } catch (Exception e) {
      throwable = Optional.of(e);
    }
    if (throwable.isPresent()) {
      setPermanentError(
          Status.FAILED_PRECONDITION
              .withCause(throwable.get())
              .withDescription("Failed to close preexisting stream after error.")
              .asRuntimeException());
      return;
    }
    logger.atFine().withCause(t).log(
        "Stream disconnected attempting retry, after %s milliseconds", backoffTime);
    ScheduledFuture<?> retry =
        systemExecutor.schedule(observer::triggerReinitialize, backoffTime, MILLISECONDS);
  }

  @Override
  public final void onCompleted() {
    logger.atFine().log("Stream completed for %s.", initialRequest);
    boolean expectedCompletion;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      expectedCompletion = completed;
    }
    if (!expectedCompletion) {
      setPermanentError(
          Status.FAILED_PRECONDITION
              .withDescription("Server unexpectedly closed stream.")
              .asRuntimeException());
    }
  }
}
