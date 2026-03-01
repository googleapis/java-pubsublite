/*
 * Copyright 2026 Google LLC
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
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.getCallContext;

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.KafkaPartitionPublisherFactory;
import com.google.cloud.pubsublite.cloudpubsub.internal.WrappingPublisher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.PartitionCountWatchingPublisherSettings;
import com.google.cloud.pubsublite.internal.wire.PartitionPublisherFactory;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingMetadata;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.internal.wire.UuidBuilder;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import com.google.cloud.pubsublite.v1.PublisherServiceSettings;
import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.CallOptions;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

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
          .setDelayThresholdDuration(Duration.ofMillis(50))
          .build();

  // Required parameters.

  /** The topic path to publish to. */
  public abstract TopicPath topicPath();

  // Optional parameters.
  /** A KeyExtractor for getting the routing key from a message. */
  abstract Optional<KeyExtractor> keyExtractor();

  /** A MessageTransformer for constructing wire messages from Cloud Pub/Sub PubsubMessages. */
  abstract Optional<MessageTransformer<PubsubMessage, Message>> messageTransformer();

  /** Batching settings for this publisher to use. Apply per-partition. */
  public abstract BatchingSettings batchingSettings();

  /**
   * Whether idempotence is enabled, where the server will ensure that unique messages within a
   * single publisher session are stored only once. Default true.
   */
  public abstract boolean enableIdempotence();

  /** Whether request compression is enabled. Default true. */
  public abstract boolean enableCompression();

  /** A provider for credentials. */
  abstract CredentialsProvider credentialsProvider();

  /**
   * A Framework tag for internal metrics. Please set this if integrating with a public framework!
   */
  abstract Framework framework();

  /**
   * A supplier for new PublisherServiceClients. Should return a new client each time. If present,
   * ignores CredentialsProvider.
   */
  abstract Optional<PublisherServiceClient> serviceClient();

  /** The AdminClient to use, if provided. */
  abstract Optional<AdminClient> adminClient();

  // For testing.
  abstract SinglePartitionPublisherBuilder.Builder underlyingBuilder();

  /** The messaging backend to use. Defaults to PUBSUB_LITE for backward compatibility. */
  public abstract MessagingBackend messagingBackend();

  /**
   * Kafka-specific configuration properties. Only used when messagingBackend is MANAGED_KAFKA.
   * Common properties include: - "bootstrap.servers": Kafka broker addresses - "compression.type":
   * Compression algorithm (e.g., "snappy", "gzip") - "max.in.flight.requests.per.connection":
   * Pipelining configuration
   */
  public abstract Optional<Map<String, Object>> kafkaProperties();

  /** Get a new builder for a PublisherSettings. */
  public static Builder newBuilder() {
    return new AutoValue_PublisherSettings.Builder()
        .setFramework(Framework.of("CLOUD_PUBSUB_SHIM"))
        .setCredentialsProvider(
            PublisherServiceSettings.defaultCredentialsProviderBuilder().build())
        .setBatchingSettings(DEFAULT_BATCHING_SETTINGS)
        .setEnableIdempotence(true)
        .setEnableCompression(true)
        .setUnderlyingBuilder(SinglePartitionPublisherBuilder.newBuilder())
        .setMessagingBackend(MessagingBackend.PUBSUB_LITE);
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

    /**
     * Whether idempotence is enabled, where the server will ensure that unique messages within a
     * single publisher session are stored only once. Default true.
     */
    public abstract Builder setEnableIdempotence(boolean enableIdempotence);

    /** Whether request compression is enabled. Default true. */
    public abstract Builder setEnableCompression(boolean enableCompression);

    /** A provider for credentials. */
    public abstract Builder setCredentialsProvider(CredentialsProvider credentialsProvider);

    /**
     * A Framework tag for internal metrics. Please set this if integrating with a public framework!
     */
    public abstract Builder setFramework(Framework framework);

    /** The PublisherServiceClient to use, if provided. */
    public abstract Builder setServiceClient(PublisherServiceClient client);

    /** The AdminClient to use, if provided. */
    public abstract Builder setAdminClient(AdminClient adminClient);

    // For testing.
    @VisibleForTesting
    abstract Builder setUnderlyingBuilder(
        SinglePartitionPublisherBuilder.Builder underlyingBuilder);

    /** Sets the messaging backend. Defaults to PUBSUB_LITE. */
    public abstract Builder setMessagingBackend(MessagingBackend backend);

    /** Sets Kafka-specific properties. Only used when backend is MANAGED_KAFKA. */
    public abstract Builder setKafkaProperties(Map<String, Object> properties);

    public abstract PublisherSettings build();
  }

  private PublisherServiceClient newServiceClient() throws ApiException {
    if (serviceClient().isPresent()) return serviceClient().get();
    try {
      return PublisherServiceClient.create(
          addDefaultSettings(
              topicPath().location().extractRegion(),
              PublisherServiceSettings.newBuilder().setCredentialsProvider(credentialsProvider())));
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  private PartitionPublisherFactory getPartitionPublisherFactory() {
    // Check backend and return appropriate factory
    if (messagingBackend() == MessagingBackend.MANAGED_KAFKA) {
      return new KafkaPartitionPublisherFactory(this);
    }

    // Existing Pub/Sub Lite implementation
    PublisherServiceClient client = newServiceClient();
    ByteString publisherClientId = UuidBuilder.toByteString(UuidBuilder.generate());
    return new PartitionPublisherFactory() {
      @Override
      public com.google.cloud.pubsublite.internal.Publisher<MessageMetadata> newPublisher(
          Partition partition) throws ApiException {
        SinglePartitionPublisherBuilder.Builder singlePartitionBuilder =
            underlyingBuilder()
                .setBatchingSettings(batchingSettings())
                .setTopic(topicPath())
                .setPartition(partition)
                .setStreamFactory(
                    responseStream -> {
                      GrpcCallContext context =
                          getCallContext(
                              PubsubContext.of(framework()),
                              RoutingMetadata.of(topicPath(), partition));
                      if (enableCompression()) {
                        context =
                            context.withCallOptions(CallOptions.DEFAULT.withCompression("gzip"));
                      }
                      return client.publishCallable().splitCall(responseStream, context);
                    });
        if (enableIdempotence()) {
          singlePartitionBuilder.setClientId(publisherClientId);
        }
        return singlePartitionBuilder.build();
      }

      @Override
      public void close() {
        client.close();
      }
    };
  }

  private AdminClient getAdminClient() throws ApiException {
    if (adminClient().isPresent()) return adminClient().get();
    try {
      return AdminClient.create(
          AdminClientSettings.newBuilder()
              .setServiceClient(
                  AdminServiceClient.create(
                      addDefaultSettings(
                          topicPath().location().extractRegion(),
                          AdminServiceSettings.newBuilder()
                              .setCredentialsProvider(credentialsProvider()))))
              .setRegion(topicPath().location().extractRegion())
              .build());
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @SuppressWarnings("CheckReturnValue")
  Publisher instantiate() throws ApiException {
    // For Kafka backend, use simpler publisher that doesn't need partition watching
    if (messagingBackend() == MessagingBackend.MANAGED_KAFKA) {
      return new com.google.cloud.pubsublite.cloudpubsub.internal.KafkaPublisher(this);
    }

    if (batchingSettings().getFlowControlSettings().getMaxOutstandingElementCount() != null
        || batchingSettings().getFlowControlSettings().getMaxOutstandingRequestBytes() != null) {
      throw new CheckedApiException(
              "Pub/Sub Lite does not support flow control settings for publishing.",
              Code.INVALID_ARGUMENT)
          .underlying;
    }
    KeyExtractor keyExtractor = keyExtractor().orElse(KeyExtractor.DEFAULT);
    MessageTransformer<PubsubMessage, Message> messageTransformer =
        messageTransformer()
            .orElseGet(() -> MessageTransforms.fromCpsPublishTransformer(keyExtractor));

    PartitionCountWatchingPublisherSettings.Builder publisherSettings =
        PartitionCountWatchingPublisherSettings.newBuilder()
            .setTopic(topicPath())
            .setPublisherFactory(getPartitionPublisherFactory())
            .setAdminClient(getAdminClient());
    return new WrappingPublisher(publisherSettings.build().instantiate(), messageTransformer);
  }
}
