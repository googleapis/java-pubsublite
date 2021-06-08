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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultMetadata;
import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.api.gax.core.CredentialsProvider;
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
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.AssignerSettings;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.internal.wire.CommitterSettings;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.RoutingMetadata;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceSettings;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceSettings;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceSettings;
import com.google.common.collect.ImmutableList;
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
  // Required parameters.
  /**
   * The receiver which handles new messages sent by the Pub/Sub Lite system. Only one downcall from
   * any connected partition will be outstanding at a time, and blocking in this receiver callback
   * will block forward progress.
   */
  abstract MessageReceiver receiver();

  /** The subscription to use to receive messages. */
  abstract SubscriptionPath subscriptionPath();

  /**
   * The per-partition flow control settings. Because these apply per-partition, if you are using
   * them to bound memory usage, keep in mind the number of partitions in the associated topic.
   */
  abstract FlowControlSettings perPartitionFlowControlSettings();

  // Optional parameters.

  /**
   * The partitions this subscriber should connect to to receive messages. If not empty, disables
   * auto-assignment.
   */
  abstract List<Partition> partitions();

  /**
   * The MessageTransformer to get PubsubMessages from Pub/Sub Lite wire messages. The messageId
   * field must not be set on the returned message.
   */
  abstract Optional<MessageTransformer<SequencedMessage, PubsubMessage>> transformer();

  /** A provider for credentials. */
  abstract CredentialsProvider credentialsProvider();

  /**
   * A Framework tag for internal metrics. Please set this if integrating with a public framework!
   */
  abstract Framework framework();

  /**
   * A supplier for new SubscriberServiceClients. Should return a new client each time. If present,
   * ignores CredentialsProvider.
   */
  abstract Optional<Supplier<SubscriberServiceClient>> subscriberServiceClientSupplier();

  /**
   * A supplier for new CursorServiceClients. Should return a new client each time. If present,
   * ignores CredentialsProvider.
   */
  abstract Optional<Supplier<CursorServiceClient>> cursorServiceClientSupplier();

  /**
   * A client to connect to the Pub/Sub lite assignment service. If present, ignores
   * CredentialsProvider.
   */
  abstract Optional<PartitionAssignmentServiceClient> assignmentServiceClient();

  /**
   * A handler for the action to take when {@link com.google.cloud.pubsub.v1.AckReplyConsumer#nack}
   * is called. In Pub/Sub Lite, only a single subscriber for a given subscription is connected to
   * any partition at a time, and there is no other client that may be able to handle messages.
   */
  abstract Optional<NackHandler> nackHandler();

  public static Builder newBuilder() {
    return new AutoValue_SubscriberSettings.Builder()
        .setFramework(Framework.of("CLOUD_PUBSUB_SHIM"))
        .setPartitions(ImmutableList.of())
        .setCredentialsProvider(
            SubscriberServiceSettings.defaultCredentialsProviderBuilder().build());
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

    /**
     * The MessageTransformer to get PubsubMessages from Pub/Sub Lite wire messages. The messageId
     * field must not be set on the returned message.
     */
    public abstract Builder setTransformer(
        MessageTransformer<SequencedMessage, PubsubMessage> transformer);

    /** A provider for credentials. */
    public abstract Builder setCredentialsProvider(CredentialsProvider provider);

    /**
     * A Framework tag for internal metrics. Please set this if integrating with a public framework!
     */
    public abstract Builder setFramework(Framework framework);

    /**
     * A supplier for new SubscriberServiceClients. Should return a new client each time. If
     * present, ignores CredentialsProvider.
     */
    public abstract Builder setSubscriberServiceClientSupplier(
        Supplier<SubscriberServiceClient> supplier);

    /**
     * A supplier for new CursorServiceClients. Should return a new client each time. If present,
     * ignores CredentialsProvider.
     */
    public abstract Builder setCursorServiceClientSupplier(Supplier<CursorServiceClient> supplier);

    /**
     * A client to connect to the Pub/Sub lite assignment service. If present, ignores
     * CredentialsProvider.
     */
    public abstract Builder setAssignmentServiceClient(PartitionAssignmentServiceClient client);

    /**
     * A handler for the action to take when {@link
     * com.google.cloud.pubsub.v1.AckReplyConsumer#nack} is called. In Pub/Sub Lite, only a single
     * subscriber for a given subscription is connected to any partition at a time, and there is no
     * other client that may be able to handle messages.
     */
    public abstract Builder setNackHandler(NackHandler nackHandler);

    public abstract SubscriberSettings build();
  }

  private SubscriberServiceClient newSubscriberServiceClient(Partition partition)
      throws ApiException {
    if (subscriberServiceClientSupplier().isPresent()) {
      return subscriberServiceClientSupplier().get().get();
    }
    try {
      SubscriberServiceSettings.Builder settingsBuilder =
          SubscriberServiceSettings.newBuilder().setCredentialsProvider(credentialsProvider());
      settingsBuilder =
          addDefaultMetadata(
              PubsubContext.of(framework()),
              RoutingMetadata.of(subscriptionPath(), partition),
              settingsBuilder);
      return SubscriberServiceClient.create(
          addDefaultSettings(subscriptionPath().location().region(), settingsBuilder));
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  private CursorServiceClient newCursorServiceClient() throws ApiException {
    if (cursorServiceClientSupplier().isPresent()) {
      return cursorServiceClientSupplier().get().get();
    }
    try {
      return CursorServiceClient.create(
          addDefaultSettings(
              subscriptionPath().location().region(),
              CursorServiceSettings.newBuilder().setCredentialsProvider(credentialsProvider())));
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  Subscriber newPartitionSubscriber(Partition partition) throws CheckedApiException {
    try {
      SubscriberBuilder.Builder wireSubscriberBuilder =
          SubscriberBuilder.newBuilder()
              .setPartition(partition)
              .setSubscriptionPath(subscriptionPath())
              .setServiceClient(newSubscriberServiceClient(partition))
              .setInitialLocation(
                  SeekRequest.newBuilder().setNamedTarget(NamedTarget.COMMITTED_CURSOR).build());

      Committer wireCommitter =
          CommitterSettings.newBuilder()
              .setSubscriptionPath(subscriptionPath())
              .setPartition(partition)
              .setServiceClient(newCursorServiceClient())
              .build()
              .instantiate();

      return new SinglePartitionSubscriber(
          receiver(),
          MessageTransforms.addIdCpsSubscribeTransformer(
              partition, transformer().orElse(MessageTransforms.toCpsSubscribeTransformer())),
          new AckSetTrackerImpl(wireCommitter),
          nackHandler().orElse(new NackHandler() {}),
          (messageConsumer, resetHandler) ->
              wireSubscriberBuilder
                  .setMessageConsumer(messageConsumer)
                  .setResetHandler(resetHandler)
                  .build(),
          perPartitionFlowControlSettings());
    } catch (Throwable t) {
      throw toCanonical(t);
    }
  }

  private PartitionAssignmentServiceClient getAssignmentServiceClient() throws ApiException {
    if (assignmentServiceClient().isPresent()) {
      return assignmentServiceClient().get();
    }
    try {
      return PartitionAssignmentServiceClient.create(
          addDefaultSettings(
              subscriptionPath().location().region(),
              PartitionAssignmentServiceSettings.newBuilder()
                  .setCredentialsProvider(credentialsProvider())));
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @SuppressWarnings("CheckReturnValue")
  Subscriber instantiate() throws ApiException {
    PartitionSubscriberFactory partitionSubscriberFactory = this::newPartitionSubscriber;

    if (partitions().isEmpty()) {
      AssignerSettings.Builder assignerSettings =
          AssignerSettings.newBuilder()
              .setSubscriptionPath(subscriptionPath())
              .setServiceClient(getAssignmentServiceClient());
      AssignerFactory assignerFactory =
          receiver -> assignerSettings.setReceiver(receiver).build().instantiate();
      return new AssigningSubscriber(partitionSubscriberFactory, assignerFactory);
    }

    List<Subscriber> perPartitionSubscribers = new ArrayList<>();
    for (Partition partition : partitions()) {
      try {
        perPartitionSubscribers.add(partitionSubscriberFactory.newSubscriber(partition));
      } catch (CheckedApiException e) {
        throw e.underlying;
      }
    }
    return MultiPartitionSubscriber.of(perPartitionSubscribers);
  }
}
