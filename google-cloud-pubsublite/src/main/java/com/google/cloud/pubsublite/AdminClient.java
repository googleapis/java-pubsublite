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
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.internal.ApiBackgroundResource;
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
   * @param subscription The subscription to create.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     the subscription on success.
   */
  ApiFuture<Subscription> createSubscription(Subscription subscription);

  /**
   * The offset at which a newly created subscription will start receiving messages.
   *
   * <p>BEGINNING refers to the offset of the oldest retained message. END refers to the current
   * HEAD offset.
   */
  public enum StartingOffset {
    BEGINNING,
    END
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
      Subscription subscription, StartingOffset startingOffset);

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
   * Delete the subscription with id {@code id} if it exists.
   *
   * @param path The path of the subscription to retrieve.
   * @return A future that will have either an error {@link com.google.api.gax.rpc.ApiException} or
   *     void on success. Deleting nonexistent subscriptions will cause the future to have an
   *     exception with status {@link com.google.api.gax.rpc.StatusCode.Code#NOT_FOUND}
   */
  ApiFuture<Void> deleteSubscription(SubscriptionPath path);
}
