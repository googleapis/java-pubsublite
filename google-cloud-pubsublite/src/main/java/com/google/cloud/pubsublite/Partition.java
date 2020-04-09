// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite;

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;

/** A partition of a topic. */
@AutoValue
public abstract class Partition {
  public static Partition create(long partition) throws StatusException {
    checkArgument(partition >= 0, "Partitions are zero indexed.");
    return new AutoValue_Partition(partition);
  }

  public abstract long value();
}
