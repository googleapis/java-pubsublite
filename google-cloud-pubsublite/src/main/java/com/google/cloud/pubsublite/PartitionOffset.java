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

package com.google.cloud.pubsublite;

import com.google.auto.value.AutoValue;
import java.io.Serializable;

@AutoValue
public abstract class PartitionOffset implements Serializable {
  private static final long serialVersionUID = 771082451165552118L;

  public abstract Partition partition();

  public abstract Offset offset();

  public static Builder builder() {
    return new AutoValue_PartitionOffset.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder partition(Partition partition);

    public abstract Builder offset(Offset offset);

    public abstract PartitionOffset build();
  }
}
