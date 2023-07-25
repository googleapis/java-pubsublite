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

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.common.base.Preconditions;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.annotation.Nullable;

/**
 * A SingleConnection handles the state for a stream with an initial connection request that may
 * only be sent once and needs to wait for a response before sending other requests.
 *
 * @param <StreamRequestT> The request type sent on the stream.
 * @param <StreamResponseT> The response type sent from the stream.
 * @param <ClientResponseT> The response type sent to the client.
 */
public abstract class SingleConnection<StreamRequestT, StreamResponseT, ClientResponseT>
    implements ResponseObserver<StreamResponseT>, AutoCloseable {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  protected static final Duration DEFAULT_STREAM_IDLE_TIMEOUT = Duration.ofMinutes(2);

  private final ClientStream<StreamRequestT> requestStream;
  private final ResponseObserver<ClientResponseT> clientStream;
  private final StreamIdleTimer streamIdleTimer;

  @GuardedBy("this")
  private boolean receivedInitial = false;

  @GuardedBy("this")
  private final Queue<StreamRequestT> bufferedBeforeInitial = new ArrayDeque<>();

  @GuardedBy("this")
  private boolean completed = false;

  protected abstract void handleStreamResponse(StreamResponseT response) throws CheckedApiException;

  protected SingleConnection(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      ResponseObserver<ClientResponseT> clientStream,
      Duration streamIdleTimeout) {
    this.clientStream = clientStream;
    this.streamIdleTimer = new StreamIdleTimer(streamIdleTimeout, this::onStreamIdle);
    this.requestStream = streamFactory.New(this);
  }

  protected SingleConnection(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      ResponseObserver<ClientResponseT> clientStream) {
    this(streamFactory, clientStream, DEFAULT_STREAM_IDLE_TIMEOUT);
  }

  protected void initialize(StreamRequestT initialRequest) {
    this.requestStream.send(initialRequest);
  }

  protected synchronized void sendToStream(StreamRequestT request) {
    if (completed) {
      log.atFine().log("Sent request after stream completion: %s", request);
      return;
    }
    if (!receivedInitial) {
      bufferedBeforeInitial.add(request);
      return;
    }
    requestStream.send(request);
  }

  protected void sendToClient(ClientResponseT response) {
    synchronized (this) {
      if (completed) {
        log.atFine().log("Sent response after stream completion: %s", response);
        return;
      }
      // We should not send data to the client before receiving the initial value.
      Preconditions.checkState(receivedInitial);
    }
    // The upcall may be reentrant, possibly on another thread while this thread is blocked.
    clientStream.onResponse(response);
  }

  protected void setError(CheckedApiException error) {
    abort(error);
  }

  // Records the connection as completed and performs tear down, if not already completed. Returns
  // whether the connection was already complete.
  private synchronized boolean completeStream() {
    try {
      if (completed) {
        return true;
      }
      completed = true;
      streamIdleTimer.close();
    } catch (Exception e) {
      log.atSevere().withCause(e).log("Error occurred while shutting down connection.");
    }
    return false;
  }

  @Override
  public void close() {
    if (completeStream()) {
      return;
    }
    requestStream.closeSend();
    clientStream.onComplete();
  }

  private void abort(CheckedApiException error) {
    if (completeStream()) {
      return;
    }
    requestStream.closeSendWithError(error.underlying);
    clientStream.onError(error);
  }

  // ResponseObserver implementation
  @Override
  public void onStart(StreamController streamController) {}

  @Override
  public void onResponse(StreamResponseT response) {
    synchronized (this) {
      streamIdleTimer.restart();
      if (completed) {
        log.atFine().log("Received response on stream after completion: %s", response);
        return;
      }
      if (!receivedInitial) {
        handleInitial();
      }
    }
    try {
      handleStreamResponse(response);
    } catch (CheckedApiException e) {
      abort(e);
    }
  }

  @GuardedBy("this")
  private void handleInitial() {
    for (@Nullable StreamRequestT req = bufferedBeforeInitial.poll();
        req != null;
        req = bufferedBeforeInitial.poll()) {
      requestStream.send(req);
    }
    receivedInitial = true;
  }

  @Override
  public void onError(Throwable t) {
    if (completeStream()) {
      return;
    }
    clientStream.onError(t);
    requestStream.closeSendWithError(t);
  }

  @Override
  public void onComplete() {
    if (completeStream()) {
      return;
    }
    clientStream.onComplete();
    requestStream.closeSend();
  }

  private void onStreamIdle() {
    onError(new CheckedApiException("Detected idle stream.", Code.ABORTED));
  }
}
