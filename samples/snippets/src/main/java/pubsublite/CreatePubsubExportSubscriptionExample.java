/*
 * Copyright 2022 Google LLC
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

// [START pubsublite_create_pubsub_export_subscription]
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudRegionOrZone;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SeekTarget;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ExportConfig;
import com.google.cloud.pubsublite.proto.ExportConfig.PubSubConfig;
import com.google.cloud.pubsublite.proto.ExportConfig.State;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.DeliveryRequirement;
import java.util.concurrent.ExecutionException;

public class CreatePubsubExportSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    String topicId = "your-topic-id";
    String subscriptionId = "your-subscription-id";
    String pubsubTopicId = "destination-topic-id";
    long projectNumber = Long.parseLong("123456789");
    // True if using a regional location. False if using a zonal location.
    // https://cloud.google.com/pubsub/lite/docs/topics
    boolean regional = false;

    createPubsubExportSubscriptionExample(
        cloudRegion, zoneId, projectNumber, topicId, subscriptionId, pubsubTopicId, regional);
  }

  public static void createPubsubExportSubscriptionExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String topicId,
      String subscriptionId,
      String pubsubTopicId,
      boolean regional)
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

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setLocation(location)
            .setProject(ProjectNumber.of(projectNumber))
            .setName(SubscriptionName.of(subscriptionId))
            .build();

    com.google.pubsub.v1.TopicName pubsubTopicName =
        com.google.pubsub.v1.TopicName.of(String.valueOf(projectNumber), pubsubTopicId);

    Subscription subscription =
        Subscription.newBuilder()
            .setDeliveryConfig(
                // Possible values for DeliveryRequirement:
                // - `DELIVER_IMMEDIATELY`
                // - `DELIVER_AFTER_STORED`
                // You may choose whether to wait for a published message to be successfully written
                // to storage before the server delivers it to subscribers. `DELIVER_IMMEDIATELY` is
                // suitable for applications that need higher throughput.
                DeliveryConfig.newBuilder()
                    .setDeliveryRequirement(DeliveryRequirement.DELIVER_IMMEDIATELY))
            .setExportConfig(
                // Configures an export subscription that writes messages to a Pub/Sub topic.
                ExportConfig.newBuilder()
                    // Possible values for State:
                    // - `ACTIVE`: enable message processing.
                    // - `PAUSED`: suspend message processing.
                    .setDesiredState(State.ACTIVE)
                    .setPubsubConfig(
                        PubSubConfig.newBuilder().setTopic(pubsubTopicName.toString())))
            .setName(subscriptionPath.toString())
            .setTopic(topicPath.toString())
            .build();

    // Target location within the message backlog that the subscription should be initialized to.
    SeekTarget initialLocation = SeekTarget.of(BacklogLocation.BEGINNING);

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(location.extractRegion()).build();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources, or
    // use "try-with-close" statement to do this automatically.
    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Subscription response = adminClient.createSubscription(subscription, initialLocation).get();
      System.out.println(response.getAllFields() + " created successfully.");
    } catch (ExecutionException e) {
      try {
        throw e.getCause();
      } catch (AlreadyExistsException alreadyExists) {
        System.out.println("This subscription already exists.");
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
// [END pubsublite_create_pubsub_export_subscription]
