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
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.protobuf.Timestamp;
import java.util.HashMap;
import java.util.List;
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
 *
 * <p>Supports dry-run mode to report resolved offsets without performing the reset, and post-reset
 * validation to confirm that offsets were committed correctly.
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
    private final boolean validated;

    private MigrationResult(
        Status status, long pslOffset, long kafkaOffset, String message, boolean validated) {
      this.status = status;
      this.pslOffset = pslOffset;
      this.kafkaOffset = kafkaOffset;
      this.message = message;
      this.validated = validated;
    }

    private MigrationResult(Status status, long pslOffset, long kafkaOffset, String message) {
      this(status, pslOffset, kafkaOffset, message, false);
    }

    public static MigrationResult success(long pslOffset, long kafkaOffset) {
      return new MigrationResult(
          Status.SUCCESS,
          pslOffset,
          kafkaOffset,
          "Migrated PSL offset " + pslOffset + " -> Kafka offset " + kafkaOffset);
    }

    public static MigrationResult successValidated(long pslOffset, long kafkaOffset) {
      return new MigrationResult(
          Status.SUCCESS,
          pslOffset,
          kafkaOffset,
          "Migrated PSL offset " + pslOffset + " -> Kafka offset " + kafkaOffset + " (validated)",
          true);
    }

    public static MigrationResult dryRun(long pslOffset, long kafkaOffset) {
      return new MigrationResult(
          Status.DRY_RUN,
          pslOffset,
          kafkaOffset,
          "DRY RUN: Would migrate PSL offset " + pslOffset + " -> Kafka offset " + kafkaOffset);
    }

    public static MigrationResult noCommittedOffset(long kafkaOffset) {
      return new MigrationResult(
          Status.NO_PSL_OFFSET,
          0,
          kafkaOffset,
          "No PSL committed offset found; reset Kafka to earliest offset " + kafkaOffset);
    }

    public static MigrationResult noCommittedOffsetDryRun(long kafkaOffset) {
      return new MigrationResult(
          Status.DRY_RUN,
          0,
          kafkaOffset,
          "DRY RUN: No PSL committed offset; would reset Kafka to earliest offset " + kafkaOffset);
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

    public static MigrationResult validationFailed(
        long pslOffset, long expectedKafkaOffset, long actualKafkaOffset) {
      return new MigrationResult(
          Status.VALIDATION_FAILED,
          pslOffset,
          expectedKafkaOffset,
          "Validation failed: expected Kafka offset "
              + expectedKafkaOffset
              + " but found "
              + actualKafkaOffset);
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

    public boolean isValidated() {
      return validated;
    }

    @Override
    public String toString() {
      return String.format(
          "MigrationResult{status=%s, pslOffset=%d, kafkaOffset=%d, validated=%s, message='%s'}",
          status, pslOffset, kafkaOffset, validated, message);
    }

    public enum Status {
      SUCCESS,
      DRY_RUN,
      NO_PSL_OFFSET,
      NO_KAFKA_OFFSET,
      VALIDATION_FAILED,
      ERROR
    }
  }

  /**
   * Migrates consumer offsets from Pub/Sub Lite to Kafka for the given partitions.
   *
   * <p>This is the original entry point that performs the actual migration (not a dry run) without
   * post-reset validation. See {@link #migrateOffsets(CursorClient, TopicStatsClient,
   * KafkaTopicStatsClient, KafkaCursorClient, TopicPath, SubscriptionPath, SubscriptionPath,
   * String, Iterable, boolean, boolean)} for the full-featured version.
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
    return migrateOffsets(
        pslCursorClient,
        pslTopicStatsClient,
        kafkaTopicStatsClient,
        kafkaCursorClient,
        pslTopicPath,
        pslSubscriptionPath,
        kafkaSubscriptionPath,
        kafkaTopicName,
        partitions,
        /* dryRun= */ false,
        /* validate= */ false);
  }

  /**
   * Migrates consumer offsets from Pub/Sub Lite to Kafka for the given partitions, with options for
   * dry-run mode and post-reset validation.
   *
   * <p>For each partition, this method:
   *
   * <ol>
   *   <li>Reads the committed offset from the PSL cursor client
   *   <li>Looks up the publish_time for that offset via the PSL topic stats client
   *   <li>Finds the corresponding Kafka offset via the Kafka topic stats client
   *   <li>If {@code dryRun} is false, resets the Kafka consumer group offset
   *   <li>If {@code validate} is true, reads back committed offsets to confirm the reset
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
   * @param dryRun If true, resolve offsets but do not perform the reset.
   * @param validate If true (and not dryRun), read back committed offsets after reset to confirm.
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
      Iterable<Partition> partitions,
      boolean dryRun,
      boolean validate) {

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
            if (dryRun) {
              results.put(
                  partition, MigrationResult.noCommittedOffsetDryRun(earliestKafka.value()));
            } else {
              results.put(partition, MigrationResult.noCommittedOffset(earliestKafka.value()));
            }
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
            if (dryRun) {
              results.put(
                  partition, MigrationResult.noCommittedOffsetDryRun(earliestKafka.value()));
            } else {
              results.put(partition, MigrationResult.noCommittedOffset(earliestKafka.value()));
            }
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
            if (dryRun) {
              results.put(
                  partition, MigrationResult.dryRun(pslOffset.value(), kafkaOffset.get().value()));
            } else {
              results.put(
                  partition, MigrationResult.success(pslOffset.value(), kafkaOffset.get().value()));
            }
            log.info(
                "Partition "
                    + partition.value()
                    + ": Kafka offset = "
                    + kafkaOffset.get().value()
                    + (dryRun ? " (dry run)" : ""));
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

      if (dryRun) {
        // Dry run: skip the actual reset
        log.info("DRY RUN: Would reset Kafka offsets: " + kafkaOffsetsToReset);
        return ApiFutures.immediateFuture(results);
      }

      // Step 4: Reset all Kafka consumer group offsets in one batch
      if (!kafkaOffsetsToReset.isEmpty()) {
        log.info("Resetting Kafka offsets: " + kafkaOffsetsToReset);
        kafkaCursorClient
            .resetOffsets(kafkaSubscriptionPath, kafkaTopicName, kafkaOffsetsToReset)
            .get();
        log.info("Kafka offsets reset successfully");

        // Step 5 (optional): Post-reset validation
        if (validate) {
          log.info("Validating committed offsets after reset...");
          results =
              validateCommittedOffsets(
                  kafkaCursorClient,
                  kafkaSubscriptionPath,
                  kafkaTopicName,
                  kafkaOffsetsToReset,
                  results);
        }
      }

      return ApiFutures.immediateFuture(results);

    } catch (Exception e) {
      log.log(Level.SEVERE, "Offset migration failed", e);
      return ApiFutures.immediateFailedFuture(
          new RuntimeException("Offset migration failed: " + e.getMessage(), e));
    }
  }

  /**
   * Validates that committed offsets match the expected values after a reset.
   *
   * <p>Reads back the committed offsets from Kafka and compares them against the expected offsets.
   * Updates MigrationResult entries to reflect validation status.
   */
  static Map<Partition, MigrationResult> validateCommittedOffsets(
      KafkaCursorClient kafkaCursorClient,
      SubscriptionPath kafkaSubscriptionPath,
      String kafkaTopicName,
      Map<Partition, Offset> expectedOffsets,
      Map<Partition, MigrationResult> currentResults) {

    Map<Partition, MigrationResult> updatedResults = new HashMap<>(currentResults);

    try {
      List<PartitionCursor> committedCursors =
          kafkaCursorClient.readCommittedOffsets(kafkaSubscriptionPath, kafkaTopicName).get();

      Map<Long, Long> committedByPartition = new HashMap<>();
      for (PartitionCursor pc : committedCursors) {
        committedByPartition.put((long) pc.getPartition(), pc.getCursor().getOffset());
      }

      for (Map.Entry<Partition, Offset> entry : expectedOffsets.entrySet()) {
        Partition partition = entry.getKey();
        long expectedOffset = entry.getValue().value();
        Long actualOffset = committedByPartition.get(partition.value());
        MigrationResult original = currentResults.get(partition);

        if (actualOffset != null && actualOffset == expectedOffset) {
          log.info(
              "Partition "
                  + partition.value()
                  + ": Validation passed (offset="
                  + actualOffset
                  + ")");
          updatedResults.put(
              partition,
              MigrationResult.successValidated(
                  original != null ? original.getPslOffset() : 0, expectedOffset));
        } else {
          long actual = actualOffset != null ? actualOffset : -1;
          log.warning(
              "Partition "
                  + partition.value()
                  + ": Validation failed (expected="
                  + expectedOffset
                  + ", actual="
                  + actual
                  + ")");
          updatedResults.put(
              partition,
              MigrationResult.validationFailed(
                  original != null ? original.getPslOffset() : 0, expectedOffset, actual));
        }
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Post-reset validation failed", e);
      // Don't overwrite successful migration results on validation failure
      for (Map.Entry<Partition, Offset> entry : expectedOffsets.entrySet()) {
        MigrationResult original = currentResults.get(entry.getKey());
        if (original != null && original.getStatus() == MigrationResult.Status.SUCCESS) {
          log.warning(
              "Partition "
                  + entry.getKey().value()
                  + ": Migration succeeded but validation could not be performed: "
                  + e.getMessage());
        }
      }
    }

    return updatedResults;
  }
}
