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

// [START pubsublite_create_topic]

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
import io.grpc.StatusRuntimeException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CreateTopicExample {

  public static void runCreateTopicExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    String TOPIC_NAME = "Your Topic Name";
    long PROJECT_NUMBER = 123456789L;
    Integer PARTITIONS = 1;

    CreateTopicExample.createTopicExample(
        CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME, PARTITIONS);
  }

  public static void createTopicExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String TOPIC_NAME, int PARTITIONS)
      throws Exception {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

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

      Topic topic =
          Topic.newBuilder()
              .setPartitionConfig(
                  PartitionConfig.newBuilder()
                      // Set publishing throughput to 1*4 MiB per sec. This must be 1-4.
                      .setScale(1)
                      .setCount(PARTITIONS))
              .setRetentionConfig(
                  RetentionConfig.newBuilder()
                      .setPeriod(Durations.fromDays(1))
                      // Set storage per partition to 100 GiB. This must be 30 GiB-10 TiB.
                      .setPerPartitionBytes(100_000_000_000L))
              .setName(topicPath.value())
              .build();

      // Create admin client
      AdminClient adminClient =
          AdminClientBuilder.builder().setRegion(cloudRegion).setExecutor(executor).build();

      System.out.println(
          adminClient.createTopic(topic).get().getAllFields() + " created successfully.");
    } catch (StatusRuntimeException e) {
      System.out.println("Failed to create a topic: \n" + e.toString());
    } finally {
      executor.shutdown();
      executor.awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
// [END pubsublite_create_topic]
