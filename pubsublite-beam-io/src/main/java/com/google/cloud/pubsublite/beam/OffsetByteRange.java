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

import com.google.auto.value.AutoValue;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.schemas.AutoValueSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;

@AutoValue
@DefaultSchema(AutoValueSchema.class)
abstract class OffsetByteRange {
  abstract OffsetRange getRange();

  abstract long getByteCount();

  static OffsetByteRange of(OffsetRange range, long byteCount) {
    return new AutoValue_OffsetByteRange(range, byteCount);
  }

  static OffsetByteRange of(OffsetRange range) {
    return of(range, 0);
  }
}
