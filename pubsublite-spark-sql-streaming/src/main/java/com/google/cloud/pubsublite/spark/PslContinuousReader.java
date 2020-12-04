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

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;
import org.apache.spark.sql.types.StructType;

public class PslContinuousReader implements ContinuousReader, Serializable {

  private final PslDataSourceOptions options;
  private final SubscriptionPath subscriptionPath;
  private final AdminServiceClient adminServiceClient;
  private final CursorServiceClient cursorServiceClient;
  private final MultiPartitionCommitter committer;
  private SparkSourceOffset startOffset;

  public PslContinuousReader(PslDataSourceOptions options) {
    this(
        options,
        options.newAdminClient(),
        options.newCursorClient(),
        new MultiPartitionCommitter(
            (partition) ->
                CommitterBuilder.newBuilder()
                    .setSubscriptionPath(options.subscriptionPath())
                    .setPartition(partition)
                    .setServiceClient(options.newCursorClient())
                    .build()));
  }

  @VisibleForTesting
  public PslContinuousReader(
      PslDataSourceOptions options,
      AdminServiceClient adminServiceClient,
      CursorServiceClient cursorServiceClient,
      MultiPartitionCommitter committer) {
    this.options = options;
    this.subscriptionPath = options.subscriptionPath();
    this.adminServiceClient = adminServiceClient;
    this.cursorServiceClient = cursorServiceClient;
    this.committer = committer;
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

    Subscription sub = adminServiceClient.getSubscription(subscriptionPath.toString());
    TopicPartitions topicPartitions = adminServiceClient.getTopicPartitions(sub.getTopic());

    PslSourceOffset pslSourceOffset = new PslSourceOffset(topicPartitions.getPartitionCount());
    CursorServiceClient.ListPartitionCursorsPagedResponse resp =
        cursorServiceClient.listPartitionCursors(subscriptionPath.toString());
    for (PartitionCursor p : resp.iterateAll()) {
      pslSourceOffset.set(
          Partition.of(p.getPartition()),
          com.google.cloud.pubsublite.Offset.of(p.getCursor().getOffset()));
    }
    startOffset = PslSparkUtils.toSparkSourceOffset(pslSourceOffset);
  }

  @Override
  public void commit(Offset end) {
    assert SparkSourceOffset.class.isAssignableFrom(end.getClass())
        : "end offset is not assignable to SparkSourceOffset.";
    committer.commit(PslSparkUtils.toPslSourceOffset((SparkSourceOffset) end));
  }

  @Override
  public void stop() {
    cursorServiceClient.shutdown();
    adminServiceClient.shutdown();
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
                    PslPartitionOffset.builder().partition(e.getKey()).offset(e.getValue()).build(),
                    options))
        .collect(Collectors.toList());
  }
}
