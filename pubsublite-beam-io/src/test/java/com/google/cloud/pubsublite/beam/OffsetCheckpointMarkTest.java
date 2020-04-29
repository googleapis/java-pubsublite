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

package com.google.cloud.pubsublite.beam;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.apache.beam.sdk.coders.Coder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class OffsetCheckpointMarkTest {
  @Captor private ArgumentCaptor<Map<Partition, Offset>> mapCaptor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void finalizeFinalizesWithOffsets() throws Exception {
    Map<Partition, Offset> map =
        ImmutableMap.of(
            Partition.create(10), Offset.create(15), Partition.create(85), Offset.create(0));
    OffsetFinalizer finalizer = mock(OffsetFinalizer.class);
    OffsetCheckpointMark mark = new OffsetCheckpointMark(finalizer, map);
    mark.finalizeCheckpoint();
    verify(finalizer).finalizeOffsets(mapCaptor.capture());
    assertThat(mapCaptor.getValue()).containsExactlyEntriesIn(map);
  }

  @Test
  public void coderDropsFinalizerKeepsOffsets() throws Exception {
    Coder<OffsetCheckpointMark> coder = OffsetCheckpointMark.getCoder();
    OffsetFinalizer finalizer = mock(OffsetFinalizer.class);
    OffsetCheckpointMark mark =
        new OffsetCheckpointMark(
            finalizer,
            ImmutableMap.of(
                Partition.create(10), Offset.create(15), Partition.create(85), Offset.create(0)));

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    coder.encode(mark, output);
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
    OffsetCheckpointMark decoded = coder.decode(input);
    assertThat(mark.partitionOffsetMap).containsExactlyEntriesIn(decoded.partitionOffsetMap);
    decoded.finalizeCheckpoint();
    verifyZeroInteractions(finalizer);
  }
}
