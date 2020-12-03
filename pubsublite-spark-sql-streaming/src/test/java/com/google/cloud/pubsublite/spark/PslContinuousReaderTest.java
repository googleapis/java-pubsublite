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
import static org.mockito.Mockito.mock;

import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.junit.Test;

public class PslContinuousReaderTest {

  private static final PslDataSourceOptions OPTIONS =
      PslDataSourceOptions.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .credentialsAccessToken("abc")
          .build();
  private final CursorServiceClient cursorClient = mock(CursorServiceClient.class);
  private final AdminServiceClient adminClient = mock(AdminServiceClient.class);
  private final MultiPartitionCommitter committer = mock(MultiPartitionCommitter.class);
  private final PslContinuousReader reader =
      new PslContinuousReader(OPTIONS, adminClient, cursorClient, committer);

  @Test
  public void testEmptyStartOffset() {
    CursorServiceClient.ListPartitionCursorsPagedResponse resp =
        mock(CursorServiceClient.ListPartitionCursorsPagedResponse.class);
    when(adminClient.getSubscription(eq(UnitTestExamples.exampleSubscriptionPath().toString())))
        .thenReturn(
            Subscription.newBuilder()
                .setTopic(UnitTestExamples.exampleTopicPath().toString())
                .build());
    when(adminClient.getTopicPartitions(eq(UnitTestExamples.exampleTopicPath().toString())))
        .thenReturn(TopicPartitions.newBuilder().setPartitionCount(2).build());
    when(resp.iterateAll())
        .thenReturn(
            ImmutableList.of(
                PartitionCursor.newBuilder()
                    .setPartition(1)
                    .setCursor(
                        Cursor.newBuilder()
                            .setOffset(UnitTestExamples.exampleOffset().value())
                            .build())
                    .build()));
    doReturn(resp).when(cursorClient).listPartitionCursors(anyString());

    reader.setStartOffset(Optional.empty());
    assertThat(((PslSourceOffset) reader.getStartOffset()).getPartitionOffsetMap())
        .containsExactly(
            Partition.of(0), Offset.of(-1),
            Partition.of(1), Offset.of(UnitTestExamples.exampleOffset().value() - 1));
  }

  @Test
  public void testValidStartOffset() {
    PslSourceOffset offset =
        new PslSourceOffset(ImmutableMap.of(Partition.of(1), UnitTestExamples.exampleOffset()));
    reader.setStartOffset(Optional.of(offset));
    assertThat(reader.getStartOffset()).isEqualTo(offset);
  }

  @Test
  public void testMergeOffsets() {
    PslPartitionOffset po1 =
        PslPartitionOffset.builder()
            .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
            .partition(Partition.of(1))
            .offset(Offset.of(10))
            .build();
    PslPartitionOffset po2 =
        PslPartitionOffset.builder()
            .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
            .partition(Partition.of(2))
            .offset(Offset.of(5))
            .build();
    assertThat(reader.mergeOffsets(new PslPartitionOffset[] {po1, po2}))
        .isEqualTo(PslSourceOffset.merge(new PslPartitionOffset[] {po1, po2}));
  }

  @Test
  public void testDeserializeOffset() {
    PslSourceOffset offset =
        new PslSourceOffset(ImmutableMap.of(Partition.of(1), UnitTestExamples.exampleOffset()));
    assertThat(reader.deserializeOffset(offset.json())).isEqualTo(offset);
  }

  @Test
  public void testCommit() {
    PslSourceOffset offset =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(1), Offset.of(10L),
                Partition.of(2), Offset.of(8L)));
    PslSourceOffset expectedCommitOffset =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(1), Offset.of(11L),
                Partition.of(2), Offset.of(9L)));
    reader.commit(offset);
    verify(committer, times(1)).commit(eq(expectedCommitOffset));
  }
}
