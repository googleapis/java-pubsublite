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

package com.google.cloud.pubsublite.kafka;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingPublisherBuilder;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import io.grpc.StatusException;
import org.apache.kafka.clients.producer.Producer;

@AutoValue
public abstract class ProducerSettings {
  private static final Framework FRAMEWORK = Framework.of("KAFKA_SHIM");

  // Required parameters.
  abstract TopicPath topicPath();

  public static Builder newBuilder() {
    return new AutoValue_ProducerSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopicPath(TopicPath path);

    public abstract ProducerSettings build();
  }

  public Producer<byte[], byte[]> instantiate() throws StatusException {
    SinglePartitionPublisherBuilder.Builder builder =
        SinglePartitionPublisherBuilder.newBuilder()
            .setContext(PubsubContext.of(FRAMEWORK))
            .setTopic(topicPath());
    RoutingPublisherBuilder.Builder routingBuilder =
        RoutingPublisherBuilder.newBuilder().setTopic(topicPath()).setPublisherBuilder(builder);
    return new PubsubLiteProducer(
        routingBuilder.build(), PartitionLookupUtils.numPartitions(topicPath()), topicPath());
  }
}
