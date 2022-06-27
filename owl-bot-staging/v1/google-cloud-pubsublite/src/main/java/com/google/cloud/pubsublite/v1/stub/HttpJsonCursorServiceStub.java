/*
 * Copyright 2022 Google LLC
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

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.core.BetaApi;
import com.google.api.core.InternalApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.httpjson.ApiMethodDescriptor;
import com.google.api.gax.httpjson.HttpJsonCallSettings;
import com.google.api.gax.httpjson.HttpJsonStubCallableFactory;
import com.google.api.gax.httpjson.ProtoMessageRequestFormatter;
import com.google.api.gax.httpjson.ProtoMessageResponseParser;
import com.google.api.gax.httpjson.ProtoRestSerializer;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.protobuf.TypeRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * REST stub implementation for the CursorService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
@BetaApi
public class HttpJsonCursorServiceStub extends CursorServiceStub {
  private static final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

  private static final ApiMethodDescriptor<CommitCursorRequest, CommitCursorResponse>
      commitCursorMethodDescriptor =
          ApiMethodDescriptor.<CommitCursorRequest, CommitCursorResponse>newBuilder()
              .setFullMethodName("google.cloud.pubsublite.v1.CursorService/CommitCursor")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<CommitCursorRequest>newBuilder()
                      .setPath(
                          "/v1/cursor/{subscription=projects/*/locations/*/subscriptions/*}:commitCursor",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<CommitCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(
                                fields, "subscription", request.getSubscription());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<CommitCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearSubscription().build()))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<CommitCursorResponse>newBuilder()
                      .setDefaultInstance(CommitCursorResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<
          ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsMethodDescriptor =
          ApiMethodDescriptor
              .<ListPartitionCursorsRequest, ListPartitionCursorsResponse>newBuilder()
              .setFullMethodName("google.cloud.pubsublite.v1.CursorService/ListPartitionCursors")
              .setHttpMethod("GET")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<ListPartitionCursorsRequest>newBuilder()
                      .setPath(
                          "/v1/cursor/{parent=projects/*/locations/*/subscriptions/*}/cursors",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<ListPartitionCursorsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "parent", request.getParent());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<ListPartitionCursorsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "pageSize", request.getPageSize());
                            serializer.putQueryParam(fields, "pageToken", request.getPageToken());
                            return fields;
                          })
                      .setRequestBodyExtractor(request -> null)
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<ListPartitionCursorsResponse>newBuilder()
                      .setDefaultInstance(ListPartitionCursorsResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private final UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable;
  private final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable;
  private final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable;

  private final BackgroundResource backgroundResources;
  private final HttpJsonStubCallableFactory callableFactory;

  public static final HttpJsonCursorServiceStub create(CursorServiceStubSettings settings)
      throws IOException {
    return new HttpJsonCursorServiceStub(settings, ClientContext.create(settings));
  }

  public static final HttpJsonCursorServiceStub create(ClientContext clientContext)
      throws IOException {
    return new HttpJsonCursorServiceStub(
        CursorServiceStubSettings.newHttpJsonBuilder().build(), clientContext);
  }

  public static final HttpJsonCursorServiceStub create(
      ClientContext clientContext, HttpJsonStubCallableFactory callableFactory) throws IOException {
    return new HttpJsonCursorServiceStub(
        CursorServiceStubSettings.newHttpJsonBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of HttpJsonCursorServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonCursorServiceStub(
      CursorServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new HttpJsonCursorServiceCallableFactory());
  }

  /**
   * Constructs an instance of HttpJsonCursorServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonCursorServiceStub(
      CursorServiceStubSettings settings,
      ClientContext clientContext,
      HttpJsonStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    HttpJsonCallSettings<CommitCursorRequest, CommitCursorResponse> commitCursorTransportSettings =
        HttpJsonCallSettings.<CommitCursorRequest, CommitCursorResponse>newBuilder()
            .setMethodDescriptor(commitCursorMethodDescriptor)
            .setTypeRegistry(typeRegistry)
            .build();
    HttpJsonCallSettings<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
        listPartitionCursorsTransportSettings =
            HttpJsonCallSettings
                .<ListPartitionCursorsRequest, ListPartitionCursorsResponse>newBuilder()
                .setMethodDescriptor(listPartitionCursorsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .build();

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

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  @InternalApi
  public static List<ApiMethodDescriptor> getMethodDescriptors() {
    List<ApiMethodDescriptor> methodDescriptors = new ArrayList<>();
    methodDescriptors.add(commitCursorMethodDescriptor);
    methodDescriptors.add(listPartitionCursorsMethodDescriptor);
    return methodDescriptors;
  }

  @Override
  public UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable() {
    return commitCursorCallable;
  }

  @Override
  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable() {
    return listPartitionCursorsCallable;
  }

  @Override
  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable() {
    return listPartitionCursorsPagedCallable;
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
