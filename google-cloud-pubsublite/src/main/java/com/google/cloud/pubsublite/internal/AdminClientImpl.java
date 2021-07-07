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
import com.google.api.core.ApiFutures;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SeekTarget;
import com.google.cloud.pubsublite.SubscriptionPath;
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
import com.google.cloud.pubsublite.proto.ListReservationsRequest;
import com.google.cloud.pubsublite.proto.ListReservationsResponse;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest.NamedTarget;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.FieldMask;
import java.util.List;

public class AdminClientImpl extends ApiResourceAggregation implements AdminClient {
  private final CloudRegion region;
  AdminServiceClient serviceClient;

  public AdminClientImpl(CloudRegion region, AdminServiceClient serviceClient) {
    super(serviceClient);
    this.region = region;
    this.serviceClient = serviceClient;
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  @Override
  public ApiFuture<Topic> createTopic(Topic topic) {
    TopicPath path = TopicPath.parse(topic.getName());
    return serviceClient
        .createTopicCallable()
        .futureCall(
            CreateTopicRequest.newBuilder()
                .setParent(path.locationPath().toString())
                .setTopic(topic)
                .setTopicId(path.name().value())
                .build());
  }

  @Override
  public ApiFuture<Topic> getTopic(TopicPath path) {
    return serviceClient
        .getTopicCallable()
        .futureCall(GetTopicRequest.newBuilder().setName(path.toString()).build());
  }

  @Override
  public ApiFuture<Long> getTopicPartitionCount(TopicPath path) {
    return ApiFutures.transform(
        serviceClient
            .getTopicPartitionsCallable()
            .futureCall(GetTopicPartitionsRequest.newBuilder().setName(path.toString()).build()),
        TopicPartitions::getPartitionCount,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<List<Topic>> listTopics(LocationPath path) {
    return ApiFutures.transform(
        serviceClient
            .listTopicsCallable()
            .futureCall(ListTopicsRequest.newBuilder().setParent(path.toString()).build()),
        ListTopicsResponse::getTopicsList,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Topic> updateTopic(Topic topic, FieldMask mask) {
    return serviceClient
        .updateTopicCallable()
        .futureCall(UpdateTopicRequest.newBuilder().setTopic(topic).setUpdateMask(mask).build());
  }

  @Override
  public ApiFuture<Void> deleteTopic(TopicPath path) {
    return ApiFutures.transform(
        serviceClient
            .deleteTopicCallable()
            .futureCall(DeleteTopicRequest.newBuilder().setName(path.toString()).build()),
        x -> null,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<List<SubscriptionPath>> listTopicSubscriptions(TopicPath path) {
    return ApiFutures.transform(
        serviceClient
            .listTopicSubscriptionsCallable()
            .futureCall(
                ListTopicSubscriptionsRequest.newBuilder().setName(path.toString()).build()),
        result -> {
          ImmutableList.Builder<SubscriptionPath> builder = ImmutableList.builder();
          for (String subscription : result.getSubscriptionsList()) {
            builder.add(SubscriptionPath.parse(subscription));
          }
          return builder.build();
        },
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Subscription> createSubscription(
      Subscription subscription, BacklogLocation startingOffset) {
    SubscriptionPath path = SubscriptionPath.parse(subscription.getName());
    return serviceClient
        .createSubscriptionCallable()
        .futureCall(
            CreateSubscriptionRequest.newBuilder()
                .setParent(path.locationPath().toString())
                .setSubscription(subscription)
                .setSubscriptionId(path.name().toString())
                .setSkipBacklog(startingOffset == BacklogLocation.END)
                .build());
  }

  @Override
  public ApiFuture<Subscription> getSubscription(SubscriptionPath path) {
    return serviceClient
        .getSubscriptionCallable()
        .futureCall(GetSubscriptionRequest.newBuilder().setName(path.toString()).build());
  }

  @Override
  public ApiFuture<List<Subscription>> listSubscriptions(LocationPath path) {
    return ApiFutures.transform(
        serviceClient
            .listSubscriptionsCallable()
            .futureCall(ListSubscriptionsRequest.newBuilder().setParent(path.toString()).build()),
        ListSubscriptionsResponse::getSubscriptionsList,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Subscription> updateSubscription(Subscription subscription, FieldMask mask) {
    return serviceClient
        .updateSubscriptionCallable()
        .futureCall(
            UpdateSubscriptionRequest.newBuilder()
                .setSubscription(subscription)
                .setUpdateMask(mask)
                .build());
  }

  @Override
  public OperationFuture<SeekSubscriptionResponse, OperationMetadata> seekSubscription(
      SubscriptionPath path, SeekTarget target) {
    SeekSubscriptionRequest.Builder request =
        SeekSubscriptionRequest.newBuilder().setName(path.toString());
    switch (target.getKind()) {
      case BACKLOG_LOCATION:
        if (target.backlogLocation() == BacklogLocation.END) {
          request.setNamedTarget(NamedTarget.HEAD);
        } else if (target.backlogLocation() == BacklogLocation.BEGINNING) {
          request.setNamedTarget(NamedTarget.TAIL);
        }
        break;
      case PUBLISH_TIME:
        request.getTimeTargetBuilder().setPublishTime(target.publishTime());
        break;
      case EVENT_TIME:
        request.getTimeTargetBuilder().setEventTime(target.eventTime());
        break;
    }
    return serviceClient.seekSubscriptionOperationCallable().futureCall(request.build());
  }

  @Override
  public ApiFuture<Void> deleteSubscription(SubscriptionPath path) {
    return ApiFutures.transform(
        serviceClient
            .deleteSubscriptionCallable()
            .futureCall(DeleteSubscriptionRequest.newBuilder().setName(path.toString()).build()),
        x -> null,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Reservation> createReservation(Reservation reservation) {
    ReservationPath path = ReservationPath.parse(reservation.getName());
    return serviceClient
        .createReservationCallable()
        .futureCall(
            CreateReservationRequest.newBuilder()
                .setParent(path.locationPath().toString())
                .setReservation(reservation)
                .setReservationId(path.name().value())
                .build());
  }

  @Override
  public ApiFuture<Reservation> getReservation(ReservationPath path) {
    return serviceClient
        .getReservationCallable()
        .futureCall(GetReservationRequest.newBuilder().setName(path.toString()).build());
  }

  @Override
  public ApiFuture<List<Reservation>> listReservations(LocationPath path) {
    return ApiFutures.transform(
        serviceClient
            .listReservationsCallable()
            .futureCall(ListReservationsRequest.newBuilder().setParent(path.toString()).build()),
        ListReservationsResponse::getReservationsList,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<Reservation> updateReservation(Reservation reservation, FieldMask mask) {
    return serviceClient
        .updateReservationCallable()
        .futureCall(
            UpdateReservationRequest.newBuilder()
                .setReservation(reservation)
                .setUpdateMask(mask)
                .build());
  }

  @Override
  public ApiFuture<Void> deleteReservation(ReservationPath path) {
    return ApiFutures.transform(
        serviceClient
            .deleteReservationCallable()
            .futureCall(DeleteReservationRequest.newBuilder().setName(path.toString()).build()),
        x -> null,
        MoreExecutors.directExecutor());
  }

  @Override
  public ApiFuture<List<TopicPath>> listReservationTopics(ReservationPath path) {
    return ApiFutures.transform(
        serviceClient
            .listReservationTopicsCallable()
            .futureCall(ListReservationTopicsRequest.newBuilder().setName(path.toString()).build()),
        result -> {
          ImmutableList.Builder<TopicPath> builder = ImmutableList.builder();
          for (String subscription : result.getTopicsList()) {
            builder.add(TopicPath.parse(subscription));
          }
          return builder.build();
        },
        MoreExecutors.directExecutor());
  }
}
