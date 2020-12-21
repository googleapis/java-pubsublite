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

import static com.google.cloud.pubsublite.internal.ServiceClients.addDefaultSettings;

import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.internal.AdminClientImpl;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
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
  abstract Optional<RetrySettings> retrySettings();

  /** A stub to use to connect. */
  abstract Optional<AdminServiceClient> serviceClient();

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

    /** A service client to use to connect. */
    public abstract Builder setServiceClient(AdminServiceClient serviceClient);

    /** Build the settings object. */
    public abstract AdminClientSettings build();
  }

  AdminClient instantiate() throws ApiException {
    AdminServiceClient serviceClient;
    if (serviceClient().isPresent()) {
      serviceClient = serviceClient().get();
    } else {
      try {
        AdminServiceSettings.Builder builder =
            addDefaultSettings(region(), AdminServiceSettings.newBuilder()).toBuilder();
        if (retrySettings().isPresent()) {
          builder.applyToAllUnaryMethods(
              callBuilder -> {
                callBuilder.setRetrySettings(retrySettings().get());
                return null;
              });
        }
        serviceClient = AdminServiceClient.create(builder.build());
      } catch (Throwable t) {
        throw ExtractStatus.toCanonical(t).underlying;
      }
    }
    return new AdminClientImpl(region(), serviceClient);
  }
}
