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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.CreateReservationRequest;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteReservationRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetReservationRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsResponse;
import com.google.cloud.pubsublite.proto.ListReservationsRequest;
import com.google.cloud.pubsublite.proto.ListReservationsResponse;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.stub.AdminServiceStub;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class AdminClientImplTest {
  private static final FieldMask MASK = FieldMask.newBuilder().addPaths("config").build();

  private static final Topic TOPIC_2 =
      example(Topic.class)
          .toBuilder()
          .setPartitionConfig(PartitionConfig.newBuilder().setCount(88))
          .build();
  private static final Subscription SUBSCRIPTION_2 =
      example(Subscription.class)
          .toBuilder()
          .setDeliveryConfig(
              DeliveryConfig.newBuilder()
                  .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_IMMEDIATELY))
          .build();
  private static final Reservation RESERVATION_2 =
      example(Reservation.class)
          .toBuilder()
          .setThroughputCapacity(example(Reservation.class).getThroughputCapacity() + 1)
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

  @Mock UnaryCallable<CreateReservationRequest, Reservation> createReservationCallable;
  @Mock UnaryCallable<GetReservationRequest, Reservation> getReservationCallable;
  @Mock UnaryCallable<ListReservationsRequest, ListReservationsResponse> listReservationsCallable;
  @Mock UnaryCallable<UpdateReservationRequest, Reservation> updateReservationCallable;
  @Mock UnaryCallable<DeleteReservationRequest, Empty> deleteReservationCallable;

  @Mock
  UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsResponse>
      listReservationTopicsCallable;

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

    when(stub.createReservationCallable()).thenReturn(createReservationCallable);
    when(stub.getReservationCallable()).thenReturn(getReservationCallable);
    when(stub.listReservationsCallable()).thenReturn(listReservationsCallable);
    when(stub.updateReservationCallable()).thenReturn(updateReservationCallable);
    when(stub.deleteReservationCallable()).thenReturn(deleteReservationCallable);
    when(stub.listReservationTopicsCallable()).thenReturn(listReservationTopicsCallable);

    client = new AdminClientImpl(example(CloudRegion.class), AdminServiceClient.create(stub));
  }

  @After
  public void tearDown() throws Exception {
    client.shutdownNow();
    verify(stub).shutdownNow();
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(example(CloudRegion.class));
  }

  @Test
  public void createTopic_Ok() throws Exception {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(example(TopicPath.class).locationPath().toString())
            .setTopic(example(Topic.class))
            .setTopicId(example(TopicName.class).value())
            .build();

    when(createTopicCallable.futureCall(request)).thenReturn(immediateFuture(TOPIC_2));

    assertThat(client.createTopic(example(Topic.class)).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void createTopic_Error() {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(example(TopicPath.class).locationPath().toString())
            .setTopic(example(Topic.class))
            .setTopicId(example(TopicName.class).value())
            .build();

    when(createTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.createTopic(example(Topic.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateTopic_Ok() throws Exception {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(example(Topic.class)).setUpdateMask(MASK).build();

    when(updateTopicCallable.futureCall(request)).thenReturn(immediateFuture(TOPIC_2));

    assertThat(client.updateTopic(example(Topic.class), MASK).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void updateTopic_NonRetryableError() {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(example(Topic.class)).setUpdateMask(MASK).build();

    when(updateTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.updateTopic(example(Topic.class), MASK), Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteTopic_Ok() throws Exception {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(deleteTopicCallable.futureCall(request))
        .thenReturn(immediateFuture(Empty.getDefaultInstance()));

    client.deleteTopic(example(TopicPath.class)).get();
  }

  @Test
  public void deleteTopic_Error() {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(deleteTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.deleteTopic(example(TopicPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopic_Ok() throws Exception {
    GetTopicRequest request =
        GetTopicRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(getTopicCallable.futureCall(request)).thenReturn(immediateFuture(example(Topic.class)));

    assertThat(client.getTopic(example(TopicPath.class)).get()).isEqualTo(example(Topic.class));
  }

  @Test
  public void getTopic_Error() {
    GetTopicRequest request =
        GetTopicRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(getTopicCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(client.getTopic(example(TopicPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopicPartitionCount_Ok() throws Exception {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(getTopicPartitionsCallable.futureCall(request))
        .thenReturn(immediateFuture(TopicPartitions.newBuilder().setPartitionCount(10).build()));

    assertThat(client.getTopicPartitionCount(example(TopicPath.class)).get()).isEqualTo(10);
  }

  @Test
  public void getTopicPartitionCount_NonRetryableError() {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(example(TopicPath.class).toString()).build();

    when(getTopicPartitionsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.getTopicPartitionCount(example(TopicPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listTopicSubscriptions_Ok() throws Exception {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder()
            .setName(example(TopicPath.class).toString())
            .build();

    SubscriptionPath path1 =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(111))
            .setName(SubscriptionName.of("def"))
            .setLocation(example(CloudZone.class))
            .build();
    SubscriptionPath path2 =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(222))
            .setName(SubscriptionName.of("abc"))
            .setLocation(example(CloudZone.class))
            .build();
    ListTopicSubscriptionsResponse response =
        ListTopicSubscriptionsResponse.newBuilder()
            .addSubscriptions(path1.toString())
            .addSubscriptions(path2.toString())
            .build();

    when(listTopicSubscriptionsCallable.futureCall(request)).thenReturn(immediateFuture(response));

    assertThat(client.listTopicSubscriptions(example(TopicPath.class)).get())
        .containsExactly(path1, path2);
  }

  @Test
  public void listTopicSubscriptions_Error() {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder()
            .setName(example(TopicPath.class).toString())
            .build();

    when(listTopicSubscriptionsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.listTopicSubscriptions(example(TopicPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listTopics_Ok() throws Exception {
    ListTopicsRequest request =
        ListTopicsRequest.newBuilder().setParent(example(LocationPath.class).toString()).build();

    when(listTopicsCallable.futureCall(request))
        .thenReturn(
            immediateFuture(
                ListTopicsResponse.newBuilder()
                    .addAllTopics(ImmutableList.of(TOPIC_2, example(Topic.class)))
                    .build()));

    assertThat(client.listTopics(example(LocationPath.class)).get())
        .containsExactly(TOPIC_2, example(Topic.class));
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
            .setParent(example(SubscriptionPath.class).locationPath().toString())
            .setSubscription(example(Subscription.class))
            .setSubscriptionId(example(SubscriptionName.class).value())
            .setSkipBacklog(true)
            .build();

    when(createSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(client.createSubscription(example(Subscription.class)).get())
        .isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void createSubscription_Error() {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(example(SubscriptionPath.class).locationPath().toString())
            .setSubscription(example(Subscription.class))
            .setSubscriptionId(example(SubscriptionName.class).value())
            .setSkipBacklog(true)
            .build();

    when(createSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.createSubscription(example(Subscription.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void createSubscriptionAtBeginning_Ok() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(example(SubscriptionPath.class).locationPath().toString())
            .setSubscription(example(Subscription.class))
            .setSubscriptionId(example(SubscriptionName.class).value())
            .setSkipBacklog(false)
            .build();

    when(createSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(
            client.createSubscription(example(Subscription.class), BacklogLocation.BEGINNING).get())
        .isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void createSubscriptionAtBeginning_Error() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(example(SubscriptionPath.class).locationPath().toString())
            .setSubscription(example(Subscription.class))
            .setSubscriptionId(example(SubscriptionName.class).value())
            .setSkipBacklog(false)
            .build();

    when(createSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.createSubscription(example(Subscription.class), BacklogLocation.BEGINNING),
        Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateSubscription_Ok() throws Exception {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(example(Subscription.class))
            .setUpdateMask(MASK)
            .build();

    when(updateSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(SUBSCRIPTION_2));

    assertThat(client.updateSubscription(example(Subscription.class), MASK).get())
        .isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void updateSubscription_Error() {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(example(Subscription.class))
            .setUpdateMask(MASK)
            .build();

    when(updateSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.updateSubscription(example(Subscription.class), MASK), Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteSubscription_Ok() throws Exception {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder()
            .setName(example(SubscriptionPath.class).toString())
            .build();

    when(deleteSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(Empty.getDefaultInstance()));

    client.deleteSubscription(example(SubscriptionPath.class)).get();
  }

  @Test
  public void deleteSubscription_Error() {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder()
            .setName(example(SubscriptionPath.class).toString())
            .build();

    when(deleteSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.deleteSubscription(example(SubscriptionPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getSubscription_Ok() throws Exception {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder()
            .setName(example(SubscriptionPath.class).toString())
            .build();

    when(getSubscriptionCallable.futureCall(request))
        .thenReturn(immediateFuture(example(Subscription.class)));

    assertThat(client.getSubscription(example(SubscriptionPath.class)).get())
        .isEqualTo(example(Subscription.class));
  }

  @Test
  public void getSubscription_Error() {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder()
            .setName(example(SubscriptionPath.class).toString())
            .build();

    when(getSubscriptionCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.getSubscription(example(SubscriptionPath.class)), Code.FAILED_PRECONDITION);
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
                    .addAllSubscriptions(
                        ImmutableList.of(SUBSCRIPTION_2, example(Subscription.class)))
                    .build()));

    assertThat(client.listSubscriptions(example(LocationPath.class)).get())
        .containsExactly(SUBSCRIPTION_2, example(Subscription.class));
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

  @Test
  public void createReservation_Ok() throws Exception {
    CreateReservationRequest request =
        CreateReservationRequest.newBuilder()
            .setParent(example(ReservationPath.class).locationPath().toString())
            .setReservation(example(Reservation.class))
            .setReservationId(example(ReservationName.class).value())
            .build();

    when(createReservationCallable.futureCall(request)).thenReturn(immediateFuture(RESERVATION_2));

    assertThat(client.createReservation(example(Reservation.class)).get()).isEqualTo(RESERVATION_2);
  }

  @Test
  public void createReservation_Error() {
    CreateReservationRequest request =
        CreateReservationRequest.newBuilder()
            .setParent(example(ReservationPath.class).locationPath().toString())
            .setReservation(example(Reservation.class))
            .setReservationId(example(ReservationName.class).value())
            .build();

    when(createReservationCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.createReservation(example(Reservation.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateReservation_Ok() throws Exception {
    UpdateReservationRequest request =
        UpdateReservationRequest.newBuilder()
            .setReservation(example(Reservation.class))
            .setUpdateMask(MASK)
            .build();

    when(updateReservationCallable.futureCall(request)).thenReturn(immediateFuture(RESERVATION_2));

    assertThat(client.updateReservation(example(Reservation.class), MASK).get())
        .isEqualTo(RESERVATION_2);
  }

  @Test
  public void updateReservation_NonRetryableError() {
    UpdateReservationRequest request =
        UpdateReservationRequest.newBuilder()
            .setReservation(example(Reservation.class))
            .setUpdateMask(MASK)
            .build();

    when(updateReservationCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.updateReservation(example(Reservation.class), MASK), Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteReservation_Ok() throws Exception {
    DeleteReservationRequest request =
        DeleteReservationRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    when(deleteReservationCallable.futureCall(request))
        .thenReturn(immediateFuture(Empty.getDefaultInstance()));

    client.deleteReservation(example(ReservationPath.class)).get();
  }

  @Test
  public void deleteReservation_Error() {
    DeleteReservationRequest request =
        DeleteReservationRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    when(deleteReservationCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.deleteReservation(example(ReservationPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void getReservation_Ok() throws Exception {
    GetReservationRequest request =
        GetReservationRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    when(getReservationCallable.futureCall(request))
        .thenReturn(immediateFuture(example(Reservation.class)));

    assertThat(client.getReservation(example(ReservationPath.class)).get())
        .isEqualTo(example(Reservation.class));
  }

  @Test
  public void getReservation_Error() {
    GetReservationRequest request =
        GetReservationRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    when(getReservationCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.getReservation(example(ReservationPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listReservationTopics_Ok() throws Exception {
    ListReservationTopicsRequest request =
        ListReservationTopicsRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    TopicPath path1 =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(111))
            .setName(TopicName.of("def"))
            .setLocation(example(CloudZone.class))
            .build();
    TopicPath path2 =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(222))
            .setName(TopicName.of("abc"))
            .setLocation(example(CloudZone.class))
            .build();
    ListReservationTopicsResponse response =
        ListReservationTopicsResponse.newBuilder()
            .addTopics(path1.toString())
            .addTopics(path2.toString())
            .build();

    when(listReservationTopicsCallable.futureCall(request)).thenReturn(immediateFuture(response));

    assertThat(client.listReservationTopics(example(ReservationPath.class)).get())
        .containsExactly(path1, path2);
  }

  @Test
  public void listReservationTopics_Error() {
    ListReservationTopicsRequest request =
        ListReservationTopicsRequest.newBuilder()
            .setName(example(ReservationPath.class).toString())
            .build();

    when(listReservationTopicsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.listReservationTopics(example(ReservationPath.class)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void listReservations_Ok() throws Exception {
    ListReservationsRequest request =
        ListReservationsRequest.newBuilder()
            .setParent(example(LocationPath.class).toString())
            .build();

    when(listReservationsCallable.futureCall(request))
        .thenReturn(
            immediateFuture(
                ListReservationsResponse.newBuilder()
                    .addAllReservations(ImmutableList.of(RESERVATION_2, example(Reservation.class)))
                    .build()));

    assertThat(client.listReservations(example(LocationPath.class)).get())
        .containsExactly(RESERVATION_2, example(Reservation.class));
  }

  @Test
  public void listReservations_Error() {
    ListReservationsRequest request =
        ListReservationsRequest.newBuilder()
            .setParent(example(LocationPath.class).toString())
            .build();

    when(listReservationsCallable.futureCall(request)).thenReturn(failedPreconditionFuture());

    assertFutureThrowsCode(
        client.listReservations(example(LocationPath.class)), Code.FAILED_PRECONDITION);
  }
}
