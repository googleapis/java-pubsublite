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

package com.google.cloud.pubsublite.internal;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SeekTarget;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.FieldMask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.GroupIdNotFoundException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;

/**
 * An AdminClient implementation that wraps Kafka's AdminClient.
 *
 * <p>This maps Pub/Sub Lite admin operations to Kafka admin operations: - Topics:
 * Create/Delete/List/Get mapped to Kafka topic operations - Subscriptions: Mapped to Kafka consumer
 * groups - Reservations: No-ops (PSL-specific feature that doesn't exist in Kafka)
 */
public class KafkaAdminClient implements AdminClient {
  private static final Logger log = Logger.getLogger(KafkaAdminClient.class.getName());

  // Retry settings for describeTopics to handle metadata propagation delays after topic creation.
  private static final int MAX_DESCRIBE_RETRIES = 5;
  private static final long DESCRIBE_RETRY_DELAY_MS = 2000;
  private static final ScheduledExecutorService RETRY_EXECUTOR =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "kafka-admin-retry");
            t.setDaemon(true);
            return t;
          });

  private final CloudRegion region;
  private final org.apache.kafka.clients.admin.AdminClient kafkaAdmin;
  private final int defaultPartitions;
  private final short defaultReplicationFactor;
  private final KafkaAdminLifecycle lifecycle;

  /**
   * Creates a new KafkaAdminClient.
   *
   * @param region The cloud region for this client.
   * @param kafkaProperties Kafka connection properties (must include bootstrap.servers).
   * @param defaultPartitions Default number of partitions for new topics.
   * @param defaultReplicationFactor Default replication factor for new topics.
   */
  public KafkaAdminClient(
      CloudRegion region,
      Map<String, Object> kafkaProperties,
      int defaultPartitions,
      short defaultReplicationFactor) {
    this.region = region;
    this.defaultPartitions = defaultPartitions;
    this.defaultReplicationFactor = defaultReplicationFactor;

    Properties props = new Properties();
    props.putAll(kafkaProperties);
    this.kafkaAdmin = org.apache.kafka.clients.admin.AdminClient.create(props);
    this.lifecycle = new KafkaAdminLifecycle(this.kafkaAdmin);
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  // Topic Operations

  @Override
  public ApiFuture<Topic> createTopic(Topic topic) {
    String topicName = extractTopicName(topic.getName());
    int partitions =
        topic.getPartitionConfig().getCount() > 0
            ? (int) topic.getPartitionConfig().getCount()
            : defaultPartitions;

    NewTopic newTopic = new NewTopic(topicName, partitions, defaultReplicationFactor);

    return toApiFuture(
        kafkaAdmin.createTopics(Collections.singleton(newTopic)).all(),
        v -> topic,
        e -> {
          if (e instanceof TopicExistsException) {
            throw new CheckedApiException(
                    "Topic already exists: " + topicName, StatusCode.Code.ALREADY_EXISTS)
                .underlying;
          }
          throw new RuntimeException("Failed to create topic: " + topicName, e);
        });
  }

  @Override
  public ApiFuture<Topic> getTopic(TopicPath path) {
    String topicName = path.name().value();
    return describeTopicWithRetry(topicName, desc -> buildTopic(path, desc));
  }

  @Override
  public ApiFuture<Long> getTopicPartitionCount(TopicPath path) {
    String topicName = path.name().value();
    return describeTopicWithRetry(topicName, desc -> (long) desc.partitions().size());
  }

  /**
   * Describes a topic with retry logic to handle transient UnknownTopicOrPartitionException. After
   * topic creation, Kafka brokers may take a short time to propagate metadata, causing
   * describeTopics to fail transiently.
   */
  private <R> ApiFuture<R> describeTopicWithRetry(
      String topicName, java.util.function.Function<TopicDescription, R> mapper) {
    com.google.api.core.SettableApiFuture<R> resultFuture =
        com.google.api.core.SettableApiFuture.create();
    describeTopicAttempt(topicName, mapper, resultFuture, MAX_DESCRIBE_RETRIES);
    return resultFuture;
  }

  private <R> void describeTopicAttempt(
      String topicName,
      java.util.function.Function<TopicDescription, R> mapper,
      com.google.api.core.SettableApiFuture<R> resultFuture,
      int retriesRemaining) {
    KafkaFuture<TopicDescription> kafkaFuture =
        kafkaAdmin
            .describeTopics(Collections.singleton(topicName))
            .topicNameValues()
            .get(topicName);

    new KafkaFutureAdapter<>(kafkaFuture)
        .toApiFuture()
        .addListener(
            () -> {
              try {
                TopicDescription desc = new KafkaFutureAdapter<>(kafkaFuture).toApiFuture().get();
                resultFuture.set(mapper.apply(desc));
              } catch (Exception e) {
                Throwable cause = unwrapException(e);
                if (cause instanceof UnknownTopicOrPartitionException && retriesRemaining > 0) {
                  log.info(
                      "Topic '"
                          + topicName
                          + "' not yet available, retrying ("
                          + retriesRemaining
                          + " retries left)...");
                  RETRY_EXECUTOR.schedule(
                      () ->
                          describeTopicAttempt(
                              topicName, mapper, resultFuture, retriesRemaining - 1),
                      DESCRIBE_RETRY_DELAY_MS,
                      TimeUnit.MILLISECONDS);
                } else if (cause instanceof UnknownTopicOrPartitionException) {
                  resultFuture.setException(
                      new CheckedApiException(
                              "Topic not found: " + topicName, StatusCode.Code.NOT_FOUND)
                          .underlying);
                } else {
                  resultFuture.setException(
                      new RuntimeException("Failed to describe topic: " + topicName, cause));
                }
              }
            },
            MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<List<Topic>> listTopics(LocationPath path) {
    return toApiFuture(
        kafkaAdmin.listTopics().names(),
        topicNames -> {
          List<Topic> topics = new ArrayList<>();
          for (String name : topicNames) {
            // Skip internal Kafka topics
            if (!name.startsWith("__")) {
              TopicPath topicPath =
                  TopicPath.newBuilder()
                      .setProject(path.project())
                      .setLocation(path.location())
                      .setName(com.google.cloud.pubsublite.TopicName.of(name))
                      .build();
              topics.add(Topic.newBuilder().setName(topicPath.toString()).build());
            }
          }
          return topics;
        },
        e -> {
          throw new RuntimeException("Failed to list topics", e);
        });
  }

  @Override
  public ApiFuture<Topic> updateTopic(Topic topic, FieldMask mask) {
    // Kafka doesn't support most topic updates without recreation
    // For now, just return the topic as-is (no-op for updates)
    log.warning(
        "Topic updates are not fully supported in Kafka backend. "
            + "Some fields may not be updated: "
            + mask);
    return ApiFutures.immediateFuture(topic);
  }

  @Override
  public ApiFuture<Void> deleteTopic(TopicPath path) {
    String topicName = path.name().value();

    return toApiFuture(
        kafkaAdmin.deleteTopics(Collections.singleton(topicName)).all(),
        v -> null,
        e -> {
          if (e instanceof UnknownTopicOrPartitionException) {
            throw new CheckedApiException(
                    "Topic not found: " + topicName, StatusCode.Code.NOT_FOUND)
                .underlying;
          }
          throw new RuntimeException("Failed to delete topic: " + topicName, e);
        });
  }

  @Override
  public ApiFuture<List<SubscriptionPath>> listTopicSubscriptions(TopicPath path) {
    // In Kafka, "subscriptions" are consumer groups
    // This lists consumer groups that have committed offsets for this topic
    String topicName = path.name().value();

    return toApiFuture(
        kafkaAdmin.listConsumerGroups().all(),
        groups -> {
          List<SubscriptionPath> subscriptions = new ArrayList<>();
          for (org.apache.kafka.clients.admin.ConsumerGroupListing group : groups) {
            // Create a subscription path from the consumer group
            SubscriptionPath subPath =
                SubscriptionPath.newBuilder()
                    .setProject(path.project())
                    .setLocation(path.location())
                    .setName(com.google.cloud.pubsublite.SubscriptionName.of(group.groupId()))
                    .build();
            subscriptions.add(subPath);
          }
          return subscriptions;
        },
        e -> {
          throw new RuntimeException("Failed to list subscriptions for topic: " + topicName, e);
        });
  }

  // Subscription Operations
  // Subscriptions map to Kafka consumer groups

  @Override
  public ApiFuture<Subscription> createSubscription(
      Subscription subscription, BacklogLocation startingOffset) {
    // In Kafka, consumer groups are created implicitly when a consumer joins
    // We just validate the topic exists and return the subscription
    String topicName = extractTopicName(subscription.getTopic());

    return toApiFuture(
        kafkaAdmin.describeTopics(Collections.singleton(topicName)).allTopicNames(),
        descriptions -> {
          if (!descriptions.containsKey(topicName)) {
            throw new CheckedApiException(
                    "Topic not found: " + topicName, StatusCode.Code.NOT_FOUND)
                .underlying;
          }
          return subscription;
        },
        e -> {
          if (e instanceof UnknownTopicOrPartitionException) {
            throw new CheckedApiException(
                    "Topic not found: " + topicName, StatusCode.Code.NOT_FOUND)
                .underlying;
          }
          throw new RuntimeException("Failed to create subscription", e);
        });
  }

  @Override
  public ApiFuture<Subscription> createSubscription(Subscription subscription, SeekTarget target) {
    // Seek target is not directly supported in Kafka consumer group creation
    // The seek would need to be done when the consumer connects
    return createSubscription(subscription, BacklogLocation.END);
  }

  @Override
  public ApiFuture<Subscription> getSubscription(SubscriptionPath path) {
    String groupId = path.name().value();

    return toApiFuture(
        kafkaAdmin.describeConsumerGroups(Collections.singleton(groupId)).all(),
        descriptions -> {
          if (!descriptions.containsKey(groupId)) {
            throw new CheckedApiException(
                    "Subscription (consumer group) not found: " + groupId,
                    StatusCode.Code.NOT_FOUND)
                .underlying;
          }
          return Subscription.newBuilder().setName(path.toString()).build();
        },
        e -> {
          throw new RuntimeException("Failed to get subscription: " + groupId, e);
        });
  }

  @Override
  public ApiFuture<List<Subscription>> listSubscriptions(LocationPath path) {
    return toApiFuture(
        kafkaAdmin.listConsumerGroups().all(),
        groups -> {
          List<Subscription> subscriptions = new ArrayList<>();
          for (org.apache.kafka.clients.admin.ConsumerGroupListing group : groups) {
            SubscriptionPath subPath =
                SubscriptionPath.newBuilder()
                    .setProject(path.project())
                    .setLocation(path.location())
                    .setName(com.google.cloud.pubsublite.SubscriptionName.of(group.groupId()))
                    .build();
            subscriptions.add(Subscription.newBuilder().setName(subPath.toString()).build());
          }
          return subscriptions;
        },
        e -> {
          throw new RuntimeException("Failed to list subscriptions", e);
        });
  }

  @Override
  public ApiFuture<Subscription> updateSubscription(Subscription subscription, FieldMask mask) {
    // Consumer group configuration updates are limited in Kafka
    log.warning("Subscription updates are limited in Kafka backend");
    return ApiFutures.immediateFuture(subscription);
  }

  @Override
  public OperationFuture<SeekSubscriptionResponse, OperationMetadata> seekSubscription(
      SubscriptionPath path, SeekTarget target) {
    // Kafka consumer group offset seeking is done via consumer API, not admin API
    // This would require resetting consumer group offsets
    throw new UnsupportedOperationException(
        "Seek subscription is not directly supported in Kafka backend. "
            + "Use consumer API to seek to specific offsets.");
  }

  @Override
  public ApiFuture<Void> deleteSubscription(SubscriptionPath path) {
    String groupId = path.name().value();

    // Consumer groups in Kafka are created implicitly when a consumer joins.
    // If no consumer has joined yet, the group won't exist, so treat GroupIdNotFoundException
    // as success rather than an error.
    com.google.api.core.SettableApiFuture<Void> resultFuture =
        com.google.api.core.SettableApiFuture.create();

    KafkaFuture<Void> kafkaFuture =
        kafkaAdmin.deleteConsumerGroups(Collections.singleton(groupId)).all();

    kafkaFuture.whenComplete(
        (result, error) -> {
          if (error == null) {
            resultFuture.set(null);
          } else {
            Throwable cause = unwrapException(error);
            if (cause instanceof GroupIdNotFoundException) {
              resultFuture.set(null);
            } else {
              resultFuture.setException(
                  new RuntimeException(
                      "Failed to delete subscription (consumer group): " + groupId, cause));
            }
          }
        });

    return resultFuture;
  }

  // Reservation Operations
  // Reservations are PSL-specific and don't exist in Kafka - all are no-ops

  @Override
  public ApiFuture<Reservation> createReservation(Reservation reservation) {
    log.info("Reservations are not supported in Kafka backend. Operation is a no-op.");
    return ApiFutures.immediateFuture(reservation);
  }

  @Override
  public ApiFuture<Reservation> getReservation(ReservationPath path) {
    log.info("Reservations are not supported in Kafka backend. Returning empty reservation.");
    return ApiFutures.immediateFuture(Reservation.newBuilder().setName(path.toString()).build());
  }

  @Override
  public ApiFuture<List<Reservation>> listReservations(LocationPath path) {
    log.info("Reservations are not supported in Kafka backend. Returning empty list.");
    return ApiFutures.immediateFuture(Collections.emptyList());
  }

  @Override
  public ApiFuture<Reservation> updateReservation(Reservation reservation, FieldMask mask) {
    log.info("Reservations are not supported in Kafka backend. Operation is a no-op.");
    return ApiFutures.immediateFuture(reservation);
  }

  @Override
  public ApiFuture<Void> deleteReservation(ReservationPath path) {
    log.info("Reservations are not supported in Kafka backend. Operation is a no-op.");
    return ApiFutures.immediateFuture(null);
  }

  @Override
  public ApiFuture<List<TopicPath>> listReservationTopics(ReservationPath path) {
    log.info("Reservations are not supported in Kafka backend. Returning empty list.");
    return ApiFutures.immediateFuture(Collections.emptyList());
  }

  // Lifecycle

  @Override
  public void close() {
    lifecycle.close();
  }

  @Override
  public void shutdown() {
    lifecycle.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return lifecycle.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return lifecycle.isTerminated();
  }

  @Override
  public void shutdownNow() {
    lifecycle.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return lifecycle.awaitTermination(duration, unit);
  }

  // Helper Methods

  private String extractTopicName(String fullPath) {
    int lastSlash = fullPath.lastIndexOf('/');
    return lastSlash >= 0 ? fullPath.substring(lastSlash + 1) : fullPath;
  }

  private Topic buildTopic(TopicPath path, TopicDescription desc) {
    return Topic.newBuilder()
        .setName(path.toString())
        .setPartitionConfig(
            com.google.cloud.pubsublite.proto.Topic.PartitionConfig.newBuilder()
                .setCount(desc.partitions().size())
                .build())
        .build();
  }

  private <T, R> ApiFuture<R> toApiFuture(
      KafkaFuture<T> kafkaFuture,
      java.util.function.Function<T, R> successMapper,
      java.util.function.Function<Throwable, RuntimeException> errorMapper) {
    return ApiFutures.transform(
        ApiFutures.catching(
            new KafkaFutureAdapter<>(kafkaFuture).toApiFuture(),
            Throwable.class,
            t -> {
              throw errorMapper.apply(unwrapException(t));
            },
            MoreExecutors.directExecutor()),
        successMapper::apply,
        MoreExecutors.directExecutor());
  }

  /**
   * Unwraps CompletionException and ExecutionException wrappers to get the root cause. Kafka's
   * KafkaFuture internally uses CompletableFuture, which wraps exceptions in CompletionException
   * when they propagate through thenApply/allOf chains.
   */
  private static Throwable unwrapException(Throwable t) {
    Throwable cause = t;
    while ((cause instanceof CompletionException || cause instanceof ExecutionException)
        && cause.getCause() != null) {
      cause = cause.getCause();
    }
    return cause;
  }

  /** Adapter to convert KafkaFuture to ApiFuture. */
  private static class KafkaFutureAdapter<T> {
    private final KafkaFuture<T> kafkaFuture;

    KafkaFutureAdapter(KafkaFuture<T> kafkaFuture) {
      this.kafkaFuture = kafkaFuture;
    }

    ApiFuture<T> toApiFuture() {
      com.google.api.core.SettableApiFuture<T> apiFuture =
          com.google.api.core.SettableApiFuture.create();

      kafkaFuture.whenComplete(
          (result, error) -> {
            if (error != null) {
              apiFuture.setException(error);
            } else {
              apiFuture.set(result);
            }
          });

      return apiFuture;
    }
  }
}
