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

package com.google.cloud.pubsublite.spark;

import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.CursorClientSettings;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.internal.TopicStatsClientSettings;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.RoutingMetadata;
import com.google.cloud.pubsublite.internal.wire.ServiceClients;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceSettings;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceSettings;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.cloud.pubsublite.v1.TopicStatsServiceSettings;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.spark.sql.sources.v2.DataSourceOptions;

@AutoValue
public abstract class PslDataSourceOptions implements Serializable {
  private static final long serialVersionUID = 2680059304693561607L;

  @Nullable
  public abstract String credentialsKey();

  public abstract SubscriptionPath subscriptionPath();

  @Nullable
  public abstract FlowControlSettings flowControlSettings();

  public abstract long maxBatchOffsetRange();

  public static Builder builder() {
    return new AutoValue_PslDataSourceOptions.Builder()
        .setCredentialsKey(null)
        // TODO(jiangmichael): Revisit this later about if we need to expose this as a user
        // configurable option. Ideally we should expose bytes range/# msgs range not
        // offsets range since PSL doesn't guarantee offset = msg.
        .setMaxBatchOffsetRange(Constants.DEFAULT_BATCH_OFFSET_RANGE);
  }

  public static PslDataSourceOptions fromSparkDataSourceOptions(DataSourceOptions options) {
    if (!options.get(Constants.SUBSCRIPTION_CONFIG_KEY).isPresent()) {
      throw new IllegalArgumentException(Constants.SUBSCRIPTION_CONFIG_KEY + " is required.");
    }

    Builder builder = builder();
    Optional<String> value;
    if ((value = options.get(Constants.CREDENTIALS_KEY_CONFIG_KEY)).isPresent()) {
      builder.setCredentialsKey(value.get());
    }
    return builder
        .setSubscriptionPath(
            SubscriptionPath.parse(options.get(Constants.SUBSCRIPTION_CONFIG_KEY).get()))
        .setFlowControlSettings(
            FlowControlSettings.builder()
                .setMessagesOutstanding(
                    options.getLong(
                        Constants.MESSAGES_OUTSTANDING_CONFIG_KEY,
                        Constants.DEFAULT_MESSAGES_OUTSTANDING))
                .setBytesOutstanding(
                    options.getLong(
                        Constants.BYTES_OUTSTANDING_CONFIG_KEY,
                        Constants.DEFAULT_BYTES_OUTSTANDING))
                .build())
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setCredentialsKey(String credentialsKey);

    public abstract Builder setSubscriptionPath(SubscriptionPath subscriptionPath);

    public abstract Builder setMaxBatchOffsetRange(long maxBatchOffsetRange);

    public abstract Builder setFlowControlSettings(FlowControlSettings flowControlSettings);

    public abstract PslDataSourceOptions build();
  }

  MultiPartitionCommitter newMultiPartitionCommitter(long topicPartitionCount) {
    return new MultiPartitionCommitterImpl(
        topicPartitionCount,
        (partition) ->
            CommitterBuilder.newBuilder()
                .setSubscriptionPath(this.subscriptionPath())
                .setPartition(partition)
                .setServiceClient(newCursorServiceClient())
                .build());
  }

  PartitionSubscriberFactory getSubscriberFactory() {
    return (partition, consumer) -> {
      PubsubContext context = PubsubContext.of(Constants.FRAMEWORK);
      SubscriberServiceSettings.Builder settingsBuilder =
          SubscriberServiceSettings.newBuilder()
              .setCredentialsProvider(new PslCredentialsProvider(this));
      ServiceClients.addDefaultMetadata(
          context, RoutingMetadata.of(this.subscriptionPath(), partition), settingsBuilder);
      try {
        SubscriberServiceClient serviceClient =
            SubscriberServiceClient.create(
                addDefaultSettings(this.subscriptionPath().location().region(), settingsBuilder));
        return SubscriberBuilder.newBuilder()
            .setSubscriptionPath(this.subscriptionPath())
            .setPartition(partition)
            .setContext(context)
            .setServiceClient(serviceClient)
            .setMessageConsumer(consumer)
            .build();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to create subscriber service.", e);
      }
    };
  }

  // TODO(b/jiangmichael): Make XXXClientSettings accept creds so we could simplify below methods.
  private CursorServiceClient newCursorServiceClient() {
    try {
      return CursorServiceClient.create(
          addDefaultSettings(
              this.subscriptionPath().location().region(),
              CursorServiceSettings.newBuilder()
                  .setCredentialsProvider(new PslCredentialsProvider(this))));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create CursorServiceClient.");
    }
  }

  CursorClient newCursorClient() {
    return CursorClient.create(
        CursorClientSettings.newBuilder()
            .setRegion(this.subscriptionPath().location().region())
            .setServiceClient(newCursorServiceClient())
            .build());
  }

  private AdminServiceClient newAdminServiceClient() {
    try {
      return AdminServiceClient.create(
          addDefaultSettings(
              this.subscriptionPath().location().region(),
              AdminServiceSettings.newBuilder()
                  .setCredentialsProvider(new PslCredentialsProvider(this))));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create AdminServiceClient.");
    }
  }

  AdminClient newAdminClient() {
    return AdminClient.create(
        AdminClientSettings.newBuilder()
            .setRegion(this.subscriptionPath().location().region())
            .setServiceClient(newAdminServiceClient())
            .build());
  }

  private TopicStatsServiceClient newTopicStatsServiceClient() {
    try {
      return TopicStatsServiceClient.create(
          addDefaultSettings(
              this.subscriptionPath().location().region(),
              TopicStatsServiceSettings.newBuilder()
                  .setCredentialsProvider(new PslCredentialsProvider(this))));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create TopicStatsServiceClient.");
    }
  }

  TopicStatsClient newTopicStatsClient() {
    return TopicStatsClient.create(
        TopicStatsClientSettings.newBuilder()
            .setRegion(this.subscriptionPath().location().region())
            .setServiceClient(newTopicStatsServiceClient())
            .build());
  }
}
