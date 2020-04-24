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

// [START pubsublite_get_topic]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientBuilder;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.Topic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetTopicExample {

  public static void runGetTopicExample() {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    long PROJECT_NUMBER = 123456789L;
    String TOPIC_NAME = "Your Lite Topic Name";

    GetTopicExample.getTopicExample(CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME);
  }

  public static void getTopicExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String TOPIC_NAME) {

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setTopicName(topicName)
              .build();

      ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

      // Create admin client
      AdminClient adminClient =
          AdminClientBuilder.builder().setRegion(cloudRegion).setExecutor(executor).build();

      Topic topic = adminClient.getTopic(topicPath).get();
      long numPartitions = adminClient.getTopicPartitionCount(topicPath).get();

      System.out.println(
          "Topic: " + topic.getAllFields() + " has " + numPartitions + " partition(s).");

      executor.shutdown();
      executor.awaitTermination(10, TimeUnit.SECONDS);
    } catch (Throwable t) {
      System.out.println("Error in test: " + t);
    }
  }
}
// [END pubsublite_get_topic]
