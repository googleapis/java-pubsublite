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

import com.google.auto.service.AutoService;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.v2.ContinuousReadSupport;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.MicroBatchReadSupport;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousReader;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.types.StructType;

@AutoService(DataSourceRegister.class)
public final class PslDataSource
    implements DataSourceV2, ContinuousReadSupport, MicroBatchReadSupport, DataSourceRegister {

  @Override
  public String shortName() {
    return "pubsublite";
  }

  @Override
  public ContinuousReader createContinuousReader(
      Optional<StructType> schema, String checkpointLocation, DataSourceOptions options) {
    if (schema.isPresent()) {
      throw new IllegalArgumentException(
          "PubSub Lite uses fixed schema and custom schema is not allowed");
    }

    PslDataSourceOptions pslDataSourceOptions =
        PslDataSourceOptions.fromSparkDataSourceOptions(options);
    CursorClient cursorClient = pslDataSourceOptions.newCursorClient();
    AdminClient adminClient = pslDataSourceOptions.newAdminClient();
    SubscriptionPath subscriptionPath = pslDataSourceOptions.subscriptionPath();
    long topicPartitionCount = PartitionLookupUtils.numPartitions(subscriptionPath, adminClient);
    MultiPartitionCommitter committer =
        new MultiPartitionCommitterImpl(
            topicPartitionCount,
            (partition) ->
                CommitterBuilder.newBuilder()
                    .setSubscriptionPath(subscriptionPath)
                    .setPartition(partition)
                    .setServiceClient(pslDataSourceOptions.newCursorServiceClient())
                    .build());
    return new PslContinuousReader(
        cursorClient,
        committer,
        subscriptionPath,
        Objects.requireNonNull(pslDataSourceOptions.flowControlSettings()),
        topicPartitionCount);
  }

  @Override
  public MicroBatchReader createMicroBatchReader(
      Optional<StructType> schema, String checkpointLocation, DataSourceOptions options) {
    if (schema.isPresent()) {
      throw new IllegalArgumentException(
          "PubSub Lite uses fixed schema and custom schema is not allowed");
    }

    PslDataSourceOptions pslDataSourceOptions =
        PslDataSourceOptions.fromSparkDataSourceOptions(options);
    CursorClient cursorClient = pslDataSourceOptions.newCursorClient();
    AdminClient adminClient = pslDataSourceOptions.newAdminClient();
    SubscriptionPath subscriptionPath = pslDataSourceOptions.subscriptionPath();
    TopicPath topicPath;
    try {
      topicPath = TopicPath.parse(adminClient.getSubscription(subscriptionPath).get().getTopic());
    } catch (Throwable t) {
      throw new IllegalStateException(
          "Unable to get topic for subscription " + subscriptionPath, t);
    }
    long topicPartitionCount = PartitionLookupUtils.numPartitions(topicPath, adminClient);
    MultiPartitionCommitter committer =
        new MultiPartitionCommitterImpl(
            topicPartitionCount,
            (partition) ->
                CommitterBuilder.newBuilder()
                    .setSubscriptionPath(subscriptionPath)
                    .setPartition(partition)
                    .setServiceClient(pslDataSourceOptions.newCursorServiceClient())
                    .build());

    return new PslMicroBatchReader(
        cursorClient,
        committer,
        subscriptionPath,
        PslSparkUtils.toSparkSourceOffset(getHeadOffset(topicPath)),
        Objects.requireNonNull(pslDataSourceOptions.flowControlSettings()),
        topicPartitionCount);
  }

  private static PslSourceOffset getHeadOffset(TopicPath topicPath) {
    // TODO(jiangmichael): Replace it with real implementation.
    HeadOffsetReader headOffsetReader =
        new HeadOffsetReader() {
          @Override
          public PslSourceOffset getHeadOffset(TopicPath topic) {
            return PslSourceOffset.builder()
                .partitionOffsetMap(
                    ImmutableMap.of(
                        Partition.of(0), com.google.cloud.pubsublite.Offset.of(50),
                        Partition.of(1), com.google.cloud.pubsublite.Offset.of(50)))
                .build();
          }

          @Override
          public void close() {}
        };
    try {
      return headOffsetReader.getHeadOffset(topicPath);
    } catch (CheckedApiException e) {
      throw new IllegalStateException("Unable to get head offset for topic " + topicPath, e);
    }
  }
}
