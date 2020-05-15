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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.cloudpubsub.internal.AckSetTrackerImpl;
import com.google.cloud.pubsublite.cloudpubsub.internal.MultiPartitionSubscriber;
import com.google.cloud.pubsublite.cloudpubsub.internal.SinglePartitionSubscriber;
import com.google.cloud.pubsublite.internal.Preconditions;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.common.collect.ImmutableList;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Settings for a Subscriber object.
@AutoValue
public abstract class SubscriberSettings {

  private static final Framework FRAMEWORK = Framework.of("CLOUD_PUBSUB_SHIM");

  // Required parameters.
  abstract MessageReceiver receiver();

  abstract SubscriptionPath subscriptionPath();

  abstract ImmutableList<Partition> partitions();

  abstract FlowControlSettings perPartitionFlowControlSettings();

  // Optional parameters.
  abstract Optional<MessageTransformer<SequencedMessage, PubsubMessage>> transformer();

  abstract Optional<SubscriberServiceGrpc.SubscriberServiceStub> subscriberServiceStub();

  abstract Optional<CursorServiceGrpc.CursorServiceStub> cursorServiceStub();

  abstract Optional<NackHandler> nackHandler();

  public static Builder newBuilder() {
    return new AutoValue_SubscriberSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setReceiver(MessageReceiver receiver);

    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartitions(List<Partition> partition);

    public abstract Builder setPerPartitionFlowControlSettings(FlowControlSettings settings);

    // Optional parameters.
    public abstract Builder setTransformer(
        MessageTransformer<SequencedMessage, PubsubMessage> transformer);

    public abstract Builder setSubscriberServiceStub(
        SubscriberServiceGrpc.SubscriberServiceStub stub);

    public abstract Builder setCursorServiceStub(CursorServiceGrpc.CursorServiceStub stub);

    public abstract Builder setNackHandler(NackHandler nackHandler);

    abstract SubscriberSettings autoBuild();

    public SubscriberSettings build() throws StatusException {
      SubscriberSettings settings = autoBuild();
      Preconditions.checkArgument(
          !settings.partitions().isEmpty(), "Must provide at least one partition.");
      SubscriptionPaths.check(settings.subscriptionPath());
      return settings;
    }
  }

  @SuppressWarnings("CheckReturnValue")
  Subscriber instantiate() throws StatusException {
    SubscriberBuilder.Builder wireSubscriberBuilder = SubscriberBuilder.newBuilder();
    wireSubscriberBuilder.setSubscriptionPath(subscriptionPath());
    subscriberServiceStub().ifPresent(wireSubscriberBuilder::setSubscriberServiceStub);
    wireSubscriberBuilder.setContext(PubsubContext.of(FRAMEWORK));

    CommitterBuilder.Builder wireCommitterBuilder = CommitterBuilder.newBuilder();
    wireCommitterBuilder.setSubscriptionPath(subscriptionPath());
    cursorServiceStub().ifPresent(wireCommitterBuilder::setCursorStub);

    List<Subscriber> perPartitionSubscribers = new ArrayList<>();
    for (Partition partition : partitions()) {
      wireSubscriberBuilder.setPartition(partition);
      wireCommitterBuilder.setPartition(partition);
      perPartitionSubscribers.add(
          new SinglePartitionSubscriber(
              receiver(),
              transformer().orElse(MessageTransforms.toCpsSubscribeTransformer()),
              new AckSetTrackerImpl(wireCommitterBuilder.build()),
              nackHandler().orElse(new NackHandler() {}),
              messageConsumer -> wireSubscriberBuilder.setMessageConsumer(messageConsumer).build(),
              perPartitionFlowControlSettings()));
    }
    return MultiPartitionSubscriber.of(perPartitionSubscribers);
  }
}
