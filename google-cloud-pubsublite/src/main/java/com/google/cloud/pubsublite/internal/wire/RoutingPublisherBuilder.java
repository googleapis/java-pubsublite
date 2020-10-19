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
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.DefaultRoutingPolicy;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;

@AutoValue
public abstract class RoutingPublisherBuilder {
  // Required parameters.
  abstract TopicPath topic();

  abstract PartitionPublisherFactory publisherFactory();

  // Optional parameters.
  abstract Optional<Integer> numPartitions();

  public static Builder newBuilder() {
    return new AutoValue_RoutingPublisherBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath path);

    public abstract Builder setPublisherFactory(PartitionPublisherFactory factory);

    // Optional parameters.
    public abstract Builder setNumPartitions(Integer numPartitions);

    abstract RoutingPublisherBuilder autoBuild();

    public Publisher<PublishMetadata> build() throws ApiException {
      RoutingPublisherBuilder builder = autoBuild();
      int numPartitions;
      if (builder.numPartitions().isPresent()) {
        numPartitions = builder.numPartitions().get();
      } else {
        numPartitions = PartitionLookupUtils.numPartitions(builder.topic());
      }

      ImmutableMap.Builder<Partition, Publisher<PublishMetadata>> publisherMapBuilder =
          ImmutableMap.builder();
      for (int i = 0; i < numPartitions; i++) {
        publisherMapBuilder.put(
            Partition.of(i),
            builder.publisherFactory().newPublisher(Partition.of(i)));
      }

      return new RoutingPublisher(
          publisherMapBuilder.build(), new DefaultRoutingPolicy(numPartitions));
    }
  }
}
