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
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Map;

public class CursorClientImpl extends ApiResourceAggregation implements CursorClient {
  private final CloudRegion region;
  private final CursorServiceClient serviceClient;

  public CursorClientImpl(CloudRegion region, CursorServiceClient serviceClient) {
    super(serviceClient);
    this.region = region;
    this.serviceClient = serviceClient;
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  @Override
  public ApiFuture<Map<Partition, Offset>> listPartitionCursors(SubscriptionPath path) {
    return ApiFutures.transform(
        serviceClient
            .listPartitionCursorsCallable()
            .futureCall(
                ListPartitionCursorsRequest.newBuilder().setParent(path.toString()).build()),
        response -> {
          ImmutableMap.Builder<Partition, Offset> resultBuilder = ImmutableMap.builder();
          for (PartitionCursor partitionCursor : response.getPartitionCursorsList()) {
            resultBuilder.put(
                Partition.of(partitionCursor.getPartition()),
                Offset.of(partitionCursor.getCursor().getOffset()));
          }
          return resultBuilder.build();
        },
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Void> commitCursor(SubscriptionPath path, Partition partition, Offset offset) {
    return ApiFutures.transform(
        serviceClient
            .commitCursorCallable()
            .futureCall(
                CommitCursorRequest.newBuilder()
                    .setSubscription(path.toString())
                    .setPartition(partition.value())
                    .setCursor(Cursor.newBuilder().setOffset(offset.value()))
                    .build()),
        x -> null,
        MoreExecutors.directExecutor());
  }
}
