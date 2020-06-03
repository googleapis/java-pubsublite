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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.common.math.LongMath;
import io.grpc.StatusException;
import java.util.Optional;

// A TokenCounter stores the amount of outstanding byte and message flow control tokens.
class TokenCounter {
  private long bytes = 0;
  private long messages = 0;

  long bytes() {
    return bytes;
  }

  long messages() {
    return messages;
  }

  void add(long deltaBytes, long deltaMessages) throws StatusException {
    checkArgument(deltaBytes >= 0);
    checkArgument(deltaMessages >= 0);

    bytes = LongMath.saturatedAdd(bytes, deltaBytes);
    messages = LongMath.saturatedAdd(messages, deltaMessages);
  }

  void sub(long deltaBytes, long deltaMessages) throws StatusException {
    checkArgument(deltaBytes >= 0);
    checkArgument(deltaMessages >= 0);
    checkState(
        deltaBytes <= bytes, "Received messages that account for more bytes than were requested.");
    checkState(deltaMessages <= messages, "Received more messages than were requested.");

    bytes -= deltaBytes;
    messages -= deltaMessages;
  }

  void reset() {
    bytes = 0;
    messages = 0;
  }

  Optional<FlowControlRequest> toFlowControlRequest() {
    if (bytes == 0 && messages == 0) return Optional.empty();
    return Optional.of(
        FlowControlRequest.newBuilder()
            .setAllowedBytes(bytes)
            .setAllowedMessages(messages)
            .build());
  }
}
