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

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Rate limited HeadOffsetReader, utilizing a LoadingCache that refreshes all partitions head
 * offsets for the topic at most once per minute.
 */
public class LimitingHeadOffsetReader implements PerTopicHeadOffsetReader {

  private final TopicStatsClient topicStatsClient;
  private final TopicPath topic;
  private final long topicPartitionCount;
  private final AsyncLoadingCache<Partition, Offset> cachedHeadOffsets;

  @VisibleForTesting
  public LimitingHeadOffsetReader(
      TopicStatsClient topicStatsClient, TopicPath topic, long topicPartitionCount, Ticker ticker) {
    this.topicStatsClient = topicStatsClient;
    this.topic = topic;
    this.topicPartitionCount = topicPartitionCount;
    this.cachedHeadOffsets =
        Caffeine.newBuilder()
            .ticker(ticker)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .buildAsync(this::loadHeadOffset);
  }

  private CompletableFuture<Offset> loadHeadOffset(Partition partition, Executor executor) {

    CompletableFuture<Offset> result = new CompletableFuture<>();
    ApiFutures.addCallback(
        topicStatsClient.computeHeadCursor(topic, partition),
        new ApiFutureCallback<Cursor>() {
          @Override
          public void onFailure(Throwable t) {
            result.completeExceptionally(t);
          }

          @Override
          public void onSuccess(Cursor c) {
            result.complete(Offset.of(c.getOffset()));
          }
        },
        MoreExecutors.directExecutor());
    return result;
  }

  @Override
  public PslSourceOffset getHeadOffset() {
    Set<Partition> keySet = new HashSet<>();
    for (int i = 0; i < topicPartitionCount; i++) {
      keySet.add(Partition.of(i));
    }
    CompletableFuture<Map<Partition, Offset>> future = cachedHeadOffsets.getAll(keySet);
    try {
      return PslSourceOffset.builder().partitionOffsetMap(future.get()).build();
    } catch (Throwable t) {
      throw new IllegalStateException("Unable to compute head offset for topic: " + topic, t);
    }
  }

  @Override
  public void close() {
    topicStatsClient.close();
  }
}
