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

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.DefaultRoutingPolicy;
import com.google.cloud.pubsublite.internal.Publisher;
import java.time.Duration;

@AutoValue
public abstract class PartitionCountWatchingPublisherSettings {
  // Required parameters.
  abstract TopicPath topic();

  abstract PartitionPublisherFactory publisherFactory();

  abstract AdminClient adminClient();

  // Optional parameters
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

    public abstract Builder setAdminClient(AdminClient client);

    // Optional parameters.
    public abstract Builder setConfigPollPeriod(Duration period);

    public abstract PartitionCountWatchingPublisherSettings build();
  }

  public Publisher<PublishMetadata> instantiate() throws ApiException {
    return new PartitionCountWatchingPublisher(
        publisherFactory(),
        DefaultRoutingPolicy::new,
        new PartitionCountWatcherImpl.Factory(topic(), adminClient(), configPollPeriod()));
  }
}
