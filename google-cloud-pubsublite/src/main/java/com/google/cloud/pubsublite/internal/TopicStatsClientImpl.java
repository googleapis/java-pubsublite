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
package com.google.cloud.pubsublite.internal;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.ExecutorAsBackgroundResource;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetryingExecutor;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc;
import io.grpc.StatusException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TopicStatsClientImpl implements BackgroundResource, TopicStatsClient {
  private final ExecutorAsBackgroundResource executorResource;
  private final CloudRegion region;
  private final TopicStatsServiceGrpc.TopicStatsServiceBlockingStub stub;
  private final RetryingExecutor<ComputeMessageStatsResponse> retryingExecutor;

  public TopicStatsClientImpl(
      CloudRegion region,
      TopicStatsServiceGrpc.TopicStatsServiceBlockingStub stub,
      RetrySettings retrySettings) {
    this(
        region,
        stub,
        retrySettings,
        // TODO: Consider allowing tuning in the future.
        Executors.newScheduledThreadPool(6));
  }

  private TopicStatsClientImpl(
      CloudRegion region,
      TopicStatsServiceGrpc.TopicStatsServiceBlockingStub stub,
      RetrySettings retrySettings,
      ScheduledExecutorService executor) {
    this.executorResource = new ExecutorAsBackgroundResource(executor);
    this.region = region;
    this.stub = stub;
    this.retryingExecutor = RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  // BackgroundResource implementation.
  @Override
  public void shutdown() {
    executorResource.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return executorResource.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return executorResource.isTerminated();
  }

  @Override
  public void shutdownNow() {
    executorResource.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return executorResource.awaitTermination(duration, unit);
  }

  @Override
  public void close() throws StatusException {
    try {
      executorResource.close();
    } catch (Exception e) {
      throw ExtractStatus.toCanonical(e);
    }
  }

  // TopicStatsClient Implementation
  @Override
  public ApiFuture<ComputeMessageStatsResponse> computeMessageStats(
      TopicPath path, Partition partition, Offset start, Offset end) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          return stub.computeMessageStats(
              ComputeMessageStatsRequest.newBuilder()
                  .setTopic(path.value())
                  .setPartition(partition.value())
                  .setStartCursor(Cursor.newBuilder().setOffset(start.value()).build())
                  .setEndCursor(Cursor.newBuilder().setOffset(end.value()).build())
                  .build());
        },
        retryingExecutor);
  }
}
