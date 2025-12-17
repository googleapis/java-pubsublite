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

import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.cloudpubsub.MessagingBackend;
import com.google.cloud.pubsublite.internal.AdminClientImpl;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.KafkaAdminClient;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
import java.util.Map;
import java.util.Optional;

/** Settings for construction a Pub/Sub Lite AdminClient. */
@AutoValue
public abstract class AdminClientSettings {
  // Required parameters.
  /**
   * The <a href="https://cloud.google.com/pubsub/lite/docs/locations">cloud region</a> to perform
   * admin operations for.
   */
  abstract CloudRegion region();

  // Optional parameters.
  /** The retry settings for this client. */
  abstract Optional<RetrySettings> retrySettings();

  /** A stub to use to connect. */
  abstract Optional<AdminServiceClient> serviceClient();

  /** The backend messaging system to use (e.g., PUBSUB_LITE or MANAGED_KAFKA). */
  public abstract MessagingBackend messagingBackend();

  /** Kafka-specific properties for when using MANAGED_KAFKA backend. */
  public abstract Optional<Map<String, Object>> kafkaProperties();

  /** Default number of partitions for new Kafka topics. */
  public abstract int kafkaDefaultPartitions();

  /** Default replication factor for new Kafka topics. */
  public abstract short kafkaDefaultReplicationFactor();

  public static Builder newBuilder() {
    return new AutoValue_AdminClientSettings.Builder()
        .setRetrySettings(Constants.DEFAULT_RETRY_SETTINGS)
        .setMessagingBackend(MessagingBackend.PUBSUB_LITE)
        .setKafkaDefaultPartitions(3)
        .setKafkaDefaultReplicationFactor((short) 1);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.

    /** The cloud region to perform admin operations for. */
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.

    /** The retry settings for this client. */
    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    /** A service client to use to connect. */
    public abstract Builder setServiceClient(AdminServiceClient serviceClient);

    /** Set the backend messaging system to use (e.g., PUBSUB_LITE or MANAGED_KAFKA). */
    public abstract Builder setMessagingBackend(MessagingBackend backend);

    /** Set Kafka-specific properties for when using MANAGED_KAFKA backend. */
    public abstract Builder setKafkaProperties(Map<String, Object> kafkaProperties);

    /** Set default number of partitions for new Kafka topics. */
    public abstract Builder setKafkaDefaultPartitions(int partitions);

    /** Set default replication factor for new Kafka topics. */
    public abstract Builder setKafkaDefaultReplicationFactor(short replicationFactor);

    /** Build the settings object. */
    public abstract AdminClientSettings build();
  }

  AdminClient instantiate() throws ApiException {
    // For Kafka backend, use KafkaAdminClient
    if (messagingBackend() == MessagingBackend.MANAGED_KAFKA) {
      if (!kafkaProperties().isPresent()) {
        throw new IllegalStateException(
            "kafkaProperties must be set when using MANAGED_KAFKA backend");
      }
      return new KafkaAdminClient(
          region(),
          kafkaProperties().get(),
          kafkaDefaultPartitions(),
          kafkaDefaultReplicationFactor());
    }

    // For Pub/Sub Lite backend, use AdminClientImpl
    AdminServiceClient serviceClient;
    if (serviceClient().isPresent()) {
      serviceClient = serviceClient().get();
    } else {
      try {
        AdminServiceSettings.Builder builder =
            addDefaultSettings(region(), AdminServiceSettings.newBuilder()).toBuilder();
        if (retrySettings().isPresent()) {
          builder.applyToAllUnaryMethods(
              callBuilder -> {
                callBuilder.setRetrySettings(retrySettings().get());
                return null;
              });
        }
        serviceClient = AdminServiceClient.create(builder.build());
      } catch (Throwable t) {
        throw ExtractStatus.toCanonical(t).underlying;
      }
    }
    return new AdminClientImpl(region(), serviceClient);
  }
}
