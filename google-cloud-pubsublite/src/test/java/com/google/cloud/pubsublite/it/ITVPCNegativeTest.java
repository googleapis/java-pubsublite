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

package com.google.cloud.pubsublite.it;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.LocationPaths;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberSettings;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.DeliveryRequirement;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.Durations;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

/* Integration tests for VPC-SC */
public class ITVPCNegativeTest {
  private static final boolean IS_VPC_TEST =
      System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC") != null
          && System.getenv("GOOGLE_CLOUD_TESTS_IN_VPCSC").equalsIgnoreCase("true");
  private static final String OUTSIDE_VPC_PROJECT =
      System.getenv("GOOGLE_CLOUD_TESTS_VPCSC_OUTSIDE_PERIMETER_PROJECT_NUMBER");
  private static final String CLOUD_REGION = "us-central1";
  private static final char ZONE_ID = 'b';
  private static final Long PROJECT_NUMBER =
      OUTSIDE_VPC_PROJECT == null ? 0 : Long.parseLong(OUTSIDE_VPC_PROJECT);
  private static final String SUFFIX = UUID.randomUUID().toString();
  private static final String TOPIC_NAME = "lite-topic-" + SUFFIX;
  private static final String SUBSCRIPTION_NAME = "lite-subscription-" + SUFFIX;
  private static final int PARTITIONS = 1;
  private static final int MESSAGE_COUNT = 1;
  private static final List<Integer> PARTITION_NOS = ImmutableList.of(0);

  private CloudRegion cloudRegion;
  private CloudZone zone;
  private ProjectNumber projectNum;
  private LocationPath locationPath;
  private TopicName topicName;
  private TopicPath topicPath;
  private Topic topic;
  private SubscriptionName subscriptionName;
  private SubscriptionPath subscriptionPath;
  private Subscription subscription;
  private AdminClientSettings adminClientSettings;
  private AdminClient adminClient;

