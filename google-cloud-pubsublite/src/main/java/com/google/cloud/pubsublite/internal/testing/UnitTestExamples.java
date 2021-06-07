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

package com.google.cloud.pubsublite.internal.testing;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.PartitionConfig;
import com.google.cloud.pubsublite.proto.Topic.RetentionConfig;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ImmutableTypeToInstanceMap;
import com.google.protobuf.util.Durations;

public final class UnitTestExamples {
  private UnitTestExamples() {}

  private static final ImmutableTypeToInstanceMap<Object> MAP =
      ImmutableTypeToInstanceMap.builder()
          .put(Partition.class, examplePartition())
          .put(CloudRegion.class, exampleRegion())
          .put(CloudZone.class, exampleZone())
          .put(ProjectNumber.class, exampleProjectNumber())
          .put(TopicName.class, exampleTopicName())
          .put(TopicPath.class, exampleTopicPath())
          .put(Topic.class, exampleTopic())
          .put(SubscriptionName.class, exampleSubscriptionName())
          .put(SubscriptionPath.class, exampleSubscriptionPath())
          .put(Subscription.class, exampleSubscription())
          .put(ReservationName.class, exampleReservationName())
          .put(ReservationPath.class, exampleReservationPath())
          .put(Reservation.class, exampleReservation())
          .put(LocationPath.class, exampleLocationPath())
          .put(Offset.class, exampleOffset())
          .build();

  public static <T> T example(Class<T> klass) {
    T instance = (T) MAP.getInstance(klass);
    Preconditions.checkNotNull(instance);
    return instance;
  }

  public static Partition examplePartition() {
    return Partition.of(33);
  }

  public static CloudRegion exampleRegion() {
    return CloudRegion.of("europe-fake2");
  }

  public static CloudZone exampleZone() {
    return CloudZone.of(exampleRegion(), 'z');
  }

  public static ProjectNumber exampleProjectNumber() {
    return ProjectNumber.of(3298749273L);
  }

  public static TopicName exampleTopicName() {
    return TopicName.of("example-topic");
  }

  public static TopicPath exampleTopicPath() {
    return TopicPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleZone())
        .setName(exampleTopicName())
        .build();
  }

  public static Topic exampleTopic() {
    return Topic.newBuilder()
        .setName(exampleTopicPath().toString())
        .setPartitionConfig(PartitionConfig.newBuilder().setCount(10))
        .setRetentionConfig(RetentionConfig.newBuilder().setPeriod(Durations.fromDays(1)))
        .build();
  }

  public static SubscriptionName exampleSubscriptionName() {
    return SubscriptionName.of("example-subscription");
  }

  public static SubscriptionPath exampleSubscriptionPath() {
    return SubscriptionPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleZone())
        .setName(exampleSubscriptionName())
        .build();
  }

  public static Subscription exampleSubscription() {
    return Subscription.newBuilder()
        .setDeliveryConfig(
            DeliveryConfig.newBuilder()
                .setDeliveryRequirement(DeliveryConfig.DeliveryRequirement.DELIVER_AFTER_STORED))
        .setName(exampleSubscriptionPath().toString())
        .setTopic(exampleTopicPath().toString())
        .build();
  }

  public static ReservationName exampleReservationName() {
    return ReservationName.of("example-reservation");
  }

  public static ReservationPath exampleReservationPath() {
    return ReservationPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleRegion())
        .setName(exampleReservationName())
        .build();
  }

  public static Reservation exampleReservation() {
    return Reservation.newBuilder()
        .setName(exampleReservationPath().toString())
        .setThroughputCapacity(423597)
        .build();
  }

  public static LocationPath exampleLocationPath() {
    return LocationPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleZone())
        .build();
  }

  public static Offset exampleOffset() {
    return Offset.of(9827340L);
  }
}
