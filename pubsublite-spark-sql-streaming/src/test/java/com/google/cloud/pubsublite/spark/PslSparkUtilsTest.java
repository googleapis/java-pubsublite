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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import org.junit.Test;

public class PslSparkUtilsTest {

  @Test
  public void testSourceOffsetConversion() {
    PslSourceOffset pslSourceOffset = new PslSourceOffset(2);
    pslSourceOffset.set(Partition.of(0L), Offset.of(10));
    pslSourceOffset.set(Partition.of(1L), Offset.of(50));

    SparkSourceOffset sparkSourceOffset = PslSparkUtils.toSparkSourceOffset(pslSourceOffset);
    assertThat(sparkSourceOffset.getPartitionOffsetMap().get(Partition.of(0L)).offset())
        .isEqualTo(9L);
    assertThat(sparkSourceOffset.getPartitionOffsetMap().get(Partition.of(1L)).offset())
        .isEqualTo(49L);

    assertThat(PslSparkUtils.toPslSourceOffset(sparkSourceOffset)).isEqualTo(pslSourceOffset);
  }

  @Test
  public void testToPslPartitionOffset() {
    SparkPartitionOffset sparkPartitionOffset =
        SparkPartitionOffset.builder().partition(Partition.of(1L)).offset(10L).build();
    PslPartitionOffset pslPartitionOffset =
        PslPartitionOffset.builder().partition(Partition.of(1L)).offset(Offset.of(11L)).build();
    assertThat(PslSparkUtils.toPslPartitionOffset(sparkPartitionOffset))
        .isEqualTo(pslPartitionOffset);
  }
}
