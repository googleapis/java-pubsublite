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

// [START pubsublite_create_subscription]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.DeliveryRequirement;
import io.grpc.StatusRuntimeException;

public class CreateSubscriptionExample {

  public static void runCreateSubscriptionExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    long PROJECT_NUMBER = 123456789L;
    String TOPIC_NAME = "Your Topic Name";
    String SUBSCRIPTION_NAME = "Your Subscription Name";

    createSubscriptionExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, SUBSCRIPTION_NAME);
  }

  public static void createSubscriptionExample(
      String CLOUD_REGION,
      char ZONE_ID,
      long PROJECT_NUMBER,
      String TOPIC_NAME,
      String SUBSCRIPTION_NAME)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);
      SubscriptionName subscriptionName = SubscriptionName.of(SUBSCRIPTION_NAME);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setTopicName(topicName)
              .build();

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
              .setTopic(topicPath.value())
              .build();

      AdminClientSettings adminClientSettings =
          AdminClientSettings.newBuilder().setRegion(cloudRegion).build();

      try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {

        Subscription response = adminClient.createSubscription(subscription).get();

        System.out.println(response.getAllFields() + "created successfully.");
      }

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to create a subscription: \n" + e.toString());
    }
  }
}
// [END pubsublite_create_subscription]
