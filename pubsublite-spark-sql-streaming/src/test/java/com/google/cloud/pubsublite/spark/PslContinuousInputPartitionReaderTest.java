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
import static org.mockito.Mockito.*;

import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.junit.Test;

public class PslContinuousInputPartitionReaderTest {

  private final BlockingPullSubscriber subscriber = mock(BlockingPullSubscriber.class);
  private ContinuousInputPartitionReader<InternalRow> reader;

  private static SequencedMessage newMessage(long offset) {
    return SequencedMessage.of(
        Message.builder().setData(ByteString.copyFromUtf8("text")).build(),
        Timestamps.EPOCH,
        Offset.of(offset),
        10000);
  }

  private void createReader() {
    reader =
        new PslContinuousInputPartitionReader(
            UnitTestExamples.exampleSubscriptionPath(),
            SparkPartitionOffset.builder()
                .partition(UnitTestExamples.examplePartition())
                .offset(UnitTestExamples.exampleOffset().value())
                .build(),
            subscriber);
  }

  @Test
  public void testPartitionReader() throws Exception {
    createReader();
    SequencedMessage message1 = newMessage(10);
    InternalRow expectedRow1 =
        PslSparkUtils.toInternalRow(
            message1,
            UnitTestExamples.exampleSubscriptionPath(),
            UnitTestExamples.examplePartition());
    SequencedMessage message2 = newMessage(13);
    InternalRow expectedRow2 =
        PslSparkUtils.toInternalRow(
            message2,
            UnitTestExamples.exampleSubscriptionPath(),
            UnitTestExamples.examplePartition());

    // Multiple get w/o next will return same msg.
    when(subscriber.pull()).thenReturn(message1);
    assertThat(reader.next()).isTrue();
    assertThat(reader.get()).isEqualTo(expectedRow1);
    assertThat(reader.get()).isEqualTo(expectedRow1);
    assertThat(((SparkPartitionOffset) reader.getOffset()).offset()).isEqualTo(10L);

    // Next will advance to next msg.
    when(subscriber.pull()).thenReturn(message2);
    assertThat(reader.next()).isTrue();
    assertThat(reader.get()).isEqualTo(expectedRow2);
    assertThat(((SparkPartitionOffset) reader.getOffset()).offset()).isEqualTo(13L);
  }
}
