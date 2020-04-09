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


import com.google.api.gax.batching.BatchingSettings;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import io.grpc.StatusException;
import java.util.Optional;

@AutoValue
public abstract class SinglePartitionPublisherBuilder {
  // Required parameters.
  abstract TopicPath topic();
  abstract Partition partition();

  // Optional parameters.
  abstract Optional<PublisherServiceGrpc.PublisherServiceStub> stub();
  abstract Optional<BatchingSettings> batchingSettings();

  // Rarely set parameters.
  abstract PubsubContext context();

  public static Builder newBuilder() {
    return new AutoValue_SinglePartitionPublisherBuilder.Builder().setContext(PubsubContext.of());
  }

  @AutoValue.Builder
  @SuppressWarnings("CheckReturnValue")
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath topic);
    public abstract Builder setPartition(Partition partition);

    // Optional parameters.
    public abstract Builder setStub(Optional<PublisherServiceGrpc.PublisherServiceStub> stub);
    public abstract Builder setBatchingSettings(Optional<BatchingSettings> batchingSettings);

    // Rarely set parameters.
    public abstract Builder setContext(PubsubContext context);

    abstract SinglePartitionPublisherBuilder autoBuild();

    public SinglePartitionPublisher build() throws StatusException {
      SinglePartitionPublisherBuilder builder = autoBuild();
      PublisherBuilder.Builder publisherBuilder =
          PublisherBuilder.builder().setTopic(builder.topic()).setPartition(builder.partition());
      builder.stub().ifPresent(publisherBuilder::setStub);
      builder.batchingSettings().ifPresent(publisherBuilder::setBatching);
      return new SinglePartitionPublisher(publisherBuilder.build(), builder.partition());
    }
  }
}
