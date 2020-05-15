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

// [START pubsublite_get_subscription]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.Subscription;
import io.grpc.StatusException;

public class GetSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    long PROJECT_NUMBER = Long.parseLong("123456789");
    String SUBSCRIPTION_NAME = "Your Lite Subscription Name";

    GetSubscriptionExample.getSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME);
  }

  public static void getSubscriptionExample(
      String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER, String SUBSCRIPTION_NAME)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      SubscriptionName subscriptionName = SubscriptionName.of(SUBSCRIPTION_NAME);

      SubscriptionPath subscriptionPath =
          SubscriptionPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setSubscriptionName(subscriptionName)
              .build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {

        Subscription subscription = adminClient.getSubscription(subscriptionPath).get();

        System.out.println("Subscription: " + subscription.getAllFields());
      }

    } catch (StatusException statusException) {
      System.out.println("Failed to get the subscription: " + statusException);
      System.out.println(statusException.getStatus().getCode());
      System.out.println(statusException.getStatus());
      throw statusException;
    }
  }
}
// [END pubsublite_get_subscription]
