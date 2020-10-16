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

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.BufferingPullSubscriber;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.CursorClientSettings;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.wire.AssignerBuilder;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.Subscription;
import org.apache.kafka.clients.consumer.Consumer;

@AutoValue
public abstract class ConsumerSettings {
  private static final Framework FRAMEWORK = Framework.of("KAFKA_SHIM");

  // Required parameters.
  abstract SubscriptionPath subscriptionPath();

  abstract FlowControlSettings perPartitionFlowControlSettings();

  // Optional parameters.
  abstract boolean autocommit();

  public static Builder newBuilder() {
    return (new AutoValue_ConsumerSettings.Builder()).setAutocommit(false);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPerPartitionFlowControlSettings(FlowControlSettings settings);

    // Optional parameters.
    public abstract Builder setAutocommit(boolean autocommit);

    public abstract ConsumerSettings build();
  }

  public Consumer<byte[], byte[]> instantiate() throws ApiException {
    CloudZone zone = subscriptionPath().location();
    try (AdminClient adminClient =
        AdminClient.create(AdminClientSettings.newBuilder().setRegion(zone.region()).build())) {
      Subscription subscription = adminClient.getSubscription(subscriptionPath()).get();
      TopicPath topic = TopicPath.parse(subscription.getTopic());
      long partitionCount = PartitionLookupUtils.numPartitions(topic);
      AssignerFactory assignerFactory =
          receiver -> {
            AssignerBuilder.Builder builder = AssignerBuilder.newBuilder();
            builder.setReceiver(receiver);
            builder.setSubscriptionPath(subscriptionPath());
            return builder.build();
          };
      PullSubscriberFactory pullSubscriberFactory =
          (partition, initialSeek) -> {
            SubscriberBuilder.Builder builder =
                SubscriberBuilder.newBuilder()
                    .setContext(PubsubContext.of(FRAMEWORK))
                    .setPartition(partition)
                    .setSubscriptionPath(subscriptionPath());
            return new BufferingPullSubscriber(
                consumer -> {
                  synchronized (builder) {
                    return builder.setMessageConsumer(consumer).build();
                  }
                },
                perPartitionFlowControlSettings(),
                initialSeek);
          };
      CommitterFactory committerFactory =
          partition ->
              CommitterBuilder.newBuilder()
                  .setSubscriptionPath(subscriptionPath())
                  .setPartition(partition)
                  .build();
      ConsumerFactory consumerFactory =
          () ->
              new SingleSubscriptionConsumerImpl(
                  topic, autocommit(), pullSubscriberFactory, committerFactory);

      CursorClient cursorClient =
          CursorClient.create(CursorClientSettings.newBuilder().setRegion(zone.region()).build());

      return new PubsubLiteConsumer(
          subscriptionPath(),
          topic,
          partitionCount,
          consumerFactory,
          assignerFactory,
          cursorClient);
    } catch (Exception e) {
      throw ExtractStatus.toCanonical(e).underlying;
    }
  }
}
