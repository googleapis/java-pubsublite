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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.AdminClientBuilder;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.AdminServiceGrpc;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.Durations;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class AdminClientImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static final CloudRegion REGION = CloudRegion.of("us-east1");
  private static final CloudZone ZONE = CloudZone.of(REGION, 'x');
  private static final FieldMask MASK = FieldMask.newBuilder().addPaths("config").build();

  private static ProjectNumber projectNumber() {
    try {
      return ProjectNumber.of(123);
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static TopicName topicName() {
    try {
      return TopicName.of("abc");
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static TopicPath topicPath() {
    try {
      return TopicPaths.newBuilder()
          .setZone(ZONE)
          .setProjectNumber(projectNumber())
          .setTopicName(topicName())
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static LocationPath topicParent() {
    try {
      return TopicPaths.getLocationPath(topicPath());
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static final Topic TOPIC =
      Topic.newBuilder()
          .setName(topicPath().value())
          .setPartitionConfig(PartitionConfig.newBuilder().setCount(10))
          .setRetentionConfig(RetentionConfig.newBuilder().setPeriod(Durations.fromDays(1)))
          .build();
  private static final Topic TOPIC_2 =
      TOPIC.toBuilder().setPartitionConfig(PartitionConfig.newBuilder().setCount(88)).build();

  private static SubscriptionName subscriptionName() {
    try {
      return SubscriptionName.of("abc");
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static SubscriptionPath subscriptionPath() {
    try {
      return SubscriptionPaths.newBuilder()
          .setZone(ZONE)
          .setProjectNumber(projectNumber())
          .setSubscriptionName(subscriptionName())
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static LocationPath subscriptionParent() {
    try {
      return SubscriptionPaths.getLocationPath(subscriptionPath());
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static final Subscription SUBSCRIPTION =
      Subscription.newBuilder()
          .setDeliveryConfig(
              DeliveryConfig.newBuilder()
                  .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_AFTER_STORED))
          .setName(subscriptionPath().value())
          .setTopic(topicPath().value())
          .build();
  private static final Subscription SUBSCRIPTION_2 =
      SUBSCRIPTION
          .toBuilder()
          .setDeliveryConfig(
              DeliveryConfig.newBuilder()
                  .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_IMMEDIATELY))
          .build();

  private final AdminServiceGrpc.AdminServiceImplBase serviceImpl =
      mock(
          AdminServiceGrpc.AdminServiceImplBase.class,
          delegatesTo(new AdminServiceGrpc.AdminServiceImplBase() {}));

  private AdminClientImpl client;

  @Before
  public void setUp() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(serviceImpl)
            .build()
            .start());
    ManagedChannel channel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);
    client =
        new AdminClientImpl(
            REGION,
            stub,
            AdminClientBuilder.DEFAULT_RETRY_SETTINGS,
            Executors.newSingleThreadScheduledExecutor());
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(REGION);
  }

  private static <T> Answer<Void> answerWith(T response) {
    return invocation -> {
      StreamObserver<T> responseObserver = invocation.getArgument(1);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return null;
    };
  }

  private static Answer<Void> answerWith(Status status) {
    return invocation -> {
      StreamObserver<?> responseObserver = invocation.getArgument(1);
      responseObserver.onError(status.asRuntimeException());
      return null;
    };
  }

  private static Answer<Void> inOrder(Answer<Void>... answers) {
    AtomicInteger count = new AtomicInteger(0);
    return invocation -> {
      int index = count.getAndIncrement();

      return answers[index].answer(invocation);
    };
  }

  @Test
  public void createTopic_Ok() throws Exception {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(topicParent().value())
            .setTopic(TOPIC)
            .setTopicId(topicName().value())
            .build();

    doAnswer(answerWith(TOPIC_2)).when(serviceImpl).createTopic(eq(request), any());

    assertThat(client.createTopic(TOPIC).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void createTopic_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(topicParent().value())
            .setTopic(TOPIC)
            .setTopicId(topicName().value())
            .build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .createTopic(eq(request), any());

    ApiFuture<Topic> future = client.createTopic(TOPIC);
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void createTopic_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      CreateTopicRequest request =
          CreateTopicRequest.newBuilder()
              .setParent(topicParent().value())
              .setTopic(TOPIC)
              .setTopicId(topicName().value())
              .build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(TOPIC_2)))
          .when(serviceImpl)
          .createTopic(eq(request), any());

      assertThat(client.createTopic(TOPIC).get()).isEqualTo(TOPIC_2);
    }
  }

  @Test
  public void createTopic_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(topicParent().value())
            .setTopic(TOPIC)
            .setTopicId(topicName().value())
            .build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(TOPIC_2)))
        .when(serviceImpl)
        .createTopic(eq(request), any());

    assertThat(client.createTopic(TOPIC).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void updateTopic_Ok() throws Exception {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

    doAnswer(answerWith(TOPIC_2)).when(serviceImpl).updateTopic(eq(request), any());

    assertThat(client.updateTopic(TOPIC, MASK).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void updateTopic_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .updateTopic(eq(request), any());

    ApiFuture<Topic> future = client.updateTopic(TOPIC, MASK);
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateTopic_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      UpdateTopicRequest request =
          UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(TOPIC_2)))
          .when(serviceImpl)
          .updateTopic(eq(request), any());

      assertThat(client.updateTopic(TOPIC, MASK).get()).isEqualTo(TOPIC_2);
    }
  }

  @Test
  public void updateTopic_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(TOPIC).setUpdateMask(MASK).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(TOPIC_2)))
        .when(serviceImpl)
        .updateTopic(eq(request), any());

    assertThat(client.updateTopic(TOPIC, MASK).get()).isEqualTo(TOPIC_2);
  }

  @Test
  public void deleteTopic_Ok() throws Exception {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(Empty.getDefaultInstance()))
        .when(serviceImpl)
        .deleteTopic(eq(request), any());

    client.deleteTopic(topicPath()).get();
  }

  @Test
  public void deleteTopic_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .deleteTopic(eq(request), any());

    ApiFuture<Void> future = client.deleteTopic(topicPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteTopic_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      DeleteTopicRequest request =
          DeleteTopicRequest.newBuilder().setName(topicPath().value()).build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(Empty.getDefaultInstance())))
          .when(serviceImpl)
          .deleteTopic(eq(request), any());

      client.deleteTopic(topicPath()).get();
    }
  }

  @Test
  public void deleteTopic_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Empty.getDefaultInstance())))
        .when(serviceImpl)
        .deleteTopic(eq(request), any());

    client.deleteTopic(topicPath()).get();
  }

  @Test
  public void getTopic_Ok() throws Exception {
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(TOPIC)).when(serviceImpl).getTopic(eq(request), any());

    assertThat(client.getTopic(topicPath()).get()).isEqualTo(TOPIC);
  }

  @Test
  public void getTopic_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION)).when(serviceImpl).getTopic(eq(request), any());

    ApiFuture<Topic> future = client.getTopic(topicPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopic_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().value()).build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(Topic.getDefaultInstance())))
          .when(serviceImpl)
          .getTopic(eq(request), any());

      client.getTopic(topicPath()).get();
    }
  }

  @Test
  public void getTopic_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Topic.getDefaultInstance())))
        .when(serviceImpl)
        .getTopic(eq(request), any());

    client.getTopic(topicPath()).get();
  }

  @Test
  public void getTopicPartitionCount_Ok() throws Exception {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(TopicPartitions.newBuilder().setPartitionCount(10).build()))
        .when(serviceImpl)
        .getTopicPartitions(eq(request), any());

    assertThat(client.getTopicPartitionCount(topicPath()).get()).isEqualTo(10);
  }

  @Test
  public void getTopicPartitionCount_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .getTopicPartitions(eq(request), any());

    ApiFuture<Long> future = client.getTopicPartitionCount(topicPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void getTopicPartitionCount_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      GetTopicPartitionsRequest request =
          GetTopicPartitionsRequest.newBuilder().setName(topicPath().value()).build();

      doAnswer(
              inOrder(
                  answerWith(Status.fromCode(code)),
                  answerWith(TopicPartitions.getDefaultInstance())))
          .when(serviceImpl)
          .getTopicPartitions(eq(request), any());

      client.getTopicPartitionCount(topicPath()).get();
    }
  }

  @Test
  public void getTopicPartitionCount_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(TopicPartitions.getDefaultInstance())))
        .when(serviceImpl)
        .getTopicPartitions(eq(request), any());

    client.getTopicPartitionCount(topicPath()).get();
  }

  @Test
  public void listTopicSubscriptions_Ok() throws Exception {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().value()).build();

    SubscriptionPath path1 =
        SubscriptionPaths.newBuilder()
            .setProjectNumber(ProjectNumber.of(111))
            .setSubscriptionName(SubscriptionName.of("def"))
            .setZone(ZONE)
            .build();
    SubscriptionPath path2 =
        SubscriptionPaths.newBuilder()
            .setProjectNumber(ProjectNumber.of(222))
            .setSubscriptionName(SubscriptionName.of("abc"))
            .setZone(ZONE)
            .build();
    ListTopicSubscriptionsResponse response =
        ListTopicSubscriptionsResponse.newBuilder()
            .addSubscriptions(path1.value())
            .addSubscriptions(path2.value())
            .build();

    doAnswer(answerWith(response)).when(serviceImpl).listTopicSubscriptions(eq(request), any());

    assertThat(client.listTopicSubscriptions(topicPath()).get()).containsExactly(path1, path2);
  }

  @Test
  public void listTopicSubscriptions_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .listTopicSubscriptions(eq(request), any());

    ApiFuture<List<SubscriptionPath>> future = client.listTopicSubscriptions(topicPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void listTopicSubscriptions_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      ListTopicSubscriptionsRequest request =
          ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().value()).build();

      doAnswer(
              inOrder(
                  answerWith(Status.fromCode(code)),
                  answerWith(ListTopicSubscriptionsResponse.getDefaultInstance())))
          .when(serviceImpl)
          .listTopicSubscriptions(eq(request), any());

      client.listTopicSubscriptions(topicPath()).get();
    }
  }

  @Test
  public void listTopicSubscriptions_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(topicPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(ListTopicSubscriptionsResponse.getDefaultInstance())))
        .when(serviceImpl)
        .listTopicSubscriptions(eq(request), any());

    client.listTopicSubscriptions(topicPath()).get();
  }

  @Test
  public void createSubscription_Ok() throws Exception {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionParent().value())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .build();

    doAnswer(answerWith(SUBSCRIPTION_2)).when(serviceImpl).createSubscription(eq(request), any());

    assertThat(client.createSubscription(SUBSCRIPTION).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void createSubscription_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionParent().value())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .createSubscription(eq(request), any());

    ApiFuture<Subscription> future = client.createSubscription(SUBSCRIPTION);
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void createSubscription_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      CreateSubscriptionRequest request =
          CreateSubscriptionRequest.newBuilder()
              .setParent(subscriptionParent().value())
              .setSubscription(SUBSCRIPTION)
              .setSubscriptionId(subscriptionName().value())
              .build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(SUBSCRIPTION_2)))
          .when(serviceImpl)
          .createSubscription(eq(request), any());

      assertThat(client.createSubscription(SUBSCRIPTION).get()).isEqualTo(SUBSCRIPTION_2);
    }
  }

  @Test
  public void createSubscription_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(subscriptionParent().value())
            .setSubscription(SUBSCRIPTION)
            .setSubscriptionId(subscriptionName().value())
            .build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(SUBSCRIPTION_2)))
        .when(serviceImpl)
        .createSubscription(eq(request), any());

    assertThat(client.createSubscription(SUBSCRIPTION).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void updateSubscription_Ok() throws Exception {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(SUBSCRIPTION)
            .setUpdateMask(MASK)
            .build();

    doAnswer(answerWith(SUBSCRIPTION_2)).when(serviceImpl).updateSubscription(eq(request), any());

    assertThat(client.updateSubscription(SUBSCRIPTION, MASK).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void updateSubscription_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(SUBSCRIPTION)
            .setUpdateMask(MASK)
            .build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .updateSubscription(eq(request), any());

    ApiFuture<Subscription> future = client.updateSubscription(SUBSCRIPTION, MASK);
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void updateSubscription_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      UpdateSubscriptionRequest request =
          UpdateSubscriptionRequest.newBuilder()
              .setSubscription(SUBSCRIPTION)
              .setUpdateMask(MASK)
              .build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(SUBSCRIPTION_2)))
          .when(serviceImpl)
          .updateSubscription(eq(request), any());

      assertThat(client.updateSubscription(SUBSCRIPTION, MASK).get()).isEqualTo(SUBSCRIPTION_2);
    }
  }

  @Test
  public void updateSubscription_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(SUBSCRIPTION)
            .setUpdateMask(MASK)
            .build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(SUBSCRIPTION_2)))
        .when(serviceImpl)
        .updateSubscription(eq(request), any());

    assertThat(client.updateSubscription(SUBSCRIPTION, MASK).get()).isEqualTo(SUBSCRIPTION_2);
  }

  @Test
  public void deleteSubscription_Ok() throws Exception {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(answerWith(Empty.getDefaultInstance()))
        .when(serviceImpl)
        .deleteSubscription(eq(request), any());

    client.deleteSubscription(subscriptionPath()).get();
  }

  @Test
  public void deleteSubscription_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .deleteSubscription(eq(request), any());

    ApiFuture<Void> future = client.deleteSubscription(subscriptionPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void deleteSubscription_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      DeleteSubscriptionRequest request =
          DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(Empty.getDefaultInstance())))
          .when(serviceImpl)
          .deleteSubscription(eq(request), any());

      client.deleteSubscription(subscriptionPath()).get();
    }
  }

  @Test
  public void deleteSubscription_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Empty.getDefaultInstance())))
        .when(serviceImpl)
        .deleteSubscription(eq(request), any());

    client.deleteSubscription(subscriptionPath()).get();
  }

  @Test
  public void getSubscription_Ok() throws Exception {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(answerWith(SUBSCRIPTION)).when(serviceImpl).getSubscription(eq(request), any());

    assertThat(client.getSubscription(subscriptionPath()).get()).isEqualTo(SUBSCRIPTION);
  }

  @Test
  public void getSubscription_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .getSubscription(eq(request), any());

    ApiFuture<Subscription> future = client.getSubscription(subscriptionPath());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void getSubscription_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      GetSubscriptionRequest request =
          GetSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

      doAnswer(
              inOrder(
                  answerWith(Status.fromCode(code)), answerWith(Subscription.getDefaultInstance())))
          .when(serviceImpl)
          .getSubscription(eq(request), any());

      client.getSubscription(subscriptionPath()).get();
    }
  }

  @Test
  public void getSubscription_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(subscriptionPath().value()).build();

    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Subscription.getDefaultInstance())))
        .when(serviceImpl)
        .getSubscription(eq(request), any());

    client.getSubscription(subscriptionPath()).get();
  }
}
