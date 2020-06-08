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

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.util.Timestamps;
import io.grpc.StatusException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FlowControlBatcherTest {

  private final FlowControlBatcher batcher = new FlowControlBatcher();

  @Test
  public void onClientFlowRequestIncrementsTokens() throws StatusException {
    FlowControlRequest clientFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(500).setAllowedMessages(10).build();
    batcher.onClientFlowRequest(clientFlowRequest);

    assertThat(batcher.releasePendingRequest().get()).isEqualTo(clientFlowRequest);
    assertThat(batcher.releasePendingRequest().isPresent()).isFalse();
    assertThat(batcher.requestForRestart().get()).isEqualTo(clientFlowRequest);
    assertThat(batcher.requestForRestart().get()).isEqualTo(clientFlowRequest);
  }

  @Test
  public void onMessagesDecrementsClientTokens() throws StatusException {
    FlowControlRequest clientFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(500).setAllowedMessages(10).build();
    batcher.onClientFlowRequest(clientFlowRequest);
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 100),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 150));
    batcher.onMessages(messages);

    assertThat(batcher.releasePendingRequest().get()).isEqualTo(clientFlowRequest);
    FlowControlRequest expectedRequestForRestart =
        FlowControlRequest.newBuilder().setAllowedBytes(250).setAllowedMessages(8).build();
    assertThat(batcher.requestForRestart().get()).isEqualTo(expectedRequestForRestart);
  }

  @Test
  public void shouldExpediteBatchRequestChecksByteRatio() throws StatusException {
    batcher.onClientFlowRequest(
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build());
    batcher.releasePendingRequest();

    batcher.onClientFlowRequest(FlowControlRequest.newBuilder().setAllowedBytes(10).build());
    assertThat(batcher.shouldExpediteBatchRequest()).isFalse();

    batcher.onClientFlowRequest(FlowControlRequest.newBuilder().setAllowedBytes(90).build());
    assertThat(batcher.shouldExpediteBatchRequest()).isTrue();
  }

  @Test
  public void shouldExpediteBatchRequestChecksMessageRatio() throws StatusException {
    batcher.onClientFlowRequest(
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build());
    batcher.releasePendingRequest();

    batcher.onClientFlowRequest(FlowControlRequest.newBuilder().setAllowedMessages(80).build());
    assertThat(batcher.shouldExpediteBatchRequest()).isFalse();

    batcher.onClientFlowRequest(FlowControlRequest.newBuilder().setAllowedMessages(20).build());
    assertThat(batcher.shouldExpediteBatchRequest()).isTrue();
  }

  @Test
  public void shouldExpediteBatchRequestHandlesDivByZero() {
    assertThat(batcher.shouldExpediteBatchRequest()).isFalse();
  }
}
