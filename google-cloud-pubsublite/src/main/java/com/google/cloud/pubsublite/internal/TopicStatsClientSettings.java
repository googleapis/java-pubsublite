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
package com.google.cloud.pubsublite.internal;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.cloudpubsub.MessagingBackend;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.cloud.pubsublite.v1.TopicStatsServiceSettings;
import java.util.Map;
import java.util.Optional;

@AutoValue
public abstract class TopicStatsClientSettings {

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract Optional<TopicStatsServiceClient> serviceClient();

  /** The backend messaging system to use (e.g., PUBSUB_LITE or MANAGED_KAFKA). */
  public abstract MessagingBackend messagingBackend();

  /** Kafka-specific properties for when using MANAGED_KAFKA backend. */
  public abstract Optional<Map<String, Object>> kafkaProperties();

  public static Builder newBuilder() {
    return new AutoValue_TopicStatsClientSettings.Builder()
        .setMessagingBackend(MessagingBackend.PUBSUB_LITE);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.
    public abstract Builder setServiceClient(TopicStatsServiceClient stub);

    /** Set the backend messaging system to use (e.g., PUBSUB_LITE or MANAGED_KAFKA). */
    public abstract Builder setMessagingBackend(MessagingBackend backend);

    /** Set Kafka-specific properties for when using MANAGED_KAFKA backend. */
    public abstract Builder setKafkaProperties(Map<String, Object> kafkaProperties);

    public abstract TopicStatsClientSettings build();
  }

  TopicStatsClient instantiate() throws ApiException {
    // For Kafka backend, use KafkaTopicStatsClient
    if (messagingBackend() == MessagingBackend.MANAGED_KAFKA) {
      if (!kafkaProperties().isPresent()) {
        throw new IllegalStateException(
            "kafkaProperties must be set when using MANAGED_KAFKA backend");
      }
      return new KafkaTopicStatsClient(region(), kafkaProperties().get());
    }

    // For Pub/Sub Lite backend, use TopicStatsClientImpl
    TopicStatsServiceClient serviceClient;
    if (serviceClient().isPresent()) {
      serviceClient = serviceClient().get();
    } else {
      try {
        serviceClient =
            TopicStatsServiceClient.create(
                addDefaultSettings(region(), TopicStatsServiceSettings.newBuilder()));
      } catch (Throwable t) {
        throw toCanonical(t).underlying;
      }
    }
    return new TopicStatsClientImpl(region(), serviceClient);
  }
}
