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

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Endpoints;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.CursorClientSettings;
import com.google.cloud.pubsublite.internal.Lazy;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.internal.TopicStatsClientSettings;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.cloud.pubsublite.v1.TopicStatsServiceSettings;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.threeten.bp.Duration;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

public class ListHead {

//    private static final Lazy<ExecutorProvider> PROVIDER =
//            new Lazy<>(
//                    () ->
//                            FixedExecutorProvider.create(
//                                    MoreExecutors.getExitingScheduledExecutorService(
//                                            new ScheduledThreadPoolExecutor(
//                                                    Math.max(4, Runtime.getRuntime().availableProcessors())))));
//
//    public static <
//            Settings extends ClientSettings<Settings>,
//            Builder extends ClientSettings.Builder<Settings, Builder>>
//    Settings addDefaultSettings(CloudRegion target, Builder builder) throws ApiException {
//        try {
//            return builder
//                    .setEndpoint("us-central1-staging-pubsublite.sandbox.googleapis.com:443")
//                    .setExecutorProvider(PROVIDER.get())
//                    .setTransportChannelProvider(
//                            InstantiatingGrpcChannelProvider.newBuilder()
//                                    .setMaxInboundMessageSize(Integer.MAX_VALUE)
//                                    .setKeepAliveTime(Duration.ofMinutes(1))
//                                    .setKeepAliveWithoutCalls(true)
//                                    .setKeepAliveTimeout(Duration.ofMinutes(1))
//                                    .build())
//                    .build();
//        } catch (Throwable t) {
//            throw toCanonical(t).underlying;
//        }
//    }
//
//
//    private static TopicStatsServiceClient newTopicStatsServiceClient() {
//        try {
//            return TopicStatsServiceClient.create(
//                    addDefaultSettings(
//                            CloudRegion.of("us-central1"),
//                            TopicStatsServiceSettings.newBuilder()));
//        } catch (IOException e) {
//            throw new IllegalStateException("Unable to create TopicStatsServiceClient.");
//        }
//    }

    public static void main(String[] args) throws Exception {


        TopicStatsClientSettings topicStatsClientSettings = TopicStatsClientSettings.newBuilder()
                .setRegion(CloudRegion.of("us-central1"))
//                .setServiceClient(newTopicStatsServiceClient())
                .build();
        TopicStatsClient topicStatsClient = TopicStatsClient.create(topicStatsClientSettings);
//        for (int i = 0; i < 10; i++) {
//            long headOffsetPartition = topicStatsClient.computeHeadCursor(TopicPath.newBuilder()
//                    .setProject(ProjectNumber.of(129988248131L))
//                    .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'b'))
//                    .setName(TopicName.of("test-spark-jiangmichael-10-partition"))
//                    .build(), Partition.of(i)).get().getOffset();
//            System.out.println("headoffset partition " + i + ":"+ headOffsetPartition);
//        }


        while (true) {
            long headOffsetPartition = topicStatsClient.computeHeadCursor(TopicPath.newBuilder()
                    .setProject(ProjectNumber.of(129988248131L))
                    .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'b'))
                    .setName(TopicName.of("test-spark-jiangmichael-10-partition"))
                    .build(), Partition.of(0)).get().getOffset();
            System.out.println("headoffset partition " + 0 + ":"+ headOffsetPartition);
            Thread.sleep(1000);
        }


    }
}
