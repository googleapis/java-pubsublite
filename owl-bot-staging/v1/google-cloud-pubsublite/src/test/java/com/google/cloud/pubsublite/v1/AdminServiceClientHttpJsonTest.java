/*
 * Copyright 2022 Google LLC
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

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationTopicsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.pubsublite.proto.ListReservationTopicsResponse;
import com.google.cloud.pubsublite.proto.ListReservationsResponse;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.LocationName;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.ReservationName;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.v1.stub.HttpJsonAdminServiceStub;
import com.google.common.collect.Lists;
import com.google.longrunning.Operation;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class AdminServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static AdminServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonAdminServiceStub.getMethodDescriptors(),
            AdminServiceSettings.getDefaultEndpoint());
    AdminServiceSettings settings =
        AdminServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                AdminServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = AdminServiceClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
  }

  @Test
  public void createTopicTest() throws Exception {
    Topic expectedResponse =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Topic topic = Topic.newBuilder().build();
    String topicId = "topicId-1139259734";

    Topic actualResponse = client.createTopic(parent, topic, topicId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createTopicExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";
    Topic topic = Topic.newBuilder().build();
    String topicId = "topicId-1139259734";

    Topic actualResponse = client.createTopic(parent, topic, topicId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createTopicExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
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
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    mockService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    Topic actualResponse = client.getTopic(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getTopicExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    mockService.addResponse(expectedResponse);

    String name = "projects/project-3828/locations/location-3828/topics/topic-3828";

    Topic actualResponse = client.getTopic(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getTopicExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-3828/locations/location-3828/topics/topic-3828";
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
    mockService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    TopicPartitions actualResponse = client.getTopicPartitions(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getTopicPartitionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String name = "projects/project-3828/locations/location-3828/topics/topic-3828";

    TopicPartitions actualResponse = client.getTopicPartitions(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getTopicPartitionsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-3828/locations/location-3828/topics/topic-3828";
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
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListTopicsPagedResponse pagedListResponse = client.listTopics(parent);

    List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listTopicsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";

    ListTopicsPagedResponse pagedListResponse = client.listTopics(parent);

    List<Topic> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listTopicsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
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
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    mockService.addResponse(expectedResponse);

    Topic topic =
        Topic.newBuilder()
            .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
            .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
            .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
            .build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Topic actualResponse = client.updateTopic(topic, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void updateTopicExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      Topic topic =
          Topic.newBuilder()
              .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartitionConfig(Topic.PartitionConfig.newBuilder().build())
              .setRetentionConfig(Topic.RetentionConfig.newBuilder().build())
              .setReservationConfig(Topic.ReservationConfig.newBuilder().build())
              .build();
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
    mockService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    client.deleteTopic(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteTopicExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String name = "projects/project-3828/locations/location-3828/topics/topic-3828";

    client.deleteTopic(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteTopicExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-3828/locations/location-3828/topics/topic-3828";
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
    mockService.addResponse(expectedResponse);

    TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");

    ListTopicSubscriptionsPagedResponse pagedListResponse = client.listTopicSubscriptions(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listTopicSubscriptionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String name = "projects/project-3828/locations/location-3828/topics/topic-3828";

    ListTopicSubscriptionsPagedResponse pagedListResponse = client.listTopicSubscriptions(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listTopicSubscriptionsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-3828/locations/location-3828/topics/topic-3828";
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
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Subscription subscription = Subscription.newBuilder().build();
    String subscriptionId = "subscriptionId1478790936";

    Subscription actualResponse = client.createSubscription(parent, subscription, subscriptionId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createSubscriptionExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";
    Subscription subscription = Subscription.newBuilder().build();
    String subscriptionId = "subscriptionId1478790936";

    Subscription actualResponse = client.createSubscription(parent, subscription, subscriptionId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createSubscriptionExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
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
    mockService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    Subscription actualResponse = client.getSubscription(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getSubscriptionExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String name = "projects/project-7420/locations/location-7420/subscriptions/subscription-7420";

    Subscription actualResponse = client.getSubscription(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getSubscriptionExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-7420/locations/location-7420/subscriptions/subscription-7420";
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
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListSubscriptionsPagedResponse pagedListResponse = client.listSubscriptions(parent);

    List<Subscription> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listSubscriptionsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";

    ListSubscriptionsPagedResponse pagedListResponse = client.listSubscriptions(parent);

    List<Subscription> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getSubscriptionsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listSubscriptionsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
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
    mockService.addResponse(expectedResponse);

    Subscription subscription =
        Subscription.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
            .build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Subscription actualResponse = client.updateSubscription(subscription, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void updateSubscriptionExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      Subscription subscription =
          Subscription.newBuilder()
              .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setDeliveryConfig(Subscription.DeliveryConfig.newBuilder().build())
              .build();
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
    mockService.addResponse(expectedResponse);

    SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    client.deleteSubscription(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteSubscriptionExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String name = "projects/project-7420/locations/location-7420/subscriptions/subscription-7420";

    client.deleteSubscription(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteSubscriptionExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-7420/locations/location-7420/subscriptions/subscription-7420";
      client.deleteSubscription(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void seekSubscriptionTest() throws Exception {
    SeekSubscriptionResponse expectedResponse = SeekSubscriptionResponse.newBuilder().build();
    Operation resultOperation =
        Operation.newBuilder()
            .setName("seekSubscriptionTest")
            .setDone(true)
            .setResponse(Any.pack(expectedResponse))
            .build();
    mockService.addResponse(resultOperation);

    SeekSubscriptionRequest request =
        SeekSubscriptionRequest.newBuilder()
            .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
            .build();

    SeekSubscriptionResponse actualResponse = client.seekSubscriptionAsync(request).get();
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void seekSubscriptionExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      SeekSubscriptionRequest request =
          SeekSubscriptionRequest.newBuilder()
              .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
              .build();
      client.seekSubscriptionAsync(request).get();
      Assert.fail("No exception raised");
    } catch (ExecutionException e) {
    }
  }

  @Test
  public void createReservationTest() throws Exception {
    Reservation expectedResponse =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
    Reservation reservation = Reservation.newBuilder().build();
    String reservationId = "reservationId1116965383";

    Reservation actualResponse = client.createReservation(parent, reservation, reservationId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createReservationExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Reservation reservation = Reservation.newBuilder().build();
      String reservationId = "reservationId1116965383";
      client.createReservation(parent, reservation, reservationId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void createReservationTest2() throws Exception {
    Reservation expectedResponse =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";
    Reservation reservation = Reservation.newBuilder().build();
    String reservationId = "reservationId1116965383";

    Reservation actualResponse = client.createReservation(parent, reservation, reservationId);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void createReservationExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
      Reservation reservation = Reservation.newBuilder().build();
      String reservationId = "reservationId1116965383";
      client.createReservation(parent, reservation, reservationId);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getReservationTest() throws Exception {
    Reservation expectedResponse =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    mockService.addResponse(expectedResponse);

    ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");

    Reservation actualResponse = client.getReservation(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getReservationExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");
      client.getReservation(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void getReservationTest2() throws Exception {
    Reservation expectedResponse =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    mockService.addResponse(expectedResponse);

    String name = "projects/project-313/locations/location-313/reservations/reservation-313";

    Reservation actualResponse = client.getReservation(name);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void getReservationExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-313/locations/location-313/reservations/reservation-313";
      client.getReservation(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listReservationsTest() throws Exception {
    Reservation responsesElement = Reservation.newBuilder().build();
    ListReservationsResponse expectedResponse =
        ListReservationsResponse.newBuilder()
            .setNextPageToken("")
            .addAllReservations(Arrays.asList(responsesElement))
            .build();
    mockService.addResponse(expectedResponse);

    LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");

    ListReservationsPagedResponse pagedListResponse = client.listReservations(parent);

    List<Reservation> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getReservationsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listReservationsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      client.listReservations(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listReservationsTest2() throws Exception {
    Reservation responsesElement = Reservation.newBuilder().build();
    ListReservationsResponse expectedResponse =
        ListReservationsResponse.newBuilder()
            .setNextPageToken("")
            .addAllReservations(Arrays.asList(responsesElement))
            .build();
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-5833/locations/location-5833";

    ListReservationsPagedResponse pagedListResponse = client.listReservations(parent);

    List<Reservation> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getReservationsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listReservationsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent = "projects/project-5833/locations/location-5833";
      client.listReservations(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void updateReservationTest() throws Exception {
    Reservation expectedResponse =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    mockService.addResponse(expectedResponse);

    Reservation reservation =
        Reservation.newBuilder()
            .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
            .setThroughputCapacity(-1174790353)
            .build();
    FieldMask updateMask = FieldMask.newBuilder().build();

    Reservation actualResponse = client.updateReservation(reservation, updateMask);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void updateReservationExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      Reservation reservation =
          Reservation.newBuilder()
              .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
              .setThroughputCapacity(-1174790353)
              .build();
      FieldMask updateMask = FieldMask.newBuilder().build();
      client.updateReservation(reservation, updateMask);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteReservationTest() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockService.addResponse(expectedResponse);

    ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");

    client.deleteReservation(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteReservationExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");
      client.deleteReservation(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void deleteReservationTest2() throws Exception {
    Empty expectedResponse = Empty.newBuilder().build();
    mockService.addResponse(expectedResponse);

    String name = "projects/project-313/locations/location-313/reservations/reservation-313";

    client.deleteReservation(name);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void deleteReservationExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-313/locations/location-313/reservations/reservation-313";
      client.deleteReservation(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listReservationTopicsTest() throws Exception {
    String responsesElement = "responsesElement-318365110";
    ListReservationTopicsResponse expectedResponse =
        ListReservationTopicsResponse.newBuilder()
            .setNextPageToken("")
            .addAllTopics(Arrays.asList(responsesElement))
            .build();
    mockService.addResponse(expectedResponse);

    ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");

    ListReservationTopicsPagedResponse pagedListResponse = client.listReservationTopics(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listReservationTopicsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      ReservationName name = ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]");
      client.listReservationTopics(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listReservationTopicsTest2() throws Exception {
    String responsesElement = "responsesElement-318365110";
    ListReservationTopicsResponse expectedResponse =
        ListReservationTopicsResponse.newBuilder()
            .setNextPageToken("")
            .addAllTopics(Arrays.asList(responsesElement))
            .build();
    mockService.addResponse(expectedResponse);

    String name = "projects/project-313/locations/location-313/reservations/reservation-313";

    ListReservationTopicsPagedResponse pagedListResponse = client.listReservationTopics(name);

    List<String> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getTopicsList().get(0), resources.get(0));

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void listReservationTopicsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String name = "projects/project-313/locations/location-313/reservations/reservation-313";
      client.listReservationTopics(name);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
