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
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CursorClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.types.StructType;

public class PslMicroBatchReader implements MicroBatchReader {

  private final CursorClient cursorClient;
  private final HeadOffsetReader headOffsetReader;
  private final MultiPartitionCommitter committer;
  private final SubscriptionPath subscriptionPath;
  private final TopicPath topicPath;
  private final FlowControlSettings flowControlSettings;
  private final long topicPartitionCount;
  private SparkSourceOffset startOffset;
  private SparkSourceOffset endOffset;

  public PslMicroBatchReader(
      CursorClient cursorClient,
      HeadOffsetReader headOffsetReader,
      MultiPartitionCommitter committer,
      SubscriptionPath subscriptionPath,
      TopicPath topicPath,
      FlowControlSettings flowControlSettings,
      long topicPartitionCount) {
    this.cursorClient = cursorClient;
    this.headOffsetReader = headOffsetReader;
    this.committer = committer;
    this.subscriptionPath = subscriptionPath;
    this.topicPath = topicPath;
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
      try {
        Map<Partition, com.google.cloud.pubsublite.Offset> pslSourceOffsetMap = new HashMap<>();
        for (int i = 0; i < topicPartitionCount; i++) {
          pslSourceOffsetMap.put(Partition.of(i), com.google.cloud.pubsublite.Offset.of(0));
        }
        cursorClient
            .listPartitionCursors(subscriptionPath)
            .get()
            .forEach((key, value) -> pslSourceOffsetMap.replace(key, value));
        startOffset =
            PslSparkUtils.toSparkSourceOffset(
                PslSourceOffset.builder().partitionOffsetMap(pslSourceOffsetMap).build());
      } catch (InterruptedException | ExecutionException e) {
        throw new IllegalStateException(
            "Failed to get information from PSL and construct startOffset", e);
      }
    }
    if (end.isPresent()) {
      assert SparkSourceOffset.class.isAssignableFrom(end.get().getClass())
          : "start offset is not assignable to PslSourceOffset.";
      endOffset = (SparkSourceOffset) end.get();
    } else {
      try {
        endOffset = PslSparkUtils.toSparkSourceOffset(headOffsetReader.getHeadOffset(topicPath));
      } catch (CheckedApiException e) {
        throw new IllegalStateException("Unable to get head offsets.", e);
      }
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
    headOffsetReader.close();
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
              if (Objects.equals(v, endPartitionOffset)) {
                // There is no message to pull for this partition.
                return null;
              }
              return new PslMicroBatchInputPartition(
                  subscriptionPath, flowControlSettings, v, endPartitionOffset);
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
