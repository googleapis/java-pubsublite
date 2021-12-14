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

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Endpoints;
import com.google.cloud.pubsublite.internal.Lazy;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentHashMap;
import org.threeten.bp.Duration;

public final class ServiceClients {
  private ServiceClients() {}

  private static final Lazy<ExecutorProvider> PROVIDER =
      new Lazy<>(
          () ->
              FixedExecutorProvider.create(
                  SystemExecutors.newDaemonExecutor("pubsub-lite-service-clients")));

  private static final ConcurrentHashMap<CloudRegion, TransportChannelProvider> CHANNELS =
      new ConcurrentHashMap<>();

  private static TransportChannelProvider getTransportChannelProvider(CloudRegion region) {
    return CHANNELS.computeIfAbsent(
        region,
        key ->
            InstantiatingGrpcChannelProvider.newBuilder()
                .setMaxInboundMessageSize(Integer.MAX_VALUE)
                .setKeepAliveTime(Duration.ofMinutes(1))
                .setKeepAliveWithoutCalls(true)
                .setKeepAliveTimeout(Duration.ofMinutes(1))
                .setPoolSize(100)
                .build());
  }

  public static <
          Settings extends ClientSettings<Settings>,
          Builder extends ClientSettings.Builder<Settings, Builder>>
      Settings addDefaultSettings(CloudRegion target, Builder builder) throws ApiException {
    try {
      return builder
          .setEndpoint(Endpoints.regionalEndpoint(target))
          .setExecutorProvider(PROVIDER.get())
          .setTransportChannelProvider(getTransportChannelProvider(target))
          .build();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  // Adds context routing metadata for publisher or subscriber.
  public static <
          Settings extends ClientSettings<Settings>,
          Builder extends ClientSettings.Builder<Settings, Builder>>
      Builder addDefaultMetadata(
          PubsubContext context, RoutingMetadata routingMetadata, Builder builder) {
    return builder.setHeaderProvider(
        () ->
            ImmutableMap.<String, String>builder()
                .putAll(context.getMetadata())
                .putAll(routingMetadata.getMetadata())
                .build());
  }
}
