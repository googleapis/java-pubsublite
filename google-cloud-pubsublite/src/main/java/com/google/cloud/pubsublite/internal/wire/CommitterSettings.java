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
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.CursorStreamFactory;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import java.time.Duration;

@AutoValue
public abstract class CommitterSettings {
  // Required parameters.
  abstract SubscriptionPath subscriptionPath();

  abstract Partition partition();

  abstract CursorStreamFactory streamFactory();

  public static Builder newBuilder() {
    return new AutoValue_CommitterSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartition(Partition partition);

    public abstract Builder setStreamFactory(CursorStreamFactory streamFactory);

    public abstract CommitterSettings build();
  }

  public Committer instantiate() {
    InitialCommitCursorRequest initialCommitCursorRequest =
        InitialCommitCursorRequest.newBuilder()
            .setSubscription(subscriptionPath().toString())
            .setPartition(partition().value())
            .build();
    return new ApiExceptionCommitter(
        new BatchingCommitter(
            new CommitterImpl(streamFactory(), initialCommitCursorRequest),
            AlarmFactory.create(Duration.ofMillis(50))));
  }
}
