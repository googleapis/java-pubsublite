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
import com.google.cloud.pubsublite.Endpoints;
import com.google.cloud.pubsublite.Stubs;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc.TopicStatsServiceBlockingStub;
import io.grpc.Status;
import io.grpc.StatusException;
import java.io.IOException;
import java.util.Optional;
import org.threeten.bp.Duration;

@AutoValue
public abstract class TopicStatsClientSettings {

  public static final RetrySettings DEFAULT_RETRY_SETTINGS =
      RetrySettings.newBuilder()
          .setInitialRetryDelay(Duration.ofMillis(100))
          .setRetryDelayMultiplier(1.3)
          .setMaxRetryDelay(Duration.ofSeconds(60))
          .setJittered(true)
          .setTotalTimeout(Duration.ofMinutes(10))
          .build();

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract Optional<RetrySettings> retrySettings();

  abstract Optional<TopicStatsServiceBlockingStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_TopicStatsClientSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.
    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    public abstract Builder setStub(TopicStatsServiceBlockingStub stub);

    public abstract TopicStatsClientSettings build();
  }

  TopicStatsClient instantiate() throws StatusException {
    TopicStatsServiceBlockingStub stub;
    if (stub().isPresent()) {
      stub = stub().get();
    } else {
      try {
        stub =
            Stubs.defaultStub(
                Endpoints.regionalEndpoint(region()), TopicStatsServiceGrpc::newBlockingStub);
      } catch (IOException e) {
        throw Status.INTERNAL
            .withCause(e)
            .withDescription("Creating topic stats stub failed.")
            .asException();
      }
    }
    return new TopicStatsClientImpl(region(), stub, retrySettings().orElse(DEFAULT_RETRY_SETTINGS));
  }
}
