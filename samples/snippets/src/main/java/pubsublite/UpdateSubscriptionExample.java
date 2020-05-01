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
import com.google.cloud.pubsublite.AdminClientBuilder;
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
import io.grpc.StatusRuntimeException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateSubscriptionExample {

  public static void runUpdateSubscriptionExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    String SUBSCRIPTION_NAME = "Your Subscription Name"; // Please use an existing subscription
    long PROJECT_NUMBER = 123456789L;

    UpdateSubscriptionExample.updateSubscriptionExample(
        CLOUD_REGION, ZONE, PROJECT_NUMBER, SUBSCRIPTION_NAME);
  }

  public static void updateSubscriptionExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String SUBSCRIPTION_NAME)
      throws Exception {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);
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
                  // The server does not wait for a published message to be successfully
                  // written to storage before delivering it to subscribers. As such, a
                  // subscriber may receive a message for which the write to storage failed.
                  // If the subscriber re-reads the offset of that message later on, there
                  // may be a gap at that offset.
                  DeliveryConfig.newBuilder()
                      .setDeliveryRequirement(DeliveryRequirement.DELIVER_IMMEDIATELY))
              .setName(subscriptionPath.value())
              .build();

      // Create admin client
      AdminClient adminClient =
          AdminClientBuilder.builder().setRegion(cloudRegion).setExecutor(executor).build();

      Subscription subscriptionBeforeUpdate = adminClient.getSubscription(subscriptionPath).get();
      System.out.println("Before update: " + subscriptionBeforeUpdate.getAllFields());

      Subscription subscriptionAfterUpdate =
          adminClient.updateSubscription(subscription, MASK).get();
      System.out.println("After update: " + subscriptionAfterUpdate.getAllFields());

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to update subscription: " + e.toString());
    } finally {
      executor.shutdown();
      executor.awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
// [END pubsublite_update_subscription]
