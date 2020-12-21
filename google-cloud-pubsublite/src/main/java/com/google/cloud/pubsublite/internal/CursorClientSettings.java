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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.ServiceClients.addDefaultSettings;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceSettings;
import java.util.Optional;

@AutoValue
public abstract class CursorClientSettings {

  // Required parameters.
  abstract CloudRegion region();

  // Optional parameters.
  abstract Optional<CursorServiceClient> serviceClient();

  public static Builder newBuilder() {
    return new AutoValue_CursorClientSettings.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    // Required parameters.
    public abstract Builder setRegion(CloudRegion region);

    // Optional parameters.
    public abstract Builder setServiceClient(CursorServiceClient serviceClient);

    public abstract CursorClientSettings build();
  }

  CursorClient instantiate() throws ApiException {
    CursorServiceClient serviceClient;
    if (serviceClient().isPresent()) {
      serviceClient = serviceClient().get();
    } else {
      try {
        serviceClient =
            CursorServiceClient.create(
                addDefaultSettings(region(), CursorServiceSettings.newBuilder()));
      } catch (Throwable t) {
        throw toCanonical(t).underlying;
      }
    }
    return new CursorClientImpl(region(), serviceClient);
  }
}
