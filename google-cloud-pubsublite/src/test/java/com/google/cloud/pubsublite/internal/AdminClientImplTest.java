/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal;

import static com.google.api.core.ApiFutures.immediateFuture;
import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertFutureThrowsCode;
import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.AdminClient.StartingOffset;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
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
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.stub.AdminServiceStub;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.Durations;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class AdminClientImplTest {
  private static final CloudRegion REGION = CloudRegion.of("us-east1");
  private static final CloudZone ZONE = CloudZone.of(REGION, 'x');
  private static final FieldMask MASK = FieldMask.newBuilder().addPaths("config").build();

  private static ProjectNumber projectNumber() {
    return ProjectNumber.of(123);
  }

  private static TopicName topicName() {
    return TopicName.of("abc");
  }

  private static TopicPath topicPath() {
    return TopicPath.newBuilder()
        .setLocation(ZONE)
        .setProject(projectNumber())
        .setName(topicName())
        .build();
  }

  private static final Topic TOPIC =
      Topic.newBuilder()
          .setName(topicPath().toString())
          .setPartitionConfig(PartitionConfig.newBuilder().setCount(10))
          .setRetentionConfig(RetentionConfig.newBuilder().setPeriod(Durations.fromDays(1)))
          .build();
  private static final Topic TOPIC_2 =
      TOPIC.toBuilder().setPartitionConfig(PartitionConfig.newBuilder().setCount(88)).build();

  private static SubscriptionName subscriptionName() {
    return SubscriptionName.of("abc");
  }

  private static SubscriptionPath subscriptionPath() {
    return SubscriptionPath.newBuilder()
        .setLocation(ZONE)
        .setProject(projectNumber())
        .setName(subscriptionName())
        .build();
  }

  private static final Subscription SUBSCRIPTION =
      Subscription.newBuilder()
          .setDeliveryConfig(
              DeliveryConfig.newBuilder()
                  .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_AFTER_STORED))
          .setName(subscriptionPath().toString())
          .setTopic(topicPath().toString())
          .build();
  private static final Subscription SUBSCRIPTION_2 =
      SUBSCRIPTION
          .toBuilder()
          .setDeliveryConfig(
              DeliveryConfig.newBuilder()
                  .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_IMMEDIATELY))
          .build();

  private static final <T> ApiFuture<T> failedPreconditionFuture() {
    return ApiFutures.immediateFailedFuture(
        new CheckedApiException(Code.FAILED_PRECONDITION).underlying);
  }

  @Mock UnaryCallable<CreateTopicRequest, Topic> createTopicCallable;
  @Mock UnaryCallable<GetTopicRequest, Topic> getTopicCallable;
  @Mock UnaryCallable<GetTopicPartitionsRequest, TopicPartitions> getTopicPartitionsCallable;
  @Mock UnaryCallable<ListTopicsRequest, ListTopicsResponse> listTopicsCallable;
  @Mock UnaryCallable<UpdateTopicRequest, Topic> updateTopicCallable;
  @Mock UnaryCallable<DeleteTopicRequest, Empty> deleteTopicCallable;

  @Mock
  UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsCallable;

  @Mock UnaryCallable<CreateSubscriptionRequest, Subscription> createSubscriptionCallable;
  @Mock UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable;

  @Mock
  UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse> listSubscriptionsCallable;

  @Mock UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable;
  @Mock UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable;

  @Mock AdminServiceStub stub;

  private AdminClientImpl client;

  @Before
  public void setUp() throws IOException {
    initMocks(this);

    when(stub.createTopicCallable()).thenReturn(createTopicCallable);
    when(stub.getTopicCallable()).thenReturn(getTopicCallable);
    when(stub.getTopicPartitionsCallable()).thenReturn(getTopicPartitionsCallable);
    when(stub.listTopicsCallable()).thenReturn(listTopicsCallable);
    when(stub.updateTopicCallable()).thenReturn(updateTopicCallable);
    when(stub.deleteTopicCallable()).thenReturn(deleteTopicCallable);
    when(stub.listTopicSubscriptionsCallable()).thenReturn(listTopicSubscriptionsCallable);

    when(stub.createSubscriptionCallable()).thenReturn(createSubscriptionCallable);
    when(stub.getSubscriptionCallable()).thenReturn(getSubscriptionCallable);
    when(stub.listSubscriptionsCallable()).thenReturn(listSubscriptionsCallable);
    when(stub.updateSubscriptionCallable()).thenReturn(updateSubscriptionCallable);
    when(stub.deleteSubscriptionCallable()).thenReturn(deleteSubscriptionCallable);

    client = new AdminClientImpl(REGION, AdminServiceClient.create(stub));
  }

  @After
  public void tearDown() throws Exception {
    client.shutdownNow();
    verify(stub).shutdownNow();
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(REGION);
  }

  @Test
  public void createTopic_Ok() throws Exception {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(topicPath().locationPath().toString())
            .setTopic(TOPIC)
            .setTopicId(topicName().value())
            .build();

    when(createTopicCallable.futureCall(request)).thenReturn(immediateFuture(TOPIC_2));

    assertThat(client.createTopic(TOPIC).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void createTopic_Error() {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(topicPath().locationPath().toString())
            .setTopic(TOPIC)
            .setTopicId(topicName().value())
            .build();

    when(createTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.createTopic(TOPIC), Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateTopic_Ok() throws Exception {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

    when(updateTopicCallable.futureCall(request)).thenReturn(immediateFuture(TOPIC_2));

    assertThat(client.updateTopic(TOPIC, MASK).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void updateTopic_NonRetryableError() {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

    when(updateTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.updateTopic(TOPIC, MASK), Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteTopic_Ok() throws Exception {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(topicPath().toString()).build();

    when(deleteTopicCallable.futureCall(request))
        .thenReturn(immediateFuture(Empty.getDefaultInstance()));

    client.deleteTopic(topicPath()).get();
  }

  @Test
  public void deleteTopic_Error() {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(topicPath().toString()).build();

    when(deleteTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.deleteTopic(topicPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopic_Ok() throws Exception {
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().toString()).build();

    when(getTopicCallable.futureCall(request)).thenReturn(immediateFuture(TOPIC));

    assertThat(client.getTopic(topicPath()).get()).isEqualTo(TOPIC);
  }

  @Test
  public void getTopic_Error() {
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().toString()).build();

    when(getTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.getTopic(topicPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopicPartitionCount_Ok() throws Exception {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(topicPath().toString()).build();

    when(getTopicPartitionsCallable.futureCall(request))
        .thenReturn(immediateFuture(TopicPartitions.newBuilder().setPartitionCount(10).build()));

    assertThat(client.getTopicPartitionCount(topicPath()).get()).isEqualTo(10);
  }

  @Test
  public void getTopicPartitionCount_NonRetryableError() {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(topicPath().toString()).build();

    when(getTopicPartitionsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.getTopicPartitionCount(topicPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listTopicSubscriptions_Ok() throws Exception {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().toString()).build();

    SubscriptionPath path1 =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(111))
            .setName(SubscriptionName.of("def"))
            .setLocation(ZONE)
            .build();
    SubscriptionPath path2 =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(222))
            .setName(SubscriptionName.of("abc"))
            .setLocation(ZONE)
            .build();
    ListTopicSubscriptionsResponse response =
        ListTopicSubscriptionsResponse.newBuilder()
            .addSubscriptions(path1.toString())
            .addSubscriptions(path2.toString())
            .build();

    when(listTopicSubscriptionsCallable.futureCall(request)).thenReturn(immediateFuture(response));

    assertThat(client.listTopicSubscriptions(topicPath()).get()).containsExactly(path1, path2);
  }

  @Test
  public void listTopicSubscriptions_Error() {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().toString()).build();

    when(listTopicSubscriptionsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.listTopicSubscriptions(topicPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listTopics_Ok() throws Exception {
    ListTopicsRequest request =
        ListTopicsRequest.newBuilder().setParent(example(LocationPath.class).toString()).build();

    when(listTopicsCallable.futureCall(request))
        .thenReturn(
            immediateFuture(
                ListTopicsResponse.newBuilder()
                    .addAllTopics(ImmutableList.of(TOPIC_2, TOPIC))
                    .build()));

    assertThat(client.listTopics(example(LocationPath.class)).get())
        .containsExactly(TOPIC_2, TOPIC);
  }

  @Test
  public void listTopics_Error() {
    ListTopicsRequest request =
        ListTopicsRequest.newBuilder().setParent(example(LocationPath.class).toString()).build();

    when(listTopicsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.listTopics(example(LocationPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void createSubscription_Ok() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionPath().locationPath().toString())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .setSkipBacklog(false)
            .build();

    when(createSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(client.createSubscription(SUBSCRIPTION).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void createSubscription_Error() {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionPath().locationPath().toString())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .setSkipBacklog(false)
            .build();

    when(createSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.createSubscription(SUBSCRIPTION), Code.FAILED_PRECONDITION);
  }

  @Test
  public void createSubscriptionAtHead_Ok() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionPath().locationPath().toString())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .setSkipBacklog(true)
            .build();

    when(createSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(client.createSubscription(SUBSCRIPTION, StartingOffset.END).get())
        .isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void createSubscriptionAtHead_Error() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionPath().locationPath().toString())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .setSkipBacklog(true)
            .build();

    when(createSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.createSubscription(SUBSCRIPTION, StartingOffset.END), Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateSubscription_Ok() throws Exception {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(SUBSCRIPTION)
            .setUpdateMask(MASK)
            .build();

    when(updateSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(client.updateSubscription(SUBSCRIPTION, MASK).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void updateSubscription_Error() {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(SUBSCRIPTION)
            .setUpdateMask(MASK)
            .build();

    when(updateSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.updateSubscription(SUBSCRIPTION, MASK), Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteSubscription_Ok() throws Exception {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().toString()).build();

    when(deleteSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(Empty.getDefaultInstance()));

    client.deleteSubscription(subscriptionPath()).get();
  }

  @Test
  public void deleteSubscription_Error() {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().toString()).build();

    when(deleteSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.deleteSubscription(subscriptionPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getSubscription_Ok() throws Exception {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(subscriptionPath().toString()).build();

    when(getSubscriptionCallable.futureCall(request)).thenReturn(immediateFuture(SUBSCRIPTION));

    assertThat(client.getSubscription(subscriptionPath()).get()).isEqualTo(SUBSCRIPTION);
  }

  @Test
  public void getSubscription_Error() {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(subscriptionPath().toString()).build();

    when(getSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.getSubscription(subscriptionPath()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listSubscriptions_Ok() throws Exception {
    ListSubscriptionsRequest request =
        ListSubscriptionsRequest.newBuilder()
            .setParent(example(LocationPath.class).toString())
            .build();

    when(listSubscriptionsCallable.futureCall(request))
        .thenReturn(
            immediateFuture(
                ListSubscriptionsResponse.newBuilder()
                    .addAllSubscriptions(ImmutableList.of(SUBSCRIPTION_2, SUBSCRIPTION))
                    .build()));

    assertThat(client.listSubscriptions(example(LocationPath.class)).get())
        .containsExactly(SUBSCRIPTION_2, SUBSCRIPTION);
  }

  @Test
  public void listSubscriptions_Error() {
    ListSubscriptionsRequest request =
        ListSubscriptionsRequest.newBuilder()
            .setParent(example(LocationPath.class).toString())
            .build();

    when(listSubscriptionsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.listSubscriptions(example(LocationPath.class)), Code.FAILED_PRECONDITION);
  }
}
