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

// [START pubsublite_delete_topic]
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudRegionOrZone;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import java.util.concurrent.ExecutionException;

public class DeleteTopicExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing topic.
    String topicId = "your-topic-id";
    long projectNumber = Long.parseLong("123456789");
    // True if using a regional location. False if using a zonal location.
    // https://cloud.google.com/pubsub/lite/docs/topics
    boolean regional = false;

    deleteTopicExample(cloudRegion, zoneId, projectNumber, topicId, regional);
  }

  public static void deleteTopicExample(
      String cloudRegion, char zoneId, long projectNumber, String topicId, boolean regional)
      throws Exception {

    CloudRegionOrZone location;
    if (regional) {
      location = CloudRegionOrZone.of(CloudRegion.of(cloudRegion));
    } else {
      location = CloudRegionOrZone.of(CloudZone.of(CloudRegion.of(cloudRegion), zoneId));
    }

    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(location)
            .setName(TopicName.of(topicId))
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      adminClient.deleteTopic(topicPath).get();
      if (regional) {
        System.out.println(topicPath.toString() + " (regional topic) deleted successfully.");
      } else {
        System.out.println(topicPath.toString() + " (zonal topic) deleted successfully.");
      }
    } catch (ExecutionException e) {
      try {
        throw e.getCause();
      } catch (NotFoundException notFound) {
        System.out.println("This topic is not found.");
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
} // [END pubsublite_delete_topic]
