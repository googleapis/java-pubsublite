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

// [START pubsublite_list_topics]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.LocationPaths;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.proto.Topic;
import io.grpc.StatusRuntimeException;
import java.util.List;

public class ListTopicsExample {

  public static void runListTopicsExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    long PROJECT_NUMBER = 123456789L;

    ListTopicsExample.listTopicsExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER);
  }

  public static void listTopicsExample(String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);

      LocationPath locationPath =
          LocationPaths.newBuilder().setProjectNumber(projectNum).setZone(zone).build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
        List<Topic> topics = adminClient.listTopics(locationPath).get();
        for (Topic t : topics) {
          System.out.println(t.getAllFields());
        }
        System.out.println(topics.size() + " topic(s) listed.");
      }

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to list topics: " + e.toString());
    }
  }
}
// [END pubsublite_list_topics]
