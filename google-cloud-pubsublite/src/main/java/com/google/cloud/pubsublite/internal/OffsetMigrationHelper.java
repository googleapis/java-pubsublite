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
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.protobuf.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orchestrates the timestamp-based offset migration from Pub/Sub Lite to Kafka (GMK).
 *
 * <p>The migration follows a 4-step process for each partition:
 *
 * <ol>
 *   <li>Read the PSL committed offset for the partition
 *   <li>Look up the publish_time corresponding to that offset
 *   <li>Find the Kafka offset whose timestamp is >= that publish_time
 *   <li>Reset the Kafka consumer group offset to the resolved position
 * </ol>
 */
public final class OffsetMigrationHelper {
  private static final Logger log = Logger.getLogger(OffsetMigrationHelper.class.getName());

  private OffsetMigrationHelper() {}

  /** Result of migrating a single partition's offset. */
  public static class MigrationResult {
    private final Status status;
    private final long pslOffset;
    private final long kafkaOffset;
    private final String message;

    private MigrationResult(Status status, long pslOffset, long kafkaOffset, String message) {
      this.status = status;
      this.pslOffset = pslOffset;
      this.kafkaOffset = kafkaOffset;
      this.message = message;
    }

    public static MigrationResult success(long pslOffset, long kafkaOffset) {
      return new MigrationResult(
          Status.SUCCESS,
          pslOffset,
          kafkaOffset,
          "Migrated PSL offset " + pslOffset + " -> Kafka offset " + kafkaOffset);
    }

    public static MigrationResult noCommittedOffset(long kafkaOffset) {
      return new MigrationResult(
          Status.NO_PSL_OFFSET,
          0,
          kafkaOffset,
          "No PSL committed offset found; reset Kafka to earliest offset " + kafkaOffset);
    }

    public static MigrationResult noKafkaOffset(long pslOffset) {
      return new MigrationResult(
          Status.NO_KAFKA_OFFSET,
          pslOffset,
          -1,
          "No Kafka offset found for PSL offset " + pslOffset + " timestamp");
    }

    public static MigrationResult error(long pslOffset, String errorMessage) {
      return new MigrationResult(Status.ERROR, pslOffset, -1, errorMessage);
    }

    public Status getStatus() {
      return status;
    }

    public long getPslOffset() {
      return pslOffset;
    }

    public long getKafkaOffset() {
      return kafkaOffset;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public String toString() {
      return String.format(
          "MigrationResult{status=%s, pslOffset=%d, kafkaOffset=%d, message='%s'}",
          status, pslOffset, kafkaOffset, message);
    }

    public enum Status {
      SUCCESS,
      NO_PSL_OFFSET,
      NO_KAFKA_OFFSET,
      ERROR
    }
  }

