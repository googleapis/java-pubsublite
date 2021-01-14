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
import static com.google.common.base.Preconditions.checkState;

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.types.StructType;

public class PslMicroBatchReader implements MicroBatchReader {

  private final CursorClient cursorClient;
  private final MultiPartitionCommitter committer;
  private final PartitionSubscriberFactory partitionSubscriberFactory;
  private final PerTopicHeadOffsetReader headOffsetReader;
  private final SubscriptionPath subscriptionPath;
  private final FlowControlSettings flowControlSettings;
  private final long topicPartitionCount;
  @Nullable private SparkSourceOffset startOffset = null;
  private SparkSourceOffset endOffset;

  public PslMicroBatchReader(
      CursorClient cursorClient,
      MultiPartitionCommitter committer,
      PartitionSubscriberFactory partitionSubscriberFactory,
      PerTopicHeadOffsetReader headOffsetReader,
      SubscriptionPath subscriptionPath,
      FlowControlSettings flowControlSettings,
      long topicPartitionCount) {
    this.cursorClient = cursorClient;
    this.committer = committer;
    this.partitionSubscriberFactory = partitionSubscriberFactory;
    this.headOffsetReader = headOffsetReader;
    this.subscriptionPath = subscriptionPath;
    this.flowControlSettings = flowControlSettings;
    this.topicPartitionCount = topicPartitionCount;
  }

  @Override
  public void setOffsetRange(Optional<Offset> start, Optional<Offset> end) {
    if (start.isPresent()) {
      checkArgument(
          SparkSourceOffset.class.isAssignableFrom(start.get().getClass()),
          "start offset is not assignable to PslSourceOffset.");
      startOffset = (SparkSourceOffset) start.get();
    } else {
      startOffset =
          PslSparkUtils.getSparkStartOffset(cursorClient, subscriptionPath, topicPartitionCount);
    }
    if (end.isPresent()) {
      checkArgument(
          SparkSourceOffset.class.isAssignableFrom(end.get().getClass()),
          "start offset is not assignable to PslSourceOffset.");
      endOffset = (SparkSourceOffset) end.get();
    } else {
      endOffset = PslSparkUtils.toSparkSourceOffset(headOffsetReader.getHeadOffset());
    }
  }

  @Override
  public Offset getStartOffset() {
    return startOffset;
  }

  @Override
  public Offset getEndOffset() {
    return endOffset;
  }

  @Override
  public Offset deserializeOffset(String json) {
    return SparkSourceOffset.fromJson(json);
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
    committer.close();
  }

  @Override
  public StructType readSchema() {
    return Constants.DEFAULT_SCHEMA;
  }

  @Override
  public List<InputPartition<InternalRow>> planInputPartitions() {
    checkState(startOffset != null);
    List<InputPartition<InternalRow>> list = new ArrayList<>();
    for (SparkPartitionOffset offset : startOffset.getPartitionOffsetMap().values()) {
      SparkPartitionOffset endPartitionOffset =
          endOffset.getPartitionOffsetMap().get(offset.partition());
      if (offset.equals(endPartitionOffset)) {
        // There is no message to pull for this partition.
        continue;
      }
      PartitionSubscriberFactory partitionSubscriberFactory = this.partitionSubscriberFactory;
      SubscriberFactory subscriberFactory =
          (consumer) -> partitionSubscriberFactory.newSubscriber(offset.partition(), consumer);
      list.add(
          new PslMicroBatchInputPartition(
              subscriptionPath,
              flowControlSettings,
              offset,
              endPartitionOffset,
              subscriberFactory));
    }
    return list;
  }
}
