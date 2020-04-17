// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal.wire;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.Publisher;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.DefaultRoutingPolicy;
import com.google.common.collect.ImmutableMap;
import io.grpc.StatusException;
import java.util.Optional;

@AutoValue
public abstract class RoutingPublisherBuilder {
  // Required parameters.
  abstract TopicPath topic();

  abstract SinglePartitionPublisherBuilder.Builder publisherBuilder();

  // Optional parameters.
  abstract Optional<Integer> numPartitions();

  public static Builder newBuilder() {
    return new AutoValue_RoutingPublisherBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath path);

    // If a topic is specified in the underlying SinglePartitionPublisherBuilder, the value will be
    // overwritten by the topic provided to the RouthingPublisherBuilder.
    public abstract Builder setPublisherBuilder(SinglePartitionPublisherBuilder.Builder builder);

    // Optional parameters.
    public abstract Builder setNumPartitions(Integer numPartitions);

    abstract RoutingPublisherBuilder autoBuild();

    public Publisher<PublishMetadata> build() throws StatusException {
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
            Partition.create(i),
            builder.publisherBuilder()
                .setTopic(builder.topic())
                .setPartition(Partition.create(i))
                .build());
      }

      return new RoutingPublisher(
          publisherMapBuilder.build(), new DefaultRoutingPolicy(numPartitions));
    }
  }
}
