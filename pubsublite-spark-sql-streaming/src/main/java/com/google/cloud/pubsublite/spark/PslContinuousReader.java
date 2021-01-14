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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;
import org.apache.spark.sql.types.StructType;

public class PslContinuousReader implements ContinuousReader {

  private final CursorClient cursorClient;
  private final MultiPartitionCommitter committer;
  private final PartitionSubscriberFactory partitionSubscriberFactory;
  private final SubscriptionPath subscriptionPath;
  private final FlowControlSettings flowControlSettings;
  private final long topicPartitionCount;
  private SparkSourceOffset startOffset;

  @VisibleForTesting
  public PslContinuousReader(
      CursorClient cursorClient,
      MultiPartitionCommitter committer,
      PartitionSubscriberFactory partitionSubscriberFactory,
      SubscriptionPath subscriptionPath,
      FlowControlSettings flowControlSettings,
      long topicPartitionCount) {
    this.cursorClient = cursorClient;
    this.committer = committer;
    this.partitionSubscriberFactory = partitionSubscriberFactory;
    this.subscriptionPath = subscriptionPath;
    this.flowControlSettings = flowControlSettings;
    this.topicPartitionCount = topicPartitionCount;
  }

  @Override
  public Offset mergeOffsets(PartitionOffset[] offsets) {
    checkArgument(
        SparkPartitionOffset.class.isAssignableFrom(offsets.getClass().getComponentType()),
        "PartitionOffset object is not assignable to SparkPartitionOffset.");
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
      checkArgument(
          SparkSourceOffset.class.isAssignableFrom(start.get().getClass()),
          "start offset is not assignable to PslSourceOffset.");
      startOffset = (SparkSourceOffset) start.get();
      return;
    }
    startOffset =
        PslSparkUtils.getSparkStartOffset(cursorClient, subscriptionPath, topicPartitionCount);
  }

  @Override
  public void commit(Offset end) {
    checkArgument(
        SparkSourceOffset.class.isAssignableFrom(end.getClass()),
        "end offset is not assignable to SparkSourceOffset.");
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
    List<InputPartition<InternalRow>> list = new ArrayList<>();
    for (SparkPartitionOffset offset : startOffset.getPartitionOffsetMap().values()) {
      PartitionSubscriberFactory partitionSubscriberFactory = this.partitionSubscriberFactory;
      SubscriberFactory subscriberFactory =
          (consumer) -> partitionSubscriberFactory.newSubscriber(offset.partition(), consumer);
      list.add(
          new PslContinuousInputPartition(
              subscriberFactory,
              SparkPartitionOffset.builder()
                  .partition(offset.partition())
                  .offset(offset.offset())
                  .build(),
              subscriptionPath,
              flowControlSettings));
    }
    return list;
  }
}
