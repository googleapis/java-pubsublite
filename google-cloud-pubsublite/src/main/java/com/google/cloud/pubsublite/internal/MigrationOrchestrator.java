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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.OffsetMigrationHelper.MigrationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orchestrates the full per-subscription migration sequence from Pub/Sub Lite to Google Managed
 * Kafka.
 *
 * <p>This class codifies the Phase 2 migration steps into a reusable, tested utility:
 *
 * <ol>
 *   <li>Stop the PSL subscriber and wait for in-flight acks to complete
 *   <li>Run OffsetMigrationHelper to resolve and reset Kafka offsets
 *   <li>Validate that migration succeeded for all partitions
 *   <li>Report results
 * </ol>
 *
 * <p>The orchestrator does not manage Kafka subscriber lifecycle — starting the Kafka subscriber is
 * the caller's responsibility after verifying migration results. This separation allows the caller
 * to inspect results and decide whether to proceed or rollback.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * MigrationOrchestrator orchestrator = MigrationOrchestrator.newBuilder()
 *     .setPslCursorClient(pslCursorClient)
 *     .setPslTopicStatsClient(pslTopicStatsClient)
 *     .setKafkaTopicStatsClient(kafkaTopicStatsClient)
 *     .setKafkaCursorClient(kafkaCursorClient)
 *     .setPslTopicPath(pslTopicPath)
 *     .setPslSubscriptionPath(pslSubscriptionPath)
 *     .setKafkaSubscriptionPath(kafkaSubscriptionPath)
 *     .setKafkaTopicName(kafkaTopicName)
 *     .setPartitionCount(partitionCount)
 *     .build();
 *
 * // Stop PSL subscriber first
 * pslSubscriber.stopAsync().awaitTerminated();
 *
 * // Run migration
 * MigrationOrchestrator.Result result = orchestrator.execute();
 *
 * if (result.isSuccessful()) {
 *     // Start Kafka subscriber
 *     kafkaSubscriber.startAsync().awaitRunning();
 * } else {
 *     // Rollback: restart PSL subscriber
 *     pslSubscriber.startAsync().awaitRunning();
 * }
 * }</pre>
 */
public final class MigrationOrchestrator {
  private static final Logger log = Logger.getLogger(MigrationOrchestrator.class.getName());

  private final CursorClient pslCursorClient;
  private final TopicStatsClient pslTopicStatsClient;
  private final KafkaTopicStatsClient kafkaTopicStatsClient;
  private final KafkaCursorClient kafkaCursorClient;
  private final TopicPath pslTopicPath;
  private final SubscriptionPath pslSubscriptionPath;
  private final SubscriptionPath kafkaSubscriptionPath;
  private final String kafkaTopicName;
  private final int partitionCount;
  private final boolean dryRun;
  private final boolean validate;

  private MigrationOrchestrator(Builder builder) {
    this.pslCursorClient = builder.pslCursorClient;
    this.pslTopicStatsClient = builder.pslTopicStatsClient;
    this.kafkaTopicStatsClient = builder.kafkaTopicStatsClient;
    this.kafkaCursorClient = builder.kafkaCursorClient;
    this.pslTopicPath = builder.pslTopicPath;
    this.pslSubscriptionPath = builder.pslSubscriptionPath;
    this.kafkaSubscriptionPath = builder.kafkaSubscriptionPath;
    this.kafkaTopicName = builder.kafkaTopicName;
    this.partitionCount = builder.partitionCount;
    this.dryRun = builder.dryRun;
    this.validate = builder.validate;
  }

  /** Result of a full migration orchestration run. */
  public static class Result {
    private final Map<Partition, MigrationResult> partitionResults;
    private final boolean successful;
    private final String summary;

