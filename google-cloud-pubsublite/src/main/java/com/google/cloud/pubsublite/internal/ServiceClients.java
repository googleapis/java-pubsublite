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

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Endpoints;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class ServiceClients {
  private ServiceClients() {}

  @GuardedBy("this")
  @Nullable
  private static ScheduledExecutorService EXECUTOR = null;

  private static synchronized ExecutorProvider executorProvider() {
    if (EXECUTOR == null) {
      EXECUTOR =
          MoreExecutors.getExitingScheduledExecutorService(
              new ScheduledThreadPoolExecutor(
                  Math.max(4, Runtime.getRuntime().availableProcessors())));
    }
    return FixedExecutorProvider.create(EXECUTOR);
  }

  public static <
          Settings extends ClientSettings<Settings>,
          Builder extends ClientSettings.Builder<Settings, Builder>>
      Settings addDefaultSettings(CloudRegion target, Builder builder) throws ApiException {
    try {
      return builder
          .setEndpoint(Endpoints.regionalEndpoint(target))
          .setExecutorProvider(executorProvider())
          .build();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }
}
