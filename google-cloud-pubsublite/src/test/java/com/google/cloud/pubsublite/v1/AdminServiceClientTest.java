/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class AdminServiceClientTest {
  private static MockServiceHelper mockServiceHelper;
  private AdminServiceClient client;
  private static MockAdminService mockAdminService;
  private LocalChannelProvider channelProvider;

  @BeforeClass
  public static void startStaticServer() {
    mockAdminService = new MockAdminService();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockAdminService));
    mockServiceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    mockServiceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    mockServiceHelper.reset();
    channelProvider = mockServiceHelper.createChannelProvider();
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
  public void createTopicTest() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Topic topic = Topic.newBuilder().build();
    String topicId = "topicId-1139259734";

    Topic actualResponse = client.createTopic(parent, topic, topicId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateTopicRequest actualRequest = ((CreateTopicRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertEquals(topic, actualRequest.getTopic());
    Assert.assertEquals(topicId, actualRequest.getTopicId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Topic topic = Topic.newBuilder().build();
      String topicId = "topicId-1139259734";
      client.createTopic(parent, topic, topicId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createTopicTest2() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    String parent = "parent-995424086";
    Topic topic = Topic.newBuilder().build();
    String topicId = "topicId-1139259734";

    Topic actualResponse = client.createTopic(parent, topic, topicId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateTopicRequest actualRequest = ((CreateTopicRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertEquals(topic, actualRequest.getTopic());
    Assert.assertEquals(topicId, actualRequest.getTopicId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createTopicExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String parent = "parent-995424086";
      Topic topic = Topic.newBuilder().build();
      String topicId = "topicId-1139259734";
      client.createTopic(parent, topic, topicId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getTopicTest() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    Topic actualResponse = client.getTopic(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicRequest actualRequest = ((GetTopicRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
      client.getTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getTopicTest2() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    Topic actualResponse = client.getTopic(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicRequest actualRequest = ((GetTopicRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getTopicExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.getTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getTopicPartitionsTest() throws Exception {
    TopicPartitions expectedResponse =
        TopicPartitions.newBuilder().setPartitionCount(-1738969222).build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    TopicPartitions actualResponse = client.getTopicPartitions(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicPartitionsRequest actualRequest = ((GetTopicPartitionsRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getTopicPartitionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
      client.getTopicPartitions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getTopicPartitionsTest2() throws Exception {
    TopicPartitions expectedResponse =
        TopicPartitions.newBuilder().setPartitionCount(-1738969222).build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    TopicPartitions actualResponse = client.getTopicPartitions(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetTopicPartitionsRequest actualRequest = ((GetTopicPartitionsRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getTopicPartitionsExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.getTopicPartitions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listTopicsTest() throws Exception {
    Topic responsesElement = Topic.newBuilder().build();
    ListTopicsResponse expectedResponse =
        ListTopicsResponse.newBuilder()
            .setNextPageToken("")
            .addAllTopics(Arrays.asList(responsesElement))
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListTopicsPagedResponse pagedListResponse = client.listTopics(parent);

    List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListTopicsRequest actualRequest = ((ListTopicsRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listTopicsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      client.listTopics(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listTopicsTest2() throws Exception {
    Topic responsesElement = Topic.newBuilder().build();
    ListTopicsResponse expectedResponse =
        ListTopicsResponse.newBuilder()
            .setNextPageToken("")
            .addAllTopics(Arrays.asList(responsesElement))
            .build();
    mockAdminService.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListTopicsPagedResponse pagedListResponse = client.listTopics(parent);

    List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListTopicsRequest actualRequest = ((ListTopicsRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listTopicsExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listTopics(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateTopicTest() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    Topic topic = Topic.newBuilder().build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Topic actualResponse = client.updateTopic(topic, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateTopicRequest actualRequest = ((UpdateTopicRequest) actualRequests.get(0));

    Assert.assertEquals(topic, actualRequest.getTopic());
    Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      Topic topic = Topic.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();
      client.updateTopic(topic, updateMask);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteTopicTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    client.deleteTopic(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteTopicRequest actualRequest = ((DeleteTopicRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteTopicExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
      client.deleteTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteTopicTest2() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    client.deleteTopic(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteTopicRequest actualRequest = ((DeleteTopicRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteTopicExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.deleteTopic(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listTopicSubscriptionsTest() throws Exception {
    String responsesElement = "responsesElement-318365110";
    ListTopicSubscriptionsResponse expectedResponse =
        ListTopicSubscriptionsResponse.newBuilder()
            .setNextPageToken("")
            .addAllSubscriptions(Arrays.asList(responsesElement))
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
        ((ListTopicSubscriptionsRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listTopicSubscriptionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
      client.listTopicSubscriptions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listTopicSubscriptionsTest2() throws Exception {
    String responsesElement = "responsesElement-318365110";
    ListTopicSubscriptionsResponse expectedResponse =
        ListTopicSubscriptionsResponse.newBuilder()
            .setNextPageToken("")
            .addAllSubscriptions(Arrays.asList(responsesElement))
            .build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    ListTopicSubscriptionsPagedResponse pagedListResponse = client.listTopicSubscriptions(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListTopicSubscriptionsRequest actualRequest =
        ((ListTopicSubscriptionsRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listTopicSubscriptionsExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.listTopicSubscriptions(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createSubscriptionTest() throws Exception {
    Subscription expectedResponse =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Subscription subscription = Subscription.newBuilder().build();
    String subscriptionId = "subscriptionId1478790936";

    Subscription actualResponse = client.createSubscription(parent, subscription, subscriptionId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateSubscriptionRequest actualRequest = ((CreateSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertEquals(subscription, actualRequest.getSubscription());
    Assert.assertEquals(subscriptionId, actualRequest.getSubscriptionId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Subscription subscription = Subscription.newBuilder().build();
      String subscriptionId = "subscriptionId1478790936";
      client.createSubscription(parent, subscription, subscriptionId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createSubscriptionTest2() throws Exception {
    Subscription expectedResponse =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    String parent = "parent-995424086";
    Subscription subscription = Subscription.newBuilder().build();
    String subscriptionId = "subscriptionId1478790936";

    Subscription actualResponse = client.createSubscription(parent, subscription, subscriptionId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CreateSubscriptionRequest actualRequest = ((CreateSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertEquals(subscription, actualRequest.getSubscription());
    Assert.assertEquals(subscriptionId, actualRequest.getSubscriptionId());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void createSubscriptionExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String parent = "parent-995424086";
      Subscription subscription = Subscription.newBuilder().build();
      String subscriptionId = "subscriptionId1478790936";
      client.createSubscription(parent, subscription, subscriptionId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getSubscriptionTest() throws Exception {
    Subscription expectedResponse =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    Subscription actualResponse = client.getSubscription(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetSubscriptionRequest actualRequest = ((GetSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
      client.getSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getSubscriptionTest2() throws Exception {
    Subscription expectedResponse =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    Subscription actualResponse = client.getSubscription(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GetSubscriptionRequest actualRequest = ((GetSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void getSubscriptionExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.getSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listSubscriptionsTest() throws Exception {
    Subscription responsesElement = Subscription.newBuilder().build();
    ListSubscriptionsResponse expectedResponse =
        ListSubscriptionsResponse.newBuilder()
            .setNextPageToken("")
            .addAllSubscriptions(Arrays.asList(responsesElement))
            .build();
    mockAdminService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListSubscriptionsPagedResponse pagedListResponse = client.listSubscriptions(parent);

    List<Subscription> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListSubscriptionsRequest actualRequest = ((ListSubscriptionsRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listSubscriptionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      client.listSubscriptions(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listSubscriptionsTest2() throws Exception {
    Subscription responsesElement = Subscription.newBuilder().build();
    ListSubscriptionsResponse expectedResponse =
        ListSubscriptionsResponse.newBuilder()
            .setNextPageToken("")
            .addAllSubscriptions(Arrays.asList(responsesElement))
            .build();
    mockAdminService.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListSubscriptionsPagedResponse pagedListResponse = client.listSubscriptions(parent);

    List<Subscription> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListSubscriptionsRequest actualRequest = ((ListSubscriptionsRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listSubscriptionsExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listSubscriptions(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateSubscriptionTest() throws Exception {
    Subscription expectedResponse =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    mockAdminService.addResponse(expectedResponse);

    Subscription subscription = Subscription.newBuilder().build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Subscription actualResponse = client.updateSubscription(subscription, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    UpdateSubscriptionRequest actualRequest = ((UpdateSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(subscription, actualRequest.getSubscription());
    Assert.assertEquals(updateMask, actualRequest.getUpdateMask());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void updateSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      Subscription subscription = Subscription.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();
      client.updateSubscription(subscription, updateMask);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteSubscriptionTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    client.deleteSubscription(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteSubscriptionRequest actualRequest = ((DeleteSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(name.toString(), actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteSubscriptionExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
      client.deleteSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteSubscriptionTest2() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockAdminService.addResponse(expectedResponse);

    String name = "name3373707";

    client.deleteSubscription(name);

    List<AbstractMessage> actualRequests = mockAdminService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    DeleteSubscriptionRequest actualRequest = ((DeleteSubscriptionRequest) actualRequests.get(0));

    Assert.assertEquals(name, actualRequest.getName());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void deleteSubscriptionExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockAdminService.addException(exception);

    try {
      String name = "name3373707";
      client.deleteSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