    Result(Map<Partition, MigrationResult> partitionResults) {
      this.partitionResults = partitionResults;

      int successCount = 0;
      int dryRunCount = 0;
      int errorCount = 0;
      int validationFailedCount = 0;

      for (MigrationResult r : partitionResults.values()) {
        switch (r.getStatus()) {
          case SUCCESS:
          case NO_PSL_OFFSET:
            successCount++;
            break;
          case DRY_RUN:
            dryRunCount++;
            break;
          case VALIDATION_FAILED:
            validationFailedCount++;
            break;
          case ERROR:
          case NO_KAFKA_OFFSET:
            errorCount++;
            break;
        }
      }

      this.successful = errorCount == 0 && validationFailedCount == 0;

      StringBuilder sb = new StringBuilder();
      sb.append("Migration ");
      if (dryRunCount > 0) {
        sb.append("dry run ");
      }
      sb.append("completed: ");
      sb.append(partitionResults.size()).append(" partitions processed");
      if (successCount > 0) sb.append(", ").append(successCount).append(" succeeded");
      if (dryRunCount > 0) sb.append(", ").append(dryRunCount).append(" resolved (dry run)");
      if (errorCount > 0) sb.append(", ").append(errorCount).append(" failed");
      if (validationFailedCount > 0)
        sb.append(", ").append(validationFailedCount).append(" validation failed");
      this.summary = sb.toString();
    }

    public Map<Partition, MigrationResult> getPartitionResults() {
      return partitionResults;
    }

    public boolean isSuccessful() {
      return successful;
    }

    public String getSummary() {
      return summary;
    }

    @Override
    public String toString() {
      return summary;
    }
  }

  /**
   * Executes the migration sequence.
   *
   * <p>This method is safe to call multiple times (idempotent offset reset). If the first attempt
   * fails partway through, calling execute() again will re-resolve and re-reset all offsets.
   *
   * @return The migration result containing per-partition outcomes.
   * @throws RuntimeException if the migration fails catastrophically.
   */
  public Result execute() {
    log.info("Starting migration orchestration for subscription: " + pslSubscriptionPath);
    log.info(
        "Target Kafka topic: "
            + kafkaTopicName
            + ", partitions: "
            + partitionCount
            + ", dryRun: "
            + dryRun
            + ", validate: "
            + validate);

    // Step 1: Build partition list
    List<Partition> partitions = new ArrayList<>();
    for (int i = 0; i < partitionCount; i++) {
      partitions.add(Partition.of(i));
    }

    // Step 2: Verify Kafka topic has messages (pre-flight check)
    try {
      Offset latestOffset =
          kafkaTopicStatsClient.getLatestOffset(kafkaTopicName, Partition.of(0)).get();
      log.info("Kafka topic " + kafkaTopicName + " partition 0 latest offset: " + latestOffset);
    } catch (Exception e) {
      log.log(
          Level.WARNING,
          "Pre-flight check failed: could not read Kafka topic " + kafkaTopicName,
          e);
      throw new RuntimeException(
          "Pre-flight check failed: cannot read Kafka topic " + kafkaTopicName + ": " + e, e);
    }

    // Step 3: Run OffsetMigrationHelper
    try {
      Map<Partition, MigrationResult> migrationResults =
          OffsetMigrationHelper.migrateOffsets(
                  pslCursorClient,
                  pslTopicStatsClient,
                  kafkaTopicStatsClient,
                  kafkaCursorClient,
                  pslTopicPath,
                  pslSubscriptionPath,
                  kafkaSubscriptionPath,
                  kafkaTopicName,
                  partitions,
                  dryRun,
                  validate)
              .get();

      Result result = new Result(migrationResults);
      log.info(result.getSummary());

      // Step 4: Log per-partition results
      for (Map.Entry<Partition, MigrationResult> entry : migrationResults.entrySet()) {
        MigrationResult r = entry.getValue();
        log.info("  Partition " + entry.getKey().value() + ": " + r.getMessage());
      }

      return result;

    } catch (Exception e) {
      log.log(Level.SEVERE, "Migration orchestration failed", e);
      throw new RuntimeException("Migration orchestration failed: " + e.getMessage(), e);
    }
  }

