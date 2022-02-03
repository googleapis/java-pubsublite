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

import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SeekTarget;
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

  private ByteArrayOutputStream bout;
  private PrintStream out;
  Random rand = new Random();

  private static final Long projectNumber =
      Long.parseLong(System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER"));
  private String cloudRegion = "us-central1";
  private final char zoneId = (char) (rand.nextInt(3) + 'a');
  private static final String suffix = UUID.randomUUID().toString();
  private static final String reservationId = "lite-reservation-" + suffix;
  private static final String topicId = "lite-topic-" + suffix;
  private static final String subscriptionId = "lite-subscription-" + suffix;
  private static final int partitions = 2;
  private static final int messageCount = 10;

  ReservationPath reservationPath =
      ReservationPath.newBuilder()
          .setProject(ProjectNumber.of(projectNumber))
          .setLocation(CloudRegion.of(cloudRegion))
          .setName(ReservationName.of(reservationId))
          .build();

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
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() throws Exception {
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
    // Get a zonal topic
    GetTopicExample.getTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString().contains(cloudRegion + "-" + zoneId + "/topics/" + topicId));
    assertThat(bout.toString()).contains(cloudRegion + "/topics/" + topicId);
    assertThat(bout.toString()).contains(String.format("%s partition(s).", partitions));

    bout.reset();
    // List regional topics.
    ListTopicsExample.listTopicsExample(cloudRegion, zoneId, projectNumber, /*regional=*/ true);
    // List zonal topics.
    ListTopicsExample.listTopicsExample(cloudRegion, zoneId, projectNumber, /*regional=*/ false);
    assertThat(bout.toString().contains(cloudRegion + "/topics/" + topicId));
    assertThat(bout.toString().contains(cloudRegion + "-" + zoneId + "/topics/" + topicId));
    assertThat(bout.toString()).contains("topic(s) listed");

    bout.reset();
    // Update a regional topic.
    UpdateTopicExample.updateTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, /*regional=*/ true);
    // Update a zonal topic.
    UpdateTopicExample.updateTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, reservationId, /*regional=*/ false);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 34359738368");
    assertThat(bout.toString()).contains("throughput_reservation: \"" + reservationPath.toString());

    bout.reset();
    // Create a subscription.
    CreateSubscriptionExample.createSubscriptionExample(
        cloudRegion, zoneId, projectNumber, topicId, subscriptionId);
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Get a subscription.
    GetSubscriptionExample.getSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("Subscription: ");
    assertThat(bout.toString()).contains(subscriptionId);

    bout.reset();
    // List subscriptions in a topic.
    ListSubscriptionsInTopicExample.listSubscriptionsInTopicExample(
        cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("subscription(s) listed");

    bout.reset();
    // List subscriptions in a project.
    ListSubscriptionsInProjectExample.listSubscriptionsInProjectExample(
        cloudRegion, zoneId, projectNumber);
    assertThat(bout.toString()).contains("subscription(s) listed");

    bout.reset();
    // Update a subscription.
    UpdateSubscriptionExample.updateSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("delivery_requirement: DELIVER_AFTER_STORED");

    bout.reset();
    // Publish.
    PublisherExample.publisherExample(cloudRegion, zoneId, projectNumber, topicId, messageCount);
    assertThat(bout.toString()).contains("Published " + messageCount + " messages.");

    bout.reset();
    // Publish with ordering key.
    PublishWithOrderingKeyExample.publishWithOrderingKeyExample(
        cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("Published a message with ordering key:");

    bout.reset();
    // Publish messages with custom attributes.
    PublishWithCustomAttributesExample.publishWithCustomAttributesExample(
        cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("Published a message with custom attributes:");

    bout.reset();
    // Publish with batch settings.
    PublishWithBatchSettingsExample.publishWithBatchSettingsExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount);
    assertThat(bout.toString())
        .contains("Published " + messageCount + " messages with batch settings.");

    bout.reset();
    // Subscribe.
    SubscriberExample.subscriberExample(cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("Listening");
    for (int i = 0; i < messageCount; ++i) {
      assertThat(bout.toString()).contains(String.format("Data : message-%s", i));
    }
    assertThat(bout.toString()).contains("Subscriber is shut down: TERMINATED");

    bout.reset();
    // Seek.
    SeekSubscriptionExample.seekSubscriptionExample(
        cloudRegion,
        zoneId,
        projectNumber,
        subscriptionId,
        SeekTarget.of(BacklogLocation.BEGINNING),
        false);
    assertThat(bout.toString()).contains("initiated successfully");

    bout.reset();
    // Delete a subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("deleted successfully");

    bout.reset();
    // Delete a regional topic.
    DeleteTopicExample.deleteTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ true);
    // Delete a zonal topic.
    DeleteTopicExample.deleteTopicExample(
        cloudRegion, zoneId, projectNumber, topicId, /*regional=*/ false);
    assertThat(bout.toString()).contains(" (regional topic) deleted successfully");
    assertThat(bout.toString()).contains(" (zonal topic) deleted successfully");

    bout.reset();
    // Delete a reservation.
    DeleteReservationExample.deleteReservationExample(projectNumber, cloudRegion, reservationId);
    assertThat(bout.toString()).contains("deleted successfully");
  }
}
