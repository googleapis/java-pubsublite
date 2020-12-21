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

package com.google.cloud.pubsublite.spark;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import org.junit.Test;

public class PslSparkUtilsTest {

  @Test
  public void testToInternalRow() {
    Message message =
        Message.builder()
            .setKey(ByteString.copyFromUtf8("key"))
            .setData(ByteString.copyFromUtf8("data"))
            .setEventTime(Timestamp.newBuilder().setSeconds(10000000L).setNanos(10).build())
            .setAttributes(
                ImmutableListMultimap.of(
                    "key1", ByteString.copyFromUtf8("val1"),
                    "key1", ByteString.copyFromUtf8("val2"),
                    "key2", ByteString.copyFromUtf8("val3")))
            .build();
    SequencedMessage sequencedMessage =
        SequencedMessage.of(
            message,
            Timestamp.newBuilder().setSeconds(10000000L).setNanos(10).build(),
            Offset.of(10L),
            10L);
    PslSparkUtils.toInternalRow(
        sequencedMessage,
        UnitTestExamples.exampleSubscriptionPath(),
        UnitTestExamples.examplePartition());
  }

  @Test
  public void testSourceOffsetConversion() {
    PslSourceOffset pslSourceOffset =
        PslSourceOffset.builder()
            .partitionOffsetMap(
                ImmutableMap.of(Partition.of(0L), Offset.of(10), Partition.of(1L), Offset.of(50)))
            .build();

    SparkSourceOffset sparkSourceOffset = PslSparkUtils.toSparkSourceOffset(pslSourceOffset);
    assertThat(sparkSourceOffset.getPartitionOffsetMap().get(Partition.of(0L)).offset())
        .isEqualTo(9L);
    assertThat(sparkSourceOffset.getPartitionOffsetMap().get(Partition.of(1L)).offset())
        .isEqualTo(49L);

    assertThat(PslSparkUtils.toPslSourceOffset(sparkSourceOffset)).isEqualTo(pslSourceOffset);
  }

  @Test
  public void testToPslPartitionOffset() {
    SparkPartitionOffset sparkPartitionOffset =
        SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(10L).build();
    PslPartitionOffset pslPartitionOffset =
        PslPartitionOffset.builder().partition(Partition.of(1L)).offset(Offset.of(11L)).build();
    assertThat(PslSparkUtils.toPslPartitionOffset(sparkPartitionOffset))
        .isEqualTo(pslPartitionOffset);
  }
}
