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
  private static final String topicId = "lite-topic-" + suffix;
  private static final String subscriptionId = "lite-subscription-" + suffix;
  private static final int partitions = 2;
  private static final int messageCount = 10;

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
    // Create a topic.
    CreateTopicExample.createTopicExample(cloudRegion, zoneId, projectNumber, topicId, partitions);
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Get a topic.
    GetTopicExample.getTopicExample(cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains(topicId);
    assertThat(bout.toString()).contains(String.format("%s partition(s).", partitions));

    bout.reset();
    // List topics.
    ListTopicsExample.listTopicsExample(cloudRegion, zoneId, projectNumber);
    assertThat(bout.toString()).contains("topic(s) listed");

    bout.reset();
    // Update a topic.
    UpdateTopicExample.updateTopicExample(cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 214748364800");
    assertThat(bout.toString()).contains("scale: 4");

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
    // Delete a subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("deleted successfully");

    bout.reset();
    // Delete a topic.
    DeleteTopicExample.deleteTopicExample(cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("deleted successfully");
  }
}
