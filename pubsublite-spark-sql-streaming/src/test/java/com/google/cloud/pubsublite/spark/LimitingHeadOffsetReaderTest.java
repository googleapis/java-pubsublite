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
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.common.testing.FakeTicker;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class LimitingHeadOffsetReaderTest {

  private final FakeTicker ticker = new FakeTicker();
  private final TopicStatsClient topicStatsClient = mock(TopicStatsClient.class);
  private final LimitingHeadOffsetReader reader =
      new LimitingHeadOffsetReader(
          topicStatsClient, UnitTestExamples.exampleTopicPath(), 1, ticker);

  @Test
  public void testRead() {
    ComputeHeadCursorResponse resp1 =
        ComputeHeadCursorResponse.newBuilder()
            .setHeadCursor(Cursor.newBuilder().setOffset(10).build())
            .build();
    ComputeHeadCursorResponse resp2 =
        ComputeHeadCursorResponse.newBuilder()
            .setHeadCursor(Cursor.newBuilder().setOffset(13).build())
            .build();
    when(topicStatsClient.computeHeadCursor(UnitTestExamples.exampleTopicPath(), Partition.of(0)))
        .thenReturn(ApiFutures.immediateFuture(resp1));
    assertThat(reader.getHeadOffset().partitionOffsetMap())
        .containsExactly(Partition.of(0), Offset.of(10));
    verify(topicStatsClient).computeHeadCursor(any(), any());

    reset(topicStatsClient);
    ticker.advance(59, TimeUnit.SECONDS);
    assertThat(reader.getHeadOffset().partitionOffsetMap())
        .containsExactly(Partition.of(0), Offset.of(10));
    verify(topicStatsClient, times(0)).computeHeadCursor(any(), any());

    reset(topicStatsClient);
    ticker.advance(2, TimeUnit.SECONDS);
    when(topicStatsClient.computeHeadCursor(UnitTestExamples.exampleTopicPath(), Partition.of(0)))
        .thenReturn(ApiFutures.immediateFuture(resp2));
    assertThat(reader.getHeadOffset().partitionOffsetMap())
        .containsExactly(Partition.of(0), Offset.of(13));
    verify(topicStatsClient).computeHeadCursor(any(), any());
  }
}
