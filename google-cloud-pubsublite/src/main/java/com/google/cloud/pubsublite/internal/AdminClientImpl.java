// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal;

import com.google.api.core.ApiFuture;
import com.google.api.core.NanoClock;
import com.google.api.gax.retrying.ExponentialRetryAlgorithm;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.RetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetryingExecutor;
import com.google.api.gax.retrying.RetryingFuture;
import com.google.api.gax.retrying.ScheduledRetryingExecutor;
import com.google.api.gax.retrying.TimedAttemptSettings;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
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
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.FieldMask;
import io.grpc.Status;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

public class AdminClientImpl implements AdminClient {
  private final CloudRegion region;
  private final AdminServiceGrpc.AdminServiceBlockingStub stub;
  private final RetryingExecutor<Void> voidRetryingExecutor;
  private final RetryingExecutor<Topic> topicRetryingExecutor;
  private final RetryingExecutor<Subscription> subscriptionRetryingExecutor;
  private final RetryingExecutor<Long> partitionCountRetryingExecutor;
  private final RetryingExecutor<List<Topic>> listTopicsRetryingExecutor;
  private final RetryingExecutor<List<SubscriptionPath>> listTopicSubscriptionsRetryingExecutor;
  private final RetryingExecutor<List<Subscription>> listSubscriptionsRetryingExecutor;

  public AdminClientImpl(
      CloudRegion region,
      AdminServiceGrpc.AdminServiceBlockingStub stub,
      RetrySettings retrySettings,
      ScheduledExecutorService executor) {
    this.region = region;
    this.stub = stub;
    this.voidRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.topicRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.subscriptionRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.partitionCountRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.listTopicsRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.listSubscriptionsRetryingExecutor = retryingExecutor(retrySettings, executor);
    this.listTopicSubscriptionsRetryingExecutor = retryingExecutor(retrySettings, executor);
  }

  private static <T> RetryingExecutor<T> retryingExecutor(
      RetrySettings settings, ScheduledExecutorService executor) {
    return new ScheduledRetryingExecutor<>(retryAlgorithm(settings), executor);
  }

  private static <T> RetryAlgorithm<T> retryAlgorithm(RetrySettings retrySettings) {
    return new RetryAlgorithm<>(
        resultRetryAlgorithm(),
        new ExponentialRetryAlgorithm(retrySettings, NanoClock.getDefaultClock()));
  }

