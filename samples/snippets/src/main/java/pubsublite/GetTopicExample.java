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

package pubsublite;

// [START pubsublite_get_topic]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.Topic;
import io.grpc.StatusException;

public class GetTopicExample {

  public static void runGetTopicExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    long PROJECT_NUMBER = Long.parseLong("123456789");
    String TOPIC_NAME = "Your Lite Topic Name";

    GetTopicExample.getTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
  }

  public static void getTopicExample(
      String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER, String TOPIC_NAME) throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setTopicName(topicName)
              .build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {

        Topic topic = adminClient.getTopic(topicPath).get();
        long numPartitions = adminClient.getTopicPartitionCount(topicPath).get();

        System.out.println(
            "Topic: " + topic.getAllFields() + " has " + numPartitions + " partition(s).");
      }

    } catch (StatusException statusException) {
      System.out.println("Failed to get the topic: " + statusException);
      System.out.println(statusException.getStatus().getCode());
      System.out.println(statusException.getStatus());
    }
  }
}
// [END pubsublite_get_topic]
