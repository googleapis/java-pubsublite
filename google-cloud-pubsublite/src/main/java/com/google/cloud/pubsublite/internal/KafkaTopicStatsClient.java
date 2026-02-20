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
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.protobuf.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult.ListOffsetsResultInfo;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * A topic stats client implementation for Kafka backend.
 *
 * <p>Pub/Sub Lite has specific RPCs to query topic statistics. Kafka does not support these RPCs
 * directly, so this implementation provides helper methods that calculate stats based on Kafka
 * primitives:
 *
 * <ul>
 *   <li>{@link #getEarliestOffset}: The oldest available message offset in the partition
 *   <li>{@link #getLatestOffset}: The newest message offset in the partition (head cursor)
 *   <li>{@link #computeBacklogBytes}: Approximate backlog calculated by comparing consumer offset
 *       against latest offset and estimating based on average message size
 *   <li>{@link #computeMessageStats}: Approximate message stats between two offsets
 * </ul>
 */
public class KafkaTopicStatsClient implements TopicStatsClient {
  private static final Logger log = Logger.getLogger(KafkaTopicStatsClient.class.getName());

  // Default average message size estimate (in bytes) when we can't calculate it
  private static final long DEFAULT_AVG_MESSAGE_SIZE = 1024; // 1KB

  private final CloudRegion region;
  private final AdminClient kafkaAdmin;
  private final AtomicBoolean isShutdown = new AtomicBoolean(false);
  private final AtomicBoolean isTerminated = new AtomicBoolean(false);

  /**
   * Creates a new KafkaTopicStatsClient.
   *
   * @param region The cloud region for this client.
   * @param kafkaProperties Kafka connection properties (must include bootstrap.servers).
   */
  public KafkaTopicStatsClient(CloudRegion region, Map<String, Object> kafkaProperties) {
    this.region = region;
    Properties props = new Properties();
    props.putAll(kafkaProperties);
    this.kafkaAdmin = AdminClient.create(props);
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  /**
   * Gets the earliest (oldest) available offset for a partition.
   *
   * <p>This is the offset of the oldest message still available in the partition (messages before
   * this have been deleted due to retention policies).
   *
   * @param topicName The Kafka topic name.
   * @param partition The partition to query.
   * @return A future containing the earliest offset.
   */
  public ApiFuture<Offset> getEarliestOffset(String topicName, Partition partition) {
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    request.put(tp, OffsetSpec.earliest());

    try {
      ListOffsetsResult result = kafkaAdmin.listOffsets(request);
      ListOffsetsResultInfo info = result.partitionResult(tp).get();
      return ApiFutures.immediateFuture(Offset.of(info.offset()));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Interrupted while getting earliest offset", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to get earliest offset", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to get earliest offset: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  /**
   * Gets the latest (newest) offset for a partition.
   *
   * <p>This is the offset that will be assigned to the next message published to the partition.
   *
   * @param topicName The Kafka topic name.
   * @param partition The partition to query.
   * @return A future containing the latest offset.
   */
  public ApiFuture<Offset> getLatestOffset(String topicName, Partition partition) {
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    request.put(tp, OffsetSpec.latest());

    try {
      ListOffsetsResult result = kafkaAdmin.listOffsets(request);
      ListOffsetsResultInfo info = result.partitionResult(tp).get();
      return ApiFutures.immediateFuture(Offset.of(info.offset()));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Interrupted while getting latest offset", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to get latest offset", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to get latest offset: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  /**
   * Gets the offset for a specific timestamp.
   *
   * <p>Returns the earliest offset whose timestamp is greater than or equal to the given timestamp.
   *
   * @param topicName The Kafka topic name.
   * @param partition The partition to query.
   * @param timestampMs The target timestamp in milliseconds since epoch.
   * @return A future containing the offset, or empty if no message exists at or after the
   *     timestamp.
   */
  public ApiFuture<Optional<Offset>> getOffsetForTimestamp(
      String topicName, Partition partition, long timestampMs) {
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    request.put(tp, OffsetSpec.forTimestamp(timestampMs));

    try {
      ListOffsetsResult result = kafkaAdmin.listOffsets(request);
      ListOffsetsResultInfo info = result.partitionResult(tp).get();

      if (info.offset() >= 0) {
        return ApiFutures.immediateFuture(Optional.of(Offset.of(info.offset())));
      } else {
        return ApiFutures.immediateFuture(Optional.empty());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Interrupted while getting offset for timestamp", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to get offset for timestamp", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to get offset for timestamp: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  /**
   * Computes the approximate backlog in bytes for a consumer group on a partition.
   *
   * <p>The backlog is calculated by:
   *
   * <ol>
   *   <li>Getting the consumer's committed offset
   *   <li>Getting the latest offset (log end offset)
   *   <li>Calculating the difference (number of unconsumed messages)
   *   <li>Multiplying by the estimated average message size
   * </ol>
   *
   * <p>Note: This is an approximation. For exact values, Kafka would need to sum the actual sizes
   * of all unconsumed messages, which is not efficiently supported.
   *
   * @param topicName The Kafka topic name.
   * @param partition The partition to query.
   * @param subscriptionPath The subscription path (used to derive consumer group ID).
   * @param estimatedAvgMessageSize The estimated average message size in bytes (use 0 for default).
   * @return A future containing the BacklogInfo with message count and byte estimate.
   */
  public ApiFuture<BacklogInfo> computeBacklogBytes(
      String topicName,
      Partition partition,
      SubscriptionPath subscriptionPath,
      long estimatedAvgMessageSize) {
    String groupId = subscriptionPath.toString().replace('/', '-');
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());

    try {
      // Get committed offset for the consumer group
      Map<TopicPartition, OffsetAndMetadata> committedOffsets =
          kafkaAdmin.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();

      // Get latest offset
      Map<TopicPartition, OffsetSpec> latestRequest = new HashMap<>();
      latestRequest.put(tp, OffsetSpec.latest());
      ListOffsetsResultInfo latestInfo =
          kafkaAdmin.listOffsets(latestRequest).partitionResult(tp).get();

      long latestOffset = latestInfo.offset();
      long committedOffset = 0;

      OffsetAndMetadata oam = committedOffsets.get(tp);
      if (oam != null) {
        committedOffset = oam.offset();
      } else {
        // No committed offset - use earliest offset as the starting point
        Map<TopicPartition, OffsetSpec> earliestRequest = new HashMap<>();
        earliestRequest.put(tp, OffsetSpec.earliest());
        ListOffsetsResultInfo earliestInfo =
            kafkaAdmin.listOffsets(earliestRequest).partitionResult(tp).get();
        committedOffset = earliestInfo.offset();
      }

      long messageCount = Math.max(0, latestOffset - committedOffset);
      long avgSize =
          estimatedAvgMessageSize > 0 ? estimatedAvgMessageSize : DEFAULT_AVG_MESSAGE_SIZE;
      long estimatedBytes = messageCount * avgSize;

      return ApiFutures.immediateFuture(new BacklogInfo(messageCount, estimatedBytes));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException("Interrupted while computing backlog", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to compute backlog", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to compute backlog: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  /** Container for backlog information. */
  public static class BacklogInfo {
    private final long messageCount;
    private final long estimatedBytes;

    public BacklogInfo(long messageCount, long estimatedBytes) {
      this.messageCount = messageCount;
      this.estimatedBytes = estimatedBytes;
    }

    /** The number of unconsumed messages. */
    public long getMessageCount() {
      return messageCount;
    }

    /** The estimated size in bytes of unconsumed messages. */
    public long getEstimatedBytes() {
      return estimatedBytes;
    }

    @Override
    public String toString() {
      return String.format(
          "BacklogInfo{messages=%d, estimatedBytes=%d}", messageCount, estimatedBytes);
    }
  }

  // TopicStatsClient Interface Implementation

  @Override
  public ApiFuture<ComputeMessageStatsResponse> computeMessageStats(
      TopicPath path, Partition partition, Offset start, Offset end) {
    // Kafka doesn't provide detailed message stats like PSL does.
    // We can only provide an approximation based on offset difference.
    long messageCount = Math.max(0, end.value() - start.value());

    // Approximate byte count using default message size
    long estimatedBytes = messageCount * DEFAULT_AVG_MESSAGE_SIZE;

    // For minimum publish time, we'd need to fetch actual messages which is expensive.
    // Return a response with what we can calculate.
    return ApiFutures.immediateFuture(
        ComputeMessageStatsResponse.newBuilder()
            .setMessageCount(messageCount)
            .setMessageBytes(estimatedBytes)
            // We cannot efficiently compute min publish time without reading messages
            .build());
  }

  @Override
  public ApiFuture<Cursor> computeHeadCursor(TopicPath path, Partition partition) {
    String topicName = path.name().value();
    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    request.put(tp, OffsetSpec.latest());

    try {
      ListOffsetsResult result = kafkaAdmin.listOffsets(request);
      ListOffsetsResultInfo info = result.partitionResult(tp).get();
      return ApiFutures.immediateFuture(Cursor.newBuilder().setOffset(info.offset()).build());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Interrupted while computing head cursor", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to compute head cursor", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to compute head cursor: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  @Override
  public ApiFuture<Optional<Cursor>> computeCursorForPublishTime(
      TopicPath path, Partition partition, Timestamp publishTime) {
    String topicName = path.name().value();
    long timestampMs = publishTime.getSeconds() * 1000 + publishTime.getNanos() / 1_000_000;

    TopicPartition tp = new TopicPartition(topicName, (int) partition.value());
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    request.put(tp, OffsetSpec.forTimestamp(timestampMs));

    try {
      ListOffsetsResult result = kafkaAdmin.listOffsets(request);
      ListOffsetsResultInfo info = result.partitionResult(tp).get();

      if (info.offset() >= 0) {
        return ApiFutures.immediateFuture(
            Optional.of(Cursor.newBuilder().setOffset(info.offset()).build()));
      } else {
        return ApiFutures.immediateFuture(Optional.empty());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Interrupted while computing cursor for publish time", StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      log.log(Level.WARNING, "Failed to compute cursor for publish time", e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed to compute cursor for publish time: " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    }
  }

  @Override
  public ApiFuture<Optional<Cursor>> computeCursorForEventTime(
      TopicPath path, Partition partition, Timestamp eventTime) {
    // Kafka doesn't have a concept of event time in the same way as PSL.
    // We can only use the message timestamp, which corresponds to publish time.
    // For event time queries, we fall back to publish time behavior.
    log.warning(
        "Kafka does not support event time queries. "
            + "Falling back to publish time behavior for cursor computation.");
    return computeCursorForPublishTime(path, partition, eventTime);
  }

  // Lifecycle

  @Override
  public void close() {
    shutdown();
  }

  @Override
  public void shutdown() {
    if (isShutdown.compareAndSet(false, true)) {
      kafkaAdmin.close();
      isTerminated.set(true);
    }
  }

  @Override
  public boolean isShutdown() {
    return isShutdown.get();
  }

  @Override
  public boolean isTerminated() {
    return isTerminated.get();
  }

  @Override
  public void shutdownNow() {
    shutdown();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return isTerminated.get();
  }
}
