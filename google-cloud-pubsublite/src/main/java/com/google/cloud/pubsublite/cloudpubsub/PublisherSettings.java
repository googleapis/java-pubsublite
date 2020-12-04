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

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.WrappingPublisher;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingPublisherBuilder;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
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

  /** A supplier for new PublisherServiceClients. Should return a new client each time. */
  abstract Optional<Supplier<PublisherServiceClient>> serviceClientSupplier();

  // For testing.
  abstract SinglePartitionPublisherBuilder.Builder underlyingBuilder();

  // For testing.
  abstract Optional<Integer> numPartitions();

  /** Get a new builder for a PublisherSettings. */
  public static Builder newBuilder() {
    return new AutoValue_PublisherSettings.Builder()
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

    /** A supplier for new PublisherServiceClients. Should return a new client each time. */
    public abstract Builder setServiceClientSupplier(Supplier<PublisherServiceClient> supplier);

    // For testing.
    abstract Builder setUnderlyingBuilder(
        SinglePartitionPublisherBuilder.Builder underlyingBuilder);

    // For testing.
    abstract Builder setNumPartitions(int numPartitions);

    public abstract PublisherSettings build();
  }

  @SuppressWarnings("CheckReturnValue")
  Publisher instantiate() throws ApiException {
    BatchingSettings batchingSettings = batchingSettings().orElse(DEFAULT_BATCHING_SETTINGS);
    KeyExtractor keyExtractor = keyExtractor().orElse(KeyExtractor.DEFAULT);
    MessageTransformer<PubsubMessage, Message> messageTransformer =
        messageTransformer()
            .orElseGet(() -> MessageTransforms.fromCpsPublishTransformer(keyExtractor));

    RoutingPublisherBuilder.Builder wireBuilder =
        RoutingPublisherBuilder.newBuilder()
            .setTopic(topicPath())
            .setPublisherFactory(
                partition -> {
                  SinglePartitionPublisherBuilder.Builder singlePartitionBuilder =
                      underlyingBuilder()
                          .setBatchingSettings(batchingSettings)
                          .setContext(PubsubContext.of(FRAMEWORK))
                          .setTopic(topicPath())
                          .setPartition(partition);
                  serviceClientSupplier()
                      .ifPresent(
                          supplier -> singlePartitionBuilder.setServiceClient(supplier.get()));
                  return singlePartitionBuilder.build();
                });

    numPartitions().ifPresent(wireBuilder::setNumPartitions);

    return new WrappingPublisher(wireBuilder.build(), messageTransformer);
  }
}
