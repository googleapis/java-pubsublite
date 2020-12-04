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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

public class PslSourceOffset {

  private final Map<Partition, Offset> partitionOffsetMap;

  public PslSourceOffset(Map<Partition, Offset> partitionOffsetMap) {
    this.partitionOffsetMap = partitionOffsetMap;
  }

  public Map<Partition, Offset> getPartitionOffsetMap() {
    return ImmutableMap.copyOf(partitionOffsetMap);
  }

  public static Builder newBuilder(long partitionCount) {
    return new Builder(partitionCount);
  }

  public static class Builder {

    private final Map<Partition, Offset> partitionOffsetMap = new HashMap<>();

    private Builder(long partitionCount) {
      for (long i = 0; i < partitionCount; i++) {
        partitionOffsetMap.put(Partition.of(i), Offset.of(0));
      }
    }

    public Builder set(Partition partition, Offset offset) {
      partitionOffsetMap.replace(partition, offset);
      return this;
    }

    public PslSourceOffset build() {
      return new PslSourceOffset(partitionOffsetMap);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PslSourceOffset that = (PslSourceOffset) o;
    return Objects.equal(partitionOffsetMap, that.partitionOffsetMap);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(partitionOffsetMap);
  }
}
