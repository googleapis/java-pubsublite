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

package com.google.cloud.pubsublite;

import com.google.api.core.ApiFuture;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.internal.ApiBackgroundResource;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.protobuf.FieldMask;
import java.util.List;

/** A client for performing Pub/Sub Lite admin operations. */
public interface AdminClient extends ApiBackgroundResource {
  static AdminClient create(AdminClientSettings settings) throws ApiException {
    return settings.instantiate();
  }

  /** The Google Cloud region this client operates on. */
  CloudRegion region();

  /**
   * Create the provided topic if it does not yet exist.
   *
   * @param topic The topic to create.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the topic on success.
   */
  ApiFuture<Topic> createTopic(Topic topic);

  /**
   * Get the topic with id {@code id} if it exists.
   *
   * @param path The path of the topic to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the topic on success.
   */
  ApiFuture<Topic> getTopic(TopicPath path);

  /**
   * Get the partitioning info for the topic with id {@code id} if it exists.
   *
   * @param path The path of the topic to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the number of topic partitions on success.
   */
  ApiFuture<Long> getTopicPartitionCount(TopicPath path);

  /**
   * List all topics for the specified project.
   *
   * @param path The path of the project to list topics for.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the list of topic paths on success.
   */
  ApiFuture<List<Topic>> listTopics(LocationPath path);

  /**
   * Update the topic with path {@code topic.getPath()} if it exists.
   *
   * @param topic The topic to update.
   * @param mask The mask indicating which fields should be updated.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the resulting topic on success. Updating nonexistent topics will cause the future to have
   *     an exception with status {@link com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Topic> updateTopic(Topic topic, FieldMask mask);

  /**
   * Delete the topic with id {@code id} if it exists.
   *
   * @param path The path of the topic to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     void on success. Deleting nonexistent topics will cause the future to have an exception
   *     with status {@link com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Void> deleteTopic(TopicPath path);

  /**
   * Get the list of subscriptions for the topic with id {@code id} if it exists.
   *
   * @param path The path of the topic to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the list of subscriptions on success.
   */
  ApiFuture<List<SubscriptionPath>> listTopicSubscriptions(TopicPath path);

  /**
   * Create the provided subscription if it does not yet exist.
   *
   * <p>By default, a new subscription will only receive messages published after the subscription
   * was created.
   *
   * @param subscription The subscription to create.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the subscription on success.
   */
  default ApiFuture<Subscription> createSubscription(Subscription subscription) {
    return createSubscription(subscription, BacklogLocation.END);
  }

  /**
   * Create the provided subscription at the given starting offset if it does not yet exist.
   *
   * @param subscription The subscription to create.
   * @param startingOffset The offset at which the new subscription will start receiving messages.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the subscription on success.
   */
  ApiFuture<Subscription> createSubscription(
      Subscription subscription, BacklogLocation startingOffset);

  /**
   * Get the subscription with id {@code id} if it exists.
   *
   * @param path The path of the subscription to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the subscription on success.
   */
  ApiFuture<Subscription> getSubscription(SubscriptionPath path);

  /**
   * List all subscriptions for the specified project.
   *
   * @param path The path of the project to list subscriptions for.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the list of subscription paths on success.
   */
  ApiFuture<List<Subscription>> listSubscriptions(LocationPath path);

  /**
   * Update the subscription with path {@code subscription.getPath()} if it exists.
   *
   * @param subscription The subscription to update.
   * @param mask The mask indicating which fields should be updated.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the resulting subscription on success. Updating nonexistent subscriptions will cause the
   *     future to have an exception with status {@link
   *     com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Subscription> updateSubscription(Subscription subscription, FieldMask mask);

  /**
   * Initiate an out-of-band seek for a subscription to a specified target, which may be timestamps
   * or named positions within the message backlog.
   *
   * <p>See https://cloud.google.com/pubsub/lite/docs/seek for more information.
   *
   * @param path The path of the subscription to seek.
   * @param target The location to seek to.
   * @return A {@link com.google.api.gax.longrunning.OperationFuture} that returns an operation name
   *     if the seek was successfully initiated, or otherwise throw an {@link
   *     com.google.api.gax.rpc.ApiException}. {@link
   *     com.google.api.gax.longrunning.OperationFuture.get()} will return a response if the seek
   *     operation completes successfully, or otherwise throw an {@link
   *     com.google.api.gax.rpc.ApiException}.
   */
  OperationFuture<SeekSubscriptionResponse, OperationMetadata> seekSubscription(
      SubscriptionPath path, SeekTarget target);

  /**
   * Delete the subscription with id {@code id} if it exists.
   *
   * @param path The path of the subscription to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     void on success. Deleting nonexistent subscriptions will cause the future to have an
   *     exception with status {@link com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Void> deleteSubscription(SubscriptionPath path);

  /**
   * Create the provided reservation if it does not yet exist.
   *
   * @param reservation The reservation to create.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the reservation on success.
   */
  ApiFuture<Reservation> createReservation(Reservation reservation);

  /**
   * Get the reservation with id {@code id} if it exists.
   *
   * @param path The path of the reservation to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the reservation on success.
   */
  ApiFuture<Reservation> getReservation(ReservationPath path);

  /**
   * List all reservations for the specified project.
   *
   * @param path The path of the project to list reservations for.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the list of reservation paths on success.
   */
  ApiFuture<List<Reservation>> listReservations(LocationPath path);

  /**
   * Update the reservation with path {@code reservation.getPath()} if it exists.
   *
   * @param reservation The reservation to update.
   * @param mask The mask indicating which fields should be updated.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the resulting reservation on success. Updating nonexistent reservations will cause the
   *     future to have an exception with status {@link
   *     com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Reservation> updateReservation(Reservation reservation, FieldMask mask);

  /**
   * Delete the reservation with id {@code id} if it exists.
   *
   * @param path The path of the reservation to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     void on success. Deleting nonexistent reservations will cause the future to have an
   *     exception with status {@link com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Void> deleteReservation(ReservationPath path);

  /**
   * Get the list of topics for the reservation with id {@code id} if it exists.
   *
   * @param path The path of the reservation to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the list of topics on success.
   */
  ApiFuture<List<TopicPath>> listReservationTopics(ReservationPath path);
}