  private static <T> ResultRetryAlgorithm<T> resultRetryAlgorithm() {
    return new ResultRetryAlgorithm<T>() {
      @Override
      public TimedAttemptSettings createNextAttempt(
          Throwable prevThrowable, T prevResponse, TimedAttemptSettings prevSettings) {
        return null; // Null means no specific settings.
      }

      @Override
      public boolean shouldRetry(Throwable prevThrowable, T prevResponse) {
        if (null != prevResponse) return false;
        Optional<Status> statusOr = ExtractStatus.extract(prevThrowable);
        if (!statusOr.isPresent()) return false; // Received a non-grpc error.
        return ErrorCodes.IsRetryable(statusOr.get().getCode());
      }
    };
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  private static <T> ApiFuture<T> runWithRetries(
      Callable<T> callable, RetryingExecutor<T> executor) {
    RetryingFuture<T> retryingFuture = executor.createFuture(callable);
    retryingFuture.setAttemptFuture(executor.submit(retryingFuture));
    return retryingFuture;
  }

  @Override
  public ApiFuture<Topic> createTopic(Topic topic) {
    return runWithRetries(
        () -> {
          TopicPath path = TopicPath.of(topic.getName());
          return stub.createTopic(
              CreateTopicRequest.newBuilder()
                  .setParent(TopicPaths.getLocationPath(path).value())
                  .setTopic(topic)
                  .setTopicId(TopicPaths.getTopicName(path).value())
                  .build());
        },
        topicRetryingExecutor);
  }

  @Override
  public ApiFuture<Topic> getTopic(TopicPath path) {
    return runWithRetries(
        () -> stub.getTopic(GetTopicRequest.newBuilder().setName(path.value()).build()),
        topicRetryingExecutor);
  }

  @Override
  public ApiFuture<Long> getTopicPartitionCount(TopicPath path) {
    return runWithRetries(
        () ->
            stub.getTopicPartitions(
                    GetTopicPartitionsRequest.newBuilder().setName(path.value()).build())
                .getPartitionCount(),
        partitionCountRetryingExecutor);
  }

  @Override
  public ApiFuture<List<Topic>> listTopics(LocationPath path) {
    return runWithRetries(
        () -> {
          return stub.listTopics(ListTopicsRequest.newBuilder().setParent(path.value()).build())
              .getTopicsList();
        },
        listTopicsRetryingExecutor);
  }

  @Override
  public ApiFuture<Topic> updateTopic(Topic topic, FieldMask mask) {
    return runWithRetries(
        () -> {
          return stub.updateTopic(
              UpdateTopicRequest.newBuilder().setTopic(topic).setUpdateMask(mask).build());
        },
        topicRetryingExecutor);
  }

  @Override
  @SuppressWarnings("UnusedReturnValue")
  public ApiFuture<Void> deleteTopic(TopicPath path) {
    return runWithRetries(
        () -> {
          stub.deleteTopic(DeleteTopicRequest.newBuilder().setName(path.value()).build());
          return null;
        },
        voidRetryingExecutor);
  }

  @Override
  public ApiFuture<List<SubscriptionPath>> listTopicSubscriptions(TopicPath path) {
    return runWithRetries(
        () -> {
          ImmutableList.Builder<SubscriptionPath> builder = ImmutableList.builder();
          for (String subscription :
              stub.listTopicSubscriptions(
                      ListTopicSubscriptionsRequest.newBuilder().setName(path.value()).build())
                  .getSubscriptionsList()) {
            SubscriptionPath subscription_path = SubscriptionPath.of(subscription);
            SubscriptionPaths.check(subscription_path);
            builder.add(subscription_path);
          }
          return builder.build();
        },
        listTopicSubscriptionsRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> createSubscription(Subscription subscription) {
    return runWithRetries(
        () -> {
          SubscriptionPath path = SubscriptionPath.of(subscription.getName());
          return stub.createSubscription(
              CreateSubscriptionRequest.newBuilder()
                  .setParent(SubscriptionPaths.getLocationPath(path).value())
                  .setSubscription(subscription)
                  .setSubscriptionId(SubscriptionPaths.getSubscriptionName(path).value())
                  .build());
        },
        subscriptionRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> getSubscription(SubscriptionPath path) {
    return runWithRetries(
        () ->
            stub.getSubscription(GetSubscriptionRequest.newBuilder().setName(path.value()).build()),
        subscriptionRetryingExecutor);
  }

  @Override
  public ApiFuture<List<Subscription>> listSubscriptions(LocationPath path) {
    return runWithRetries(
        () -> {
          return stub.listSubscriptions(
                  ListSubscriptionsRequest.newBuilder().setParent(path.value()).build())
              .getSubscriptionsList();
        },
        listSubscriptionsRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> updateSubscription(Subscription subscription, FieldMask mask) {
    return runWithRetries(
        () -> {
          return stub.updateSubscription(
              UpdateSubscriptionRequest.newBuilder()
                  .setSubscription(subscription)
                  .setUpdateMask(mask)
                  .build());
        },
        subscriptionRetryingExecutor);
  }

  @Override
  @SuppressWarnings("UnusedReturnValue")
  public ApiFuture<Void> deleteSubscription(SubscriptionPath path) {
    return runWithRetries(
        () -> {
          stub.deleteSubscription(
              DeleteSubscriptionRequest.newBuilder().setName(path.value()).build());
          return null;
        },
        voidRetryingExecutor);
  }
}
