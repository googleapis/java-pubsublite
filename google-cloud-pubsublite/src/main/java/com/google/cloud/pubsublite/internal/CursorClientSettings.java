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
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceBlockingStub;
import io.grpc.StatusException;
import java.util.Optional;

@AutoValue
public abstract class CursorClientSettings {

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract RetrySettings retrySettings();

  abstract Optional<CursorServiceBlockingStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_CursorClientSettings.Builder()
        .setRetrySettings(Constants.DEFAULT_RETRY_SETTINGS);
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    // Optional parameters.
    public abstract Builder setStub(CursorServiceBlockingStub stub);

    public abstract CursorClientSettings build();
  }

  CursorClient instantiate() throws StatusException {
    CursorServiceBlockingStub stub;
    if (stub().isPresent()) {
      stub = stub().get();
    } else {
      stub = Stubs.defaultStub(region(), CursorServiceGrpc::newBlockingStub);
    }
    return new CursorClientImpl(region(), stub, retrySettings());
  }
}
