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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class MultiPartitionCommitterImplTest {

  @Test
  public void testCommit() {
    Committer committer1 = mock(Committer.class);
    Committer committer2 = mock(Committer.class);
    when(committer1.startAsync())
        .thenReturn(committer1)
        .thenThrow(new IllegalStateException("should only init once"));
    when(committer2.startAsync())
        .thenReturn(committer2)
        .thenThrow(new IllegalStateException("should only init once"));
    MultiPartitionCommitterImpl multiCommitter =
        new MultiPartitionCommitterImpl(
            2,
            (p) -> {
              if (p.value() == 0L) {
                return committer1;
              } else {
                return committer2;
              }
            });
    verify(committer1, times(1)).startAsync();
    verify(committer2, times(1)).startAsync();

    PslSourceOffset offset =
        PslSourceOffset.builder()
            .partitionOffsetMap(
                ImmutableMap.of(
                    Partition.of(0), Offset.of(10L),
                    Partition.of(1), Offset.of(8L)))
            .build();
    SettableApiFuture<Void> future1 = SettableApiFuture.create();
    SettableApiFuture<Void> future2 = SettableApiFuture.create();
    when(committer1.commitOffset(eq(Offset.of(10L)))).thenReturn(future1);
    when(committer2.commitOffset(eq(Offset.of(8L)))).thenReturn(future2);
    multiCommitter.commit(offset);
    verify(committer1, times(1)).commitOffset(eq(Offset.of(10L)));
    verify(committer2, times(1)).commitOffset(eq(Offset.of(8L)));
  }

  @Test
  public void testClose() {
    Committer committer = mock(Committer.class);
    when(committer.startAsync())
        .thenReturn(committer)
        .thenThrow(new IllegalStateException("should only init once"));
    MultiPartitionCommitterImpl multiCommitter =
        new MultiPartitionCommitterImpl(1, (p) -> committer);

    PslSourceOffset offset =
        PslSourceOffset.builder()
            .partitionOffsetMap(ImmutableMap.of(Partition.of(0), Offset.of(10L)))
            .build();
    SettableApiFuture<Void> future1 = SettableApiFuture.create();
    when(committer.commitOffset(eq(Offset.of(10L)))).thenReturn(future1);
    when(committer.stopAsync()).thenReturn(committer);
    multiCommitter.commit(offset);

    multiCommitter.close();
    verify(committer, times(1)).stopAsync();
  }
}
