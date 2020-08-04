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

import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsublite.internal.ChannelCache;
import com.google.common.collect.ImmutableList;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.AbstractStub;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class Stubs {
  private static final ChannelCache channels = new ChannelCache();

  public static <StubT extends AbstractStub<StubT>> StubT defaultStub(
      String target, Function<Channel, StubT> stubFactory) throws IOException {
    return stubFactory
        .apply(ClientInterceptors.intercept(channels.get(target), getClientInterceptors()))
        .withCallCredentials(
            MoreCallCredentials.from(
                GoogleCredentials.getApplicationDefault()
                    .createScoped(
                        ImmutableList.of("https://www.googleapis.com/auth/cloud-platform"))));
  }

  private static List<ClientInterceptor> getClientInterceptors() {
    List<ClientInterceptor> clientInterceptors = new ArrayList<>();
    Map<String, String> apiClientHeaders =
        ApiClientHeaderProvider.newBuilder()
            .setClientLibToken("gccl", GaxProperties.getLibraryVersion(Stubs.class))
            .setTransportToken(
                GaxGrpcProperties.getGrpcTokenName(), GaxGrpcProperties.getGrpcVersion())
            .build()
            .getHeaders();
    clientInterceptors.add(
        new ClientInterceptor() {
          @Override
          public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
              MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);
            return new SimpleForwardingClientCall<ReqT, RespT>(call) {
              @Override
              public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
                for (Entry<String, String> apiClientHeader : apiClientHeaders.entrySet()) {
                  headers.put(
                      Key.of(apiClientHeader.getKey(), Metadata.ASCII_STRING_MARSHALLER),
                      apiClientHeader.getValue());
                }
                super.start(responseListener, headers);
              }
            };
          }
        });
    return clientInterceptors;
  }

  private Stubs() {}
}
