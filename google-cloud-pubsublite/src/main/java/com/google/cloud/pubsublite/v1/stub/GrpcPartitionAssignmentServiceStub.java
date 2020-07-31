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
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
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
public class GrpcPartitionAssignmentServiceStub extends PartitionAssignmentServiceStub {

  private static final MethodDescriptor<PartitionAssignmentRequest, PartitionAssignment>
      assignPartitionsMethodDescriptor =
          MethodDescriptor.<PartitionAssignmentRequest, PartitionAssignment>newBuilder()
              .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(
                  "google.cloud.pubsublite.v1.PartitionAssignmentService/AssignPartitions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(PartitionAssignmentRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(PartitionAssignment.getDefaultInstance()))
              .build();

  private final BackgroundResource backgroundResources;

  private final BidiStreamingCallable<PartitionAssignmentRequest, PartitionAssignment>
      assignPartitionsCallable;

  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcPartitionAssignmentServiceStub create(
      PartitionAssignmentServiceStubSettings settings) throws IOException {
    return new GrpcPartitionAssignmentServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcPartitionAssignmentServiceStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcPartitionAssignmentServiceStub(
        PartitionAssignmentServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcPartitionAssignmentServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcPartitionAssignmentServiceStub(
        PartitionAssignmentServiceStubSettings.newBuilder().build(),
        clientContext,
        callableFactory);
  }

  /**
   * Constructs an instance of GrpcPartitionAssignmentServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcPartitionAssignmentServiceStub(
      PartitionAssignmentServiceStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcPartitionAssignmentServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcPartitionAssignmentServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected GrpcPartitionAssignmentServiceStub(
      PartitionAssignmentServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    GrpcCallSettings<PartitionAssignmentRequest, PartitionAssignment>
        assignPartitionsTransportSettings =
            GrpcCallSettings.<PartitionAssignmentRequest, PartitionAssignment>newBuilder()
                .setMethodDescriptor(assignPartitionsMethodDescriptor)
                .build();

    this.assignPartitionsCallable =
        callableFactory.createBidiStreamingCallable(
            assignPartitionsTransportSettings, settings.assignPartitionsSettings(), clientContext);

    backgroundResources = new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public BidiStreamingCallable<PartitionAssignmentRequest, PartitionAssignment>
      assignPartitionsCallable() {
    return assignPartitionsCallable;
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
