/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pubsublite;

// [START pubsublite_update_topic]

import com.google.api.client.http.MultipartContent;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientBuilder;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.protobuf.util.Durations;
import com.google.protobuf.FieldMask;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateTopicExample {

  public static void runUpdateTopicExample() {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    String TOPIC_NAME = "Your Topic Name"; // Please use an existing topic
    long PROJECT_NUMBER = 123456789L;

    UpdateTopicExample.updateTopicExample(CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME);
  }

  public static void updateTopicExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String TOPIC_NAME) {

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);
      Iterable<String> iterablePaths =
          Arrays.asList(
              "partition_config.scale",
              "retention_config.per_partition_bytes",
              "retention_config.period");
      FieldMask MASK = FieldMask.newBuilder().addAllPaths(iterablePaths).build();

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setTopicName(topicName)
              .build();

      Topic topic =
          Topic.newBuilder()
              .setPartitionConfig(
                  PartitionConfig.newBuilder()
                      .setScale(
                          4) // Set publishing throughput to 4*4 MiB per sec. This must be 1-4.
                      .build())
              .setRetentionConfig(
                  RetentionConfig.newBuilder()
                      .setPerPartitionBytes(
                          200_000_000_000L) // 200 GiB. This must be 30 GiB-10 TiB.
                      .setPeriod(Durations.fromDays(7)))
              .setName(topicPath.value())
              .build();

      ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

      // Create admin client
      AdminClient adminClient =
          AdminClientBuilder.builder().setRegion(cloudRegion).setExecutor(executor).build();

      Topic topicBeforeUpdate = adminClient.getTopic(topicPath).get();
      System.out.println("Before update: " + topicBeforeUpdate.getAllFields());

      Topic topicAfterUpdate = adminClient.updateTopic(topic, MASK).get();
      System.out.println("After update: " + topicAfterUpdate.getAllFields());

      executor.shutdown();
      executor.awaitTermination(10, TimeUnit.SECONDS);

    } catch (Throwable t) {
      System.out.println("Error in test: " + t);
    }
  }
}
// [END pubsublite_update_topic]
