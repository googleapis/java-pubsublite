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
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Endpoints;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.Stubs;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import com.google.common.base.Preconditions;
import io.grpc.Metadata;
import io.grpc.StatusException;
import io.grpc.stub.MetadataUtils;
import java.io.IOException;
import java.util.Optional;
import org.threeten.bp.Duration;

/**
 * A builder for a PubSub Lite Publisher. Basic usage:
 *
 * <pre>{@code
 * Publisher<Offset> publisher = PublisherBuilder.builder()
 *   .setTopic(
 *     TopicPaths.newBuilder()
 *       .setProjectNumber(MY_PROJECT)
 *       .setTopicName(MY_TOPIC)
 *       .setZone(CloudZone.of("us-east1-a"))
 *       .build())
 *  .setPartition(Partition.of(10))
 *  .build();
 * }</pre>
 *
 * <p>Custom batching settings and a custom GRPC stub can also be set.
 */
@AutoValue
public abstract class PublisherBuilder {
  public static final BatchingSettings DEFAULT_BATCHING_SETTINGS =
      BatchingSettings.newBuilder()
          .setDelayThreshold(Duration.ofMillis(50))
          .setElementCountThreshold(1000L)
          .setRequestByteThreshold(Constants.MAX_PUBLISH_BATCH_BYTES)
          .setIsEnabled(true)
          .build();
  public static final BatchingSettings DISABLED_BATCHING_SETTINGS =
      BatchingSettings.newBuilder()
          .setElementCountThreshold(1L)
          .setRequestByteThreshold(1L)
          .setIsEnabled(true)
          .build();

  // Required parameters.
  abstract TopicPath topic();

  abstract Partition partition();

  // Optional parameters.
  abstract BatchingSettings batching();

  abstract Optional<PublisherServiceGrpc.PublisherServiceStub> stub();

  abstract PubsubContext context();

  public static Builder builder() {
    Builder impl = new AutoValue_PublisherBuilder.Builder();
    return impl.setBatching(DEFAULT_BATCHING_SETTINGS).setContext(PubsubContext.of());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopic(TopicPath path);

    public abstract Builder setPartition(Partition partition);

    // Optional parameters.
    public abstract Builder setBatching(BatchingSettings batching);

    public abstract Builder setStub(PublisherServiceGrpc.PublisherServiceStub stub);

    public abstract Builder setContext(PubsubContext context);

    abstract PublisherBuilder autoBuild();

    public Publisher<Offset> build() throws StatusException {
      PublisherBuilder autoBuilt = autoBuild();
      PublisherServiceGrpc.PublisherServiceStub actualStub;
      try {
        actualStub =
            autoBuilt.stub().isPresent()
                ? autoBuilt.stub().get()
                : Stubs.defaultStub(
                    Endpoints.regionalEndpoint(TopicPaths.getZone(autoBuilt.topic()).region()),
                    PublisherServiceGrpc::newStub);
      } catch (IOException e) {
        throw ExtractStatus.toCanonical(e);
      }
      Metadata metadata = autoBuilt.context().getMetadata();
      metadata.merge(RoutingMetadata.of(autoBuilt.topic(), autoBuilt.partition()));
      actualStub = MetadataUtils.attachHeaders(actualStub, metadata);
      return new PublisherImpl(
          actualStub,
          InitialPublishRequest.newBuilder()
              .setTopic(autoBuilt.topic().value())
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
