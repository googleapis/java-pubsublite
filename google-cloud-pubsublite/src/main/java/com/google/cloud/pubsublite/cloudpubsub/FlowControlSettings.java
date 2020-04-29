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

import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;

// Describes limits on bytes and messages outstanding for a single partition Pub/Sub Lite
// subscriber. These are hard limits enforced by the server.
@AutoValue
public abstract class FlowControlSettings {
  public static Builder builder() {
    return new AutoValue_FlowControlSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setBytesOutstanding(long bytes);

    public abstract Builder setMessagesOutstanding(long elements);

    abstract FlowControlSettings autoBuild();

    public FlowControlSettings build() throws StatusException {
      FlowControlSettings settings = autoBuild();
      checkState(settings.bytesOutstanding() > 0, "Cannot have 0 or less bytes outstanding.");
      checkState(settings.messagesOutstanding() > 0, "Cannot have 0 or less messages outstanding.");
      return settings;
    }
  }

  public abstract long bytesOutstanding();

  public abstract long messagesOutstanding();
}
