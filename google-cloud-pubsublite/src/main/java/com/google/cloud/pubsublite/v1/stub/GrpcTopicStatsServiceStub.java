/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsublite.v1.stub;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.RequestParamsExtractor;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.common.collect.ImmutableMap;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * gRPC stub implementation for Pub/Sub Lite API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator")
@BetaApi("A restructuring of stub classes is planned, so this may break in the future")
public class GrpcTopicStatsServiceStub extends TopicStatsServiceStub {

  private static final MethodDescriptor<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsMethodDescriptor =
          MethodDescriptor.<ComputeMessageStatsRequest, ComputeMessageStatsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeMessageStats")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ComputeMessageStatsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ComputeMessageStatsResponse.getDefaultInstance()))
              .build();

  private final BackgroundResource backgroundResources;

  private final UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable;

  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcTopicStatsServiceStub create(TopicStatsServiceStubSettings settings)
      throws IOException {
    return new GrpcTopicStatsServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcTopicStatsServiceStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcTopicStatsServiceStub(
        TopicStatsServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcTopicStatsServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcTopicStatsServiceStub(
        TopicStatsServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcTopicStatsServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcTopicStatsServiceStub(
      TopicStatsServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new GrpcTopicStatsServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcTopicStatsServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcTopicStatsServiceStub(
      TopicStatsServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    GrpcCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsTransportSettings =
            GrpcCallSettings.<ComputeMessageStatsRequest, ComputeMessageStatsResponse>newBuilder()
                .setMethodDescriptor(computeMessageStatsMethodDescriptor)
                .setParamsExtractor(
                    new RequestParamsExtractor<ComputeMessageStatsRequest>() {
                      @Override
                      public Map<String, String> extract(ComputeMessageStatsRequest request) {
                        ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
                        params.put("topic", String.valueOf(request.getTopic()));
                        return params.build();
                      }
                    })
                .build();

    this.computeMessageStatsCallable =
        callableFactory.createUnaryCallable(
            computeMessageStatsTransportSettings,
            settings.computeMessageStatsSettings(),
            clientContext);

    backgroundResources = new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable() {
    return computeMessageStatsCallable;
  }

  @Override
  public final void close() {
    shutdown();
  }

  @Override
  public void shutdown() {
    backgroundResources.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return backgroundResources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return backgroundResources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    backgroundResources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }
}
