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
package com.google.cloud.pubsublite.internal;

import com.google.api.gax.retrying.RetrySettings;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Stubs;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc.TopicStatsServiceBlockingStub;
import io.grpc.StatusException;
import java.util.Optional;

@AutoValue
public abstract class TopicStatsClientSettings {

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract RetrySettings retrySettings();

  abstract Optional<TopicStatsServiceBlockingStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_TopicStatsClientSettings.Builder()
        .setRetrySettings(Constants.DEFAULT_RETRY_SETTINGS);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    // Optional parameters.
    public abstract Builder setStub(TopicStatsServiceBlockingStub stub);

    public abstract TopicStatsClientSettings build();
  }

  TopicStatsClient instantiate() throws StatusException {
    TopicStatsServiceBlockingStub stub;
    if (stub().isPresent()) {
      stub = stub().get();
    } else {
      stub = Stubs.defaultStub(region(), TopicStatsServiceGrpc::newBlockingStub);
    }
    return new TopicStatsClientImpl(region(), stub, retrySettings());
  }
}
