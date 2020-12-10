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

package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;
import org.apache.spark.sql.types.StructType;

public class PslContinuousReader implements ContinuousReader {

  private final PslDataSourceOptions options;
  private final CursorClient cursorClient;
  private final MultiPartitionCommitter committer;
  private final long topicPartitionCount;
  private SparkSourceOffset startOffset;

  public PslContinuousReader(PslDataSourceOptions options) {
    this.options = options;
    this.cursorClient = options.newCursorClient();
    AdminClient adminClient = options.newAdminClient();
    try {
      Subscription sub = adminClient.getSubscription(options.subscriptionPath()).get();
      this.topicPartitionCount =
          adminClient.getTopicPartitionCount(TopicPath.parse(sub.getTopic())).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(
          "Failed to get information of subscription " + options.subscriptionPath(), e);
    }
    this.committer =
        new MultiPartitionCommitterImpl(
            topicPartitionCount,
            (partition) ->
                CommitterBuilder.newBuilder()
                    .setSubscriptionPath(options.subscriptionPath())
                    .setPartition(partition)
                    .setServiceClient(options.newCursorServiceClient())
                    .build());
  }

  @VisibleForTesting
  public PslContinuousReader(
      PslDataSourceOptions options,
      CursorClient cursorClient,
      MultiPartitionCommitterImpl committer,
      long topicPartitionCount) {
    this.options = options;
    this.cursorClient = cursorClient;
    this.committer = committer;
    this.topicPartitionCount = topicPartitionCount;
  }

  @Override
  public Offset mergeOffsets(PartitionOffset[] offsets) {
    assert SparkPartitionOffset.class.isAssignableFrom(offsets.getClass().getComponentType())
        : "PartitionOffset object is not assignable to SparkPartitionOffset.";
    return SparkSourceOffset.merge(
        Arrays.copyOf(offsets, offsets.length, SparkPartitionOffset[].class));
  }

  @Override
  public Offset deserializeOffset(String json) {
    return SparkSourceOffset.fromJson(json);
  }

  @Override
  public Offset getStartOffset() {
    return startOffset;
  }

  @Override
  public void setStartOffset(Optional<Offset> start) {
    if (start.isPresent()) {
      assert SparkSourceOffset.class.isAssignableFrom(start.get().getClass())
          : "start offset is not assignable to PslSourceOffset.";
      startOffset = (SparkSourceOffset) start.get();
      return;
    }
    try {
      Map<Partition, com.google.cloud.pubsublite.Offset> pslSourceOffsetMap = new HashMap<>();
      for (int i = 0; i < topicPartitionCount; i++) {
        pslSourceOffsetMap.put(Partition.of(i), com.google.cloud.pubsublite.Offset.of(0));
      }
      cursorClient
          .listPartitionCursors(options.subscriptionPath())
          .get()
          .entrySet()
          .forEach((e) -> pslSourceOffsetMap.replace(e.getKey(), e.getValue()));
      startOffset =
          PslSparkUtils.toSparkSourceOffset(
              PslSourceOffset.builder().partitionOffsetMap(pslSourceOffsetMap).build());
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(
          "Failed to get information from PSL and construct startOffset", e);
    }
  }

  @Override
  public void commit(Offset end) {
    assert SparkSourceOffset.class.isAssignableFrom(end.getClass())
        : "end offset is not assignable to SparkSourceOffset.";
    committer.commit(PslSparkUtils.toPslSourceOffset((SparkSourceOffset) end));
  }

  @Override
  public void stop() {
    cursorClient.shutdown();
    committer.close();
  }

  @Override
  public StructType readSchema() {
    return Constants.DEFAULT_SCHEMA;
  }

  @Override
  public List<InputPartition<InternalRow>> planInputPartitions() {
    return startOffset.getPartitionOffsetMap().entrySet().stream()
        .map(
            e ->
                new PslContinuousInputPartition(
                    SparkPartitionOffset.builder()
                        .partition(e.getKey())
                        .offset(e.getValue().offset())
                        .build(),
                    options.subscriptionPath(),
                    Objects.requireNonNull(options.flowControlSettings())))
        .collect(Collectors.toList());
  }
}
