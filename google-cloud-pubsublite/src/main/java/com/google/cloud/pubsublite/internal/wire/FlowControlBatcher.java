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
  private final TokenCounter clientTokens = new TokenCounter();

  // The pending aggregate flow control request that needs to be sent to the stream.
  private final TokenCounter pendingTokens = new TokenCounter();

  void onClientFlowRequest(FlowControlRequest request) throws StatusException {
    clientTokens.add(request.getAllowedBytes(), request.getAllowedMessages());
    pendingTokens.add(request.getAllowedBytes(), request.getAllowedMessages());
  }

  void onMessages(Collection<SequencedMessage> received) throws StatusException {
    long byteSize = received.stream().mapToLong(SequencedMessage::byteSize).sum();
    clientTokens.sub(byteSize, received.size());
  }

  void onClientSeek() {
    clientTokens.reset();
    pendingTokens.reset();
  }

  // The caller must send the FlowControlRequest to the stream, as pending tokens are reset.
  Optional<FlowControlRequest> requestForRestart() {
    pendingTokens.reset();
    return clientTokens.toFlowControlRequest();
  }

  // The caller must send the FlowControlRequest to the stream, as pending tokens are reset.
  Optional<FlowControlRequest> releasePendingRequest() {
    Optional<FlowControlRequest> request = pendingTokens.toFlowControlRequest();
    pendingTokens.reset();
    return request;
  }

  boolean shouldExpediteBatchRequest() {
    if (exceedsExpediteRatio(pendingTokens.bytes(), clientTokens.bytes())) return true;
    if (exceedsExpediteRatio(pendingTokens.messages(), clientTokens.messages())) return true;
    return false;
  }

  private boolean exceedsExpediteRatio(long pending, long client) {
    return client > 0 && ((double) pending / client) >= EXPEDITE_BATCH_REQUEST_RATIO;
  }
}
