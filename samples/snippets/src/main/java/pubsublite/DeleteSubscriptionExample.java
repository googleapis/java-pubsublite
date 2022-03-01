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

// [START pubsublite_delete_subscription]
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudRegionOrZone;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import java.util.concurrent.ExecutionException;

public class DeleteSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing subscription.
    String subscriptionId = "your-subscription-id";
    long projectNumber = Long.parseLong("123456789");
    boolean regional = false;

    deleteSubscriptionExample(cloudRegion, zoneId, projectNumber, subscriptionId, regional);
  }

  public static void deleteSubscriptionExample(
      String cloudRegion, char zoneId, long projectNumber, String subscriptionId, boolean regional)
      throws Exception {

    CloudRegionOrZone location = null;

    if (regional) {
      location = CloudRegionOrZone.of(CloudRegion.of(cloudRegion));
    } else {
      location = CloudRegionOrZone.of(CloudZone.of(CloudRegion.of(cloudRegion), zoneId));
    }

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setLocation(location)
            .setProject(ProjectNumber.of(projectNumber))
            .setName(SubscriptionName.of(subscriptionId))
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      adminClient.deleteSubscription(subscriptionPath).get();
      System.out.println(subscriptionPath + " deleted successfully.");
    } catch (ExecutionException e) {
      try {
        throw e.getCause();
      } catch (NotFoundException notFound) {
        System.out.println("This subscription is not found.");
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
// [END pubsublite_delete_subscription]
