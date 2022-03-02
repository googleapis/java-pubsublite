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

// [START pubsublite_list_subscriptions_in_project]
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudRegionOrZone;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.proto.Subscription;
import java.util.List;

public class ListSubscriptionsInProjectExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    long projectNumber = Long.parseLong("123456789");
    // True if using a regional location. False if using a zonal location.
    // https://cloud.google.com/pubsub/lite/docs/topics
    boolean regional = true;

    listSubscriptionsInProjectExample(cloudRegion, zoneId, projectNumber, regional);
  }

  public static void listSubscriptionsInProjectExample(
      String cloudRegion, char zoneId, long projectNumber, boolean regional) throws Exception {

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    CloudRegionOrZone location;
    if (regional) {
      location = CloudRegionOrZone.of(CloudRegion.of(cloudRegion));
    } else {
      location = CloudRegionOrZone.of(CloudZone.of(CloudRegion.of(cloudRegion), zoneId));
    }

    LocationPath locationPath =
        LocationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(location)
            .build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      List<Subscription> subscriptions = adminClient.listSubscriptions(locationPath).get();
      for (Subscription subscription : subscriptions) {
        System.out.println(subscription.getAllFields());
      }
      System.out.println(subscriptions.size() + " subscription(s) listed in the project.");
    }
  }
}
// [END pubsublite_list_subscriptions_in_project]
