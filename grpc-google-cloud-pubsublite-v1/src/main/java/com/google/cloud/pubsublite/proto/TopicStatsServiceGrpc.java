/*
 * Copyright 2025 Google LLC
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
package com.google.cloud.pubsublite.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 *
 *
 * <pre>
 * This service allows users to get stats about messages in their topic.
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class TopicStatsServiceGrpc {

  private TopicStatsServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME =
      "google.cloud.pubsublite.v1.TopicStatsService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
          com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
      getComputeMessageStatsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ComputeMessageStats",
      requestType = com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
          com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
      getComputeMessageStatsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
            com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
        getComputeMessageStatsMethod;
    if ((getComputeMessageStatsMethod = TopicStatsServiceGrpc.getComputeMessageStatsMethod)
        == null) {
      synchronized (TopicStatsServiceGrpc.class) {
        if ((getComputeMessageStatsMethod = TopicStatsServiceGrpc.getComputeMessageStatsMethod)
            == null) {
          TopicStatsServiceGrpc.getComputeMessageStatsMethod =
              getComputeMessageStatsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
                          com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ComputeMessageStats"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new TopicStatsServiceMethodDescriptorSupplier("ComputeMessageStats"))
                      .build();
        }
      }
    }
    return getComputeMessageStatsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest,
          com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
      getComputeHeadCursorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ComputeHeadCursor",
      requestType = com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest,
          com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
      getComputeHeadCursorMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest,
            com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
        getComputeHeadCursorMethod;
    if ((getComputeHeadCursorMethod = TopicStatsServiceGrpc.getComputeHeadCursorMethod) == null) {
      synchronized (TopicStatsServiceGrpc.class) {
        if ((getComputeHeadCursorMethod = TopicStatsServiceGrpc.getComputeHeadCursorMethod)
            == null) {
          TopicStatsServiceGrpc.getComputeHeadCursorMethod =
              getComputeHeadCursorMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest,
                          com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ComputeHeadCursor"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new TopicStatsServiceMethodDescriptorSupplier("ComputeHeadCursor"))
                      .build();
        }
      }
    }
    return getComputeHeadCursorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest,
          com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
      getComputeTimeCursorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ComputeTimeCursor",
      requestType = com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest,
          com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
      getComputeTimeCursorMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest,
            com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
        getComputeTimeCursorMethod;
    if ((getComputeTimeCursorMethod = TopicStatsServiceGrpc.getComputeTimeCursorMethod) == null) {
      synchronized (TopicStatsServiceGrpc.class) {
        if ((getComputeTimeCursorMethod = TopicStatsServiceGrpc.getComputeTimeCursorMethod)
            == null) {
          TopicStatsServiceGrpc.getComputeTimeCursorMethod =
              getComputeTimeCursorMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest,
                          com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ComputeTimeCursor"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new TopicStatsServiceMethodDescriptorSupplier("ComputeTimeCursor"))
                      .build();
        }
      }
    }
    return getComputeTimeCursorMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static TopicStatsServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceStub>() {
          @java.lang.Override
          public TopicStatsServiceStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TopicStatsServiceStub(channel, callOptions);
          }
        };
    return TopicStatsServiceStub.newStub(factory, channel);
  }

  /** Creates a new blocking-style stub that supports all types of calls on the service */
  public static TopicStatsServiceBlockingV2Stub newBlockingV2Stub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceBlockingV2Stub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceBlockingV2Stub>() {
          @java.lang.Override
          public TopicStatsServiceBlockingV2Stub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TopicStatsServiceBlockingV2Stub(channel, callOptions);
          }
        };
    return TopicStatsServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TopicStatsServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceBlockingStub>() {
          @java.lang.Override
          public TopicStatsServiceBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TopicStatsServiceBlockingStub(channel, callOptions);
          }
        };
    return TopicStatsServiceBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static TopicStatsServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TopicStatsServiceFutureStub>() {
          @java.lang.Override
          public TopicStatsServiceFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new TopicStatsServiceFutureStub(channel, callOptions);
          }
        };
    return TopicStatsServiceFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public interface AsyncService {

    /**
     *
     *
     * <pre>
     * Compute statistics about a range of messages in a given topic and
     * partition.
     * </pre>
     */
    default void computeMessageStats(
        com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getComputeMessageStatsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Compute the head cursor for the partition.
     * The head cursor's offset is guaranteed to be less than or equal to all
     * messages which have not yet been acknowledged as published, and
     * greater than the offset of any message whose publish has already
     * been acknowledged. It is zero if there have never been messages in the
     * partition.
     * </pre>
     */
    default void computeHeadCursor(
        com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getComputeHeadCursorMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Compute the corresponding cursor for a publish or event time in a topic
     * partition.
     * </pre>
     */
    default void computeTimeCursor(
        com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getComputeTimeCursorMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service TopicStatsService.
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public abstract static class TopicStatsServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return TopicStatsServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service TopicStatsService.
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public static final class TopicStatsServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TopicStatsServiceStub> {
    private TopicStatsServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TopicStatsServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TopicStatsServiceStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Compute statistics about a range of messages in a given topic and
     * partition.
     * </pre>
     */
    public void computeMessageStats(
        com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getComputeMessageStatsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Compute the head cursor for the partition.
     * The head cursor's offset is guaranteed to be less than or equal to all
     * messages which have not yet been acknowledged as published, and
     * greater than the offset of any message whose publish has already
     * been acknowledged. It is zero if there have never been messages in the
     * partition.
     * </pre>
     */
    public void computeHeadCursor(
        com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getComputeHeadCursorMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Compute the corresponding cursor for a publish or event time in a topic
     * partition.
     * </pre>
     */
    public void computeTimeCursor(
        com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getComputeTimeCursorMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service TopicStatsService.
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public static final class TopicStatsServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<TopicStatsServiceBlockingV2Stub> {
    private TopicStatsServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TopicStatsServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TopicStatsServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Compute statistics about a range of messages in a given topic and
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse computeMessageStats(
        com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getComputeMessageStatsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the head cursor for the partition.
     * The head cursor's offset is guaranteed to be less than or equal to all
     * messages which have not yet been acknowledged as published, and
     * greater than the offset of any message whose publish has already
     * been acknowledged. It is zero if there have never been messages in the
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse computeHeadCursor(
        com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getComputeHeadCursorMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the corresponding cursor for a publish or event time in a topic
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse computeTimeCursor(
        com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getComputeTimeCursorMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service TopicStatsService.
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public static final class TopicStatsServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TopicStatsServiceBlockingStub> {
    private TopicStatsServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TopicStatsServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TopicStatsServiceBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Compute statistics about a range of messages in a given topic and
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse computeMessageStats(
        com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getComputeMessageStatsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the head cursor for the partition.
     * The head cursor's offset is guaranteed to be less than or equal to all
     * messages which have not yet been acknowledged as published, and
     * greater than the offset of any message whose publish has already
     * been acknowledged. It is zero if there have never been messages in the
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse computeHeadCursor(
        com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getComputeHeadCursorMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the corresponding cursor for a publish or event time in a topic
     * partition.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse computeTimeCursor(
        com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getComputeTimeCursorMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service TopicStatsService.
   *
   * <pre>
   * This service allows users to get stats about messages in their topic.
   * </pre>
   */
  public static final class TopicStatsServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TopicStatsServiceFutureStub> {
    private TopicStatsServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TopicStatsServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TopicStatsServiceFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Compute statistics about a range of messages in a given topic and
     * partition.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>
        computeMessageStats(com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getComputeMessageStatsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the head cursor for the partition.
     * The head cursor's offset is guaranteed to be less than or equal to all
     * messages which have not yet been acknowledged as published, and
     * greater than the offset of any message whose publish has already
     * been acknowledged. It is zero if there have never been messages in the
     * partition.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>
        computeHeadCursor(com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getComputeHeadCursorMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Compute the corresponding cursor for a publish or event time in a topic
     * partition.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>
        computeTimeCursor(com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getComputeTimeCursorMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_COMPUTE_MESSAGE_STATS = 0;
  private static final int METHODID_COMPUTE_HEAD_CURSOR = 1;
  private static final int METHODID_COMPUTE_TIME_CURSOR = 2;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_COMPUTE_MESSAGE_STATS:
          serviceImpl.computeMessageStats(
              (com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>)
                  responseObserver);
          break;
        case METHODID_COMPUTE_HEAD_CURSOR:
          serviceImpl.computeHeadCursor(
              (com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>)
                  responseObserver);
          break;
        case METHODID_COMPUTE_TIME_CURSOR:
          serviceImpl.computeTimeCursor(
              (com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>)
                  responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
            getComputeMessageStatsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
                    com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>(
                    service, METHODID_COMPUTE_MESSAGE_STATS)))
        .addMethod(
            getComputeHeadCursorMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest,
                    com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse>(
                    service, METHODID_COMPUTE_HEAD_CURSOR)))
        .addMethod(
            getComputeTimeCursorMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest,
                    com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse>(
                    service, METHODID_COMPUTE_TIME_CURSOR)))
        .build();
  }

  private abstract static class TopicStatsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TopicStatsServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.TopicStatsProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TopicStatsService");
    }
  }

  private static final class TopicStatsServiceFileDescriptorSupplier
      extends TopicStatsServiceBaseDescriptorSupplier {
    TopicStatsServiceFileDescriptorSupplier() {}
  }

  private static final class TopicStatsServiceMethodDescriptorSupplier
      extends TopicStatsServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    TopicStatsServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TopicStatsServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new TopicStatsServiceFileDescriptorSupplier())
                      .addMethod(getComputeMessageStatsMethod())
                      .addMethod(getComputeHeadCursorMethod())
                      .addMethod(getComputeTimeCursorMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
