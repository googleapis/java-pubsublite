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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultMetadata;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.WrappingPublisher;
import com.google.cloud.pubsublite.internal.wire.PartitionCountWatchingPublisherSettings;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingMetadata;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import com.google.cloud.pubsublite.v1.PublisherServiceSettings;
import com.google.common.annotations.VisibleForTesting;
import com.google.pubsub.v1.PubsubMessage;
import java.util.Optional;
import java.util.function.Supplier;
import org.threeten.bp.Duration;

/**
 * Settings for instantiating a Pub/Sub Lite publisher emulating the Cloud Pub/Sub Publisher API.
 */
@AutoValue
public abstract class PublisherSettings {
  public static final BatchingSettings DEFAULT_BATCHING_SETTINGS =
      BatchingSettings.newBuilder()
          .setIsEnabled(true)
          .setElementCountThreshold(1000L)
          .setRequestByteThreshold(Constants.MAX_PUBLISH_BATCH_BYTES)
          .setDelayThreshold(Duration.ofMillis(50))
          .build();
  private static final Framework FRAMEWORK = Framework.of("CLOUD_PUBSUB_SHIM");

  // Required parameters.

  /** The topic path to publish to. */
  abstract TopicPath topicPath();

  // Optional parameters.
  /** A KeyExtractor for getting the routing key from a message. */
  abstract Optional<KeyExtractor> keyExtractor();

  /** A MessageTransformer for constructing wire messages from Cloud Pub/Sub PubsubMessages. */
  abstract Optional<MessageTransformer<PubsubMessage, Message>> messageTransformer();

  /** Batching settings for this publisher to use. Apply per-partition. */
  abstract Optional<BatchingSettings> batchingSettings();

  /** A provider for credentials. */
  abstract CredentialsProvider credentialsProvider();

  /**
   * A supplier for new PublisherServiceClients. Should return a new client each time. If present,
   * ignores CredentialsProvider.
   */
  abstract Optional<Supplier<PublisherServiceClient>> serviceClientSupplier();

  /** The AdminClient to use, if provided. */
  abstract Optional<AdminClient> adminClient();

  // For testing.
  abstract SinglePartitionPublisherBuilder.Builder underlyingBuilder();

  /** Get a new builder for a PublisherSettings. */
  public static Builder newBuilder() {
    return new AutoValue_PublisherSettings.Builder()
        .setCredentialsProvider(
            PublisherServiceSettings.defaultCredentialsProviderBuilder().build())
        .setUnderlyingBuilder(SinglePartitionPublisherBuilder.newBuilder());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.

    /** The topic path to publish to. */
    public abstract Builder setTopicPath(TopicPath path);

    // Optional parameters.
    /** A KeyExtractor for getting the routing key from a message. */
    public abstract Builder setKeyExtractor(KeyExtractor keyExtractor);

    /** A MessageTransformer for constructing wire messages from Cloud Pub/Sub PubsubMessages. */
    public abstract Builder setMessageTransformer(
        MessageTransformer<PubsubMessage, Message> messageTransformer);

    /** Batching settings for this publisher to use. Apply per-partition. */
    public abstract Builder setBatchingSettings(BatchingSettings batchingSettings);

    /** A provider for credentials. */
    public abstract Builder setCredentialsProvider(CredentialsProvider credentialsProvider);

    /**
     * A supplier for new PublisherServiceClients. Should return a new client each time. If present,
     * ignores CredentialsProvider.
     */
    public abstract Builder setServiceClientSupplier(Supplier<PublisherServiceClient> supplier);

    /** The AdminClient to use, if provided. */
    public abstract Builder setAdminClient(AdminClient adminClient);

    // For testing.
    @VisibleForTesting
    abstract Builder setUnderlyingBuilder(
        SinglePartitionPublisherBuilder.Builder underlyingBuilder);

    public abstract PublisherSettings build();
  }

  private PublisherServiceClient newServiceClient(Partition partition) throws ApiException {
    if (serviceClientSupplier().isPresent()) return serviceClientSupplier().get().get();
    PublisherServiceSettings.Builder settingsBuilder = PublisherServiceSettings.newBuilder();
    settingsBuilder = settingsBuilder.setCredentialsProvider(credentialsProvider());
    settingsBuilder =
        addDefaultMetadata(
            PubsubContext.of(FRAMEWORK),
            RoutingMetadata.of(topicPath(), partition),
            settingsBuilder);
    try {
      return PublisherServiceClient.create(
          addDefaultSettings(topicPath().location().region(), settingsBuilder));
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  private AdminClient getAdminClient() throws ApiException {
    if (adminClient().isPresent()) return adminClient().get();
    try {
      return AdminClient.create(
          AdminClientSettings.newBuilder()
              .setServiceClient(
                  AdminServiceClient.create(
                      addDefaultSettings(
                          topicPath().location().region(),
                          AdminServiceSettings.newBuilder()
                              .setCredentialsProvider(credentialsProvider()))))
              .setRegion(topicPath().location().region())
              .build());
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @SuppressWarnings("CheckReturnValue")
  Publisher instantiate() throws ApiException {
    BatchingSettings batchingSettings = batchingSettings().orElse(DEFAULT_BATCHING_SETTINGS);
    KeyExtractor keyExtractor = keyExtractor().orElse(KeyExtractor.DEFAULT);
    MessageTransformer<PubsubMessage, Message> messageTransformer =
        messageTransformer()
            .orElseGet(() -> MessageTransforms.fromCpsPublishTransformer(keyExtractor));

    PartitionCountWatchingPublisherSettings.Builder publisherSettings =
        PartitionCountWatchingPublisherSettings.newBuilder()
            .setTopic(topicPath())
            .setPublisherFactory(
                partition -> {
                  SinglePartitionPublisherBuilder.Builder singlePartitionBuilder =
                      underlyingBuilder()
                          .setBatchingSettings(batchingSettings)
                          .setTopic(topicPath())
                          .setPartition(partition)
                          .setServiceClient(newServiceClient(partition));
                  return singlePartitionBuilder.build();
                })
            .setAdminClient(getAdminClient());
    return new WrappingPublisher(publisherSettings.build().instantiate(), messageTransformer);
  }
}
