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

package com.google.cloud.pubsublite.kafka;

import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RecordTransformsTest {
  private static final Message MESSAGE =
      Message.builder()
          .setKey(ByteString.copyFromUtf8("abc"))
          .setData(ByteString.copyFromUtf8("def"))
          .setEventTime(Timestamp.newBuilder().setSeconds(1).setNanos(1000000).build())
          .setAttributes(
              ImmutableListMultimap.of(
                  "xxx",
                  ByteString.copyFromUtf8("yyy"),
                  "zzz",
                  ByteString.copyFromUtf8("zzz"),
                  "zzz",
                  ByteString.copyFromUtf8("zzz")))
          .build();

  @Test
  public void publishTransform() {
    ProducerRecord<byte[], byte[]> record =
        new ProducerRecord<>(
            example(TopicPath.class).toString(),
            null,
            1001L,
            "abc".getBytes(),
            "def".getBytes(),
            ImmutableList.of(
                LiteHeaders.toHeader("xxx", ByteString.copyFromUtf8("yyy")),
                LiteHeaders.toHeader("zzz", ByteString.copyFromUtf8("zzz")),
                LiteHeaders.toHeader("zzz", ByteString.copyFromUtf8("zzz"))));
    Message message = RecordTransforms.toMessage(record);
    assertThat(message).isEqualTo(MESSAGE);
  }

  @Test
  public void subscribeTransform() {
    SequencedMessage sequencedMessage =
        SequencedMessage.of(
            MESSAGE, Timestamp.newBuilder().setNanos(12345).build(), example(Offset.class), 123L);
    ConsumerRecord<byte[], byte[]> record =
        RecordTransforms.fromMessage(
            sequencedMessage, example(TopicPath.class), example(Partition.class));
    assertThat(record.key()).isEqualTo("abc".getBytes());
    assertThat(record.value()).isEqualTo("def".getBytes());
    assertThat(record.timestampType()).isEqualTo(TimestampType.CREATE_TIME);
    assertThat(record.timestamp()).isEqualTo(1001L);
    ImmutableListMultimap.Builder<String, ByteString> headers = ImmutableListMultimap.builder();
    record
        .headers()
        .forEach(header -> headers.put(header.key(), ByteString.copyFrom(header.value())));
    assertThat(headers.build()).isEqualTo(MESSAGE.attributes());
    assertThat(record.offset()).isEqualTo(example(Offset.class).value());
    assertThat(record.topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(record.partition()).isEqualTo(example(Partition.class).value());
  }
}
