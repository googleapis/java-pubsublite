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
  // If the pending flow request exceeds this ratio of the current outstanding tokens, the pending
  // flow request should be flushed to the stream ASAP.
  private static double EXPEDITE_BATCH_REQUEST_RATIO = 0.5;

  // The current amount of outstanding byte and message flow control tokens.
  private TokenCounter currentOutstandingTokens = new TokenCounter();

  // The pending aggregate flow control request that needs to be sent to the stream.
  private TokenCounter pendingFlowRequest = new TokenCounter();

  void onClientFlowRequest(FlowControlRequest request) throws StatusException {
    currentOutstandingTokens.add(request.getAllowedBytes(), request.getAllowedMessages());
    pendingFlowRequest.add(request.getAllowedBytes(), request.getAllowedMessages());
  }

  void onMessages(Collection<SequencedMessage> received) throws StatusException {
    long byteSize = received.stream().mapToLong(SequencedMessage::byteSize).sum();
    currentOutstandingTokens.sub(byteSize, received.size());
  }

  void onClientSeek() {
    currentOutstandingTokens.reset();
    pendingFlowRequest.reset();
  }

  // Must be called after a FlowControlRequest has been sent to the stream.
  void onFlowRequestDispatched() {
    pendingFlowRequest.reset();
  }

  Optional<FlowControlRequest> requestForRestart() {
    return currentOutstandingTokens.toFlowControlRequest();
  }

  Optional<FlowControlRequest> pendingBatchRequest() {
    return pendingFlowRequest.toFlowControlRequest();
  }

  boolean shouldExpediteBatchRequest() {
    if (exceedsExpediteRatio(pendingFlowRequest.bytes(), currentOutstandingTokens.bytes()))
      return true;
    if (exceedsExpediteRatio(pendingFlowRequest.messages(), currentOutstandingTokens.messages()))
      return true;
    return false;
  }

  private boolean exceedsExpediteRatio(long pending, long current) {
    return current > 0 && (pending / current) > EXPEDITE_BATCH_REQUEST_RATIO;
  }
}
