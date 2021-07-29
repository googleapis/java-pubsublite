/*
 * Copyright 2021 Google LLC
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

import com.google.protobuf.ByteString;
import java.util.Map;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class of utilities for transforming between Cloud Pub/Sub messages and the beam wrapper for
 * them.
 */
class CloudPubsubMessages {
  private static final Logger LOG = LoggerFactory.getLogger(CloudPubsubMessages.class);

  private CloudPubsubMessages() {}

  static com.google.pubsub.v1.PubsubMessage toProto(PubsubMessage input) {
    com.google.pubsub.v1.PubsubMessage.Builder message =
        com.google.pubsub.v1.PubsubMessage.newBuilder()
            .setData(ByteString.copyFrom(input.getPayload()));
    // TODO(BEAM-8085) this should not be null
    Map<String, String> attributes = input.getAttributeMap();
    if (attributes != null) {
      message.putAllAttributes(attributes);
    }
    String messageId = input.getMessageId();
    if (messageId != null) {
      message.setMessageId(messageId);
    }
    return message.build();
  }

  static PubsubMessage fromProto(com.google.pubsub.v1.PubsubMessage proto) {
    if (!proto.getOrderingKey().isEmpty()) {
      LOG.warn(
          "Dropping ordering key for message id `{}` with key `{}`.",
          proto.getMessageId(),
          proto.getOrderingKey());
    }
    return new PubsubMessage(
        proto.getData().toByteArray(), proto.getAttributesMap(), proto.getMessageId());
  }
}
