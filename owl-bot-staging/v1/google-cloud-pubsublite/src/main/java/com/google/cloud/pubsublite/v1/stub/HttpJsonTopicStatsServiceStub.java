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
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
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
 * REST stub implementation for the TopicStatsService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
@BetaApi
public class HttpJsonTopicStatsServiceStub extends TopicStatsServiceStub {
  private static final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

  private static final ApiMethodDescriptor<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsMethodDescriptor =
          ApiMethodDescriptor.<ComputeMessageStatsRequest, ComputeMessageStatsResponse>newBuilder()
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeMessageStats")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<ComputeMessageStatsRequest>newBuilder()
                      .setPath(
                          "/v1/topicStats/{topic=projects/*/locations/*/topics/*}:computeMessageStats",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeMessageStatsRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "topic", request.getTopic());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeMessageStatsRequest> serializer =
                                ProtoRestSerializer.create();
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearTopic().build()))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<ComputeMessageStatsResponse>newBuilder()
                      .setDefaultInstance(ComputeMessageStatsResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorMethodDescriptor =
          ApiMethodDescriptor.<ComputeHeadCursorRequest, ComputeHeadCursorResponse>newBuilder()
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeHeadCursor")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<ComputeHeadCursorRequest>newBuilder()
                      .setPath(
                          "/v1/topicStats/{topic=projects/*/locations/*/topics/*}:computeHeadCursor",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeHeadCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "topic", request.getTopic());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeHeadCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearTopic().build()))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<ComputeHeadCursorResponse>newBuilder()
                      .setDefaultInstance(ComputeHeadCursorResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorMethodDescriptor =
          ApiMethodDescriptor.<ComputeTimeCursorRequest, ComputeTimeCursorResponse>newBuilder()
              .setFullMethodName("google.cloud.pubsublite.v1.TopicStatsService/ComputeTimeCursor")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<ComputeTimeCursorRequest>newBuilder()
                      .setPath(
                          "/v1/topicStats/{topic=projects/*/locations/*/topics/*}:computeTimeCursor",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeTimeCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "topic", request.getTopic());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<ComputeTimeCursorRequest> serializer =
                                ProtoRestSerializer.create();
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearTopic().build()))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<ComputeTimeCursorResponse>newBuilder()
                      .setDefaultInstance(ComputeTimeCursorResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private final UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable;
  private final UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorCallable;
  private final UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorCallable;

  private final BackgroundResource backgroundResources;
  private final HttpJsonStubCallableFactory callableFactory;

  public static final HttpJsonTopicStatsServiceStub create(TopicStatsServiceStubSettings settings)
      throws IOException {
    return new HttpJsonTopicStatsServiceStub(settings, ClientContext.create(settings));
  }

  public static final HttpJsonTopicStatsServiceStub create(ClientContext clientContext)
      throws IOException {
    return new HttpJsonTopicStatsServiceStub(
        TopicStatsServiceStubSettings.newHttpJsonBuilder().build(), clientContext);
  }

  public static final HttpJsonTopicStatsServiceStub create(
      ClientContext clientContext, HttpJsonStubCallableFactory callableFactory) throws IOException {
    return new HttpJsonTopicStatsServiceStub(
        TopicStatsServiceStubSettings.newHttpJsonBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of HttpJsonTopicStatsServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonTopicStatsServiceStub(
      TopicStatsServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new HttpJsonTopicStatsServiceCallableFactory());
  }

  /**
   * Constructs an instance of HttpJsonTopicStatsServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonTopicStatsServiceStub(
      TopicStatsServiceStubSettings settings,
      ClientContext clientContext,
      HttpJsonStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    HttpJsonCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsTransportSettings =
            HttpJsonCallSettings
                .<ComputeMessageStatsRequest, ComputeMessageStatsResponse>newBuilder()
                .setMethodDescriptor(computeMessageStatsMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .build();
    HttpJsonCallSettings<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
        computeHeadCursorTransportSettings =
            HttpJsonCallSettings.<ComputeHeadCursorRequest, ComputeHeadCursorResponse>newBuilder()
                .setMethodDescriptor(computeHeadCursorMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .build();
    HttpJsonCallSettings<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
        computeTimeCursorTransportSettings =
            HttpJsonCallSettings.<ComputeTimeCursorRequest, ComputeTimeCursorResponse>newBuilder()
                .setMethodDescriptor(computeTimeCursorMethodDescriptor)
                .setTypeRegistry(typeRegistry)
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

  @InternalApi
  public static List<ApiMethodDescriptor> getMethodDescriptors() {
    List<ApiMethodDescriptor> methodDescriptors = new ArrayList<>();
    methodDescriptors.add(computeMessageStatsMethodDescriptor);
    methodDescriptors.add(computeHeadCursorMethodDescriptor);
    methodDescriptors.add(computeTimeCursorMethodDescriptor);
    return methodDescriptors;
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
