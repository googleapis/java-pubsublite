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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriberImpl;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import java.util.Optional;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;
import org.junit.Test;

public class PslMicroBatchInputPartitionReaderTest {

  private final BlockingPullSubscriberImpl subscriber = mock(BlockingPullSubscriberImpl.class);
  private InputPartitionReader<InternalRow> reader;

  private static SequencedMessage newMessage(long offset) {
    return SequencedMessage.of(
        Message.builder().setData(ByteString.copyFromUtf8("text")).build(),
        Timestamps.EPOCH,
        Offset.of(offset),
        10000);
  }

  private void createReader(long endOffset) {
    reader =
        new PslMicroBatchInputPartitionReader(
            UnitTestExamples.exampleSubscriptionPath(),
            SparkPartitionOffset.builder()
                .partition(UnitTestExamples.examplePartition())
                .offset(endOffset)
                .build(),
            subscriber);
  }

  @Test
  public void testPartitionReader() throws Exception {
    long endOffset = 14L;
    createReader(endOffset);
    SequencedMessage message1 = newMessage(10L);
    InternalRow expectedRow1 =
        PslSparkUtils.toInternalRow(
            message1,
            UnitTestExamples.exampleSubscriptionPath(),
            UnitTestExamples.examplePartition());
    SequencedMessage message2 = newMessage(endOffset);
    InternalRow expectedRow2 =
        PslSparkUtils.toInternalRow(
            message2,
            UnitTestExamples.exampleSubscriptionPath(),
            UnitTestExamples.examplePartition());

    // Multiple get w/o next will return same msg.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message1));
    assertThat(reader.next()).isTrue();
    assertThat(reader.get()).isEqualTo(expectedRow1);
    assertThat(reader.get()).isEqualTo(expectedRow1);

    // Next will advance to next msg which is also the last msg in the batch.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message2));
    assertThat(reader.next()).isTrue();
    assertThat(reader.get()).isEqualTo(expectedRow2);

    // Now it already reached the end of the batch
    assertThat(reader.next()).isFalse();
  }

  @Test
  public void testPartitionReaderNewMessageExceedsRange() throws Exception {
    long endOffset = 14L;
    createReader(endOffset);
    SequencedMessage message1 = newMessage(10L);
    InternalRow expectedRow1 =
        PslSparkUtils.toInternalRow(
            message1,
            UnitTestExamples.exampleSubscriptionPath(),
            UnitTestExamples.examplePartition());
    SequencedMessage message2 = newMessage(endOffset + 1);

    // Multiple get w/o next will return same msg.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message1));
    assertThat(reader.next()).isTrue();
    assertThat(reader.get()).isEqualTo(expectedRow1);
    assertThat(reader.get()).isEqualTo(expectedRow1);

    // Next will advance to next msg, and recognize it's out of the batch range.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message2));
    assertThat(reader.next()).isFalse();
  }
}
