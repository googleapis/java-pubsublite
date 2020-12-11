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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.ServiceClients.addDefaultSettings;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceSettings;
import java.util.Optional;

@AutoValue
public abstract class CommitterBuilder {
  // Required parameters.
  abstract SubscriptionPath subscriptionPath();

  abstract Partition partition();

  // Optional parameters.
  abstract Optional<CursorServiceClient> serviceClient();

  public static Builder newBuilder() {
    return new AutoValue_CommitterBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setPartition(Partition partition);

    // Optional parameters.
    public abstract Builder setServiceClient(CursorServiceClient client);

    abstract CommitterBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public Committer build() throws ApiException {
      CommitterBuilder builder = autoBuild();

      CursorServiceClient serviceClient;
      if (builder.serviceClient().isPresent()) {
        serviceClient = builder.serviceClient().get();
      } else {
        try {
          serviceClient =
              CursorServiceClient.create(
                  addDefaultSettings(
                      builder.subscriptionPath().location().region(),
                      CursorServiceSettings.newBuilder()));
        } catch (Throwable t) {
          throw toCanonical(t).underlying;
        }
      }

      InitialCommitCursorRequest initialCommitCursorRequest =
          InitialCommitCursorRequest.newBuilder()
              .setSubscription(builder.subscriptionPath().toString())
              .setPartition(builder.partition().value())
              .build();
      return new ApiExceptionCommitter(
          new CommitterImpl(serviceClient, initialCommitCursorRequest));
    }
  }
}
