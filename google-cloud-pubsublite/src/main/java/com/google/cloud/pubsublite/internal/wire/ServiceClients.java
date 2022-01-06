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

import com.google.api.gax.core.FixedExecutorProvider;
import com.google.api.gax.grpc.GrpcCallContext;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Endpoints;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.threeten.bp.Duration;

public final class ServiceClients {
  // Default to 10 channels per client to avoid server limitations on streams and requests
  // per-channel.
  private static final int CLIENT_POOL_SIZE =
      Integer.parseInt(System.getProperty("PUBSUB_LITE_CHANNELS_PER_CLIENT", "10"));

  private ServiceClients() {}

  private static TransportChannelProvider getTransportChannelProvider() {
    return InstantiatingGrpcChannelProvider.newBuilder()
        .setMaxInboundMessageSize(Integer.MAX_VALUE)
        .setKeepAliveTime(Duration.ofMinutes(1))
        .setKeepAliveWithoutCalls(true)
        .setKeepAliveTimeout(Duration.ofMinutes(1))
        .setPoolSize(CLIENT_POOL_SIZE)
        .setExecutor(SystemExecutors.getFuturesExecutor())
        .build();
  }

  public static <
          Settings extends ClientSettings<Settings>,
          Builder extends ClientSettings.Builder<Settings, Builder>>
      Settings addDefaultSettings(CloudRegion target, Builder builder) throws ApiException {
    try {
      return builder
          .setEndpoint(Endpoints.regionalEndpoint(target))
          .setBackgroundExecutorProvider(
              FixedExecutorProvider.create(SystemExecutors.getAlarmExecutor()))
          .setTransportChannelProvider(getTransportChannelProvider())
          .build();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  public static ApiCallContext getCallContext(
      PubsubContext context, RoutingMetadata routingMetadata) {
    return GrpcCallContext.createDefault()
        .withExtraHeaders(
            Multimaps.asMap(
                ImmutableListMultimap.<String, String>builder()
                    .putAll(context.getMetadata().entrySet())
                    .putAll(routingMetadata.getMetadata().entrySet())
                    .build()));
  }
}
