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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.api.gax.batching.BatchingSettings;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.WrappingPublisher;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingPublisherBuilder;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc.PublisherServiceStub;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;
import java.util.Optional;
import org.threeten.bp.Duration;

@AutoValue
public abstract class PublisherBuilder {
  public static final BatchingSettings DEFAULT_BATCHING_SETTINGS =
      BatchingSettings.newBuilder()
          .setIsEnabled(true)
          .setElementCountThreshold(1000L)
          .setRequestByteThreshold(Constants.MAX_PUBLISH_BATCH_BYTES)
          .setDelayThreshold(Duration.ofMillis(50))
          .build();
  private static final Framework FRAMEWORK = Framework.of("CLOUD_PUBSUB_SHIM");

  // Required parameters.
  abstract TopicPath topicPath();

  // Optional parameters.
  abstract Optional<KeyExtractor> keyExtractor();

  abstract Optional<MessageTransformer<PubsubMessage, Message>> messageTransformer();

  abstract Optional<BatchingSettings> batchingSettings();

  abstract Optional<PublisherServiceStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_PublisherBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopicPath(TopicPath path);

    // Optional parameters.
    public abstract Builder setKeyExtractor(KeyExtractor keyExtractor);

    public abstract Builder setMessageTransformer(
        MessageTransformer<PubsubMessage, Message> messageTransformer);

    public abstract Builder setBatchingSettings(BatchingSettings batchingSettings);

    public abstract Builder setStub(PublisherServiceStub stub);

    abstract PublisherBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public PublisherApiService build() throws StatusException {
      PublisherBuilder builder = autoBuild();
      BatchingSettings batchingSettings =
          builder.batchingSettings().orElse(DEFAULT_BATCHING_SETTINGS);
      KeyExtractor keyExtractor = builder.keyExtractor().orElse(KeyExtractor.DEFAULT);
      MessageTransformer<PubsubMessage, Message> messageTransformer =
          builder
              .messageTransformer()
              .orElseGet(() -> MessageTransforms.fromCpsPublishTransformer(keyExtractor));

      SinglePartitionPublisherBuilder.Builder singlePartitionPublisherBuilder =
          SinglePartitionPublisherBuilder.newBuilder()
              .setBatchingSettings(Optional.of(batchingSettings))
              .setStub(builder.stub())
              .setContext(PubsubContext.of(FRAMEWORK));

      RoutingPublisherBuilder.Builder wireBuilder =
          RoutingPublisherBuilder.newBuilder()
              .setTopic(builder.topicPath())
              .setPublisherBuilder(singlePartitionPublisherBuilder);

      return new WrappingPublisher(wireBuilder.build(), messageTransformer);
    }
  }
}