  /**
   * Performs a post-migration validation by checking consumer group lag.
   *
   * <p>After migration, this verifies that the Kafka subscriber would start from a reasonable
   * position by checking the backlog on each partition. A large backlog may indicate that the
   * offset was set too far back, while zero backlog on all partitions after a migration from an
   * active PSL subscription may indicate the offset was set too far forward.
   *
   * @return A map of partition to backlog message count.
   */
  public Map<Partition, Long> checkBacklog() {
    Map<Partition, Long> backlog = new java.util.HashMap<>();
    for (int i = 0; i < partitionCount; i++) {
      Partition partition = Partition.of(i);
      try {
        KafkaTopicStatsClient.BacklogInfo info =
            kafkaTopicStatsClient
                .computeBacklogBytes(kafkaTopicName, partition, kafkaSubscriptionPath, 0)
                .get();
        backlog.put(partition, info.getMessageCount());
        log.info(
            "Partition "
                + i
                + " backlog: "
                + info.getMessageCount()
                + " messages ("
                + info.getEstimatedBytes()
                + " bytes est.)");
      } catch (Exception e) {
        log.log(Level.WARNING, "Failed to check backlog for partition " + i, e);
        backlog.put(partition, -1L);
      }
    }
    return backlog;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private CursorClient pslCursorClient;
    private TopicStatsClient pslTopicStatsClient;
    private KafkaTopicStatsClient kafkaTopicStatsClient;
    private KafkaCursorClient kafkaCursorClient;
    private TopicPath pslTopicPath;
    private SubscriptionPath pslSubscriptionPath;
    private SubscriptionPath kafkaSubscriptionPath;
    private String kafkaTopicName;
    private int partitionCount;
    private boolean dryRun = false;
    private boolean validate = true;

    private Builder() {}

    public Builder setPslCursorClient(CursorClient pslCursorClient) {
      this.pslCursorClient = pslCursorClient;
      return this;
    }

    public Builder setPslTopicStatsClient(TopicStatsClient pslTopicStatsClient) {
      this.pslTopicStatsClient = pslTopicStatsClient;
      return this;
    }

    public Builder setKafkaTopicStatsClient(KafkaTopicStatsClient kafkaTopicStatsClient) {
      this.kafkaTopicStatsClient = kafkaTopicStatsClient;
      return this;
    }

    public Builder setKafkaCursorClient(KafkaCursorClient kafkaCursorClient) {
      this.kafkaCursorClient = kafkaCursorClient;
      return this;
    }

    public Builder setPslTopicPath(TopicPath pslTopicPath) {
      this.pslTopicPath = pslTopicPath;
      return this;
    }

    public Builder setPslSubscriptionPath(SubscriptionPath pslSubscriptionPath) {
      this.pslSubscriptionPath = pslSubscriptionPath;
      return this;
    }

    public Builder setKafkaSubscriptionPath(SubscriptionPath kafkaSubscriptionPath) {
      this.kafkaSubscriptionPath = kafkaSubscriptionPath;
      return this;
    }

    public Builder setKafkaTopicName(String kafkaTopicName) {
      this.kafkaTopicName = kafkaTopicName;
      return this;
    }

    public Builder setPartitionCount(int partitionCount) {
      this.partitionCount = partitionCount;
      return this;
    }

    public Builder setDryRun(boolean dryRun) {
      this.dryRun = dryRun;
      return this;
    }

    public Builder setValidate(boolean validate) {
      this.validate = validate;
      return this;
    }

    public MigrationOrchestrator build() {
      if (pslCursorClient == null) throw new IllegalStateException("pslCursorClient is required");
      if (pslTopicStatsClient == null)
        throw new IllegalStateException("pslTopicStatsClient is required");
      if (kafkaTopicStatsClient == null)
        throw new IllegalStateException("kafkaTopicStatsClient is required");
      if (kafkaCursorClient == null)
        throw new IllegalStateException("kafkaCursorClient is required");
      if (pslTopicPath == null) throw new IllegalStateException("pslTopicPath is required");
      if (pslSubscriptionPath == null)
        throw new IllegalStateException("pslSubscriptionPath is required");
      if (kafkaSubscriptionPath == null)
        throw new IllegalStateException("kafkaSubscriptionPath is required");
      if (kafkaTopicName == null) throw new IllegalStateException("kafkaTopicName is required");
      if (partitionCount <= 0) throw new IllegalStateException("partitionCount must be > 0");
      return new MigrationOrchestrator(this);
    }
  }
}
