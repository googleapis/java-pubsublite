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
import com.google.cloud.pubsublite.proto.AttributeValues;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import java.util.Optional;

/** A user message. */
@AutoValue
public abstract class Message {
  /** The key for this message. All messages with the same key are routed to the same partition. */
  public abstract ByteString key();

  /** The data payload for this message. */
  public abstract ByteString data();

  /** A multimap of attributes for this message. */
  public abstract ImmutableListMultimap<String, ByteString> attributes();

  /** The user-provided event time for this message. */
  public abstract Optional<Timestamp> eventTime();

  /** Get a new builder for a message. */
  public static Builder builder() {
    return new AutoValue_Message.Builder()
        .setKey(ByteString.EMPTY)
        .setData(ByteString.EMPTY)
        .setAttributes(ImmutableListMultimap.of());
  }

  /** Convert an existing message to a builder. */
  public abstract Builder toBuilder();

  /** Convert this to a message proto. */
  public PubSubMessage toProto() {
    PubSubMessage.Builder builder = PubSubMessage.newBuilder();
    builder.setKey(key());
    builder.setData(data());
    eventTime().ifPresent(t -> builder.setEventTime(t));
    attributes()
        .asMap()
        .forEach(
            (key, values) -> {
              AttributeValues values_proto =
                  AttributeValues.newBuilder().addAllValues(values).build();
              builder.putAttributes(key, values_proto);
            });
    return builder.build();
  }

  /** Construct a message from a proto. */
  public static Message fromProto(PubSubMessage proto) {
    Message.Builder builder = Message.builder().setKey(proto.getKey()).setData(proto.getData());
    if (proto.hasEventTime()) {
      builder = builder.setEventTime(proto.getEventTime());
    }
    ImmutableListMultimap.Builder<String, ByteString> mapBuilder = ImmutableListMultimap.builder();
    proto
        .getAttributesMap()
        .forEach(
            (key, attributeValues) ->
                attributeValues.getValuesList().forEach(value -> mapBuilder.put(key, value)));
    builder = builder.setAttributes(mapBuilder.build());
    return builder.build();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    /**
     * The key for this message. All messages with the same key are routed to the same partition.
     */
    public abstract Builder setKey(ByteString key);

    /** The data payload for this message. */
    public abstract Builder setData(ByteString data);

    /** A multimap of attributes for this message. */
    public abstract Builder setAttributes(ImmutableListMultimap<String, ByteString> attributes);

    /** The user provided event time for this message. */
    public abstract Builder setEventTime(Timestamp eventTime);

    /** Build a message. */
    public abstract Message build();
  }
}
