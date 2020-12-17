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

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriberImpl;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import java.util.Optional;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.junit.Test;

public class PslContinuousInputPartitionReaderTest {

  private final BlockingPullSubscriberImpl subscriber = mock(BlockingPullSubscriberImpl.class);
  private ContinuousInputPartitionReader<InternalRow> reader;

  private static SequencedMessage newMessage(long offset) {
    return SequencedMessage.of(
        Message.builder().setData(ByteString.copyFromUtf8("text")).build(),
        Timestamps.EPOCH,
        Offset.of(offset),
        10000);
  }

  private static void verifyInternalRow(InternalRow row, long expectedOffset) {
    assertThat(row.getString(0)).isEqualTo(UnitTestExamples.exampleSubscriptionPath().toString());
    assertThat(row.getLong(1)).isEqualTo(UnitTestExamples.examplePartition().value());
    assertThat(row.getLong(2)).isEqualTo(expectedOffset);
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
    SequencedMessage message2 = newMessage(13);

    // Multiple get w/o next will return same msg.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message1));
    assertThat(reader.next()).isTrue();
    verifyInternalRow(reader.get(), 10L);
    verifyInternalRow(reader.get(), 10L);
    assertThat(((SparkPartitionOffset) reader.getOffset()).offset()).isEqualTo(10L);

    // Next will advance to next msg.
    when(subscriber.onData()).thenReturn(ApiFutures.immediateFuture(null));
    when(subscriber.messageIfAvailable()).thenReturn(Optional.of(message2));
    assertThat(reader.next()).isTrue();
    verifyInternalRow(reader.get(), 13L);
    assertThat(((SparkPartitionOffset) reader.getOffset()).offset()).isEqualTo(13L);
  }
}
