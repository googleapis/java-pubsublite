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

// [START pubsublite_update_topic]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.Durations;
import io.grpc.StatusException;
import java.util.Arrays;

public class UpdateTopicExample {

  public static void runUpdateTopicExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    String TOPIC_NAME = "Your Topic Name"; // Please use an existing topic
    long PROJECT_NUMBER = Long.parseLong("123456789");

    UpdateTopicExample.updateTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
  }

  public static void updateTopicExample(
      String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER, String TOPIC_NAME) throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
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
                      // Set publishing throughput to 4 times the standard partition
                      // throughput of 4 MiB per sec. This must be in the range [1,4]. A
                      // topic with `scale` of 2 and count of 10 is charged for 20 partitions.
                      .setScale(4)
                      .build())
              .setRetentionConfig(
                  RetentionConfig.newBuilder()
                      // Set storage per partition to 200 GiB. This must be 30 GiB-10 TiB.
                      // If the number of bytes stored in any of the topic's partitions grows
                      // beyond this value, older messages will be dropped to make room for
                      // newer ones, regardless of the value of `period`.
                      // Be careful when decreasing storage per partition as it may cause
                      // lost messages.
                      .setPerPartitionBytes(200 * 1024 * 1024 * 1024L)
                      .setPeriod(Durations.fromDays(7)))
              .setName(topicPath.value())
              .build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
        Topic topicBeforeUpdate = adminClient.getTopic(topicPath).get();
        System.out.println("Before update: " + topicBeforeUpdate.getAllFields());

        Topic topicAfterUpdate = adminClient.updateTopic(topic, MASK).get();
        System.out.println("After update: " + topicAfterUpdate.getAllFields());
      }

    } catch (StatusException statusException) {
      System.out.println("Failed to update topic: " + statusException);
      System.out.println(statusException.getStatus().getCode());
      System.out.println(statusException.getStatus());
    }
  }
}
// [END pubsublite_update_topic]
