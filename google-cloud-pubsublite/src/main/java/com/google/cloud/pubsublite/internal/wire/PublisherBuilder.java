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
import com.google.api.gax.batching.FlowController.LimitExceededBehavior;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.common.base.Preconditions;

/**
 * A builder for a PubSub Lite Publisher. Basic usage:
 *
 * <pre>{@code
 * Publisher<Offset> publisher = PublisherBuilder.builder()
 *   .setTopic(
 *     TopicPath.newBuilder()
 *       .setProject(MY_PROJECT)
 *       .setName(MY_TOPIC)
 *       .setLocation(CloudZone.of("us-east1-a"))
 *       .build())
 *  .setPartition(Partition.of(10))
 *  .build();
 * }</pre>
 *
 * <p>A custom service client can also be set.
 */
@AutoValue
public abstract class PublisherBuilder {
  public static final BatchingSettings DISABLED_BATCHING_SETTINGS =
      BatchingSettings.newBuilder()
          .setElementCountThreshold(1L)
          .setRequestByteThreshold(1L)
          .setIsEnabled(true)
          .build();

  // Required parameters.
  abstract TopicPath topic();

  abstract Partition partition();

  abstract BatchingSettings batching();

  abstract PublishStreamFactory streamFactory();

  // Optional parameters.
  public static Builder builder() {
    return new AutoValue_PublisherBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath path);

    public abstract Builder setPartition(Partition partition);

    public abstract Builder setBatching(BatchingSettings batching);

    public abstract Builder setStreamFactory(PublishStreamFactory streamFactory);

    abstract PublisherBuilder autoBuild();

    public Publisher<Offset> build() throws ApiException {
      PublisherBuilder autoBuilt = autoBuild();
      return new PublisherImpl(
          autoBuilt.streamFactory(),
          InitialPublishRequest.newBuilder()
              .setTopic(autoBuilt.topic().toString())
              .setPartition(autoBuilt.partition().value())
              .build(),
          validateBatchingSettings(autoBuilt.batching()));
    }

    static BatchingSettings validateBatchingSettings(BatchingSettings batching) {
      Preconditions.checkArgument(
          batching
              .getFlowControlSettings()
              .getLimitExceededBehavior()
              .equals(LimitExceededBehavior.Ignore),
          "Publisher does not accept flow control settings as the behavior would be non-obvious"
              + " given a Publisher implementation. Use external flow control instead.");
      if (!batching.getIsEnabled()) {
        return DISABLED_BATCHING_SETTINGS;
      }
      Preconditions.checkNotNull(batching.getElementCountThreshold());
      Preconditions.checkNotNull(batching.getRequestByteThreshold());
      Preconditions.checkArgument(
          batching.getElementCountThreshold() <= Constants.MAX_PUBLISH_BATCH_COUNT,
          "Batching element count greater than max value.");
      Preconditions.checkArgument(
          batching.getRequestByteThreshold() <= Constants.MAX_PUBLISH_BATCH_BYTES,
          "Batching byte count greater than max value.");
      return batching;
    }
  }
}
