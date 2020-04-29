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

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Endpoints;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.Stubs;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc.SubscriberServiceStub;
import com.google.common.collect.ImmutableList;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.MetadataUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

@AutoValue
public abstract class SubscriberBuilder {
  // Required parameters.
  abstract Consumer<ImmutableList<SequencedMessage>> messageConsumer();

  abstract SubscriptionPath subscriptionPath();

  abstract Partition partition();

  // Optional parameters.
  abstract Optional<SubscriberServiceStub> subscriberServiceStub();

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
    public abstract Builder setSubscriberServiceStub(
        SubscriberServiceGrpc.SubscriberServiceStub stub);

    public abstract Builder setContext(PubsubContext context);

    abstract SubscriberBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public Subscriber build() throws StatusException {
      SubscriberBuilder builder = autoBuild();
      SubscriptionPaths.check(builder.subscriptionPath());

      SubscriberServiceGrpc.SubscriberServiceStub subscriberServiceStub;
      if (builder.subscriberServiceStub().isPresent()) {
        subscriberServiceStub = builder.subscriberServiceStub().get();
      } else {
        try {
          subscriberServiceStub =
              Stubs.defaultStub(
                  Endpoints.regionalEndpoint(
                      SubscriptionPaths.getZone(builder.subscriptionPath()).region()),
                  SubscriberServiceGrpc::newStub);
        } catch (IOException e) {
          throw Status.INTERNAL
              .withCause(e)
              .withDescription("Creating subscriber stub failed.")
              .asException();
        }
      }
      Metadata metadata = builder.context().getMetadata();
      metadata.merge(RoutingMetadata.of(builder.subscriptionPath(), builder.partition()));
      subscriberServiceStub = MetadataUtils.attachHeaders(subscriberServiceStub, metadata);

      InitialSubscribeRequest initialSubscribeRequest =
          InitialSubscribeRequest.newBuilder()
              .setSubscription(builder.subscriptionPath().value())
              .setPartition(builder.partition().value())
              .build();
      return new SubscriberImpl(
          subscriberServiceStub, initialSubscribeRequest, builder.messageConsumer());
    }
  }
}
