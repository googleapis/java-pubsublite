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
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig.Capacity;
import com.google.cloud.pubsublite.proto.Topic.ReservationConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.protobuf.util.Durations;

public class CreateTopicExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'a';
    String topicId = "your-topic-id";
    String reservationId = "your-reservation-id";
    long projectNumber = Long.parseLong("123456789");
    int partitions = 1;
    boolean regional = false;

    createTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, partitions, regional);
  }

  public static void createTopicExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String topicId,
      String reservationId,
      int partitions,
      boolean regional)
      throws Exception {

    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    TopicPath topicPath = null;
    if (regional) {
      // A regional topic path.
      topicPath =
          TopicPath.newBuilder()
              .setProject(ProjectNumber.of(projectNumber))
              .setLocation(CloudRegion.of(cloudRegion))
              .setName(TopicName.of(topicId))
              .build();
    } else {
      // A zonal topic path.
      topicPath =
          TopicPath.newBuilder()
              .setProject(ProjectNumber.of(projectNumber))
              .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
              .setName(TopicName.of(topicId))
              .build();
    }

    Topic topic =
        Topic.newBuilder()
            .setPartitionConfig(
                PartitionConfig.newBuilder()
                    // Set throughput capacity per partition in MiB/s.
                    .setCapacity(
                        Capacity.newBuilder()
                            // Must be 4-16 MiB/s.
                            .setPublishMibPerSec(4)
                            // Must be 4-32 MiB/s.
                            .setSubscribeMibPerSec(8)
                            .build())
                    .setCount(partitions))
            .setRetentionConfig(
                RetentionConfig.newBuilder()
                    // How long messages are retained.
                    .setPeriod(Durations.fromDays(1))
                    // Set storage per partition to 30 GiB. This must be 30 GiB-10 TiB.
                    // If the number of bytes stored in any of the topic's partitions grows
                    // beyond this value, older messages will be dropped to make room for
                    // newer ones, regardless of the value of `period`.
                    .setPerPartitionBytes(30 * 1024 * 1024 * 1024L))
            .setReservationConfig(
                ReservationConfig.newBuilder()
                    .setThroughputReservation(reservationPath.toString())
                    .build())
            .setName(topicPath.toString())
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Topic response = adminClient.createTopic(topic).get();
      if (regional) {
        System.out.println(response.getAllFields() + " (regional topic) created successfully.");
      } else {
        System.out.println(response.getAllFields() + " (zonal topic) created successfully.");
      }
    }
  }
}
// [END pubsublite_create_topic]
