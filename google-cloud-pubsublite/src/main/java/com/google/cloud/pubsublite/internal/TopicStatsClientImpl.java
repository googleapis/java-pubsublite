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
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.common.util.concurrent.MoreExecutors;

public class TopicStatsClientImpl extends ApiResourceAggregation implements TopicStatsClient {
  private final CloudRegion region;
  private final TopicStatsServiceClient serviceClient;

  public TopicStatsClientImpl(CloudRegion region, TopicStatsServiceClient serviceClient) {
    super(serviceClient);
    this.region = region;
    this.serviceClient = serviceClient;
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  @Override
  public ApiFuture<ComputeMessageStatsResponse> computeMessageStats(
      TopicPath path, Partition partition, Offset start, Offset end) {
    return serviceClient
        .computeMessageStatsCallable()
        .futureCall(
            ComputeMessageStatsRequest.newBuilder()
                .setTopic(path.toString())
                .setPartition(partition.value())
                .setStartCursor(Cursor.newBuilder().setOffset(start.value()).build())
                .setEndCursor(Cursor.newBuilder().setOffset(end.value()).build())
                .build());
  }

  @Override
  public ApiFuture<Cursor> computeHeadCursor(TopicPath path, Partition partition) {
    return ApiFutures.transform(
        serviceClient
            .computeHeadCursorCallable()
            .futureCall(
                ComputeHeadCursorRequest.newBuilder()
                    .setTopic(path.toString())
                    .setPartition(partition.value())
                    .build()),
        ComputeHeadCursorResponse::getHeadCursor,
        MoreExecutors.directExecutor());
  }
}