  static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
  }

  @Rule public Timeout globalTimeout = Timeout.seconds(300); // 5 minute timeout

  @BeforeClass
  public static void checkRequirements() {
    // Skip these integration tests if IS_VPC_TEST is false.
    assumeTrue(
        "To run tests, GOOGLE_CLOUD_TESTS_IN_VPCSC environment variable needs to be set to True",
        IS_VPC_TEST);

    // If IS_VPC_TEST is true we require the following env variables.
    requireEnvVar("GOOGLE_CLOUD_TESTS_VPCSC_OUTSIDE_PERIMETER_PROJECT_NUMBER");
  }

  @Before
  public void setUp() throws Exception {
    // Set up configs for location, topic, and subscription to test against.
    cloudRegion = CloudRegion.of(CLOUD_REGION);
    zone = CloudZone.of(cloudRegion, ZONE_ID);
    projectNum = ProjectNumber.of(PROJECT_NUMBER);
    locationPath = LocationPaths.newBuilder().setProjectNumber(projectNum).setZone(zone).build();
    topicName = TopicName.of(TOPIC_NAME);
    topicPath =
        TopicPaths.newBuilder()
            .setZone(zone)
            .setProjectNumber(projectNum)
            .setTopicName(topicName)
            .build();
    topic =
        Topic.newBuilder()
            .setPartitionConfig(PartitionConfig.newBuilder().setScale(1).setCount(PARTITIONS))
            .setRetentionConfig(
                RetentionConfig.newBuilder()
                    .setPeriod(Durations.fromDays(1))
                    .setPerPartitionBytes(100 * 1024 * 1024 * 1024L))
            .setName(topicPath.value())
            .build();
    subscriptionName = SubscriptionName.of(SUBSCRIPTION_NAME);
    subscriptionPath =
        SubscriptionPaths.newBuilder()
            .setZone(zone)
            .setProjectNumber(projectNum)
            .setSubscriptionName(subscriptionName)
            .build();
    subscription =
        Subscription.newBuilder()
            .setDeliveryConfig(
                DeliveryConfig.newBuilder()
                    .setDeliveryRequirement(DeliveryRequirement.DELIVER_AFTER_STORED))
            .setName(subscriptionPath.value())
            .setTopic(topicPath.value())
            .build();

    // Instantiate an AdminClient to test with.
    adminClientSettings = AdminClientSettings.newBuilder().setRegion(cloudRegion).build();
    adminClient = AdminClient.create(adminClientSettings);
  }

  @After
  public void tearDown() throws Exception {
    adminClient.close();
  }

  private void checkExceptionForVPCError(StatusRuntimeException e) {
    assertEquals(Status.Code.PERMISSION_DENIED, e.getStatus().getCode());
    assertThat(e.getStatus().getDescription())
        .contains("Request is prohibited by organization's policy");
  }

  private void checkExceptionForVPCError(StatusException e) {
    assertEquals(Status.Code.PERMISSION_DENIED, e.getStatus().getCode());
    assertThat(e.getStatus().getDescription())
        .contains("Request is prohibited by organization's policy");
  }

  @Test
  public void deniedCreateTopic() {
    try {
      adminClient.createTopic(topic).get();

      // If we succesfully create a topic we need to clean it up.
      adminClient.deleteTopic(topicPath).get();

      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedDeleteTopic() {
    try {
      adminClient.deleteTopic(topicPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedGetTopic() {
    try {
      adminClient.getTopic(topicPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedListTopics() {
    try {
      adminClient.listTopics(locationPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedListTopicSubscriptions() {
    try {
      adminClient.listTopicSubscriptions(topicPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedUpdateTopic() {
    try {
      FieldMask mask = FieldMask.newBuilder().addPaths("partition_config.scale").build();
      adminClient.updateTopic(topic, mask).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedCreateSubscription() {
    try {
      adminClient.createSubscription(subscription).get();

      // If we successfully create a subscription we need to clean it up.
      adminClient.deleteSubscription(subscriptionPath).get();

      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedDeleteSubscription() {
    try {
      adminClient.deleteSubscription(subscriptionPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedGetSubscription() {
    try {
      adminClient.getSubscription(subscriptionPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedListSubscriptions() {
    try {
      adminClient.listSubscriptions(locationPath).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedUpdateSubscription() {
    try {
      FieldMask mask =
          FieldMask.newBuilder().addPaths("delivery_config.delivery_requirement").build();
      adminClient.updateSubscription(subscription, mask).get();
      fail("Expected PERMISSION_DENIED StatusRuntimeException");
    } catch (InterruptedException e) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + e.toString());
    } catch (ExecutionException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusRuntimeException) thrown);
    }
  }

  @Test
  public void deniedPublish() {
    try {
      PublisherSettings publisherSettings =
          PublisherSettings.newBuilder().setTopicPath(topicPath).build();

      Publisher publisher = Publisher.create(publisherSettings);
      fail("Expected PERMISSION_DENIED StatusException");
    } catch (StatusException e) {
      checkExceptionForVPCError(e);
    }
  }

  @Test
  public void deniedSubscriber() {
    try {
      FlowControlSettings flowControlSettings =
          FlowControlSettings.builder()
              // Set outstanding bytes to 10 MiB per partition.
              .setBytesOutstanding(10 * 1024 * 1024L)
              .setMessagesOutstanding(Long.MAX_VALUE)
              .build();

      List<Partition> partitions = new ArrayList<>();
      for (Integer num : PARTITION_NOS) {
        partitions.add(Partition.of(num));
      }

      MessageReceiver receiver =
          new MessageReceiver() {
            @Override
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
              fail("Expected PERMISSION_DENIED StatusException");
            }
          };

      SubscriberSettings subscriberSettings =
          SubscriberSettings.newBuilder()
              .setSubscriptionPath(subscriptionPath)
              .setPerPartitionFlowControlSettings(flowControlSettings)
              .setPartitions(partitions)
              .setReceiver(receiver)
              .build();

      Subscriber subscriber = Subscriber.create(subscriberSettings);

      // Start the subscriber. Upon successful starting, its state will become RUNNING.
      subscriber.startAsync().awaitRunning();
      subscriber.awaitTerminated(30, TimeUnit.SECONDS);
      fail("Expected PERMISSION_DENIED StatusException");
    } catch (StatusException e) {
      checkExceptionForVPCError(e);
    } catch (TimeoutException t) {
      fail("Expected PERMISSION_DENIED StatusRuntimeException but got: " + t.toString());
    } catch (IllegalStateException e) {
      Throwable thrown = e.getCause();
      checkExceptionForVPCError((StatusException) thrown);
    }
  }
}
