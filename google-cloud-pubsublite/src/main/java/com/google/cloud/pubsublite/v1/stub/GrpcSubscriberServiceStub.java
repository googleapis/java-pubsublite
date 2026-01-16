/*
 * Copyright 2026 Google LLC
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
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientContext;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.longrunning.stub.GrpcOperationsStub;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the SubscriberService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public class GrpcSubscriberServiceStub extends SubscriberServiceStub {
  private static final MethodDescriptor<SubscribeRequest, SubscribeResponse>
      subscribeMethodDescriptor =
          MethodDescriptor.<SubscribeRequest, SubscribeResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName("google.cloud.pubsublite.v1.SubscriberService/Subscribe")
              .setRequestMarshaller(ProtoUtils.marshaller(SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(SubscribeResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private final BidiStreamingCallable<SubscribeRequest, SubscribeResponse> subscribeCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcSubscriberServiceStub create(SubscriberServiceStubSettings settings)
      throws IOException {
    return new GrpcSubscriberServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcSubscriberServiceStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcSubscriberServiceStub(
        SubscriberServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcSubscriberServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcSubscriberServiceStub(
        SubscriberServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcSubscriberServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcSubscriberServiceStub(
      SubscriberServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new GrpcSubscriberServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcSubscriberServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcSubscriberServiceStub(
      SubscriberServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<SubscribeRequest, SubscribeResponse> subscribeTransportSettings =
        GrpcCallSettings.<SubscribeRequest, SubscribeResponse>newBuilder()
            .setMethodDescriptor(subscribeMethodDescriptor)
            .build();

    this.subscribeCallable =
        callableFactory.createBidiStreamingCallable(
            subscribeTransportSettings, settings.subscribeSettings(), clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public BidiStreamingCallable<SubscribeRequest, SubscribeResponse> subscribeCallable() {
    return subscribeCallable;
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
