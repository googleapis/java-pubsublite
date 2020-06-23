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

public class UpdateSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing subscription for the sample to work.
    String subscriptionId = "your-subscription-id";
    long projectNumber = Long.parseLong("123456789");

    updateSubscriptionExample(cloudRegion, zoneId, projectNumber, subscriptionId);
  }

  public static void updateSubscriptionExample(
      String cloudRegion, char zoneId, long projectNumber, String subscriptionId) throws Exception {
    SubscriptionPath subscriptionPath =
        SubscriptionPaths.newBuilder()
            .setZone(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setProjectNumber(ProjectNumber.of(projectNumber))
            .setSubscriptionName(SubscriptionName.of(subscriptionId))
            .build();

    FieldMask MASK =
        FieldMask.newBuilder().addPaths("delivery_config.delivery_requirement").build();

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
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Subscription subscriptionBeforeUpdate = adminClient.getSubscription(subscriptionPath).get();
      System.out.println("Before update: " + subscriptionBeforeUpdate.getAllFields());

      Subscription subscriptionAfterUpdate =
          adminClient.updateSubscription(subscription, MASK).get();
      System.out.println("After update: " + subscriptionAfterUpdate.getAllFields());
    }
  }
}
// [END pubsublite_update_subscription]
