/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.v1.stub;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.longrunning.stub.GrpcOperationsStub;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the TopicStatsService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
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
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorMethodDescriptor =
          MethodDescriptor.<ComputeHeadCursorRequest, ComputeHeadCursorResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeHeadCursor")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ComputeHeadCursorRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ComputeHeadCursorResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorMethodDescriptor =
          MethodDescriptor.<ComputeTimeCursorRequest, ComputeTimeCursorResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeTimeCursor")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ComputeTimeCursorRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ComputeTimeCursorResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private final UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable;
  private final UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorCallable;
  private final UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
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
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsTransportSettings =
            GrpcCallSettings.<ComputeMessageStatsRequest, ComputeMessageStatsResponse>newBuilder()
                .setMethodDescriptor(computeMessageStatsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("topic", String.valueOf(request.getTopic()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
        computeHeadCursorTransportSettings =
            GrpcCallSettings.<ComputeHeadCursorRequest, ComputeHeadCursorResponse>newBuilder()
                .setMethodDescriptor(computeHeadCursorMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("topic", String.valueOf(request.getTopic()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
        computeTimeCursorTransportSettings =
            GrpcCallSettings.<ComputeTimeCursorRequest, ComputeTimeCursorResponse>newBuilder()
                .setMethodDescriptor(computeTimeCursorMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("topic", String.valueOf(request.getTopic()));
                      return builder.build();
                    })
                .build();

    this.computeMessageStatsCallable =
        callableFactory.createUnaryCallable(
            computeMessageStatsTransportSettings,
            settings.computeMessageStatsSettings(),
            clientContext);
    this.computeHeadCursorCallable =
        callableFactory.createUnaryCallable(
            computeHeadCursorTransportSettings,
            settings.computeHeadCursorSettings(),
            clientContext);
    this.computeTimeCursorCallable =
        callableFactory.createUnaryCallable(
            computeTimeCursorTransportSettings,
            settings.computeTimeCursorSettings(),
            clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable() {
    return computeMessageStatsCallable;
  }

  @Override
  public UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorCallable() {
    return computeHeadCursorCallable;
  }

  @Override
  public UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorCallable() {
    return computeTimeCursorCallable;
  }

  @Override
  public final void close() {
    try {
      backgroundResources.close();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to close resource", e);
    }
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
