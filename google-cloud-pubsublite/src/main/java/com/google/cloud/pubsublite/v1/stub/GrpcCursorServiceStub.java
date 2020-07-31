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

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.RequestParamsExtractor;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
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
public class GrpcCursorServiceStub extends CursorServiceStub {

  private static final MethodDescriptor<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorMethodDescriptor =
          MethodDescriptor.<StreamingCommitCursorRequest, StreamingCommitCursorResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName("google.cloud.pubsublite.v1.CursorService/StreamingCommitCursor")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(StreamingCommitCursorRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(StreamingCommitCursorResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<CommitCursorRequest, CommitCursorResponse>
      commitCursorMethodDescriptor =
          MethodDescriptor.<CommitCursorRequest, CommitCursorResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.CursorService/CommitCursor")
              .setRequestMarshaller(ProtoUtils.marshaller(CommitCursorRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(CommitCursorResponse.getDefaultInstance()))
              .build();
  private static final MethodDescriptor<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsMethodDescriptor =
          MethodDescriptor.<ListPartitionCursorsRequest, ListPartitionCursorsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.CursorService/ListPartitionCursors")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListPartitionCursorsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListPartitionCursorsResponse.getDefaultInstance()))
              .build();

  private final BackgroundResource backgroundResources;

  private final BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorCallable;
  private final UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable;
  private final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable;
  private final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable;

  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcCursorServiceStub create(CursorServiceStubSettings settings)
      throws IOException {
    return new GrpcCursorServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcCursorServiceStub create(ClientContext clientContext) throws IOException {
    return new GrpcCursorServiceStub(CursorServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcCursorServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcCursorServiceStub(
        CursorServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcCursorServiceStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcCursorServiceStub(CursorServiceStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcCursorServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcCursorServiceStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcCursorServiceStub(
      CursorServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    GrpcCallSettings<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
        streamingCommitCursorTransportSettings =
            GrpcCallSettings
                .<StreamingCommitCursorRequest, StreamingCommitCursorResponse>newBuilder()
                .setMethodDescriptor(streamingCommitCursorMethodDescriptor)
                .build();
    GrpcCallSettings<CommitCursorRequest, CommitCursorResponse> commitCursorTransportSettings =
        GrpcCallSettings.<CommitCursorRequest, CommitCursorResponse>newBuilder()
            .setMethodDescriptor(commitCursorMethodDescriptor)
            .build();
    GrpcCallSettings<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
        listPartitionCursorsTransportSettings =
            GrpcCallSettings.<ListPartitionCursorsRequest, ListPartitionCursorsResponse>newBuilder()
                .setMethodDescriptor(listPartitionCursorsMethodDescriptor)
                .setParamsExtractor(
                    new RequestParamsExtractor<ListPartitionCursorsRequest>() {
                      @Override
                      public Map<String, String> extract(ListPartitionCursorsRequest request) {
                        ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
                        params.put("parent", String.valueOf(request.getParent()));
                        return params.build();
                      }
                    })
                .build();

    this.streamingCommitCursorCallable =
        callableFactory.createBidiStreamingCallable(
            streamingCommitCursorTransportSettings,
            settings.streamingCommitCursorSettings(),
            clientContext);
    this.commitCursorCallable =
        callableFactory.createUnaryCallable(
            commitCursorTransportSettings, settings.commitCursorSettings(), clientContext);
    this.listPartitionCursorsCallable =
        callableFactory.createUnaryCallable(
            listPartitionCursorsTransportSettings,
            settings.listPartitionCursorsSettings(),
            clientContext);
    this.listPartitionCursorsPagedCallable =
        callableFactory.createPagedCallable(
            listPartitionCursorsTransportSettings,
            settings.listPartitionCursorsSettings(),
            clientContext);

    backgroundResources = new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorCallable() {
    return streamingCommitCursorCallable;
  }

  public UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable() {
    return commitCursorCallable;
  }

  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable() {
    return listPartitionCursorsPagedCallable;
  }

  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable() {
    return listPartitionCursorsCallable;
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
