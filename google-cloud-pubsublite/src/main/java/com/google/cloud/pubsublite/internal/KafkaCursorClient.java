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
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * A cursor client implementation for Kafka backend.
 *
 * <p>In Pub/Sub Lite, cursors are a first-class service with specific RPCs (commit, list, streaming
 * commit). In Kafka, cursor management is handled via consumer group offsets.
 *
 * <p>This implementation provides helper functions that interact with Kafka's consumer group offset
 * management:
 *
 * <ul>
 *   <li>{@link #commitOffset}: Save the position of the consumer for a partition
 *   <li>{@link #readCommittedOffsets}: Retrieve the saved position per partition
 * </ul>
 */
public class KafkaCursorClient implements ApiBackgroundResource {
  private static final Logger log = Logger.getLogger(KafkaCursorClient.class.getName());

  private final CloudRegion region;
  private final KafkaAdminLifecycle lifecycle;

  /**
   * Creates a new KafkaCursorClient.
   *
   * @param region The cloud region for this client.
   * @param kafkaProperties Kafka connection properties (must include bootstrap.servers).
   */
  public KafkaCursorClient(CloudRegion region, Map<String, Object> kafkaProperties) {
    this.region = region;
    Properties props = new Properties();
    props.putAll(kafkaProperties);
    this.lifecycle = new KafkaAdminLifecycle(AdminClient.create(props));
  }

  /** The Google Cloud region this client operates on. */
  public CloudRegion region() {
    return region;
  }

  /**
   * Commits an offset for a specific partition within a consumer group.
   *
   * <p>This maps to Kafka's consumer group offset commit functionality.
   *
   * @param subscriptionPath The subscription path (used to derive consumer group ID).
   * @param topicName The Kafka topic name.
   * @param partition The partition to commit offset for.
   * @param offset The offset to commit (next message to be consumed).
   * @return A future that completes when the offset is committed.
   */
  public ApiFuture<Void> commitOffset(
      SubscriptionPath subscriptionPath, String topicName, Partition partition, Offset offset) {
    String groupId = GroupIdUtils.deriveGroupId(subscriptionPath);
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    offsets.put(tp, new OffsetAndMetadata(offset.value()));

    return KafkaFutureUtils.executeWithHandling(
        () -> {
          lifecycle.adminClient().alterConsumerGroupOffsets(groupId, offsets).all().get();
          return null;
        },
        "committing offset",
        log);
  }

  /**
   * Reads the committed offsets for all partitions of a subscription (consumer group).
   *
   * <p>This maps to Kafka's listConsumerGroupOffsets functionality.
   *
   * @param subscriptionPath The subscription path (used to derive consumer group ID).
   * @param topicName The Kafka topic name.
   * @return A future containing a list of partition cursors with their committed offsets.
   */
  public ApiFuture<List<PartitionCursor>> readCommittedOffsets(
      SubscriptionPath subscriptionPath, String topicName) {
    String groupId = GroupIdUtils.deriveGroupId(subscriptionPath);

    return KafkaFutureUtils.executeWithHandling(
        () -> {
          ListConsumerGroupOffsetsResult result =
              lifecycle.adminClient().listConsumerGroupOffsets(groupId);
          Map<TopicPartition, OffsetAndMetadata> offsets =
              result.partitionsToOffsetAndMetadata().get();

          List<PartitionCursor> cursors = new ArrayList<>();
          for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsets.entrySet()) {
            TopicPartition tp = entry.getKey();
            OffsetAndMetadata oam = entry.getValue();

            // Filter to only the requested topic
            if (tp.topic().equals(topicName) && oam != null) {
              cursors.add(
                  PartitionCursor.newBuilder()
                      .setPartition(tp.partition())
                      .setCursor(Cursor.newBuilder().setOffset(oam.offset()).build())
                      .build());
            }
          }

          return cursors;
        },
        "reading committed offsets",
        log);
  }

  /**
   * Gets the committed offset for a specific partition.
   *
   * @param subscriptionPath The subscription path (used to derive consumer group ID).
   * @param topicName The Kafka topic name.
   * @param partition The partition to get offset for.
   * @return A future containing the cursor with the committed offset, or empty if not committed.
   */
  public ApiFuture<Cursor> getCommittedOffset(
      SubscriptionPath subscriptionPath, String topicName, Partition partition) {
    String groupId = GroupIdUtils.deriveGroupId(subscriptionPath);
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());

    return KafkaFutureUtils.executeWithHandling(
        () -> {
          ListConsumerGroupOffsetsResult result =
              lifecycle.adminClient().listConsumerGroupOffsets(groupId);
          Map<TopicPartition, OffsetAndMetadata> offsets =
              result.partitionsToOffsetAndMetadata().get();

          OffsetAndMetadata oam = offsets.get(tp);
          if (oam != null) {
            return Cursor.newBuilder().setOffset(oam.offset()).build();
          } else {
            // No committed offset, return offset 0
            return Cursor.newBuilder().setOffset(0).build();
          }
        },
        "getting committed offset",
        log);
  }

  /**
   * Resets offsets for a consumer group to a specific position.
   *
   * @param subscriptionPath The subscription path (used to derive consumer group ID).
   * @param topicName The Kafka topic name.
   * @param partitionOffsets Map of partition to target offset.
   * @return A future that completes when offsets are reset.
   */
  public ApiFuture<Void> resetOffsets(
      SubscriptionPath subscriptionPath,
      String topicName,
      Map<Partition, Offset> partitionOffsets) {
    String groupId = GroupIdUtils.deriveGroupId(subscriptionPath);
    Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();

    for (Map.Entry<Partition, Offset> entry : partitionOffsets.entrySet()) {
      TopicPartition tp = new TopicPartition(topicName, (int) entry.getKey().value());
      offsets.put(tp, new OffsetAndMetadata(entry.getValue().value()));
    }

    return KafkaFutureUtils.executeWithHandling(
        () -> {
          lifecycle.adminClient().alterConsumerGroupOffsets(groupId, offsets).all().get();
          return null;
        },
        "resetting offsets",
        log);
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
}
