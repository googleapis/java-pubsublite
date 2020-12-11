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
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Endpoints;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.threeten.bp.Duration;

public final class ServiceClients {
  private ServiceClients() {}

  private static final Lazy<ExecutorProvider> PROVIDER =
      new Lazy<>(
          () ->
              FixedExecutorProvider.create(
                  MoreExecutors.getExitingScheduledExecutorService(
                      new ScheduledThreadPoolExecutor(
                          Math.max(4, Runtime.getRuntime().availableProcessors())))));

  public static <
          Settings extends ClientSettings<Settings>,
          Builder extends ClientSettings.Builder<Settings, Builder>>
      Settings addDefaultSettings(CloudRegion target, Builder builder) throws ApiException {
    try {
      return builder
          .setEndpoint(Endpoints.regionalEndpoint(target))
          .setExecutorProvider(PROVIDER.get())
          .setTransportChannelProvider(
              InstantiatingGrpcChannelProvider.newBuilder()
                  .setMaxInboundMessageSize(Integer.MAX_VALUE)
                  .setKeepAliveTime(Duration.ofMinutes(1))
                  .setKeepAliveWithoutCalls(true)
                  .setKeepAliveTimeout(Duration.ofMinutes(1))
                  .build())
          .build();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }
}