  /**
   * Migrates consumer offsets from Pub/Sub Lite to Kafka for the given partitions.
   *
   * <p>For each partition, this method:
   *
   * <ol>
   *   <li>Reads the committed offset from the PSL cursor client
   *   <li>Looks up the publish_time for that offset via the PSL topic stats client
   *   <li>Finds the corresponding Kafka offset via the Kafka topic stats client
   *   <li>Resets the Kafka consumer group offset via the Kafka cursor client
   * </ol>
   *
   * @param pslCursorClient Client for reading PSL committed offsets.
   * @param pslTopicStatsClient Client for looking up PSL publish times.
   * @param kafkaTopicStatsClient Client for finding Kafka offsets by timestamp.
   * @param kafkaCursorClient Client for resetting Kafka consumer group offsets.
   * @param pslTopicPath The PSL topic path (used for publish time lookups).
   * @param pslSubscriptionPath The PSL subscription path to read offsets from.
   * @param kafkaSubscriptionPath The Kafka subscription path (consumer group) to reset.
   * @param kafkaTopicName The Kafka topic name to reset offsets for.
   * @param partitions The partitions to migrate.
   * @return A future containing a map of partition to migration result.
   */
  public static ApiFuture<Map<Partition, MigrationResult>> migrateOffsets(
      CursorClient pslCursorClient,
      TopicStatsClient pslTopicStatsClient,
      KafkaTopicStatsClient kafkaTopicStatsClient,
      KafkaCursorClient kafkaCursorClient,
      TopicPath pslTopicPath,
      SubscriptionPath pslSubscriptionPath,
      SubscriptionPath kafkaSubscriptionPath,
      String kafkaTopicName,
      Iterable<Partition> partitions) {

    Map<Partition, MigrationResult> results = new HashMap<>();

    try {
      // Step 1: Read all PSL committed offsets
      Map<Partition, Offset> pslOffsets =
          pslCursorClient.listPartitionCursors(pslSubscriptionPath).get();
      log.info("Read PSL committed offsets: " + pslOffsets);

      Map<Partition, Offset> kafkaOffsetsToReset = new HashMap<>();

      for (Partition partition : partitions) {
        try {
          Offset pslOffset = pslOffsets.get(partition);

          if (pslOffset == null || pslOffset.value() == 0) {
            // No PSL committed offset -- reset Kafka to earliest
            log.info(
                "Partition " + partition.value() + ": No PSL offset, using earliest Kafka offset");
            Offset earliestKafka =
                kafkaTopicStatsClient.getEarliestOffset(kafkaTopicName, partition).get();
            kafkaOffsetsToReset.put(partition, earliestKafka);
            results.put(partition, MigrationResult.noCommittedOffset(earliestKafka.value()));
            continue;
          }

          // Step 2: Look up PSL publish_time for this offset via computeMessageStats.
          // We query stats for a single-message range ending at the committed offset.
          // The min_publish_time in the response gives us the timestamp to use.
          Offset rangeStart = Offset.of(Math.max(0, pslOffset.value() - 1));
          ComputeMessageStatsResponse stats =
              pslTopicStatsClient
                  .computeMessageStats(pslTopicPath, partition, rangeStart, pslOffset)
                  .get();

          Timestamp publishTime = stats.getMinimumPublishTime();
          if (publishTime.equals(Timestamp.getDefaultInstance())) {
            log.warning(
                "Partition "
                    + partition.value()
                    + ": Could not determine publish time for PSL offset "
                    + pslOffset.value()
                    + ", using earliest Kafka offset as fallback");
            Offset earliestKafka =
                kafkaTopicStatsClient.getEarliestOffset(kafkaTopicName, partition).get();
            kafkaOffsetsToReset.put(partition, earliestKafka);
            results.put(partition, MigrationResult.noCommittedOffset(earliestKafka.value()));
            continue;
          }

          long timestampMs = publishTime.getSeconds() * 1000 + publishTime.getNanos() / 1_000_000;
          log.info(
              "Partition "
                  + partition.value()
                  + ": PSL offset "
                  + pslOffset.value()
                  + " -> publish_time "
                  + timestampMs
                  + "ms");

          // Step 3: Find Kafka offset >= publish_time
          Optional<Offset> kafkaOffset =
              kafkaTopicStatsClient
                  .getOffsetForTimestamp(kafkaTopicName, partition, timestampMs)
                  .get();

          if (kafkaOffset.isPresent()) {
            kafkaOffsetsToReset.put(partition, kafkaOffset.get());
            results.put(
                partition, MigrationResult.success(pslOffset.value(), kafkaOffset.get().value()));
            log.info(
                "Partition " + partition.value() + ": Kafka offset = " + kafkaOffset.get().value());
          } else {
            // No Kafka offset found for this timestamp -- use latest
            Offset latestKafka =
                kafkaTopicStatsClient.getLatestOffset(kafkaTopicName, partition).get();
            kafkaOffsetsToReset.put(partition, latestKafka);
            results.put(partition, MigrationResult.noKafkaOffset(pslOffset.value()));
            log.warning(
                "Partition "
                    + partition.value()
                    + ": No Kafka offset for timestamp, using latest: "
                    + latestKafka.value());
          }

        } catch (Exception e) {
          log.log(Level.WARNING, "Failed to migrate partition " + partition.value(), e);
          results.put(partition, MigrationResult.error(0, e.getMessage()));
        }
      }

      // Step 4: Reset all Kafka consumer group offsets in one batch
      if (!kafkaOffsetsToReset.isEmpty()) {
        log.info("Resetting Kafka offsets: " + kafkaOffsetsToReset);
        kafkaCursorClient
            .resetOffsets(kafkaSubscriptionPath, kafkaTopicName, kafkaOffsetsToReset)
            .get();
        log.info("Kafka offsets reset successfully");
      }

      return ApiFutures.immediateFuture(results);

    } catch (Exception e) {
      log.log(Level.SEVERE, "Offset migration failed", e);
      return ApiFutures.immediateFailedFuture(
          new RuntimeException("Offset migration failed: " + e.getMessage(), e));
    }
  }
}
