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
import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
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
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.AssignerBuilder;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

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

  abstract Optional<Supplier<SubscriberServiceClient>> subscriberServiceClientSupplier();

  abstract Optional<Supplier<CursorServiceClient>> cursorServiceClientSupplier();

  abstract Optional<PartitionAssignmentServiceClient> assignmentServiceClient();

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

    /** A supplier for new SubscriberServiceClients. Should return a new client each time. */
    public abstract Builder setSubscriberServiceClientSupplier(
        Supplier<SubscriberServiceClient> supplier);

    /** A supplier for new CursorServiceClients. Should return a new client each time. */
    public abstract Builder setCursorServiceClientSupplier(Supplier<CursorServiceClient> supplier);

    /** A client to connect to the Pub/Sub lite assignment service. */
    public abstract Builder setAssignmentServiceClient(PartitionAssignmentServiceClient client);

    /**
     * A handler for the action to take when {@link
     * com.google.cloud.pubsub.v1.AckReplyConsumer#nack} is called. In Pub/Sub Lite, only a single
     * subscriber for a given subscription is connected to any partition at a time, and there is no
     * other client that may be able to handle messages.
     */
    public abstract Builder setNackHandler(NackHandler nackHandler);

    abstract SubscriberSettings autoBuild();

    /** Build the SubscriberSettings instance. */
    public SubscriberSettings build() throws ApiException {
      SubscriberSettings settings = autoBuild();
      checkArgument(
          !settings.partitions().isPresent() || !settings.partitions().get().isEmpty(),
          "Must provide at least one partition if setting partitions explicitly.");
      return settings;
    }
  }

  PartitionSubscriberFactory makePartitionSubscriberFactory(SubscriptionPath canonicalPath)
      throws ApiException {
    return partition -> {
      try {
        SubscriberBuilder.Builder wireSubscriberBuilder = SubscriberBuilder.newBuilder();
        wireSubscriberBuilder.setSubscriptionPath(canonicalPath);
        subscriberServiceClientSupplier()
            .ifPresent(supplier -> wireSubscriberBuilder.setServiceClient(supplier.get()));
        wireSubscriberBuilder.setContext(PubsubContext.of(FRAMEWORK));
        wireSubscriberBuilder.setPartition(partition);

        CommitterBuilder.Builder wireCommitterBuilder = CommitterBuilder.newBuilder();
        wireCommitterBuilder.setSubscriptionPath(canonicalPath);
        cursorServiceClientSupplier()
            .ifPresent(supplier -> wireCommitterBuilder.setServiceClient(supplier.get()));
        wireCommitterBuilder.setPartition(partition);

        return new SinglePartitionSubscriber(
            receiver(),
            transformer().orElse(MessageTransforms.toCpsSubscribeTransformer()),
            new AckSetTrackerImpl(wireCommitterBuilder.build()),
            nackHandler().orElse(new NackHandler() {}),
            messageConsumer -> wireSubscriberBuilder.setMessageConsumer(messageConsumer).build(),
            perPartitionFlowControlSettings());
      } catch (Throwable t) {
        throw toCanonical(t);
      }
    };
  }

  @SuppressWarnings("CheckReturnValue")
  Subscriber instantiate() throws ApiException {
    SubscriptionPath canonicalPath = toCanonical(subscriptionPath());
    PartitionSubscriberFactory partitionSubscriberFactory =
        makePartitionSubscriberFactory(canonicalPath);

    if (!partitions().isPresent()) {
      AssignerBuilder.Builder assignerBuilder = AssignerBuilder.newBuilder();
      assignerBuilder.setSubscriptionPath(canonicalPath);
      assignmentServiceClient().ifPresent(assignerBuilder::setServiceClient);
      AssignerFactory assignerFactory =
          receiver -> {
            assignerBuilder.setReceiver(receiver);
            return assignerBuilder.build();
          };
      return new AssigningSubscriber(partitionSubscriberFactory, assignerFactory);
    }

    List<Subscriber> perPartitionSubscribers = new ArrayList<>();
    for (Partition partition : partitions().get()) {
      try {
        perPartitionSubscribers.add(partitionSubscriberFactory.newSubscriber(partition));
      } catch (CheckedApiException e) {
        throw e.underlying;
      }
    }
    return MultiPartitionSubscriber.of(perPartitionSubscribers);
  }
}
