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
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig.Capacity;
import com.google.cloud.pubsublite.proto.Topic.ReservationConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.Durations;
import java.util.Arrays;

public class UpdateTopicExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    String topicId = "your-topic-id";
    String reservationId = "your-reservation-id";
    long projectNumber = Long.parseLong("123456789");
    boolean regional = true;

    updateTopicExample(cloudRegion, zoneId, projectNumber, topicId, reservationId, regional);
  }

  public static void updateTopicExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String topicId,
      String reservationId,
      boolean regional)
      throws Exception {

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

    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    Iterable<String> iterablePaths =
        Arrays.asList(
            "partition_config.scale",
            "retention_config.per_partition_bytes",
            "retention_config.period",
            "reservation_config.throughput_reservation");

    FieldMask fieldMask = FieldMask.newBuilder().addAllPaths(iterablePaths).build();

    Topic topic =
        Topic.newBuilder()
            .setPartitionConfig(
                PartitionConfig.newBuilder()
                    .setCapacity(
                        Capacity.newBuilder()
                            .setPublishMibPerSec(16)
                            .setSubscribeMibPerSec(32)
                            .build())
                    .build())
            .setRetentionConfig(
                RetentionConfig.newBuilder()
                    // Set storage per partition to 32 GiB. This must be 30 GiB-10 TiB.
                    // If the number of bytes stored in any of the topic's partitions grows
                    // beyond this value, older messages will be dropped to make room for
                    // newer ones, regardless of the value of `period`.
                    // Be careful when decreasing storage per partition as it may cause
                    // lost messages.
                    .setPerPartitionBytes(32 * 1024 * 1024 * 1024L)
                    .setPeriod(Durations.fromDays(7)))
            .setReservationConfig(
                ReservationConfig.newBuilder()
                    .setThroughputReservation(reservationPath.toString())
                    .build())
            .setName(topicPath.toString())
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Topic topicBeforeUpdate = adminClient.getTopic(topicPath).get();
      System.out.println("Before update: " + topicBeforeUpdate.getAllFields());

      Topic topicAfterUpdate = adminClient.updateTopic(topic, fieldMask).get();
      System.out.println("After update: " + topicAfterUpdate.getAllFields());
    }
  }
}
// [END pubsublite_update_topic]
