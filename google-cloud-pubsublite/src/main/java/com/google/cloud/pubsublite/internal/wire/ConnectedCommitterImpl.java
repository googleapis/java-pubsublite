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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorRequest;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class ConnectedCommitterImpl
    extends SingleConnection<
        StreamingCommitCursorRequest, StreamingCommitCursorResponse, SequencedCommitCursorResponse>
    implements ConnectedCommitter {
  private final StreamingCommitCursorRequest initialRequest;

  private ConnectedCommitterImpl(
      StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
      StreamObserver<SequencedCommitCursorResponse> clientStream,
      StreamingCommitCursorRequest initialRequest) {
    super(streamFactory, clientStream);
    this.initialRequest = initialRequest;
    initialize(initialRequest);
  }

  static class Factory implements ConnectedCommitterFactory {
    @Override
    public ConnectedCommitter New(
        StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
        StreamObserver<SequencedCommitCursorResponse> clientStream,
        StreamingCommitCursorRequest initialRequest) {
      return new ConnectedCommitterImpl(streamFactory, clientStream, initialRequest);
    }
  }

  // SingleConnection implementation.
  @Override
  protected Status handleInitialResponse(StreamingCommitCursorResponse response) {
    if (!response.hasInitial()) {
      return Status.FAILED_PRECONDITION.withDescription(
          String.format(
              "Received non-initial first response %s on stream with initial request %s.",
              response, initialRequest));
    }
    return Status.OK;
  }

  @Override
  protected Status handleStreamResponse(StreamingCommitCursorResponse response) {
    if (!response.hasCommit()) {
      return Status.FAILED_PRECONDITION.withDescription(
          String.format(
              "Received non-commit subsequent response %s on stream with initial request %s.",
              response, initialRequest));
    }
    if (response.getCommit().getAcknowledgedCommits() <= 0) {
      return Status.FAILED_PRECONDITION.withDescription(
          String.format(
              "Received non-positive commit count response %s on stream with initial request %s.",
              response, initialRequest));
    }
    sendToClient(response.getCommit());
    return Status.OK;
  }

  // ConnectedCommitter implementation.
  @Override
  public void commit(Offset offset) {
    sendToStream(
        StreamingCommitCursorRequest.newBuilder()
            .setCommit(
                SequencedCommitCursorRequest.newBuilder()
                    .setCursor(Cursor.newBuilder().setOffset(offset.value())))
            .build());
  }
}
