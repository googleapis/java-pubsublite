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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.OffsetMigrationHelper.MigrationResult;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Timestamp;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class MigrationOrchestratorTest {
  private static final CloudRegion REGION = CloudRegion.of("us-central1");

  private static final TopicPath PSL_TOPIC_PATH =
      TopicPath.newBuilder()
          .setName(TopicName.of("test-topic"))
          .setProject(ProjectNumber.of(123))
          .setLocation(CloudZone.of(REGION, 'a'))
          .build();

  private static final SubscriptionPath PSL_SUB_PATH =
      SubscriptionPath.newBuilder()
          .setName(SubscriptionName.of("test-sub"))
          .setProject(ProjectNumber.of(123))
          .setLocation(CloudZone.of(REGION, 'a'))
          .build();

  private static final SubscriptionPath KAFKA_SUB_PATH =
      SubscriptionPath.newBuilder()
          .setName(SubscriptionName.of("test-kafka-topic"))
          .setProject(ProjectNumber.of(123))
          .setLocation(REGION)
          .build();

  private static final String KAFKA_TOPIC_NAME = "test-kafka-topic";

  private static final Timestamp PUBLISH_TIME =
      Timestamp.newBuilder().setSeconds(1700000000).setNanos(500_000_000).build();

  private static final long PUBLISH_TIME_MS = 1700000000L * 1000 + 500;

  @Mock CursorClient pslCursorClient;
  @Mock TopicStatsClient pslTopicStatsClient;
  @Mock KafkaTopicStatsClient kafkaTopicStatsClient;
  @Mock KafkaCursorClient kafkaCursorClient;

  @Before
  public void setUp() {
    initMocks(this);
  }

  private MigrationOrchestrator.Builder baseBuilder() {
    return MigrationOrchestrator.newBuilder()
        .setPslCursorClient(pslCursorClient)
        .setPslTopicStatsClient(pslTopicStatsClient)
        .setKafkaTopicStatsClient(kafkaTopicStatsClient)
        .setKafkaCursorClient(kafkaCursorClient)
        .setPslTopicPath(PSL_TOPIC_PATH)
        .setPslSubscriptionPath(PSL_SUB_PATH)
        .setKafkaSubscriptionPath(KAFKA_SUB_PATH)
        .setKafkaTopicName(KAFKA_TOPIC_NAME)
        .setPartitionCount(2)
        .setValidate(false);
  }

  private void setupSuccessfulMigration() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(
            ApiFutures.immediateFuture(
                ImmutableMap.of(
                    Partition.of(0), Offset.of(10),
                    Partition.of(1), Offset.of(20))));

    ComputeMessageStatsResponse statsResponse =
        ComputeMessageStatsResponse.newBuilder().setMinimumPublishTime(PUBLISH_TIME).build();
    when(pslTopicStatsClient.computeMessageStats(eq(PSL_TOPIC_PATH), any(), any(), any()))
        .thenReturn(ApiFutures.immediateFuture(statsResponse));

    when(kafkaTopicStatsClient.getOffsetForTimestamp(
            eq(KAFKA_TOPIC_NAME), any(), eq(PUBLISH_TIME_MS)))
        .thenReturn(ApiFutures.immediateFuture(Optional.of(Offset.of(100))));

    when(kafkaCursorClient.resetOffsets(eq(KAFKA_SUB_PATH), eq(KAFKA_TOPIC_NAME), any()))
        .thenReturn(ApiFutures.immediateFuture(null));
  }

  // =====================================================================
  //  Successful execution tests
  // =====================================================================

  @Test
  public void execute_successfulMigration() {
    setupSuccessfulMigration();

    MigrationOrchestrator orchestrator = baseBuilder().build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.isSuccessful()).isTrue();
    assertThat(result.getPartitionResults()).hasSize(2);
    assertThat(result.getSummary()).contains("2 partitions processed");
    assertThat(result.getSummary()).contains("succeeded");
  }

  @Test
  public void execute_withValidation_success() {
    setupSuccessfulMigration();

    // Set up validation readback
    when(kafkaCursorClient.readCommittedOffsets(KAFKA_SUB_PATH, KAFKA_TOPIC_NAME))
        .thenReturn(
            ApiFutures.immediateFuture(
                ImmutableList.of(
                    PartitionCursor.newBuilder()
                        .setPartition(0)
                        .setCursor(Cursor.newBuilder().setOffset(100))
                        .build(),
                    PartitionCursor.newBuilder()
                        .setPartition(1)
                        .setCursor(Cursor.newBuilder().setOffset(100))
                        .build())));

    MigrationOrchestrator orchestrator = baseBuilder().setValidate(true).build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.isSuccessful()).isTrue();
    for (MigrationResult r : result.getPartitionResults().values()) {
      assertThat(r.isValidated()).isTrue();
    }
  }

  @Test
  public void execute_idempotent_canRunTwice() {
    setupSuccessfulMigration();

    MigrationOrchestrator orchestrator = baseBuilder().build();

    MigrationOrchestrator.Result result1 = orchestrator.execute();
    assertThat(result1.isSuccessful()).isTrue();

    // Re-setup mocks since they may have been consumed
    setupSuccessfulMigration();

    MigrationOrchestrator.Result result2 = orchestrator.execute();
    assertThat(result2.isSuccessful()).isTrue();
  }

  // =====================================================================
  //  Dry-run tests
  // =====================================================================

  @Test
  public void execute_dryRun_doesNotResetOffsets() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(ApiFutures.immediateFuture(ImmutableMap.of(Partition.of(0), Offset.of(10))));

    ComputeMessageStatsResponse statsResponse =
        ComputeMessageStatsResponse.newBuilder().setMinimumPublishTime(PUBLISH_TIME).build();
    when(pslTopicStatsClient.computeMessageStats(eq(PSL_TOPIC_PATH), any(), any(), any()))
        .thenReturn(ApiFutures.immediateFuture(statsResponse));

    when(kafkaTopicStatsClient.getOffsetForTimestamp(
            eq(KAFKA_TOPIC_NAME), any(), eq(PUBLISH_TIME_MS)))
        .thenReturn(ApiFutures.immediateFuture(Optional.of(Offset.of(100))));

    MigrationOrchestrator orchestrator = baseBuilder().setPartitionCount(1).setDryRun(true).build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.isSuccessful()).isTrue();
    assertThat(result.getSummary()).contains("dry run");

    for (MigrationResult r : result.getPartitionResults().values()) {
      assertThat(r.getStatus()).isEqualTo(MigrationResult.Status.DRY_RUN);
    }

    verify(kafkaCursorClient, never()).resetOffsets(any(), any(), any());
  }

  // =====================================================================
  //  Failure tests
  // =====================================================================

  @Test
  public void execute_preflightFails_throwsException() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFailedFuture(new RuntimeException("Cannot reach Kafka")));

    MigrationOrchestrator orchestrator = baseBuilder().build();

    RuntimeException thrown = assertThrows(RuntimeException.class, orchestrator::execute);
    assertThat(thrown.getMessage()).contains("Pre-flight check failed");
    assertThat(thrown.getMessage()).contains("Cannot reach Kafka");
  }

  @Test
  public void execute_migrationFails_throwsException() {
    // Pre-flight succeeds
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    // PSL cursor fails
    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(ApiFutures.immediateFailedFuture(new RuntimeException("PSL unavailable")));

    MigrationOrchestrator orchestrator = baseBuilder().build();

    RuntimeException thrown = assertThrows(RuntimeException.class, orchestrator::execute);
    assertThat(thrown.getMessage()).contains("Migration orchestration failed");
  }

  @Test
  public void execute_partialFailure_resultNotSuccessful() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    // Partition 0 has offset, partition 1 has offset
    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(
            ApiFutures.immediateFuture(
                ImmutableMap.of(
                    Partition.of(0), Offset.of(10),
                    Partition.of(1), Offset.of(20))));

    ComputeMessageStatsResponse statsResponse =
        ComputeMessageStatsResponse.newBuilder().setMinimumPublishTime(PUBLISH_TIME).build();

    // Partition 0 succeeds
    when(pslTopicStatsClient.computeMessageStats(
            eq(PSL_TOPIC_PATH), eq(Partition.of(0)), any(), any()))
        .thenReturn(ApiFutures.immediateFuture(statsResponse));

    // Partition 1 fails
    when(pslTopicStatsClient.computeMessageStats(
            eq(PSL_TOPIC_PATH), eq(Partition.of(1)), any(), any()))
        .thenReturn(ApiFutures.immediateFailedFuture(new RuntimeException("Stats lookup failed")));

    when(kafkaTopicStatsClient.getOffsetForTimestamp(
            eq(KAFKA_TOPIC_NAME), any(), eq(PUBLISH_TIME_MS)))
        .thenReturn(ApiFutures.immediateFuture(Optional.of(Offset.of(100))));

    when(kafkaCursorClient.resetOffsets(eq(KAFKA_SUB_PATH), eq(KAFKA_TOPIC_NAME), any()))
        .thenReturn(ApiFutures.immediateFuture(null));

    MigrationOrchestrator orchestrator = baseBuilder().build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.isSuccessful()).isFalse();
    assertThat(result.getSummary()).contains("failed");
    assertThat(result.getPartitionResults().get(Partition.of(0)).getStatus())
        .isEqualTo(MigrationResult.Status.SUCCESS);
    assertThat(result.getPartitionResults().get(Partition.of(1)).getStatus())
        .isEqualTo(MigrationResult.Status.ERROR);
  }

  // =====================================================================
  //  Result class tests
  // =====================================================================

  @Test
  public void result_toString_returnsSummary() {
    setupSuccessfulMigration();

    MigrationOrchestrator orchestrator = baseBuilder().build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.toString()).isEqualTo(result.getSummary());
  }

  @Test
  public void result_withValidationFailed_notSuccessful() {
    setupSuccessfulMigration();

    // Set up validation with mismatched offsets
    when(kafkaCursorClient.readCommittedOffsets(KAFKA_SUB_PATH, KAFKA_TOPIC_NAME))
        .thenReturn(
            ApiFutures.immediateFuture(
                ImmutableList.of(
                    PartitionCursor.newBuilder()
                        .setPartition(0)
                        .setCursor(Cursor.newBuilder().setOffset(99))
                        .build(),
                    PartitionCursor.newBuilder()
                        .setPartition(1)
                        .setCursor(Cursor.newBuilder().setOffset(100))
                        .build())));

    MigrationOrchestrator orchestrator = baseBuilder().setValidate(true).build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    assertThat(result.isSuccessful()).isFalse();
    assertThat(result.getSummary()).contains("validation failed");
  }

  @Test
  public void result_noKafkaOffset_countsAsFailure() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(ApiFutures.immediateFuture(ImmutableMap.of(Partition.of(0), Offset.of(10))));

    ComputeMessageStatsResponse statsResponse =
        ComputeMessageStatsResponse.newBuilder().setMinimumPublishTime(PUBLISH_TIME).build();
    when(pslTopicStatsClient.computeMessageStats(eq(PSL_TOPIC_PATH), any(), any(), any()))
        .thenReturn(ApiFutures.immediateFuture(statsResponse));

    when(kafkaTopicStatsClient.getOffsetForTimestamp(
            eq(KAFKA_TOPIC_NAME), any(), eq(PUBLISH_TIME_MS)))
        .thenReturn(ApiFutures.immediateFuture(Optional.empty()));

    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(500)));

    when(kafkaCursorClient.resetOffsets(eq(KAFKA_SUB_PATH), eq(KAFKA_TOPIC_NAME), any()))
        .thenReturn(ApiFutures.immediateFuture(null));

    MigrationOrchestrator orchestrator = baseBuilder().setPartitionCount(1).build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    // NO_KAFKA_OFFSET counts as failure
    assertThat(result.isSuccessful()).isFalse();
    assertThat(result.getSummary()).contains("failed");
  }

  @Test
  public void result_noPslOffset_countsAsSuccess() {
    when(kafkaTopicStatsClient.getLatestOffset(eq(KAFKA_TOPIC_NAME), eq(Partition.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));

    when(pslCursorClient.listPartitionCursors(PSL_SUB_PATH))
        .thenReturn(ApiFutures.immediateFuture(ImmutableMap.of()));

    when(kafkaTopicStatsClient.getEarliestOffset(eq(KAFKA_TOPIC_NAME), any()))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(0)));

    when(kafkaCursorClient.resetOffsets(eq(KAFKA_SUB_PATH), eq(KAFKA_TOPIC_NAME), any()))
        .thenReturn(ApiFutures.immediateFuture(null));

    MigrationOrchestrator orchestrator = baseBuilder().setPartitionCount(1).build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    // NO_PSL_OFFSET counts as success (it's a valid scenario)
    assertThat(result.isSuccessful()).isTrue();
  }

  // =====================================================================
  //  checkBacklog tests
  // =====================================================================

  @Test
  public void checkBacklog_returnsPerPartitionCounts() {
    when(kafkaTopicStatsClient.computeBacklogBytes(
            eq(KAFKA_TOPIC_NAME), any(), eq(KAFKA_SUB_PATH), eq(0L)))
        .thenReturn(ApiFutures.immediateFuture(new KafkaTopicStatsClient.BacklogInfo(42, 42000)));

    MigrationOrchestrator orchestrator = baseBuilder().build();
    Map<Partition, Long> backlog = orchestrator.checkBacklog();

    assertThat(backlog).hasSize(2);
    assertThat(backlog.get(Partition.of(0))).isEqualTo(42);
    assertThat(backlog.get(Partition.of(1))).isEqualTo(42);
  }

  @Test
  public void checkBacklog_withFailure_returnsMinusOne() {
    // Partition 0 succeeds
    when(kafkaTopicStatsClient.computeBacklogBytes(
            eq(KAFKA_TOPIC_NAME), eq(Partition.of(0)), eq(KAFKA_SUB_PATH), eq(0L)))
        .thenReturn(ApiFutures.immediateFuture(new KafkaTopicStatsClient.BacklogInfo(10, 10000)));

    // Partition 1 fails
    when(kafkaTopicStatsClient.computeBacklogBytes(
            eq(KAFKA_TOPIC_NAME), eq(Partition.of(1)), eq(KAFKA_SUB_PATH), eq(0L)))
        .thenReturn(ApiFutures.immediateFailedFuture(new RuntimeException("Kafka unavailable")));

    MigrationOrchestrator orchestrator = baseBuilder().build();
    Map<Partition, Long> backlog = orchestrator.checkBacklog();

    assertThat(backlog).hasSize(2);
    assertThat(backlog.get(Partition.of(0))).isEqualTo(10);
    assertThat(backlog.get(Partition.of(1))).isEqualTo(-1);
  }

  // =====================================================================
  //  Builder validation tests
  // =====================================================================

  @Test
  public void build_missingPslCursorClient_throwsException() {
    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class,
            () ->
                MigrationOrchestrator.newBuilder()
                    .setPslTopicStatsClient(pslTopicStatsClient)
                    .setKafkaTopicStatsClient(kafkaTopicStatsClient)
                    .setKafkaCursorClient(kafkaCursorClient)
                    .setPslTopicPath(PSL_TOPIC_PATH)
                    .setPslSubscriptionPath(PSL_SUB_PATH)
                    .setKafkaSubscriptionPath(KAFKA_SUB_PATH)
                    .setKafkaTopicName(KAFKA_TOPIC_NAME)
                    .setPartitionCount(1)
                    .build());
    assertThat(thrown.getMessage()).contains("pslCursorClient");
  }

  @Test
  public void build_missingKafkaTopicName_throwsException() {
    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> baseBuilder().setKafkaTopicName(null).build());
    assertThat(thrown.getMessage()).contains("kafkaTopicName");
  }

  @Test
  public void build_zeroPartitions_throwsException() {
    IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> baseBuilder().setPartitionCount(0).build());
    assertThat(thrown.getMessage()).contains("partitionCount");
  }

  @Test
  public void build_negativePartitions_throwsException() {
    IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> baseBuilder().setPartitionCount(-1).build());
    assertThat(thrown.getMessage()).contains("partitionCount");
  }

  @Test
  public void build_allFieldsSet_succeeds() {
    // Should not throw
    MigrationOrchestrator orchestrator = baseBuilder().build();
    assertThat(orchestrator).isNotNull();
  }

  @Test
  public void build_defaultDryRunIsFalse() {
    // Verify that the default produces a non-dry-run orchestrator by executing it
    setupSuccessfulMigration();

    MigrationOrchestrator orchestrator = baseBuilder().build();
    MigrationOrchestrator.Result result = orchestrator.execute();

    // Should have called resetOffsets (not dry-run)
    verify(kafkaCursorClient).resetOffsets(any(), any(), any());
  }

  @Test
  public void build_defaultValidateIsTrue() {
    setupSuccessfulMigration();

    // Don't set validate — the default should be true
    MigrationOrchestrator orchestrator =
        MigrationOrchestrator.newBuilder()
            .setPslCursorClient(pslCursorClient)
            .setPslTopicStatsClient(pslTopicStatsClient)
            .setKafkaTopicStatsClient(kafkaTopicStatsClient)
            .setKafkaCursorClient(kafkaCursorClient)
            .setPslTopicPath(PSL_TOPIC_PATH)
            .setPslSubscriptionPath(PSL_SUB_PATH)
            .setKafkaSubscriptionPath(KAFKA_SUB_PATH)
            .setKafkaTopicName(KAFKA_TOPIC_NAME)
            .setPartitionCount(2)
            .build();

    // Set up validation (since default validate=true, it will try to read committed offsets)
    when(kafkaCursorClient.readCommittedOffsets(KAFKA_SUB_PATH, KAFKA_TOPIC_NAME))
        .thenReturn(
            ApiFutures.immediateFuture(
                ImmutableList.of(
                    PartitionCursor.newBuilder()
                        .setPartition(0)
                        .setCursor(Cursor.newBuilder().setOffset(100))
                        .build(),
                    PartitionCursor.newBuilder()
                        .setPartition(1)
                        .setCursor(Cursor.newBuilder().setOffset(100))
                        .build())));

    MigrationOrchestrator.Result result = orchestrator.execute();

    // readCommittedOffsets should have been called (validate=true by default)
    verify(kafkaCursorClient).readCommittedOffsets(KAFKA_SUB_PATH, KAFKA_TOPIC_NAME);
  }
}
