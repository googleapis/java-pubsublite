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

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.internal.TopicStatsClientSettings;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc.TopicStatsServiceBlockingStub;
import com.google.common.base.Optional;
import io.grpc.StatusException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

@AutoValue
public abstract class TopicBacklogReaderSettings implements Serializable {
  /**
   * The topic path for this backlog reader. Either topicPath or subscriptionPath must be set. If
   * both are set, subscriptionPath will be ignored.
   */
  abstract TopicPath topicPath();

  // Optional parameters
  abstract Optional<SerializableSupplier<TopicStatsServiceBlockingStub>> stub();

  public static Builder newBuilder() {
    return new AutoValue_TopicBacklogReaderSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setTopicPath(TopicPath topicPath);

    public Builder setTopicPathFromSubscriptionPath(SubscriptionPath subscriptionPath)
        throws StatusException {
      try (AdminClient adminClient =
          AdminClient.create(
              AdminClientSettings.newBuilder()
                  .setRegion(subscriptionPath.location().region())
                  .build())) {
        setTopicPath(
            TopicPath.parse(adminClient.getSubscription(subscriptionPath).get().getTopic()));
        return this;
      } catch (ExecutionException e) {
        throw ExtractStatus.toCanonical(e.getCause());
      } catch (Throwable t) {
        throw ExtractStatus.toCanonical(t);
      }
    }

    public abstract Builder setStub(SerializableSupplier<TopicStatsServiceBlockingStub> stub);

    public abstract TopicBacklogReaderSettings build();
  }

  TopicBacklogReader instantiate() throws StatusException {
    TopicStatsClientSettings.Builder builder = TopicStatsClientSettings.newBuilder();
    if (stub().isPresent()) {
      builder.setStub(stub().get().get());
    }
    builder.setRegion(topicPath().location().region());
    return new TopicBacklogReaderImpl(TopicStatsClient.create(builder.build()), topicPath());
  }
}
