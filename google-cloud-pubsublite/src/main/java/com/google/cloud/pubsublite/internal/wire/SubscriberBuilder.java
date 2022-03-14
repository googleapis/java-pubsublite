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

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.SubscribeStreamFactory;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import java.util.List;
import java.util.function.Consumer;

@AutoValue
public abstract class SubscriberBuilder {
  // Required parameters.
  abstract Consumer<List<SequencedMessage>> messageConsumer();

  abstract SubscriptionPath subscriptionPath();

  abstract Partition partition();

  abstract SubscribeStreamFactory streamFactory();

  abstract SeekRequest initialLocation();

  // Optional parameters.
  abstract SubscriberResetHandler resetHandler();

  // Whether to retry races when streams are created by other clients.
  abstract boolean retryStreamRaces();

  public static Builder newBuilder() {
    return new AutoValue_SubscriberBuilder.Builder()
        .setResetHandler(SubscriberResetHandler::unhandled)
        .setRetryStreamRaces(true);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setMessageConsumer(Consumer<List<SequencedMessage>> messageConsumer);

    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartition(Partition partition);

    public abstract Builder setStreamFactory(SubscribeStreamFactory streamFactory);

    public abstract Builder setInitialLocation(SeekRequest initialLocation);

    // Optional parameters.
    public abstract Builder setResetHandler(SubscriberResetHandler resetHandler);

    // Whether to re
    public abstract Builder setRetryStreamRaces(boolean retryStreamRaces);

    abstract SubscriberBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public Subscriber build() throws ApiException {
      SubscriberBuilder autoBuilt = autoBuild();

      InitialSubscribeRequest initialSubscribeRequest =
          InitialSubscribeRequest.newBuilder()
              .setSubscription(autoBuilt.subscriptionPath().toString())
              .setPartition(autoBuilt.partition().value())
              .build();
      return new SubscriberImpl(
          autoBuilt.streamFactory(),
          initialSubscribeRequest,
          autoBuilt.initialLocation(),
          autoBuilt.messageConsumer(),
          autoBuilt.resetHandler(),
          autoBuilt.retryStreamRaces());
    }
  }
}
