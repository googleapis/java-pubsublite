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
package com.google.cloud.pubsublite.internal.wire;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.DefaultRoutingPolicy;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import java.time.Duration;
import java.util.Optional;

@AutoValue
public abstract class PartitionCountWatchingPublisherSettings {
  // Required parameters.
  abstract TopicPath topic();

  abstract PartitionPublisherFactory publisherFactory();

  // Optional parameters
  abstract PartitionCountWatcher.Factory configWatcherFactory();

  abstract RoutingPolicy.Factory routingPolicyFactory();

  abstract Duration configPollPeriod();

  public static Builder newBuilder() {
    return new AutoValue_PartitionCountWatchingPublisherSettings.Builder()
        .setConfigPollPeriod(Duration.ofMinutes(10));
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath path);

    public abstract Builder setPublisherFactory(PartitionPublisherFactory factory);

    // Optional parameters.
    public abstract Builder setConfigWatcherFactory(PartitionCountWatcher.Factory factory);

    public abstract Builder setRoutingPolicyFactory(RoutingPolicy.Factory factory);

    public abstract Builder setConfigPollPeriod(Duration period);

    abstract Optional<PartitionCountWatcher.Factory> configWatcherFactory();

    abstract Optional<RoutingPolicy.Factory> routingPolicyFactory();

    abstract Duration configPollPeriod();

    abstract TopicPath topic();

    abstract PartitionCountWatchingPublisherSettings autoBuild();

    public PartitionCountWatchingPublisherSettings build() {
      if (!configWatcherFactory().isPresent()) {
        setConfigWatcherFactory(
            new PartitionCountWatcherImpl.Factory(
                topic(),
                AdminClient.create(
                    AdminClientSettings.newBuilder()
                        .setRegion(topic().location().region())
                        .build()),
                configPollPeriod()));
      }
      if (!routingPolicyFactory().isPresent()) {
        setRoutingPolicyFactory(DefaultRoutingPolicy::new);
      }
      return autoBuild();
    }
  }
}
