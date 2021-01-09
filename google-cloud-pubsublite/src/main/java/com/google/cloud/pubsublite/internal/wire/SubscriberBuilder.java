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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultMetadata;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceSettings;
import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.function.Consumer;

@AutoValue
public abstract class SubscriberBuilder {
  // Required parameters.
  abstract Consumer<ImmutableList<SequencedMessage>> messageConsumer();

  abstract SubscriptionPath subscriptionPath();

  abstract Partition partition();

  // Optional parameters.
  abstract Optional<SubscriberServiceClient> serviceClient();

  abstract PubsubContext context();

  public static Builder newBuilder() {
    return new AutoValue_SubscriberBuilder.Builder().setContext(PubsubContext.of());
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setMessageConsumer(
        Consumer<ImmutableList<SequencedMessage>> messageConsumer);

    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartition(Partition partition);

    // Optional parameters.
    public abstract Builder setServiceClient(SubscriberServiceClient serviceClient);

    public abstract Builder setContext(PubsubContext context);

    abstract SubscriberBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public Subscriber build() throws ApiException {
      SubscriberBuilder autoBuilt = autoBuild();

      SubscriberServiceClient serviceClient;
      if (autoBuilt.serviceClient().isPresent()) {
        serviceClient = autoBuilt.serviceClient().get();
      } else {
        try {
          SubscriberServiceSettings.Builder settingsBuilder =
              SubscriberServiceSettings.newBuilder();
          addDefaultMetadata(
              autoBuilt.context(),
              RoutingMetadata.of(autoBuilt.subscriptionPath(), autoBuilt.partition()),
              settingsBuilder);
          serviceClient =
              SubscriberServiceClient.create(
                  addDefaultSettings(
                      autoBuilt.subscriptionPath().location().region(), settingsBuilder));
        } catch (Throwable t) {
          throw toCanonical(t).underlying;
        }
      }

      InitialSubscribeRequest initialSubscribeRequest =
          InitialSubscribeRequest.newBuilder()
              .setSubscription(autoBuilt.subscriptionPath().toString())
              .setPartition(autoBuilt.partition().value())
              .build();
      return new ApiExceptionSubscriber(
          new SubscriberImpl(serviceClient, initialSubscribeRequest, autoBuilt.messageConsumer()));
    }
  }
}
