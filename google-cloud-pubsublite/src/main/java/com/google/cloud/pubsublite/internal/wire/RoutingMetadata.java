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

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import io.grpc.Metadata;
import io.grpc.Status;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

final class RoutingMetadata {
  private RoutingMetadata() {}

  static final String PARAMS_HEADER = "x-goog-request-params";
  static final Metadata.Key<String> PARAMS_KEY =
      Metadata.Key.of(PARAMS_HEADER, Metadata.ASCII_STRING_MARSHALLER);

  static Metadata of(TopicPath topic, Partition partition) {
    try {
      Metadata metadata = new Metadata();
      String topic_value = URLEncoder.encode(topic.toString(), StandardCharsets.UTF_8.toString());
      String params = String.format("partition=%s&topic=%s", partition.value(), topic_value);
      metadata.put(PARAMS_KEY, params);
      return metadata;
    } catch (UnsupportedEncodingException e) {
      throw Status.INVALID_ARGUMENT.withCause(e).asRuntimeException();
    }
  }

  static Metadata of(SubscriptionPath subscription, Partition partition) {
    try {
      Metadata metadata = new Metadata();
      String subscription_value =
          URLEncoder.encode(subscription.toString(), StandardCharsets.UTF_8.toString());
      String params =
          String.format("partition=%s&subscription=%s", partition.value(), subscription_value);
      metadata.put(PARAMS_KEY, params);
      return metadata;
    } catch (UnsupportedEncodingException e) {
      throw Status.INVALID_ARGUMENT.withCause(e).asRuntimeException();
    }
  }
}
