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
import com.google.api.gax.core.ExecutorAsBackgroundResource;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetryingExecutor;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceBlockingStub;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CursorClientImpl extends ApiResourceAggregation implements CursorClient {
  private final CloudRegion region;
  private final CursorServiceBlockingStub stub;
  private final RetryingExecutor<Map<Partition, Offset>> listRetryingExecutor;
  private final RetryingExecutor<Void> voidRetryingExecutor;

  public CursorClientImpl(
      CloudRegion region, CursorServiceBlockingStub stub, RetrySettings retrySettings) {
    this(
        region,
        stub,
        retrySettings,
        // TODO: Consider allowing tuning in the future.
        Executors.newScheduledThreadPool(6));
  }

  private CursorClientImpl(
      CloudRegion region,
      CursorServiceBlockingStub stub,
      RetrySettings retrySettings,
      ScheduledExecutorService executor) {
    super(new ExecutorAsBackgroundResource(executor));
    this.region = region;
    this.stub = stub;
    this.listRetryingExecutor = RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.voidRetryingExecutor = RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  // CursorClient Implementation
  @Override
  public ApiFuture<Map<Partition, Offset>> listPartitionCursors(SubscriptionPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          ListPartitionCursorsResponse response =
              stub.listPartitionCursors(
                  ListPartitionCursorsRequest.newBuilder().setParent(path.toString()).build());
          ImmutableMap.Builder<Partition, Offset> resultBuilder = ImmutableMap.builder();
          for (PartitionCursor partitionCursor : response.getPartitionCursorsList()) {
            resultBuilder.put(
                Partition.of(partitionCursor.getPartition()),
                Offset.of(partitionCursor.getCursor().getOffset()));
          }
          return resultBuilder.build();
        },
        listRetryingExecutor);
  }

  @Override
  public ApiFuture<Void> commitCursor(SubscriptionPath path, Partition partition, Offset offset) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          CommitCursorResponse unusedResponse =
              stub.commitCursor(
                  CommitCursorRequest.newBuilder()
                      .setSubscription(path.toString())
                      .setPartition(partition.value())
                      .setCursor(Cursor.newBuilder().setOffset(offset.value()))
                      .build());
          return null;
        },
        voidRetryingExecutor);
  }
}
