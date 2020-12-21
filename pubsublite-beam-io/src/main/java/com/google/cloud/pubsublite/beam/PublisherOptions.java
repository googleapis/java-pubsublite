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

package com.google.cloud.pubsublite.beam;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingPublisherBuilder;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import java.io.Serializable;
import javax.annotation.Nullable;

@AutoValue
public abstract class PublisherOptions implements Serializable {
  private static final long serialVersionUID = 275311613L;

  private static final Framework FRAMEWORK = Framework.of("BEAM");

  // Required parameters.
  public abstract TopicPath topicPath();

  // Optional parameters.
  /** A supplier for the stub to be used. If enabled, does not use the publisher cache. */
  @Nullable
  public abstract SerializableSupplier<PublisherServiceClient> clientSupplier();

  @Override
  public abstract int hashCode();

  public static Builder newBuilder() {
    return new AutoValue_PublisherOptions.Builder();
  }

  public boolean usesCache() {
    return clientSupplier() == null;
  }

  @SuppressWarnings("CheckReturnValue")
  Publisher<PublishMetadata> getPublisher() {
    SinglePartitionPublisherBuilder.Builder singlePartitionPublisherBuilder =
        SinglePartitionPublisherBuilder.newBuilder().setContext(PubsubContext.of(FRAMEWORK));
    if (clientSupplier() != null) {
      singlePartitionPublisherBuilder.setServiceClient(clientSupplier().get());
    }
    return RoutingPublisherBuilder.newBuilder()
        .setTopic(topicPath())
        .setPublisherFactory(
            partition ->
                singlePartitionPublisherBuilder
                    .setTopic(topicPath())
                    .setPartition(partition)
                    .build())
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopicPath(TopicPath path);

    // Optional parameters.
    public abstract Builder setClientSupplier(
        SerializableSupplier<PublisherServiceClient> supplier);

    public abstract PublisherOptions build();
  }
}
