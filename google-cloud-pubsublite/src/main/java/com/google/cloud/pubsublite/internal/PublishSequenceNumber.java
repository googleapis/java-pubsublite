/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.pubsublite.internal;

import com.google.auto.value.AutoValue;
import java.io.Serializable;

/** A sequence number for a published message, for implementing publish idempotency. */
@AutoValue
public abstract class PublishSequenceNumber implements Serializable {

  /** Create a publish sequence number from its long value. */
  public static PublishSequenceNumber of(long sequenceNumber) {
    return new AutoValue_PublishSequenceNumber(sequenceNumber);
  }

  /** The sequence number that should be set for the first message in a publisher session. */
  public static final PublishSequenceNumber FIRST = PublishSequenceNumber.of(0);

  /** Returns the next sequence number that follows the current. */
  public PublishSequenceNumber next() {
    return PublishSequenceNumber.of(value() + 1);
  }

  /** The long value of this publish sequence number. */
  public abstract long value();
}
