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

import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
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
  private Random rand = new Random();
  private List<String> cloudRegions =
      Arrays.asList(
          "us-central1", "europe-north1", "asia-east1", "australia-southeast1", "asia-northeast2");

  private static final String GOOGLE_CLOUD_PROJECT_NUMBER =
      System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER");
  private String CLOUD_REGION = cloudRegions.get(rand.nextInt(cloudRegions.size()));
  private static final char ZONE_ID = 'b';
  private static final Long PROJECT_NUMBER = Long.parseLong(GOOGLE_CLOUD_PROJECT_NUMBER);
  private static final String SUFFIX = UUID.randomUUID().toString();
  private static final String TOPIC_NAME = "lite-topic-" + SUFFIX;
  private static final String SUBSCRIPTION_NAME = "lite-subscription-" + SUFFIX;
  private static final int PARTITIONS = 2;
  private static final int MESSAGE_COUNT = 10;
  private static final List<Integer> PARTITION_NOS = ImmutableList.of(0);

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
    CreateTopicExample.createTopicExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, PARTITIONS);
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Get a topic.
    GetTopicExample.getTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains(TOPIC_NAME);
    assertThat(bout.toString()).contains(String.format("%s partition(s).", PARTITIONS));

    bout.reset();
    // List topics.
    ListTopicsExample.listTopicsExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER);
    assertThat(bout.toString()).contains("topic(s) listed");

    bout.reset();
    // Update a topic.
    UpdateTopicExample.updateTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 214748364800");
    assertThat(bout.toString()).contains("scale: 4");

    bout.reset();
    // Create a subscription.
    CreateSubscriptionExample.createSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, SUBSCRIPTION_NAME);
    assertThat(bout.toString()).contains("created successfully");

    bout.reset();
    // Get a subscription.
    GetSubscriptionExample.getSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME);
    assertThat(bout.toString()).contains("Subscription: ");
    assertThat(bout.toString()).contains(SUBSCRIPTION_NAME);

    bout.reset();
    // List subscriptions in a topic.
    ListSubscriptionsInTopicExample.listSubscriptionsInTopicExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("subscription(s) listed");

    bout.reset();
    // List subscriptions in a project.
    ListSubscriptionsInProjectExample.listSubscriptionsInProjectExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER);
    assertThat(bout.toString()).contains("subscription(s) listed");

    bout.reset();
    // Update a subscription.
    UpdateSubscriptionExample.updateSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME);
    assertThat(bout.toString()).contains("delivery_requirement: DELIVER_AFTER_STORED");

    bout.reset();
    // Publish.
    PublisherExample.publisherExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, MESSAGE_COUNT);
    assertThat(bout.toString()).contains("Published " + MESSAGE_COUNT + " messages.");

    bout.reset();
    // Publish with ordering key.
    PublishWithOrderingKeyExample.publishWithOrderingKeyExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("Published a message with ordering key:");

    bout.reset();
    // Publish messages with custom attributes.
    PublishWithCustomAttributesExample.publishWithCustomAttributesExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("Published a message with custom attributes:");

    bout.reset();
    // Publish with batch settings.
    PublishWithBatchSettingsExample.publishWithBatchSettingsExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, MESSAGE_COUNT);
    assertThat(bout.toString())
        .contains("Published " + MESSAGE_COUNT + " messages with batch settings.");

    bout.reset();
    // Subscribe.
    SubscriberExample.subscriberExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME, PARTITION_NOS);
    assertThat(bout.toString()).contains("Listening");
    assertThat(bout.toString()).contains("Data : message-0");
    assertThat(bout.toString()).contains("Subscriber is shut down: TERMINATED");

    bout.reset();
    // Delete a subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME);
    assertThat(bout.toString()).contains("deleted successfully");

    bout.reset();
    // Delete a topic.
    DeleteTopicExample.deleteTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("deleted successfully");
  }
}
