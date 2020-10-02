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
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ImmutableTypeToInstanceMap;
import io.grpc.StatusException;

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
          .put(SubscriptionName.class, exampleSubscriptionName())
          .put(SubscriptionPath.class, exampleSubscriptionPath())
          .put(Offset.class, exampleOffset())
          .build();

  public static <T> T example(Class<T> klass) {
    T instance = (T) MAP.getInstance(klass);
    Preconditions.checkNotNull(instance);
    return instance;
  }

  public static Partition examplePartition() {
    try {
      return Partition.of(33);
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  public static CloudRegion exampleRegion() {
    return CloudRegion.of("europe-fake2");
  }

  public static CloudZone exampleZone() {
    return CloudZone.of(exampleRegion(), 'z');
  }

  public static ProjectNumber exampleProjectNumber() {
    try {
      return ProjectNumber.of(3298749273L);
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  public static TopicName exampleTopicName() {
    try {
      return TopicName.of("example-topic");
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  public static TopicPath exampleTopicPath() {
    return TopicPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleZone())
        .setName(exampleTopicName())
        .build();
  }

  public static SubscriptionName exampleSubscriptionName() {
    try {
      return SubscriptionName.of("example-subscription");
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  public static SubscriptionPath exampleSubscriptionPath() {
    return SubscriptionPath.newBuilder()
        .setProject(exampleProjectNumber())
        .setLocation(exampleZone())
        .setName(exampleSubscriptionName())
        .build();
  }

  public static Offset exampleOffset() {
    return Offset.of(9827340L);
  }
}
