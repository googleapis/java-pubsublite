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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;

import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorRequest;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;

public class ConnectedCommitterImpl
    extends SingleConnection<
        StreamingCommitCursorRequest, StreamingCommitCursorResponse, SequencedCommitCursorResponse>
    implements ConnectedCommitter {
  private final StreamingCommitCursorRequest initialRequest;

  @VisibleForTesting
  ConnectedCommitterImpl(
      StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
      ResponseObserver<SequencedCommitCursorResponse> clientStream,
      StreamingCommitCursorRequest initialRequest,
      Duration streamIdleTimeout) {
    super(streamFactory, clientStream, streamIdleTimeout, /*expectInitialResponse=*/ true);
    this.initialRequest = initialRequest;
    initialize(initialRequest);
  }

  static class Factory implements ConnectedCommitterFactory {
    @Override
    public ConnectedCommitter New(
        StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
        ResponseObserver<SequencedCommitCursorResponse> clientStream,
        StreamingCommitCursorRequest initialRequest) {
      return new ConnectedCommitterImpl(
          streamFactory, clientStream, initialRequest, DEFAULT_STREAM_IDLE_TIMEOUT);
    }
  }

  // SingleConnection implementation.
  @Override
  protected void handleInitialResponse(StreamingCommitCursorResponse response)
      throws CheckedApiException {
    checkState(
        response.hasInitial(),
        String.format(
            "Received non-initial first response %s on stream with initial request %s.",
            response, initialRequest));
  }

  @Override
  protected void handleStreamResponse(StreamingCommitCursorResponse response)
      throws CheckedApiException {
    checkState(
        response.hasCommit(),
        String.format(
            "Received non-commit subsequent response %s on stream with initial request %s.",
            response, initialRequest));
    checkState(
        response.getCommit().getAcknowledgedCommits() > 0,
        String.format(
            "Received non-positive commit count response %s on stream with initial request %s.",
            response, initialRequest));
    sendToClient(response.getCommit());
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
