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

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.core.ExecutorAsBackgroundResource;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetryingExecutor;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectLookupUtils;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AdminClientImpl extends BackgroundResourceAggregation implements AdminClient {
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
      RetrySettings retrySettings) {
    this(
        region,
        stub,
        retrySettings,
        // TODO: Consider allowing tuning in the future.
        Executors.newScheduledThreadPool(6));
  }

  private AdminClientImpl(
      CloudRegion region,
      AdminServiceGrpc.AdminServiceBlockingStub stub,
      RetrySettings retrySettings,
      ScheduledExecutorService executor) {
    super(ImmutableList.of(new ExecutorAsBackgroundResource(executor)));
    this.region = region;
    this.stub = stub;
    this.voidRetryingExecutor = RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.topicRetryingExecutor = RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.subscriptionRetryingExecutor =
        RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.partitionCountRetryingExecutor =
        RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.listTopicsRetryingExecutor =
        RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.listSubscriptionsRetryingExecutor =
        RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
    this.listTopicSubscriptionsRetryingExecutor =
        RetryingExecutorUtil.retryingExecutor(retrySettings, executor);
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  @Override
  public ApiFuture<Topic> createTopic(Topic topic) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          TopicPath path = ProjectLookupUtils.toCannonical(TopicPath.parse(topic.getName()));
          return stub.createTopic(
              CreateTopicRequest.newBuilder()
                  .setParent(path.locationPath().toString())
                  .setTopic(topic)
                  .setTopicId(path.name().value())
                  .build());
        },
        topicRetryingExecutor);
  }

  @Override
  public ApiFuture<Topic> getTopic(TopicPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () ->
            stub.getTopic(
                GetTopicRequest.newBuilder()
                    .setName(ProjectLookupUtils.toCannonical(path).toString())
                    .build()),
        topicRetryingExecutor);
  }

  @Override
  public ApiFuture<Long> getTopicPartitionCount(TopicPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () ->
            stub.getTopicPartitions(
                    GetTopicPartitionsRequest.newBuilder()
                        .setName(ProjectLookupUtils.toCannonical(path).toString())
                        .build())
                .getPartitionCount(),
        partitionCountRetryingExecutor);
  }

  @Override
  public ApiFuture<List<Topic>> listTopics(LocationPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> stub.listTopics(
                ListTopicsRequest.newBuilder()
                    .setParent(ProjectLookupUtils.toCannonical(path).toString())
                    .build())
            .getTopicsList(),
        listTopicsRetryingExecutor);
  }

  @Override
  public ApiFuture<Topic> updateTopic(Topic topic, FieldMask mask) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          Topic canonical =
              topic
                  .toBuilder()
                  .setName(
                      ProjectLookupUtils.toCannonical(TopicPath.parse(topic.getName())).toString())
                  .build();
          return stub.updateTopic(
              UpdateTopicRequest.newBuilder().setTopic(canonical).setUpdateMask(mask).build());
        },
        topicRetryingExecutor);
  }

  @Override
  @SuppressWarnings("UnusedReturnValue")
  public ApiFuture<Void> deleteTopic(TopicPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          stub.deleteTopic(
              DeleteTopicRequest.newBuilder()
                  .setName(ProjectLookupUtils.toCannonical(path).toString())
                  .build());
          return null;
        },
        voidRetryingExecutor);
  }

  @Override
  public ApiFuture<List<SubscriptionPath>> listTopicSubscriptions(TopicPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          ImmutableList.Builder<SubscriptionPath> builder = ImmutableList.builder();
          for (String subscription :
              stub.listTopicSubscriptions(
                      ListTopicSubscriptionsRequest.newBuilder()
                          .setName(ProjectLookupUtils.toCannonical(path).toString())
                          .build())
                  .getSubscriptionsList()) {
            SubscriptionPath subscription_path = SubscriptionPath.parse(subscription);
            builder.add(subscription_path);
          }
          return builder.build();
        },
        listTopicSubscriptionsRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> createSubscription(Subscription subscription) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          SubscriptionPath path =
              ProjectLookupUtils.toCannonical(SubscriptionPath.parse(subscription.getName()));
          return stub.createSubscription(
              CreateSubscriptionRequest.newBuilder()
                  .setParent(path.locationPath().toString())
                  .setSubscription(subscription)
                  .setSubscriptionId(path.name().toString())
                  .build());
        },
        subscriptionRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> getSubscription(SubscriptionPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () ->
            stub.getSubscription(
                GetSubscriptionRequest.newBuilder()
                    .setName(ProjectLookupUtils.toCannonical(path).toString())
                    .build()),
        subscriptionRetryingExecutor);
  }

  @Override
  public ApiFuture<List<Subscription>> listSubscriptions(LocationPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> stub.listSubscriptions(
                ListSubscriptionsRequest.newBuilder()
                    .setParent(ProjectLookupUtils.toCannonical(path).toString())
                    .build())
            .getSubscriptionsList(),
        listSubscriptionsRetryingExecutor);
  }

  @Override
  public ApiFuture<Subscription> updateSubscription(Subscription subscription, FieldMask mask) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          Subscription canonical =
              subscription
                  .toBuilder()
                  .setName(
                      ProjectLookupUtils.toCannonical(
                              SubscriptionPath.parse(subscription.getName()))
                          .toString())
                  .build();
          return stub.updateSubscription(
              UpdateSubscriptionRequest.newBuilder()
                  .setSubscription(canonical)
                  .setUpdateMask(mask)
                  .build());
        },
        subscriptionRetryingExecutor);
  }

  @Override
  @SuppressWarnings("UnusedReturnValue")
  public ApiFuture<Void> deleteSubscription(SubscriptionPath path) {
    return RetryingExecutorUtil.runWithRetries(
        () -> {
          stub.deleteSubscription(
              DeleteSubscriptionRequest.newBuilder()
                  .setName(ProjectLookupUtils.toCannonical(path).toString())
                  .build());
          return null;
        },
        voidRetryingExecutor);
  }
}
