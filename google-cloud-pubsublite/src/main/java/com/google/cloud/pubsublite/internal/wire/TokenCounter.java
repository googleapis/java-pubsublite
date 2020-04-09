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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.common.math.LongMath;
import io.grpc.StatusException;
import java.util.Collection;
import java.util.Optional;

// A TokenCounter counts the amount of outstanding byte and message flow control tokens that the
// client believes exists for the stream.
class TokenCounter {
  private long bytes = 0;
  private long messages = 0;

  void onClientFlowRequest(FlowControlRequest request) throws StatusException {
    checkArgument(request.getAllowedMessages() >= 0);
    checkArgument(request.getAllowedBytes() >= 0);

    bytes = LongMath.saturatedAdd(bytes, request.getAllowedBytes());
    messages = LongMath.saturatedAdd(messages, request.getAllowedMessages());
  }

  void onMessages(Collection<SequencedMessage> received) throws StatusException {
    long byteSize = received.stream().mapToLong(SequencedMessage::byteSize).sum();
    checkState(
        byteSize <= bytes, "Received messages that account for more bytes than were requested.");
    checkState(received.size() <= messages, "Received more messages than were requested.");

    bytes -= byteSize;
    messages -= received.size();
  }

  Optional<FlowControlRequest> requestForRestart() {
    if (bytes == 0 && messages == 0) return Optional.empty();
    return Optional.of(
        FlowControlRequest.newBuilder()
            .setAllowedBytes(bytes)
            .setAllowedMessages(messages)
            .build());
  }
}
