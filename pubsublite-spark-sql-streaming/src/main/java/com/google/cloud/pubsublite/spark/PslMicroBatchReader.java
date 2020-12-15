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

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.types.StructType;

public class PslMicroBatchReader implements MicroBatchReader {

  private final CursorClient cursorClient;
  private final MultiPartitionCommitter committer;
  private final SubscriptionPath subscriptionPath;
  private final FlowControlSettings flowControlSettings;
  private final long topicPartitionCount;
  @Nullable private SparkSourceOffset startOffset = null;
  private SparkSourceOffset endOffset;

  public PslMicroBatchReader(
      CursorClient cursorClient,
      MultiPartitionCommitter committer,
      SubscriptionPath subscriptionPath,
      SparkSourceOffset endOffset,
      FlowControlSettings flowControlSettings,
      long topicPartitionCount) {
    this.cursorClient = cursorClient;
    this.committer = committer;
    this.subscriptionPath = subscriptionPath;
    this.endOffset = endOffset;
    this.flowControlSettings = flowControlSettings;
    this.topicPartitionCount = topicPartitionCount;
  }

  @Override
  public void setOffsetRange(Optional<Offset> start, Optional<Offset> end) {
    if (start.isPresent()) {
      assert SparkSourceOffset.class.isAssignableFrom(start.get().getClass())
          : "start offset is not assignable to PslSourceOffset.";
      startOffset = (SparkSourceOffset) start.get();
    } else {
      startOffset =
          PslSparkUtils.getSparkStartOffset(cursorClient, subscriptionPath, topicPartitionCount);
    }
    if (end.isPresent()) {
      assert SparkSourceOffset.class.isAssignableFrom(end.get().getClass())
          : "start offset is not assignable to PslSourceOffset.";
      endOffset = (SparkSourceOffset) end.get();
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
    assert SparkSourceOffset.class.isAssignableFrom(end.getClass())
        : "end offset is not assignable to SparkSourceOffset.";
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
    return startOffset.getPartitionOffsetMap().values().stream()
        .map(
            v -> {
              SparkPartitionOffset endPartitionOffset =
                  endOffset.getPartitionOffsetMap().get(v.partition());
              if (v.equals(endPartitionOffset)) {
                // There is no message to pull for this partition.
                return null;
              }
              return new PslMicroBatchInputPartition(
                  subscriptionPath,
                  flowControlSettings,
                  endPartitionOffset,
                  // TODO(jiangmichael): Pass credentials settings here.
                  (consumer) ->
                      SubscriberBuilder.newBuilder()
                          .setSubscriptionPath(subscriptionPath)
                          .setPartition(endPartitionOffset.partition())
                          .setContext(PubsubContext.of(Constants.FRAMEWORK))
                          .setMessageConsumer(consumer)
                          .build());
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
