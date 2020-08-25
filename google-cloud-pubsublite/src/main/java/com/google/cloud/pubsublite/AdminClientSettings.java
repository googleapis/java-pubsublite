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

/** Settings for construction a Pub/Sub Lite AdminClient. */
@AutoValue
public abstract class AdminClientSettings {

  // Required parameters.

  /**
   * The <a href="https://cloud.google.com/pubsub/lite/docs/locations">cloud region</a> to perform
   * admin operations for.
   */
  abstract CloudRegion region();

  // Optional parameters.

  /** The retry settings for this client. */
  abstract RetrySettings retrySettings();

  /** A stub to use to connect. */
  abstract Optional<AdminServiceBlockingStub> stub();

  public static Builder newBuilder() {
    return new AutoValue_AdminClientSettings.Builder()
        .setRetrySettings(Constants.DEFAULT_RETRY_SETTINGS);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.

    /** The cloud region to perform admin operations for. */
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.

    /** The retry settings for this client. */
    public abstract Builder setRetrySettings(RetrySettings retrySettings);

    /** A stub to use to connect. */
    public abstract Builder setStub(AdminServiceBlockingStub stub);

    /** Build the settings object. */
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
