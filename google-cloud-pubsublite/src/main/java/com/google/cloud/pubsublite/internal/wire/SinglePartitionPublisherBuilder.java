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

import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.common.annotations.VisibleForTesting;

@AutoValue
public abstract class SinglePartitionPublisherBuilder {
  // Required parameters.
  abstract TopicPath topic();

  abstract Partition partition();

  abstract PublishStreamFactory streamFactory();

  abstract BatchingSettings batchingSettings();

  // For testing.
  abstract PublisherBuilder.Builder underlyingBuilder();

  public static Builder newBuilder() {
    return new AutoValue_SinglePartitionPublisherBuilder.Builder()
        .setUnderlyingBuilder(PublisherBuilder.builder());
  }

  @AutoValue.Builder
  @SuppressWarnings("CheckReturnValue")
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath topic);

    public abstract Builder setPartition(Partition partition);

    public abstract Builder setStreamFactory(PublishStreamFactory streamFactory);

    public abstract Builder setBatchingSettings(BatchingSettings batchingSettings);

    // For testing.
    @VisibleForTesting
    abstract Builder setUnderlyingBuilder(PublisherBuilder.Builder underlyingBuilder);

    abstract SinglePartitionPublisherBuilder autoBuild();

    public Publisher<MessageMetadata> build() throws ApiException {
      SinglePartitionPublisherBuilder builder = autoBuild();
      PublisherBuilder.Builder publisherBuilder =
          builder
              .underlyingBuilder()
              .setTopic(builder.topic())
              .setPartition(builder.partition())
              .setStreamFactory(builder.streamFactory())
              .setBatching(builder.batchingSettings());
      return new SinglePartitionPublisher(publisherBuilder.build(), builder.partition());
    }
  }
}
