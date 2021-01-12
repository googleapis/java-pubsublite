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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.collect.ImmutableMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class RoutingMetadata {

  private static final String PARAMS_HEADER = "x-goog-request-params";
  private final Map<String, String> metadata;

  public static RoutingMetadata of(TopicPath topic, Partition partition) throws ApiException {
    return new RoutingMetadata(topic, partition);
  }

  public static RoutingMetadata of(SubscriptionPath subscription, Partition partition)
      throws ApiException {
    return new RoutingMetadata(subscription, partition);
  }

  private RoutingMetadata(TopicPath topic, Partition partition) {
    try {
      String topic_value = URLEncoder.encode(topic.toString(), StandardCharsets.UTF_8.toString());
      String params = String.format("partition=%s&topic=%s", partition.value(), topic_value);
      this.metadata = ImmutableMap.of(PARAMS_HEADER, params);
    } catch (UnsupportedEncodingException e) {
      throw toCanonical(e).underlying;
    }
  }

  private RoutingMetadata(SubscriptionPath subscription, Partition partition) {
    try {
      String subscription_value =
          URLEncoder.encode(subscription.toString(), StandardCharsets.UTF_8.toString());
      String params =
          String.format("partition=%s&subscription=%s", partition.value(), subscription_value);
      this.metadata = ImmutableMap.of(PARAMS_HEADER, params);
    } catch (UnsupportedEncodingException e) {
      throw toCanonical(e).underlying;
    }
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }
}
