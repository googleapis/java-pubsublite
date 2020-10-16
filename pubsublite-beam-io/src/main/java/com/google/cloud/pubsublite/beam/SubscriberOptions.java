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

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PartitionLookupUtils;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;

@AutoValue
public abstract class SubscriberOptions implements Serializable {
  private static final long serialVersionUID = 269598118L;

  private static final Framework FRAMEWORK = Framework.of("BEAM");

  // Required parameters.
  public abstract SubscriptionPath subscriptionPath();

  public abstract FlowControlSettings flowControlSettings();

  // Optional parameters.
  /** A set of partitions. If empty, retrieve the set of partitions using an admin client. */
  public abstract ImmutableSet<Partition> partitions();

  /** The class used to read backlog for the subscription described by subscriptionPath(). */
  public abstract TopicBacklogReaderSettings topicBacklogReaderSettings();

  /** A supplier for the subscriber stub to be used. */
  public abstract Optional<SerializableSupplier<SubscriberServiceClient>>
      subscriberClientSupplier();

  /** A supplier for the cursor service stub to be used. */
  public abstract Optional<SerializableSupplier<CursorServiceClient>> committerClientSupplier();

  /**
   * A factory to override subscriber creation entirely and delegate to another method. Primarily
   * useful for testing.
   */
  abstract Optional<SubscriberFactory> subscriberFactory();

  /**
   * A supplier to override committer creation entirely and delegate to another method. Primarily
   * useful for testing.
   */
  abstract Optional<SerializableSupplier<Committer>> committerSupplier();

  public static Builder newBuilder() {
    Builder builder = new AutoValue_SubscriberOptions.Builder();
    return builder.setPartitions(ImmutableSet.of());
  }

  public abstract Builder toBuilder();

  @SuppressWarnings("CheckReturnValue")
  public ImmutableMap<Partition, SubscriberFactory> getSubscriberFactories() {
    ImmutableMap.Builder<Partition, SubscriberFactory> factories = ImmutableMap.builder();
    for (Partition partition : partitions()) {
      factories.put(
          partition,
          subscriberFactory()
              .or(
                  consumer -> {
                    SubscriberBuilder.Builder builder = SubscriberBuilder.newBuilder();
                    builder.setMessageConsumer(consumer);
                    builder.setSubscriptionPath(subscriptionPath());
                    builder.setPartition(partition);
                    builder.setContext(PubsubContext.of(FRAMEWORK));
                    if (subscriberClientSupplier().isPresent()) {
                      builder.setServiceClient(subscriberClientSupplier().get().get());
                    }
                    return builder.build();
                  }));
    }
    return factories.build();
  }

  @SuppressWarnings("CheckReturnValue")
  public ImmutableMap<Partition, Committer> getCommitters() throws ApiException {
    ImmutableMap.Builder<Partition, Committer> committers = ImmutableMap.builder();
    for (Partition partition : partitions()) {
      if (committerSupplier().isPresent()) {
        committers.put(partition, committerSupplier().get().get());
      } else {
        CommitterBuilder.Builder builder = CommitterBuilder.newBuilder();
        builder.setSubscriptionPath(subscriptionPath());
        builder.setPartition(partition);
        if (committerClientSupplier().isPresent()) {
          builder.setServiceClient(committerClientSupplier().get().get());
        }
        committers.put(partition, builder.build());
      }
    }
    return committers.build();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartitions(ImmutableSet<Partition> partitions);

    public abstract Builder setFlowControlSettings(FlowControlSettings flowControlSettings);

    // Optional parameters.
    public abstract Builder setSubscriberClientSupplier(
        SerializableSupplier<SubscriberServiceClient> supplier);

    public abstract Builder setCommitterClientSupplier(
        SerializableSupplier<CursorServiceClient> supplier);

    public abstract Builder setTopicBacklogReaderSettings(
        TopicBacklogReaderSettings topicBacklogReaderSettings);

    // Used in unit tests
    abstract Builder setSubscriberFactory(SubscriberFactory subscriberFactory);

    abstract Builder setCommitterSupplier(SerializableSupplier<Committer> committerSupplier);

    // Used for implementing build();
    abstract SubscriptionPath subscriptionPath();

    abstract ImmutableSet<Partition> partitions();

    abstract Optional<TopicBacklogReaderSettings> topicBacklogReaderSettings();

    abstract SubscriberOptions autoBuild();

    public SubscriberOptions build() throws ApiException {
      if (!partitions().isEmpty() && topicBacklogReaderSettings().isPresent()) {
        return autoBuild();
      }

      if (partitions().isEmpty()) {
        int partitionCount = PartitionLookupUtils.numPartitions(subscriptionPath());
        ImmutableSet.Builder<Partition> partitions = ImmutableSet.builder();
        for (int i = 0; i < partitionCount; i++) {
          partitions.add(Partition.of(i));
        }
        setPartitions(partitions.build());
      }
      if (!topicBacklogReaderSettings().isPresent()) {
        setTopicBacklogReaderSettings(
            TopicBacklogReaderSettings.newBuilder()
                .setTopicPathFromSubscriptionPath(subscriptionPath())
                .build());
      }
      return autoBuild();
    }
  }
}
