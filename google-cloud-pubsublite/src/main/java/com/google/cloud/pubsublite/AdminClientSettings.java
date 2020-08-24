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

package com.google.cloud.pubsublite;

import com.google.api.gax.retrying.RetrySettings;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.internal.AdminClientImpl;
import com.google.cloud.pubsublite.proto.AdminServiceGrpc;
import com.google.cloud.pubsublite.proto.AdminServiceGrpc.AdminServiceBlockingStub;
import io.grpc.StatusException;
import java.util.Optional;

@AutoValue
public abstract class AdminClientSettings {

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract RetrySettings retrySettings();

  abstract Optional<AdminServiceBlockingStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_AdminClientSettings.Builder()
        .setRetrySettings(Constants.DEFAULT_RETRY_SETTINGS);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.
    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    public abstract Builder setStub(AdminServiceBlockingStub stub);

    public abstract AdminClientSettings build();
  }

  AdminClient instantiate() throws StatusException {
    AdminServiceBlockingStub stub;
    if (stub().isPresent()) {
      stub = stub().get();
    } else {
      stub = Stubs.defaultStub(region(), AdminServiceGrpc::newBlockingStub);
    }
    return new AdminClientImpl(region(), stub, retrySettings());
  }
}
