/*
 * Copyright 2026 Google LLC
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

import com.google.cloud.pubsublite.SubscriptionPath;

/** Utility for deriving Kafka consumer group IDs from subscription paths. */
public final class GroupIdUtils {

  private GroupIdUtils() {}

  /**
   * Derives a Kafka consumer group ID from a subscription path.
   *
   * <p>The group ID is derived by replacing slashes with dashes to create a valid Kafka group ID.
   * For example, {@code projects/my-project/locations/us-central1/subscriptions/my-sub} becomes
   * {@code projects-my-project-locations-us-central1-subscriptions-my-sub}.
   *
   * @param subscriptionPath The subscription path to derive the group ID from.
   * @return A valid Kafka consumer group ID.
   */
  public static String deriveGroupId(SubscriptionPath subscriptionPath) {
    return subscriptionPath.toString().replace('/', '-');
  }
}
