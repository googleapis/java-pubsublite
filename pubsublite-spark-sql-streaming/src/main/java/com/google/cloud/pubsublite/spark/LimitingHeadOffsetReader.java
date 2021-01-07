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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Rate limited HeadOffsetReader, utilizing a LoadingCache that refreshes all partitions head
 * offsets for the topic at most once per minute.
 */
public class LimitingHeadOffsetReader implements PerTopicHeadOffsetReader {

  private final TopicStatsClient topicStatsClient;
  private final TopicPath topic;
  private final long topicPartitionCount;
  private final LoadingCache<Partition, Offset> cachedHeadOffsets;

  @VisibleForTesting
  public LimitingHeadOffsetReader(
      TopicStatsClient topicStatsClient, TopicPath topic, long topicPartitionCount, Ticker ticker) {
    this.topicStatsClient = topicStatsClient;
    this.topic = topic;
    this.topicPartitionCount = topicPartitionCount;
    this.cachedHeadOffsets =
        CacheBuilder.newBuilder()
            .ticker(ticker)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(CacheLoader.from(this::loadHeadOffset));
  }

  private Offset loadHeadOffset(Partition partition) {
    try {
      return Offset.of(topicStatsClient.computeHeadCursor(topic, partition).get().getOffset());
    } catch (Throwable t) {
      throw new IllegalStateException(
          String.format(
              "Unable to compute head cursor for topic partition: [%s,%d]",
              topic, partition.value()),
          t);
    }
  }

  @Override
  public PslSourceOffset getHeadOffset() {
    try {
      Map<Partition, Offset> partitionOffsetMap = new HashMap<>();
      for (int i = 0; i < topicPartitionCount; i++) {
        partitionOffsetMap.put(Partition.of(i), cachedHeadOffsets.get(Partition.of(i)));
      }
      return PslSourceOffset.builder().partitionOffsetMap(partitionOffsetMap).build();
    } catch (ExecutionException e) {
      throw new IllegalStateException(
          "Unable to compute head offset for topic: " + topic, e.getCause());
    }
  }

  @Override
  public void close() {
    topicStatsClient.close();
  }
}
