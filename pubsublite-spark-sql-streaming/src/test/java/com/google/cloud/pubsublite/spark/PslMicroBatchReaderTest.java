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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.junit.Test;

public class PslMicroBatchReaderTest {
  private static final PslDataSourceOptions OPTIONS =
      PslDataSourceOptions.builder()
          .setSubscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .build();
  private final CursorClient cursorClient = mock(CursorClient.class);
  private final MultiPartitionCommitter committer = mock(MultiPartitionCommitter.class);
  private final PartitionSubscriberFactory partitionSubscriberFactory =
      mock(PartitionSubscriberFactory.class);
  private final PerTopicHeadOffsetReader headOffsetReader = mock(PerTopicHeadOffsetReader.class);
  private final PslMicroBatchReader reader =
      new PslMicroBatchReader(
          cursorClient,
          committer,
          partitionSubscriberFactory,
          headOffsetReader,
          UnitTestExamples.exampleSubscriptionPath(),
          OPTIONS.flowControlSettings(),
          2);

  private PslSourceOffset createPslSourceOffsetTwoPartition(long offset0, long offset1) {
    return PslSourceOffset.builder()
        .partitionOffsetMap(
            ImmutableMap.of(
                Partition.of(0L), Offset.of(offset0), Partition.of(1L), Offset.of(offset1)))
        .build();
  }

  private SparkSourceOffset createSparkSourceOffsetTwoPartition(long offset0, long offset1) {
    return new SparkSourceOffset(
        ImmutableMap.of(
            Partition.of(0L),
            SparkPartitionOffset.create(Partition.of(0L), offset0),
            Partition.of(1L),
            SparkPartitionOffset.create(Partition.of(1L), offset1)));
  }

  @Test
  public void testEmptyOffsets() {
    when(cursorClient.listPartitionCursors(UnitTestExamples.exampleSubscriptionPath()))
        .thenReturn(ApiFutures.immediateFuture(ImmutableMap.of(Partition.of(0L), Offset.of(100L))));
    when(headOffsetReader.getHeadOffset()).thenReturn(createPslSourceOffsetTwoPartition(301L, 0L));
    reader.setOffsetRange(Optional.empty(), Optional.empty());
    assertThat(((SparkSourceOffset) reader.getStartOffset()).getPartitionOffsetMap())
        .containsExactly(
            Partition.of(0L),
            SparkPartitionOffset.create(Partition.of(0L), 99L),
            Partition.of(1L),
            SparkPartitionOffset.create(Partition.of(1L), -1L));
    assertThat(((SparkSourceOffset) reader.getEndOffset()).getPartitionOffsetMap())
        .containsExactly(
            Partition.of(0L),
            SparkPartitionOffset.create(Partition.of(0L), 300L),
            Partition.of(1L),
            SparkPartitionOffset.create(Partition.of(1L), -1L));
  }

  @Test
  public void testValidOffsets() {
    SparkSourceOffset startOffset = createSparkSourceOffsetTwoPartition(10L, 100L);
    SparkSourceOffset endOffset = createSparkSourceOffsetTwoPartition(20L, 300L);
    reader.setOffsetRange(Optional.of(startOffset), Optional.of(endOffset));
    assertThat(reader.getStartOffset()).isEqualTo(startOffset);
    assertThat(reader.getEndOffset()).isEqualTo(endOffset);
  }

  @Test
  public void testDeserializeOffset() {
    SparkSourceOffset offset =
        new SparkSourceOffset(
            ImmutableMap.of(Partition.of(1L), SparkPartitionOffset.create(Partition.of(1L), 10L)));
    assertThat(reader.deserializeOffset(offset.json())).isEqualTo(offset);
  }

  @Test
  public void testCommit() {
    SparkSourceOffset offset = createSparkSourceOffsetTwoPartition(10L, 50L);
    PslSourceOffset expectedCommitOffset = createPslSourceOffsetTwoPartition(11L, 51L);
    reader.commit(offset);
    verify(committer, times(1)).commit(eq(expectedCommitOffset));
  }

  @Test
  public void testPlanInputPartitionNoMessage() {
    SparkSourceOffset startOffset = createSparkSourceOffsetTwoPartition(10L, 100L);
    SparkSourceOffset endOffset = createSparkSourceOffsetTwoPartition(20L, 100L);
    reader.setOffsetRange(Optional.of(startOffset), Optional.of(endOffset));
    assertThat(reader.planInputPartitions()).hasSize(1);
  }
}
