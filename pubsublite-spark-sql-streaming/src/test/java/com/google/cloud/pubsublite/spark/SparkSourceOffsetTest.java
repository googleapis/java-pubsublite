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

import com.google.cloud.pubsublite.Partition;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class SparkSourceOffsetTest {

  @Test
  public void roundTrip() {
    SparkSourceOffset offset =
        new SparkSourceOffset(
            ImmutableMap.of(
                // Intentionally unsorted, the serialization should make it sorted.
                Partition.of(3L),
                    SparkPartitionOffset.builder().partition(Partition.of(3L)).offset(10L).build(),
                Partition.of(1L),
                    SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(5L).build(),
                Partition.of(2L),
                    SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(8L).build()));
    assertThat(offset.json()).isEqualTo("{\"1\":5,\"2\":8,\"3\":10}");
    assertThat(SparkSourceOffset.fromJson(offset.json())).isEqualTo(offset);
  }

  @Test
  public void mergeSparkSourceOffsets() {
    SparkSourceOffset o1 =
        new SparkSourceOffset(
            ImmutableMap.of(
                Partition.of(1L),
                    SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(10L).build(),
                Partition.of(2L),
                    SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(8L).build(),
                Partition.of(3L),
                    SparkPartitionOffset.builder()
                        .partition(Partition.of(3L))
                        .offset(10L)
                        .build()));
    SparkSourceOffset o2 =
        new SparkSourceOffset(
            ImmutableMap.of(
                Partition.of(2L),
                    SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(8L).build(),
                Partition.of(3L),
                    SparkPartitionOffset.builder().partition(Partition.of(3L)).offset(11L).build(),
                Partition.of(4L),
                    SparkPartitionOffset.builder().partition(Partition.of(4L)).offset(1L).build()));
    SparkSourceOffset expected =
        new SparkSourceOffset(
            ImmutableMap.of(
                Partition.of(1L),
                    SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(10L).build(),
                Partition.of(2L),
                    SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(8L).build(),
                Partition.of(3L),
                    SparkPartitionOffset.builder().partition(Partition.of(3L)).offset(11L).build(),
                Partition.of(4L),
                    SparkPartitionOffset.builder().partition(Partition.of(4L)).offset(1L).build()));
    assertThat(SparkSourceOffset.merge(o1, o2)).isEqualTo(expected);
  }

  @Test
  public void mergeSparkPartitionOffsetsDuplicatePartition() {
    SparkPartitionOffset[] offsets = {
      SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(5L).build(),
      SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(4L).build(),
      SparkPartitionOffset.builder().partition(Partition.of(3L)).offset(10L).build()
    };
    try {
      SparkSourceOffset.merge(offsets);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessageThat().contains("same partition");
    }
  }

  @Test
  public void mergeSparkPartitionOffsets() {
    SparkPartitionOffset[] offsets = {
      SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(5L).build(),
      SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(4L).build(),
      SparkPartitionOffset.builder().partition(Partition.of(3L)).offset(10L).build()
    };
    SparkSourceOffset expected =
        new SparkSourceOffset(
            ImmutableMap.of(
                Partition.of(1L),
                    SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(5L).build(),
                Partition.of(2L),
                    SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(4L).build(),
                Partition.of(3L),
                    SparkPartitionOffset.builder()
                        .partition(Partition.of(3L))
                        .offset(10L)
                        .build()));
    assertThat(SparkSourceOffset.merge(offsets)).isEqualTo(expected);
  }

  @Test
  public void invalidMap() {
    try {
      new SparkSourceOffset(
          ImmutableMap.of(
              Partition.of(3L),
              SparkPartitionOffset.builder().partition(Partition.of(2L)).offset(10L).build()));
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessageThat().contains("don't match");
    }
  }
}
