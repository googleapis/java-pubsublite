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
import static org.junit.Assert.fail;

import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.testing.UnitTestExamples;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class PslSourceOffsetTest {

  @Test
  public void roundTrip() {
    PslSourceOffset offset =
        new PslSourceOffset(
            ImmutableMap.of(
                // Intentionally unsorted, the serialization should make it sorted.
                Partition.of(3L), Offset.of(10L),
                Partition.of(1L), Offset.of(5L),
                Partition.of(2L), Offset.of(8L)));
    assertThat(offset.json()).isEqualTo("{\"1\":5,\"2\":8,\"3\":10}");
    assertThat(PslSourceOffset.fromJson(offset.json())).isEqualTo(offset);
  }

  @Test
  public void mergePslSourceOffsets() {
    PslSourceOffset o1 =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(3L), Offset.of(10L),
                Partition.of(1L), Offset.of(5L),
                Partition.of(2L), Offset.of(8L)));
    PslSourceOffset o2 =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(3L), Offset.of(8L),
                Partition.of(2L), Offset.of(11L),
                Partition.of(4L), Offset.of(1L)));
    PslSourceOffset expected =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(1L), Offset.of(5L),
                Partition.of(2L), Offset.of(11L),
                Partition.of(3L), Offset.of(10L),
                Partition.of(4L), Offset.of(1L)));
    assertThat(PslSourceOffset.merge(o1, o2)).isEqualTo(expected);
  }

  @Test
  public void mergePslPartitionOffsetsDuplicatePartition() {
    PslPartitionOffset[] offsets = {
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(3L))
          .offset(Offset.of(10L))
          .build(),
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(1L))
          .offset(Offset.of(5L))
          .build(),
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(1L))
          .offset(Offset.of(4L))
          .build()
    };
    try {
      PslSourceOffset.merge(offsets);
      fail();
    } catch (AssertionError e) {
      assertThat(e).hasMessageThat().contains("same partition");
    }
  }

  @Test
  public void mergePslPartitionOffsets() {
    PslPartitionOffset[] offsets = {
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(3L))
          .offset(Offset.of(10L))
          .build(),
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(1L))
          .offset(Offset.of(5L))
          .build(),
      PslPartitionOffset.builder()
          .subscriptionPath(UnitTestExamples.exampleSubscriptionPath())
          .partition(Partition.of(2L))
          .offset(Offset.of(4L))
          .build()
    };
    PslSourceOffset expected =
        new PslSourceOffset(
            ImmutableMap.of(
                Partition.of(3L), Offset.of(10L),
                Partition.of(1L), Offset.of(5L),
                Partition.of(2L), Offset.of(4L)));
    assertThat(PslSourceOffset.merge(offsets)).isEqualTo(expected);
  }
}
