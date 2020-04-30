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
import com.google.cloud.pubsublite.internal.Preconditions;
import io.grpc.Status;
import io.grpc.StatusException;

// Information about a successful publish operation. Can be encoded in the string returned by the
// Cloud Pub/Sub publish() api.
@AutoValue
public abstract class PublishMetadata {
  public abstract Partition partition();

  public abstract Offset offset();

  public static PublishMetadata create(Partition partition, Offset offset) {
    return new AutoValue_PublishMetadata(partition, offset);
  }

  public static PublishMetadata decode(String encoded) throws StatusException {
    String[] split = encoded.split(":");
    Preconditions.checkArgument(split.length == 2, "Invalid encoded PublishMetadata.");
    try {
      Partition partition = Partition.create(Long.parseLong(split[0]));
      Offset offset = Offset.create(Long.parseLong(split[1]));
      return create(partition, offset);
    } catch (NumberFormatException e) {
      throw Status.INVALID_ARGUMENT
          .withCause(e)
          .withDescription("Invalid encoded PublishMetadata.")
          .asException();
    }
  }

  public String encode() {
    return String.format("%s:%s", partition().value(), offset().value());
  }
}
