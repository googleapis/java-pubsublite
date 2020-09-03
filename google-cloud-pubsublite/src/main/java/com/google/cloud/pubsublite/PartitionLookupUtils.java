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

import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.proto.Subscription;
import io.grpc.StatusException;
import java.util.concurrent.ExecutionException;

/** Utilities for looking up information on partitions. */
public final class PartitionLookupUtils {
  private PartitionLookupUtils() {}

  /** Look up the number of partitions in a topic. */
  public static int numPartitions(TopicPath topic) throws StatusException {
    try (AdminClient client =
        AdminClient.create(
            AdminClientSettings.newBuilder().setRegion(topic.location().region()).build())) {;
      return numPartitions(topic, client);
    } catch (Exception e) {
      throw ExtractStatus.toCanonical(e);
    }
  }

  /** Look up the number of partitions in a topic using the provided AdminClient. */
  public static int numPartitions(TopicPath topic, AdminClient client) throws StatusException {
    ApiFuture<Long> partitionCountFuture = client.getTopicPartitionCount(topic);
    try {
      long numPartitions = partitionCountFuture.get();
      checkState(
          numPartitions > 0, "Config has 0 or less partitions configured. This config is invalid.");
      checkState(
          numPartitions < Integer.MAX_VALUE,
          "Config has more than Integer.MAX_VALUE partitions configured. This"
              + " config cannot be used with this client library.");
      return (int) numPartitions;
    } catch (ExecutionException e) {
      throw ExtractStatus.toCanonical(e.getCause());
    } catch (InterruptedException t) {
      throw ExtractStatus.toCanonical(t);
    } catch (Throwable t) {
      throw ExtractStatus.toCanonical(t);
    }
  }

  /** Look up the number of partitions in the topic associated with a subscription. */
  public static int numPartitions(SubscriptionPath subscription) throws StatusException {
    try (AdminClient client =
        AdminClient.create(
            AdminClientSettings.newBuilder().setRegion(subscription.location().region()).build())) {
      return numPartitions(subscription, client);
    } catch (Exception e) {
      throw ExtractStatus.toCanonical(e);
    }
  }

  /**
   * Look up the number of partitions in the topic associated with a subscription using the provided
   * AdminClient.
   */
  public static int numPartitions(SubscriptionPath subscription, AdminClient client)
      throws StatusException {
    ApiFuture<Subscription> subscriptionFuture = client.getSubscription(subscription);
    try {
      return numPartitions(TopicPath.parse(subscriptionFuture.get().getTopic()), client);
    } catch (ExecutionException e) {
      throw ExtractStatus.toCanonical(e.getCause());
    } catch (Throwable t) {
      throw ExtractStatus.toCanonical(t);
    }
  }
}
