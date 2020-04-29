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

/** An offset in the partition. */
@AutoValue
public abstract class Offset implements Comparable<Offset> {
  public static Offset create(long offset) {
    return new AutoValue_Offset(offset);
  }

  public abstract long value();

  @Override
  public int compareTo(Offset o) {
    return Long.compare(value(), o.value());
  }
}
