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
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientContext;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
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
public class GrpcPublisherServiceStub extends PublisherServiceStub {

  private static final MethodDescriptor<PublishRequest, PublishResponse> publishMethodDescriptor =
      MethodDescriptor.<PublishRequest, PublishResponse>newBuilder()
          .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
          .setFullMethodName("google.cloud.pubsublite.v1.PublisherService/Publish")
          .setRequestMarshaller(ProtoUtils.marshaller(PublishRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(PublishResponse.getDefaultInstance()))
          .build();

  private final BackgroundResource backgroundResources;

  private final BidiStreamingCallable<PublishRequest, PublishResponse> publishCallable;

  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcPublisherServiceStub create(PublisherServiceStubSettings settings)
      throws IOException {
    return new GrpcPublisherServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcPublisherServiceStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcPublisherServiceStub(
        PublisherServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcPublisherServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcPublisherServiceStub(
        PublisherServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcPublisherServiceStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcPublisherServiceStub(
      PublisherServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new GrpcPublisherServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcPublisherServiceStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcPublisherServiceStub(
      PublisherServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    GrpcCallSettings<PublishRequest, PublishResponse> publishTransportSettings =
        GrpcCallSettings.<PublishRequest, PublishResponse>newBuilder()
            .setMethodDescriptor(publishMethodDescriptor)
            .build();

    this.publishCallable =
        callableFactory.createBidiStreamingCallable(
            publishTransportSettings, settings.publishSettings(), clientContext);

    backgroundResources = new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public BidiStreamingCallable<PublishRequest, PublishResponse> publishCallable() {
    return publishCallable;
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
