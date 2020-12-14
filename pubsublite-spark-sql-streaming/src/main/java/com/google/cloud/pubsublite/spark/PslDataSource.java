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

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.proto.Subscription;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.spark.sql.sources.DataSourceRegister;
import org.apache.spark.sql.sources.v2.ContinuousReadSupport;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.MicroBatchReadSupport;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousReader;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.types.StructType;

public class PslDataSource implements DataSourceV2, ContinuousReadSupport,
        MicroBatchReadSupport, DataSourceRegister {

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
    long topicPartitionCount;
    try {
      Subscription sub = adminClient.getSubscription(subscriptionPath).get();
      topicPartitionCount =
          adminClient.getTopicPartitionCount(TopicPath.parse(sub.getTopic())).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(
          "Failed to get information of subscription " + pslDataSourceOptions.subscriptionPath(),
          e);
    }
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
  public MicroBatchReader createMicroBatchReader(Optional<StructType> schema,
                                                 String checkpointLocation, DataSourceOptions options) {
    if (schema.isPresent()) {
      throw new IllegalArgumentException(
              "PubSub Lite uses fixed schema and custom schema is not allowed");
    }
    return new PslMicroBatchReader(PslDataSourceOptions.fromSparkDataSourceOptions(options));
  }
}
