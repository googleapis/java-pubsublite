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
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.DeliveryRequirement;

public class CreateSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    String topicId = "your-topic-id";
    String subscriptionId = "your-subscription-id";
    long projectNumber = Long.parseLong("123456789");

    createSubscriptionExample(cloudRegion, zoneId, projectNumber, topicId, subscriptionId);
  }

  public static void createSubscriptionExample(
      String cloudRegion, char zoneId, long projectNumber, String topicId, String subscriptionId)
      throws Exception {

    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setName(TopicName.of(topicId))
            .build();

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setProject(ProjectNumber.of(projectNumber))
            .setName(SubscriptionName.of(subscriptionId))
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
            .setName(subscriptionPath.toString())
            .setTopic(topicPath.toString())
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Subscription response = adminClient.createSubscription(subscription).get();
      System.out.println(response.getAllFields() + "created successfully.");
    }
  }
}
// [END pubsublite_create_subscription]
