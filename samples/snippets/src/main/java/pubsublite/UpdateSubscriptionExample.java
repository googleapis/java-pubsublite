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

// [START pubsublite_update_subscription]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.DeliveryRequirement;
import com.google.protobuf.FieldMask;
import io.grpc.StatusException;

public class UpdateSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    String SUBSCRIPTION_NAME = "Your Subscription Name"; // Please use an existing subscription
    long PROJECT_NUMBER = Long.parseLong("123456789");

    UpdateSubscriptionExample.updateSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME);
  }

  public static void updateSubscriptionExample(
      String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER, String SUBSCRIPTION_NAME)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      SubscriptionName subscriptionName = SubscriptionName.of(SUBSCRIPTION_NAME);

      FieldMask MASK =
          FieldMask.newBuilder().addPaths("delivery_config.delivery_requirement").build();

      SubscriptionPath subscriptionPath =
          SubscriptionPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setSubscriptionName(subscriptionName)
              .build();

      Subscription subscription =
          Subscription.newBuilder()
              .setDeliveryConfig(
                  // DELIVER_AFTER_STORED ensures that the server won't deliver a published message
                  // to subscribers until the message has been successfully written to storage.
                  DeliveryConfig.newBuilder()
                      .setDeliveryRequirement(DeliveryRequirement.DELIVER_AFTER_STORED))
              .setName(subscriptionPath.value())
              .build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {

        Subscription subscriptionBeforeUpdate = adminClient.getSubscription(subscriptionPath).get();
        System.out.println("Before update: " + subscriptionBeforeUpdate.getAllFields());

        Subscription subscriptionAfterUpdate =
            adminClient.updateSubscription(subscription, MASK).get();
        System.out.println("After update: " + subscriptionAfterUpdate.getAllFields());
      }

    } catch (StatusException statusException) {
      System.out.println("Failed to update subscription: " + statusException);
      System.out.println(statusException.getStatus().getCode());
      System.out.println(statusException.getStatus());
      throw statusException;
    }
  }
}
// [END pubsublite_update_subscription]
