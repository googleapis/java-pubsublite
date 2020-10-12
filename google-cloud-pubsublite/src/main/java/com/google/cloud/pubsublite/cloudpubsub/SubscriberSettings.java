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

import static com.google.cloud.pubsublite.ProjectLookupUtils.toCanonical;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.AckSetTrackerImpl;
import com.google.cloud.pubsublite.cloudpubsub.internal.AssigningSubscriber;
import com.google.cloud.pubsublite.cloudpubsub.internal.MultiPartitionSubscriber;
import com.google.cloud.pubsublite.cloudpubsub.internal.PartitionSubscriberFactory;
import com.google.cloud.pubsublite.cloudpubsub.internal.SinglePartitionSubscriber;
import com.google.cloud.pubsublite.internal.Preconditions;
import com.google.cloud.pubsublite.internal.wire.AssignerBuilder;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceStub;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Settings for instantiating a Pub/Sub Lite subscriber emulating the Cloud Pub/Sub Subscriber API.
 */
@AutoValue
public abstract class SubscriberSettings {

  private static final Framework FRAMEWORK = Framework.of("CLOUD_PUBSUB_SHIM");

  // Required parameters.
  abstract MessageReceiver receiver();

  abstract SubscriptionPath subscriptionPath();

  abstract FlowControlSettings perPartitionFlowControlSettings();

  // Optional parameters.

  // If set, disables auto-assignment.
  abstract Optional<List<Partition>> partitions();

  abstract Optional<MessageTransformer<SequencedMessage, PubsubMessage>> transformer();

  abstract Optional<SubscriberServiceGrpc.SubscriberServiceStub> subscriberServiceStub();

  abstract Optional<CursorServiceGrpc.CursorServiceStub> cursorServiceStub();

  abstract Optional<PartitionAssignmentServiceStub> assignmentServiceStub();

  abstract Optional<NackHandler> nackHandler();

  public static Builder newBuilder() {
    return new AutoValue_SubscriberSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.

    /**
     * The receiver which handles new messages sent by the Pub/Sub Lite system. Only one downcall
     * from any connected partition will be outstanding at a time, and blocking in this receiver
     * callback will block forward progress.
     */
    public abstract Builder setReceiver(MessageReceiver receiver);

    /** The subscription to use to receive messages. */
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    /**
     * The per-partition flow control settings. Because these apply per-partition, if you are using
     * them to bound memory usage, keep in mind the number of partitions in the associated topic.
     */
    public abstract Builder setPerPartitionFlowControlSettings(FlowControlSettings settings);

    // Optional parameters.

    /**
     * The partitions this subscriber should connect to to receive messages. If set, disables
     * auto-assignment.
     */
    public abstract Builder setPartitions(List<Partition> partition);

    /** The MessageTransformer to get PubsubMessages from Pub/Sub Lite wire messages. */
    public abstract Builder setTransformer(
        MessageTransformer<SequencedMessage, PubsubMessage> transformer);

    /** A stub to connect to the Pub/Sub lite subscriber service. */
    public abstract Builder setSubscriberServiceStub(
        SubscriberServiceGrpc.SubscriberServiceStub stub);

    /** A stub to connect to the Pub/Sub lite cursor service. */
    public abstract Builder setCursorServiceStub(CursorServiceGrpc.CursorServiceStub stub);

    /** A stub to connect to the Pub/Sub lite assignment service. */
    public abstract Builder setAssignmentServiceStub(PartitionAssignmentServiceStub stub);

    /**
     * A handler for the action to take when {@link
     * com.google.cloud.pubsub.v1.AckReplyConsumer#nack} is called. In Pub/Sub Lite, only a single
     * subscriber for a given subscription is connected to any partition at a time, and there is no
     * other client that may be able to handle messages.
     */
    public abstract Builder setNackHandler(NackHandler nackHandler);

    abstract SubscriberSettings autoBuild();

    /** Build the SubscriberSettings instance. */
    public SubscriberSettings build() throws StatusException {
      SubscriberSettings settings = autoBuild();
      Preconditions.checkArgument(
          !settings.partitions().isPresent() || !settings.partitions().get().isEmpty(),
          "Must provide at least one partition if setting partitions explicitly.");
      return settings;
    }
  }

  @SuppressWarnings("CheckReturnValue")
  Subscriber instantiate() throws StatusException {
    SubscriptionPath canonicalPath = toCanonical(subscriptionPath());

    SubscriberBuilder.Builder wireSubscriberBuilder = SubscriberBuilder.newBuilder();
    wireSubscriberBuilder.setSubscriptionPath(canonicalPath);
    subscriberServiceStub().ifPresent(wireSubscriberBuilder::setSubscriberServiceStub);
    wireSubscriberBuilder.setContext(PubsubContext.of(FRAMEWORK));

    CommitterBuilder.Builder wireCommitterBuilder = CommitterBuilder.newBuilder();
    wireCommitterBuilder.setSubscriptionPath(canonicalPath);
    cursorServiceStub().ifPresent(wireCommitterBuilder::setCursorStub);

    PartitionSubscriberFactory partitionSubscriberFactory =
        partition -> {
          wireSubscriberBuilder.setPartition(partition);
          wireCommitterBuilder.setPartition(partition);
          return new SinglePartitionSubscriber(
              receiver(),
              transformer().orElse(MessageTransforms.toCpsSubscribeTransformer()),
              new AckSetTrackerImpl(wireCommitterBuilder.build()),
              nackHandler().orElse(new NackHandler() {}),
              messageConsumer -> wireSubscriberBuilder.setMessageConsumer(messageConsumer).build(),
              perPartitionFlowControlSettings());
        };

    if (!partitions().isPresent()) {
      AssignerBuilder.Builder assignerBuilder = AssignerBuilder.newBuilder();
      assignerBuilder.setSubscriptionPath(canonicalPath);
      assignmentServiceStub().ifPresent(assignerBuilder::setAssignmentStub);
      AssignerFactory assignerFactory =
          receiver -> {
            assignerBuilder.setReceiver(receiver);
            return assignerBuilder.build();
          };
      return new AssigningSubscriber(partitionSubscriberFactory, assignerFactory);
    }

    List<Subscriber> perPartitionSubscribers = new ArrayList<>();
    for (Partition partition : partitions().get()) {
      wireSubscriberBuilder.setPartition(partition);
      wireCommitterBuilder.setPartition(partition);
      perPartitionSubscribers.add(partitionSubscriberFactory.New(partition));
    }
    return MultiPartitionSubscriber.of(perPartitionSubscribers);
  }
}
