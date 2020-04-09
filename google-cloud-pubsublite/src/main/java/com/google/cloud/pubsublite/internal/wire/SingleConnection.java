// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.common.base.Preconditions;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.Monitor.Guard;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * A SingleConnection handles the state for a stream with an initial connection request that may
 * only be sent once and needs to wait for a response before sending other requests.
 *
 * @param <StreamRequestT> The request type sent on the stream.
 * @param <StreamResponseT> The response type sent from the stream.
 * @param <ClientResponseT> The response type sent to the client.
 */
public abstract class SingleConnection<StreamRequestT, StreamResponseT, ClientResponseT>
    implements StreamObserver<StreamResponseT>, AutoCloseable {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final StreamObserver<StreamRequestT> requestStream;
  // onError and onCompleted may be called with connectionMonitor held. All messages will be sent
  // without it held.
  private final StreamObserver<ClientResponseT> clientStream;

  private final CloseableMonitor connectionMonitor = new CloseableMonitor();

  @GuardedBy("connectionMonitor.monitor")
  private boolean receivedInitial = false;

  @GuardedBy("connectionMonitor.monitor")
  private boolean completed = false;

  protected abstract Status handleInitialResponse(StreamResponseT response);

  protected abstract Status handleStreamResponse(StreamResponseT response);

  protected SingleConnection(
      StreamFactory<StreamRequestT, StreamResponseT> streamFactory,
      StreamObserver<ClientResponseT> clientStream) {
    this.clientStream = clientStream;
    this.requestStream = streamFactory.New(this);
  }

  protected void initialize(StreamRequestT initialRequest) {
    this.requestStream.onNext(initialRequest);
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
      requestStream.onNext(request);
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
    clientStream.onNext(response);
  }

  protected void setError(Status error) {
    Preconditions.checkArgument(!error.isOk());
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      abort(error);
    }
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
    requestStream.onCompleted();
    clientStream.onCompleted();
  }

  @GuardedBy("connectionMonitor.monitor")
  private void abort(Status error) {
    Preconditions.checkArgument(!error.isOk());
    completed = true;
    requestStream.onError(error.asRuntimeException());
    clientStream.onError(error.asRuntimeException());
  }

  // StreamObserver implementation
  @Override
  public void onNext(StreamResponseT response) {
    boolean isFirst;
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) {
        log.atFine().log("Received response on stream after completion: %s", response);
        return;
      }
      isFirst = !receivedInitial;
      receivedInitial = true;
    }
    Status responseStatus;
    if (isFirst) {
      responseStatus = handleInitialResponse(response);
    } else {
      responseStatus = handleStreamResponse(response);
    }
    if (!responseStatus.isOk()) {
      try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
        abort(responseStatus);
      }
    }
  }

  @Override
  public void onError(Throwable t) {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
    }
    clientStream.onError(t);
  }

  @Override
  public void onCompleted() {
    try (CloseableMonitor.Hold h = connectionMonitor.enter()) {
      if (completed) return;
      completed = true;
      clientStream.onCompleted();
    }
  }
}
