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

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.wire.SerialBatcher.UnbatchedMessage;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.protobuf.ByteString;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SerialBatcherTest {
  private static final PubSubMessage MESSAGE_1 =
      PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("data")).build();
  private static final PubSubMessage MESSAGE_2 =
      PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("other data")).build();
  private static final PubSubMessage MESSAGE_3 =
      PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("more data")).build();

  private static List<PubSubMessage> extractMessages(List<List<UnbatchedMessage>> messages) {
    return messages.stream()
        .flatMap(batch -> batch.stream().map(UnbatchedMessage::message))
        .collect(Collectors.toList());
  }

  private static List<PubSubMessage> extractMessagesFromBatch(List<UnbatchedMessage> messages) {
    return messages.stream().map(UnbatchedMessage::message).collect(Collectors.toList());
  }

  @Test
  public void needsImmediateFlushAtMessageLimit() throws Exception {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 10000, /*messageLimit=*/ 1);
    ApiFuture<Offset> future = batcher.add(PubSubMessage.getDefaultInstance());
    List<List<UnbatchedMessage>> batches = batcher.flush();
    assertThat(batches).hasSize(1);
    List<UnbatchedMessage> messages = batches.get(0);
    assertThat(messages).hasSize(1);
    assertThat(future.isDone()).isFalse();
    messages.get(0).future().set(Offset.of(43));
    assertThat(future.get()).isEqualTo(Offset.of(43));
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void moreThanLimitMultipleBatches() throws Exception {
    SerialBatcher batcher =
        new SerialBatcher(
            /*byteLimit=*/ MESSAGE_1.getSerializedSize() + MESSAGE_2.getSerializedSize(),
            /*messageLimit=*/ 1000);
    batcher.add(MESSAGE_1);
    batcher.add(MESSAGE_2);
    batcher.add(MESSAGE_3);
    List<List<UnbatchedMessage>> batches = batcher.flush();
    assertThat(batches).hasSize(2);
    assertThat(extractMessagesFromBatch(batches.get(0))).containsExactly(MESSAGE_1, MESSAGE_2);
    assertThat(extractMessagesFromBatch(batches.get(1))).containsExactly(MESSAGE_3);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void flushMessageLimit() {
    SerialBatcher batcher = new SerialBatcher(/*byteLimit=*/ 10000, /*messageLimit=*/ 2);
    batcher.add(MESSAGE_1);
    batcher.add(MESSAGE_2);
    batcher.add(MESSAGE_3);
    List<List<UnbatchedMessage>> batches = batcher.flush();
    assertThat(batches.size()).isEqualTo(2);
    assertThat(extractMessagesFromBatch(batches.get(0))).containsExactly(MESSAGE_1, MESSAGE_2);
    assertThat(extractMessagesFromBatch(batches.get(1))).containsExactly(MESSAGE_3);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void flushByteLimit() {
    SerialBatcher batcher =
        new SerialBatcher(
            /*byteLimit=*/ MESSAGE_1.getSerializedSize() + MESSAGE_2.getSerializedSize() + 1,
            /*messageLimit=*/ 10000);
    batcher.add(MESSAGE_1);
    batcher.add(MESSAGE_2);
    batcher.add(MESSAGE_3);
    List<List<UnbatchedMessage>> batches = batcher.flush();
    assertThat(batches.size()).isEqualTo(2);
    assertThat(extractMessagesFromBatch(batches.get(0))).containsExactly(MESSAGE_1, MESSAGE_2);
    assertThat(extractMessagesFromBatch(batches.get(1))).containsExactly(MESSAGE_3);
  }

  @Test
  @SuppressWarnings({"CheckReturnValue", "FutureReturnValueIgnored"})
  public void batchesMessagesAtLimit() {
    SerialBatcher batcher =
        new SerialBatcher(
            /*byteLimit=*/ MESSAGE_1.getSerializedSize() + MESSAGE_2.getSerializedSize(),
            /*messageLimit=*/ 10000);
    batcher.add(MESSAGE_2);
    batcher.add(MESSAGE_1);
    assertThat(extractMessages(batcher.flush())).containsExactly(MESSAGE_2, MESSAGE_1);
  }
}
