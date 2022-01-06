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
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.common.base.Preconditions;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.Monitor.Guard;
import com.google.errorprone.annotations.concurrent.GuardedBy;

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

  private final ClientStream<StreamRequestT> requestStream;
  private final ResponseObserver<ClientResponseT> clientStream;
  private final boolean expectInitial;

  private final CloseableMonitor connectionMonitor = new CloseableMonitor();

  @GuardedBy("connectionMonitor.monitor")
  private boolean receivedInitial = false;

  @GuardedBy("connectionMonitor.monitor")
  private boolean completed = false;

  protected abstract void handleInitialResponse(StreamResponseT response)
      throws CheckedApiException;

  protected abstract void handleStreamResponse(StreamResponseT response) throws CheckedApiException;

  protected SingleConnection(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      ResponseObserver<ClientResponseT> clientStream,
      boolean expectInitialResponse) {
    this.clientStream = clientStream;
    this.requestStream = streamFactory.New(this);
    this.expectInitial = expectInitialResponse;
  }

  protected SingleConnection(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      ResponseObserver<ClientResponseT> clientStream) {
    this(streamFactory, clientStream, /*expectInitialResponse=*/ true);
  }

  protected void initialize(StreamRequestT initialRequest) {
    this.requestStream.send(initialRequest);
    if (!expectInitial) {
      return;
    }
    try (CloseableMonitor.Hold h =
        connectionMonitor.enterWhenUninterruptibly(
            new Guard(connectionMonitor.monitor) {
              @Override
              public boolean isSatisfied() {
                return receivedInitial || completed;
              }
            })) {}
  }

  protected void sendToStream(StreamRequestT request) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) {
        log.atFine().log("Sent request after stream completion: %s", request);
        return;
      }
      // This should be impossible to not have received the initial request, or be completed, and
      // the caller has access to this object.
      Preconditions.checkState(receivedInitial);
      requestStream.send(request);
    }
  }

  protected void sendToClient(ClientResponseT response) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) {
        log.atFine().log("Sent response after stream completion: %s", response);
        return;
      }
      // This should be impossible to not have received the initial request, or be completed, and
      // the caller has access to this object.
      Preconditions.checkState(receivedInitial);
    }
    // The upcall may be reentrant, possibly on another thread while this thread is blocked.
    clientStream.onResponse(response);
  }

  protected void setError(CheckedApiException error) {
    abort(error);
  }

  protected boolean isCompleted() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      return completed;
    }
  }

  @Override
  public void close() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    requestStream.closeSend();
    clientStream.onComplete();
  }

  private void abort(CheckedApiException error) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    requestStream.closeSendWithError(error.underlying);
    clientStream.onError(error);
  }

  // ResponseObserver implementation
  @Override
  public void onStart(StreamController streamController) {}

  @Override
  public void onResponse(StreamResponseT response) {
    boolean isFirst;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) {
        log.atFine().log("Received response on stream after completion: %s", response);
        return;
      }
      isFirst = !receivedInitial;
      receivedInitial = true;
    }
    try {
      if (isFirst) {
        handleInitialResponse(response);
      } else {
        handleStreamResponse(response);
      }
    } catch (CheckedApiException e) {
      abort(e);
    }
  }

  @Override
  public void onError(Throwable t) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    clientStream.onError(t);
    requestStream.closeSendWithError(t);
  }

  @Override
  public void onComplete() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    clientStream.onComplete();
    requestStream.closeSend();
  }
}
