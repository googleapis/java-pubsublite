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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.internal.CheckedApiException;

/**
 * Information about a successful publish operation. Can be encoded in the string returned by the
 * Cloud Pub/Sub {@link com.google.cloud.pubsub.v1.Publisher#publish} api.
 */
@AutoValue
public abstract class PublishMetadata {
  /** The partition a message was published to. */
  public abstract Partition partition();

  /** The offset a message was assigned. */
  public abstract Offset offset();

  /** Construct a PublishMetadata from a Partition and Offset. */
  public static PublishMetadata of(Partition partition, Offset offset) {
    return new AutoValue_PublishMetadata(partition, offset);
  }

  /** Decode a PublishMetadata from the Cloud Pub/Sub ack id. */
  public static PublishMetadata decode(String encoded) throws ApiException {
    String[] split = encoded.split(":");
    checkArgument(split.length == 2, "Invalid encoded PublishMetadata.");
    try {
      Partition partition = Partition.of(Long.parseLong(split[0]));
      Offset offset = Offset.of(Long.parseLong(split[1]));
      return of(partition, offset);
    } catch (NumberFormatException e) {
      throw new CheckedApiException("Invalid encoded PublishMetadata.", e, Code.INVALID_ARGUMENT)
          .underlying;
    }
  }

  /** Encode a publish metadata as a Cloud Pub/Sub ack id. */
  public String encode() {
    return String.format("%s:%s", partition().value(), offset().value());
  }
}
