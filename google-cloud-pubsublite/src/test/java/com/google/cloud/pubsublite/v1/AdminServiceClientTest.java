/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsublite.v1;

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.LocationName;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.common.collect.Lists;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@javax.annotation.Generated("by GAPIC")
public class AdminServiceClientTest {
  private static MockAdminService mockAdminService;
  private static MockCursorService mockCursorService;
  private static MockPublisherService mockPublisherService;
  private static MockSubscriberService mockSubscriberService;
  private static MockPartitionAssignmentService mockPartitionAssignmentService;
  private static MockTopicStatsService mockTopicStatsService;
  private static MockServiceHelper serviceHelper;
  private AdminServiceClient client;
  private LocalChannelProvider channelProvider;

  @BeforeClass
  public static void startStaticServer() {
    mockAdminService = new MockAdminService();
    mockCursorService = new MockCursorService();
    mockPublisherService = new MockPublisherService();
    mockSubscriberService = new MockSubscriberService();
    mockPartitionAssignmentService = new MockPartitionAssignmentService();
    mockTopicStatsService = new MockTopicStatsService();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(),
            Arrays.<MockGrpcService>asList(
                mockAdminService,
                mockCursorService,
                mockPublisherService,
                mockSubscriberService,
                mockPartitionAssignmentService,
                mockTopicStatsService));
    serviceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    serviceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    serviceHelper.reset();
    channelProvider = serviceHelper.createChannelProvider();
    AdminServiceSettings settings =
        AdminServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = AdminServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  @SuppressWarnings("all")
  public void createTopicTest() {
    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Topic expectedResponse = Topic.newBuilder().setName(name.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Topic topic = Topic.newBuilder().build();
    String topicId = "topicId-957291989";

    Topic actualResponse = client.createTopic(parent, topic, topicId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateTopicRequest actualRequest = (CreateTopicRequest) actualRequests.get(0);

    Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
    Assert.assertEquals(topic, actualRequest.getTopic());
    Assert.assertEquals(topicId, actualRequest.getTopicId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void createTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Topic topic = Topic.newBuilder().build();
      String topicId = "topicId-957291989";

      client.createTopic(parent, topic, topicId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getTopicTest() {
    TopicName name2 = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Topic expectedResponse = Topic.newBuilder().setName(name2.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    Topic actualResponse = client.getTopic(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicRequest actualRequest = (GetTopicRequest) actualRequests.get(0);

    Assert.assertEquals(name, TopicName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

      client.getTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getTopicPartitionsTest() {
    long partitionCount = 1738969222L;
    TopicPartitions expectedResponse =
        TopicPartitions.newBuilder().setPartitionCount(partitionCount).build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    TopicPartitions actualResponse = client.getTopicPartitions(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicPartitionsRequest actualRequest = (GetTopicPartitionsRequest) actualRequests.get(0);

    Assert.assertEquals(name, TopicName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getTopicPartitionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

      client.getTopicPartitions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listTopicsTest() {
    String nextPageToken = "";
    Topic topicsElement = Topic.newBuilder().build();
    List<Topic> topics = Arrays.asList(topicsElement);
    ListTopicsResponse expectedResponse =
        ListTopicsResponse.newBuilder()
            .setNextPageToken(nextPageToken)
            .addAllTopics(topics)
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListTopicsPagedResponse pagedListResponse = client.listTopics(parent);

    List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());
    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListTopicsRequest actualRequest = (ListTopicsRequest) actualRequests.get(0);

    Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listTopicsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

      client.listTopics(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateTopicTest() {
    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Topic expectedResponse = Topic.newBuilder().setName(name.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    Topic topic = Topic.newBuilder().build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Topic actualResponse = client.updateTopic(topic, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateTopicRequest actualRequest = (UpdateTopicRequest) actualRequests.get(0);

    Assert.assertEquals(topic, actualRequest.getTopic());
    Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      Topic topic = Topic.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();

      client.updateTopic(topic, updateMask);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteTopicTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    client.deleteTopic(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteTopicRequest actualRequest = (DeleteTopicRequest) actualRequests.get(0);

    Assert.assertEquals(name, TopicName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

      client.deleteTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listTopicSubscriptionsTest() {
    String nextPageToken = "";
    String subscriptionsElement = "subscriptionsElement1698708147";
    List<String> subscriptions = Arrays.asList(subscriptionsElement);
    ListTopicSubscriptionsResponse expectedResponse =
        ListTopicSubscriptionsResponse.newBuilder()
            .setNextPageToken(nextPageToken)
            .addAllSubscriptions(subscriptions)
            .build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    ListTopicSubscriptionsPagedResponse pagedListResponse = client.listTopicSubscriptions(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());
    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListTopicSubscriptionsRequest actualRequest =
        (ListTopicSubscriptionsRequest) actualRequests.get(0);

    Assert.assertEquals(name, TopicName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listTopicSubscriptionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

      client.listTopicSubscriptions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void createSubscriptionTest() {
    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
    TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Subscription expectedResponse =
        Subscription.newBuilder().setName(name.toString()).setTopic(topic.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Subscription subscription = Subscription.newBuilder().build();
    String subscriptionId = "subscriptionId-1402099843";

    Subscription actualResponse = client.createSubscription(parent, subscription, subscriptionId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateSubscriptionRequest actualRequest = (CreateSubscriptionRequest) actualRequests.get(0);

    Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
    Assert.assertEquals(subscription, actualRequest.getSubscription());
    Assert.assertEquals(subscriptionId, actualRequest.getSubscriptionId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void createSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Subscription subscription = Subscription.newBuilder().build();
      String subscriptionId = "subscriptionId-1402099843";

      client.createSubscription(parent, subscription, subscriptionId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void getSubscriptionTest() {
    SubscriptionName name2 = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
    TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Subscription expectedResponse =
        Subscription.newBuilder().setName(name2.toString()).setTopic(topic.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    Subscription actualResponse = client.getSubscription(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetSubscriptionRequest actualRequest = (GetSubscriptionRequest) actualRequests.get(0);

    Assert.assertEquals(name, SubscriptionName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void getSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

      client.getSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listSubscriptionsTest() {
    String nextPageToken = "";
    Subscription subscriptionsElement = Subscription.newBuilder().build();
    List<Subscription> subscriptions = Arrays.asList(subscriptionsElement);
    ListSubscriptionsResponse expectedResponse =
        ListSubscriptionsResponse.newBuilder()
            .setNextPageToken(nextPageToken)
            .addAllSubscriptions(subscriptions)
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListSubscriptionsPagedResponse pagedListResponse = client.listSubscriptions(parent);

    List<Subscription> resources = Lists.newArrayList(pagedListResponse.iterateAll());
    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListSubscriptionsRequest actualRequest = (ListSubscriptionsRequest) actualRequests.get(0);

    Assert.assertEquals(parent, LocationName.parse(actualRequest.getParent()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listSubscriptionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

      client.listSubscriptions(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void updateSubscriptionTest() {
    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
    TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    Subscription expectedResponse =
        Subscription.newBuilder().setName(name.toString()).setTopic(topic.toString()).build();
    mockAdminService.addResponse(expectedResponse);

    Subscription subscription = Subscription.newBuilder().build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Subscription actualResponse = client.updateSubscription(subscription, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateSubscriptionRequest actualRequest = (UpdateSubscriptionRequest) actualRequests.get(0);

    Assert.assertEquals(subscription, actualRequest.getSubscription());
    Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void updateSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      Subscription subscription = Subscription.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();

      client.updateSubscription(subscription, updateMask);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void deleteSubscriptionTest() {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    client.deleteSubscription(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteSubscriptionRequest actualRequest = (DeleteSubscriptionRequest) actualRequests.get(0);

    Assert.assertEquals(name, SubscriptionName.parse(actualRequest.getName()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void deleteSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

      client.deleteSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }
}
