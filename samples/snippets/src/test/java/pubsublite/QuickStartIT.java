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

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SeekTarget;
import com.google.pubsub.v1.TopicName;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class QuickStartIT {

  private TopicAdminClient topicAdminClient;
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static Random rand = new Random();
  private static final Long projectNumber =
      Long.parseLong(System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER"));
  private static final String cloudRegion = "us-central1";
  private static final char zoneId = (char) (rand.nextInt(3) + 'a');
  private static final String suffix = UUID.randomUUID().toString();
  private static final String reservationId = "lite-reservation-" + suffix;
  private static final String topicId = "lite-topic-" + suffix;
  private static final String subscriptionId = "lite-subscription-" + suffix;
  private static final String exportSubscriptionId = "lite-export-subscription-" + suffix;
  private static final String pubsubTopicId = "pubsub-topic-" + suffix;
  private static final int partitions = 2;
  private static final int messageCount = 10;

  private static final ReservationPath reservationPath =
      ReservationPath.newBuilder()
          .setProject(ProjectNumber.of(projectNumber))
          .setLocation(CloudRegion.of(cloudRegion))
          .setName(ReservationName.of(reservationId))
          .build();
  private static final TopicName pubsubTopicName =
      TopicName.of(projectNumber.toString(), pubsubTopicId);

  private static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
  }

  @Rule public Timeout globalTimeout = Timeout.seconds(300); // 5 minute timeout

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT_NUMBER");
  }

  @Before
  public void setUp() throws Exception {
    topicAdminClient = TopicAdminClient.create();
    topicAdminClient.createTopic(pubsubTopicName);
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() throws Exception {
    topicAdminClient.deleteTopic(pubsubTopicName);
    topicAdminClient.close();
    System.setOut(null);
  }

  @Test
  public void testQuickstart() throws Exception {

    // Create a reservation.
    CreateReservationExample.createReservationExample(
        projectNumber, cloudRegion, reservationId, /*throughputCapacity=*/ 4);
    assertThat(bout.toString()).contains(reservationId);
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Create a regional topic.
    CreateTopicExample.createTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, partitions, /*regional=*/ true);
    assertThat(bout.toString()).contains(" (regional topic) created successfully");

    bout.reset();
    // Create a zonal topic.
    CreateTopicExample.createTopicExample(
        cloudRegion,
        zoneId,
        projectNumber,
        topicId,
        reservationId,
        partitions,
        /*regional=*/ false);
    assertThat(bout.toString()).contains(" (zonal topic) created successfully");

    bout.reset();
    // Get a reservation.
    GetReservationExample.getReservationExample(projectNumber, cloudRegion, reservationId);
    assertThat(bout.toString()).contains(reservationId);
    assertThat(bout.toString()).contains("4 units of throughput capacity.");

    bout.reset();
    // List reservations.
    ListReservationsExample.listReservationsExample(projectNumber, cloudRegion);
    assertThat(bout.toString()).contains("reservation(s) listed");

    bout.reset();
    // Update reservation to have a throughput capacity of 8 units.
    UpdateReservationExample.updateReservationExample(projectNumber, cloudRegion, reservationId, 8);
    assertThat(bout.toString()).contains("throughput_capacity=8");

    bout.reset();
    // Get a regional topic.
    GetTopicExample.getTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    assertThat(bout.toString()).contains(cloudRegion + "/topics/" + topicId);
    assertThat(bout.toString()).contains(String.format("%s partition(s).", partitions));

    bout.reset();
    // Get a zonal topic
    GetTopicExample.getTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString().contains(cloudRegion + "-" + zoneId + "/topics/" + topicId));
    assertThat(bout.toString()).contains(String.format("%s partition(s).", partitions));

    bout.reset();
    // List regional topics.
    ListTopicsExample.listTopicsExample(cloudRegion, zoneId, projectNumber, /*regional=*/ true);
    assertThat(bout.toString().contains(cloudRegion + "/topics/" + topicId));
    assertThat(bout.toString()).contains("topic(s) listed");

    bout.reset();
    // List zonal topics.
    ListTopicsExample.listTopicsExample(cloudRegion, zoneId, projectNumber, /*regional=*/ false);
    assertThat(bout.toString().contains(cloudRegion + "-" + zoneId + "/topics/" + topicId));
    assertThat(bout.toString()).contains("topic(s) listed");

    bout.reset();
    // Update a regional topic.
    UpdateTopicExample.updateTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, /*regional=*/ true);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 34359738368");
    assertThat(bout.toString()).contains("throughput_reservation: \"" + reservationPath.toString());

    bout.reset();
    // Update a zonal topic.
    UpdateTopicExample.updateTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, /*regional=*/ false);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 34359738368");
    assertThat(bout.toString()).contains("throughput_reservation: \"" + reservationPath.toString());

    bout.reset();
    // Create a regional subscription.
    CreateSubscriptionExample.createSubscriptionExample(
        cloudRegion, zoneId, projectNumber, topicId, subscriptionId, /*regional=*/ true);
    assertThat(bout.toString().contains(cloudRegion + "/subscriptions/" + subscriptionId));
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Create a zonal subscription.
    CreateSubscriptionExample.createSubscriptionExample(
        cloudRegion, zoneId, projectNumber, topicId, subscriptionId, /*regional=*/ false);
    assertThat(
        bout.toString().contains(cloudRegion + "-" + zoneId + "/subscriptions/" + subscriptionId));
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Create a regional export subscription.
    CreatePubsubExportSubscriptionExample.createPubsubExportSubscriptionExample(
        cloudRegion,
        zoneId,
        projectNumber,
        topicId,
        exportSubscriptionId,
        pubsubTopicId,
        /*regional=*/ true);
    assertThat(bout.toString().contains(cloudRegion + "/subscriptions/" + exportSubscriptionId));
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Create a zonal export subscription.
    CreatePubsubExportSubscriptionExample.createPubsubExportSubscriptionExample(
        cloudRegion,
        zoneId,
        projectNumber,
        topicId,
        exportSubscriptionId,
        pubsubTopicId,
        /*regional=*/ false);
    assertThat(
        bout.toString()
            .contains(cloudRegion + "-" + zoneId + "/subscriptions/" + exportSubscriptionId));
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Get a regional subscription.
    GetSubscriptionExample.getSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ true);
    assertThat(bout.toString().contains(cloudRegion + "/subscriptions/" + subscriptionId));

    bout.reset();
    // Get a zonal subscription.
    GetSubscriptionExample.getSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ false);
    assertThat(
        bout.toString().contains(cloudRegion + "-" + zoneId + "/subscriptions/" + subscriptionId));

    bout.reset();
    // List subscriptions in a regional topic.
    ListSubscriptionsInTopicExample.listSubscriptionsInTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    assertThat(bout.toString()).contains("subscription(s) listed in the regional topic");

    // List subscriptions in a zonal topic.
    ListSubscriptionsInTopicExample.listSubscriptionsInTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString()).contains("subscription(s) listed in the zonal topic");

    bout.reset();
    // List regional subscriptions in a project.
    ListSubscriptionsInProjectExample.listSubscriptionsInProjectExample(
        cloudRegion, zoneId, projectNumber, /*regional=*/ true);
    assertThat(bout.toString()).contains("subscription(s) listed in the project");

    bout.reset();
    // List zonal subscriptions in a project.
    ListSubscriptionsInProjectExample.listSubscriptionsInProjectExample(
        cloudRegion, zoneId, projectNumber, /*regional=*/ false);
    assertThat(bout.toString()).contains("subscription(s) listed in the project");

    bout.reset();
    // Update a regional subscription.
    UpdateSubscriptionExample.updateSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ true);
    assertThat(bout.toString()).contains("delivery_requirement: DELIVER_AFTER_STORED");

    bout.reset();
    // Update a zonal subscription.
    UpdateSubscriptionExample.updateSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ false);
    assertThat(bout.toString()).contains("delivery_requirement: DELIVER_AFTER_STORED");

    bout.reset();
    // Publish to a regional topic.
    PublisherExample.publisherExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount, /*regional=*/ true);
    assertThat(bout.toString()).contains("Published " + messageCount + " messages.");

    bout.reset();
    // Publish to a zonal topic.
    PublisherExample.publisherExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount, /*regional=*/ false);
    assertThat(bout.toString()).contains("Published " + messageCount + " messages.");

    bout.reset();
    // Publish with ordering key to a regional topic.
    PublishWithOrderingKeyExample.publishWithOrderingKeyExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    assertThat(bout.toString()).contains("Published a message with ordering key:");

    bout.reset();
    // Publish with ordering key to a zonal topic.
    PublishWithOrderingKeyExample.publishWithOrderingKeyExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString()).contains("Published a message with ordering key:");

    bout.reset();
    // Publish messages with custom attributes to a regional topic.
    PublishWithCustomAttributesExample.publishWithCustomAttributesExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    assertThat(bout.toString()).contains("Published a message with custom attributes:");

    bout.reset();
    // Publish messages with custom attributes to a zonal topic.
    PublishWithCustomAttributesExample.publishWithCustomAttributesExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString()).contains("Published a message with custom attributes:");

    bout.reset();
    // Publish with batch settings to a regional topic.
    PublishWithBatchSettingsExample.publishWithBatchSettingsExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount, /*regional=*/ true);
    assertThat(bout.toString())
        .contains("Published " + messageCount + " messages with batch settings.");

    bout.reset();
    // Publish with batch settings to a zonal topic.
    PublishWithBatchSettingsExample.publishWithBatchSettingsExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount, /*regional=*/ false);
    assertThat(bout.toString())
        .contains("Published " + messageCount + " messages with batch settings.");

    bout.reset();
    // Subscribe to a regional subscription.
    SubscriberExample.subscriberExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ true);
    assertThat(bout.toString()).contains("Listening");
    for (int i = 0; i < messageCount; ++i) {
      assertThat(bout.toString()).contains(String.format("Data : message-%s", i));
    }
    assertThat(bout.toString()).contains("Subscriber is shut down: TERMINATED");

    bout.reset();
    // Subscribe to a zonal subscription.
    SubscriberExample.subscriberExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ false);
    assertThat(bout.toString()).contains("Listening");
    for (int i = 0; i < messageCount; ++i) {
      assertThat(bout.toString()).contains(String.format("Data : message-%s", i));
    }
    assertThat(bout.toString()).contains("Subscriber is shut down: TERMINATED");

    bout.reset();
    // Seek in a regional subscription.
    SeekSubscriptionExample.seekSubscriptionExample(
        cloudRegion,
        zoneId,
        projectNumber,
        subscriptionId,
        SeekTarget.of(BacklogLocation.BEGINNING),
        /*waitForOperation=*/ false,
        /*regional=*/ true);
    assertThat(bout.toString()).contains("initiated successfully");

    bout.reset();
    // Seek in a zonal subscription.
    SeekSubscriptionExample.seekSubscriptionExample(
        cloudRegion,
        zoneId,
        projectNumber,
        subscriptionId,
        SeekTarget.of(BacklogLocation.BEGINNING),
        /*waitForOperation=*/ false,
        /*regional=*/ false);
    assertThat(bout.toString()).contains("initiated successfully");

    bout.reset();
    // Delete a regional subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ true);
    assertThat(bout.toString()).contains(" deleted successfully");

    bout.reset();
    // Delete a zonal subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, /*regional=*/ false);
    assertThat(bout.toString()).contains(" deleted successfully");

    bout.reset();
    // Delete a regional export subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, exportSubscriptionId, /*regional=*/ true);
    assertThat(bout.toString()).contains(" deleted successfully");

    bout.reset();
    // Delete a zonal export subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, exportSubscriptionId, /*regional=*/ false);
    assertThat(bout.toString()).contains(" deleted successfully");

    bout.reset();
    // Delete a regional topic.
    DeleteTopicExample.deleteTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    assertThat(bout.toString()).contains(" (regional topic) deleted successfully");

    bout.reset();
    // Delete a zonal topic.
    DeleteTopicExample.deleteTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString()).contains(" (zonal topic) deleted successfully");

    bout.reset();
    // Delete a reservation.
    DeleteReservationExample.deleteReservationExample(projectNumber, cloudRegion, reservationId);
    assertThat(bout.toString()).contains("deleted successfully");
  }
}
