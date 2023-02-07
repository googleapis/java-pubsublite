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

package com.google.cloud.pubsublite.internal.wire;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.util.UUID;

/** Utilities for generating and converting 128-bit UUIDs. */
public final class UuidBuilder {

  /** Generates a random UUID. */
  public static UUID generate() {
    return UUID.randomUUID();
  }

  /** Converts a UUID to a ByteString. */
  public static ByteString toByteString(UUID uuid) {
    ByteBuffer uuidBuffer = ByteBuffer.allocate(16);
    uuidBuffer.putLong(uuid.getMostSignificantBits());
    uuidBuffer.putLong(uuid.getLeastSignificantBits());
    return ByteString.copyFrom(uuidBuffer.array());
  }

  private UuidBuilder() {}
}
