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
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.wire.SerialBatcher.UnbatchedMessage;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SerialBatcherTest {
  private static final PubSubMessage MESSAGE_1 =
      PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("Some data")).build();
  private static final PubSubMessage MESSAGE_2 =
      PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("Some other data")).build();

  private static List<PubSubMessage> extractMessages(Collection<UnbatchedMessage> messages) {
    return messages.stream().map(UnbatchedMessage::message).collect(Collectors.toList());
  }

  @Test
  public void shouldFlushAtMessageLimit() throws Exception {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 10000, /*messageLimit=*/ 1);
    assertThat(batcher.shouldFlush()).isFalse();
    ApiFuture<Offset> future = batcher.add(PubSubMessage.getDefaultInstance());
    assertThat(batcher.shouldFlush()).isTrue();
    ImmutableList<UnbatchedMessage> messages = ImmutableList.copyOf(batcher.flush());
    assertThat(messages).hasSize(1);
    assertThat(future.isDone()).isFalse();
    messages.get(0).future().set(Offset.of(43));
    assertThat(future.get()).isEqualTo(Offset.of(43));
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void shouldFlushAtMessageLimitAggregated() {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 10000, /*messageLimit=*/ 2);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_1);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_2);
    assertThat(batcher.shouldFlush()).isTrue();
    assertThat(extractMessages(batcher.flush())).containsExactly(MESSAGE_1, MESSAGE_2);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void shouldFlushAtByteLimitAggregated() {
    SerialBatcher batcher =
        new SerialBatcher(
            /*byteLimit=*/ MESSAGE_1.getSerializedSize() + 1, /*messageLimit=*/ 10000);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_1);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_2);
    assertThat(batcher.shouldFlush()).isTrue();
    assertThat(extractMessages(batcher.flush())).containsExactly(MESSAGE_1);
    Preconditions.checkArgument(MESSAGE_2.getSerializedSize() > MESSAGE_1.getSerializedSize());
    assertThat(batcher.shouldFlush()).isTrue();
    assertThat(extractMessages(batcher.flush())).containsExactly(MESSAGE_2);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void batchesMessagesAtLimit() {
    SerialBatcher batcher =
        new SerialBatcher(
            /*byteLimit=*/ MESSAGE_1.getSerializedSize() + MESSAGE_2.getSerializedSize(),
            /*messageLimit=*/ 10000);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_2);
    assertThat(batcher.shouldFlush()).isFalse();
    batcher.add(MESSAGE_1);
    assertThat(batcher.shouldFlush()).isTrue();
    assertThat(extractMessages(batcher.flush())).containsExactly(MESSAGE_2, MESSAGE_1);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void callerNoFlushFailsMessagePrecondition() {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 10000, /*messageLimit=*/ 1);
    batcher.add(MESSAGE_1);
    assertThat(batcher.shouldFlush()).isTrue();
    batcher.add(MESSAGE_2);
    assertThat(batcher.shouldFlush()).isTrue();
    batcher.add(PubSubMessage.getDefaultInstance());
    assertThat(batcher.shouldFlush()).isTrue();
    assertThrows(IllegalStateException.class, batcher::flush);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void callerNoFlushFailsBytePrecondition() {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 1, /*messageLimit=*/ 10000);
    batcher.add(MESSAGE_1);
    assertThat(batcher.shouldFlush()).isTrue();
    batcher.add(MESSAGE_2);
    assertThat(batcher.shouldFlush()).isTrue();
    batcher.add(PubSubMessage.getDefaultInstance());
    assertThat(batcher.shouldFlush()).isTrue();
    assertThrows(IllegalStateException.class, batcher::flush);
  }
}
