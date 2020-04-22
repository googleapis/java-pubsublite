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

package com.example.pubsublite;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SubscriberExampleIT {
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static final String GOOGLE_CLOUD_PROJECT_NUMBER =
    System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER");

  private static final String CLOUD_REGION = "us-central1";
  private static final char ZONE = 'b';
  private static final Long PROJECT_NUMBER = Long.parseLong(GOOGLE_CLOUD_PROJECT_NUMBER);
  private static final String SUFFIX = UUID.randomUUID().toString();
  private static final String TOPIC_NAME = "lite-topic-" + SUFFIX;
  private static final String SUBSCRIPTION_NAME = "lite-subscription-" + SUFFIX;
  private static final int MESSAGE_COUNT = 10;
  private static final int PARTITIONS = 1;
  private static final List<Integer> PARTITION_NOS = ImmutableList.of(0);

  private static void requireEnvVar(String varName) {
    assertNotNull(
      "Environment variable " + varName + " is required to perform these tests.",
      System.getenv(varName));
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT_NUMBER");
  }

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);

    // Set up
    CreateTopicExample.createTopicExample(
      CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME, PARTITIONS);
    CreateSubscriptionExample.createSubscriptionExample(
      CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME, SUBSCRIPTION_NAME);
    PublisherExample.publisherExample(
      CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME, MESSAGE_COUNT);
  }

  @After
  public void tearDown() {
    DeleteSubscriptionExample.deleteSubscriptionExample(
      CLOUD_REGION, ZONE, PROJECT_NUMBER, SUBSCRIPTION_NAME);
    DeleteTopicExample.deleteTopicExample(CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME);
    System.setOut(null);
  }

  @Test
  public void testPublisherExample() {
    SubscriberExample.subscriberExample(
      CLOUD_REGION, ZONE, PROJECT_NUMBER, SUBSCRIPTION_NAME, PARTITION_NOS);
    assertThat(bout.toString()).contains("Listening");
    assertThat(bout.toString()).contains("Partition:");
  }
}
