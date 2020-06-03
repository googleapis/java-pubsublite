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

import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import io.grpc.StatusException;
import java.util.Collection;
import java.util.Optional;

// A FlowControlBatcher manages batching of FlowControlRequests.
class FlowControlBatcher {
  // If the pending flow request exceeds this ratio of the current state, the pending flow request
  // should be flushed to the stream ASAP.
  private static double EXPEDITE_BATCH_REQUEST_RATIO = 0.5;

  // The current count of outstanding byte and message flow control tokens.
  private TokenCounter currentState = new TokenCounter();

  // The pending aggregate flow control request that needs to be sent to the stream.
  private TokenCounter pendingFlowRequest = new TokenCounter();

  void onClientFlowRequest(FlowControlRequest request) throws StatusException {
    currentState.add(request.getAllowedBytes(), request.getAllowedMessages());
    pendingFlowRequest.add(request.getAllowedBytes(), request.getAllowedMessages());
  }

  void onMessages(Collection<SequencedMessage> received) throws StatusException {
    long byteSize = received.stream().mapToLong(SequencedMessage::byteSize).sum();
    currentState.sub(byteSize, received.size());
  }

  void onClientSeek() {
    currentState.reset();
    pendingFlowRequest.reset();
  }

  // Must be called after a FlowControlRequest has been sent to the stream.
  void onFlowRequestDispatched() {
    pendingFlowRequest.reset();
  }

  Optional<FlowControlRequest> requestForRestart() {
    return currentState.toFlowControlRequest();
  }

  Optional<FlowControlRequest> pendingBatchRequest() {
    return pendingFlowRequest.toFlowControlRequest();
  }

  boolean shouldExpediteBatchRequest() {
    if (currentState.bytes() > 0
        && pendingFlowRequest.bytes() / currentState.bytes() > EXPEDITE_BATCH_REQUEST_RATIO)
      return true;
    if (currentState.messages() > 0
        && pendingFlowRequest.messages() / currentState.messages() > EXPEDITE_BATCH_REQUEST_RATIO)
      return true;
    return false;
  }
}
