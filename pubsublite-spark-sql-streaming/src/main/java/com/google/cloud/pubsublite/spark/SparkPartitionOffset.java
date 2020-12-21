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

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import java.io.Serializable;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

@AutoValue
abstract class SparkPartitionOffset implements PartitionOffset, Serializable {
  private static final long serialVersionUID = -3398208694782540866L;

  abstract Partition partition();

  abstract long offset();

  public static SparkPartitionOffset create(Partition partition, long offset) {
    return builder().partition(partition).offset(offset).build();
  }

  public static Builder builder() {
    return new AutoValue_SparkPartitionOffset.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder partition(Partition partition);

    public abstract Builder offset(long offset);

    public abstract SparkPartitionOffset build();
  }
}
