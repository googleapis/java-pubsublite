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

package com.google.cloud.pubsublite.cloudpubsub;

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import java.util.Base64;
import java.util.Collection;

/**
 * MessageTransforms details how to transform message representations from Cloud Pub/Sub to Pub/Sub
 * Lite.
 *
 * <p>Transformers are made public to allow user code that currently uses PubsubMessages to use
 * interfaces interacting with Pub/Sub Lite.
 */
public class MessageTransforms {
  private MessageTransforms() {}

  public static final String PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO =
      "x-goog-pubsublite-event-time-timestamp-proto";

  /**
   * Encode a timestamp in a way that it will be interpreted as an event time if published on a
   * message with an attribute named PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO.
   */
  public static String encodeAttributeEventTime(Timestamp timestamp) {
    return Base64.getEncoder().encodeToString(timestamp.toByteArray());
  }

  /** Decode a timestamp encoded with encodeAttributeEventTime. */
  public static Timestamp decodeAttributeEventTime(String encoded) throws ApiException {
    try {
      return Timestamp.parseFrom(Base64.getDecoder().decode(encoded));
    } catch (Exception e) {
      throw new CheckedApiException(e, Code.INVALID_ARGUMENT).underlying;
    }
  }

  /**
   * The default attribute parsing logic requires that all attributes could have been generated from
   * the Cloud Pub/Sub client library shim. This means it requires that all of them are single entry
   * representations of UTF-8 encoded strings.
   */
  private static String parseAttributes(Collection<ByteString> values) throws ApiException {
    checkArgument(
        values.size() == 1,
        "Received an unparseable message with multiple values for an attribute.");
    ByteString attribute = values.iterator().next();
    checkArgument(
        attribute.isValidUtf8(),
        String.format(
            "Received an unparseable message with a non-utf8 attribute value: %s",
            Base64.getEncoder().encodeToString(attribute.toByteArray())));
    return attribute.toStringUtf8();
  }

  static MessageTransformer<SequencedMessage, PubsubMessage> addIdCpsSubscribeTransformer(
      Partition partition, MessageTransformer<SequencedMessage, PubsubMessage> toWrap) {
    return message -> {
      PubsubMessage out = toWrap.transform(message);
      checkArgument(
          out.getMessageId().isEmpty(),
          String.format("Received non-empty message id for PubsubMessage: %s", out));
      return out.toBuilder()
          .setMessageId(PublishMetadata.of(partition, message.offset()).encode())
          .build();
    };
  }

  public static MessageTransformer<SequencedMessage, PubsubMessage> toCpsSubscribeTransformer() {
    return message -> {
      PubsubMessage.Builder outBuilder =
          toCpsPublishTransformer().transform(message.message()).toBuilder();
      outBuilder.setPublishTime(message.publishTime());
      return outBuilder.build();
    };
  }

  public static MessageTransformer<Message, PubsubMessage> toCpsPublishTransformer() {
    return message -> {
      checkArgument(
          message.key().isValidUtf8(), "Message key is not valid utf-8. Unable to parse message.");
      checkArgument(
          !message.attributes().containsKey(PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO),
          "Special timestamp attribute exists in wire message. Unable to parse message.");
      PubsubMessage.Builder outBuilder = PubsubMessage.newBuilder();
      outBuilder.setOrderingKey(message.key().toStringUtf8());
      outBuilder.setData(message.data());
      for (String key : message.attributes().keys()) {
        outBuilder.putAttributes(key, parseAttributes(message.attributes().get(key)));
      }
      message
          .eventTime()
          .ifPresent(
              eventTime ->
                  outBuilder.putAttributes(
                      PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO, encodeAttributeEventTime(eventTime)));
      return outBuilder.build();
    };
  }

  public static MessageTransformer<PubsubMessage, Message> fromCpsPublishTransformer(
      KeyExtractor keyExtractor) {
    return message -> {
      Message.Builder outBuilder = Message.builder();
      if (message.containsAttributes(PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO)) {
        outBuilder =
            outBuilder.setEventTime(
                decodeAttributeEventTime(
                    message.getAttributesOrThrow(PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO)));
      }
      outBuilder = outBuilder.setData(message.getData());
      outBuilder = outBuilder.setKey(keyExtractor.extractKey(message));
      ImmutableListMultimap.Builder<String, ByteString> attributesBuilder =
          ImmutableListMultimap.builder();
      message
          .getAttributesMap()
          .forEach(
              (key, value) -> {
                if (!PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO.equals(key)) {
                  attributesBuilder.put(key, ByteString.copyFromUtf8(value));
                }
              });
      outBuilder = outBuilder.setAttributes(attributesBuilder.build());
      return outBuilder.build();
    };
  }
}
