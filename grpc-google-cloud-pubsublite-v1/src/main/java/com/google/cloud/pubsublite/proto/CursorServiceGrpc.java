package com.google.cloud.pubsublite.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 *
 * <pre>
 * The service that a subscriber client application uses to manage committed
 * cursors while receiving messsages. A cursor represents a subscriber's
 * progress within a topic partition for a given subscription.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.30.2)",
    comments = "Source: google/cloud/pubsublite/v1/cursor.proto")
public final class CursorServiceGrpc {

  private CursorServiceGrpc() {}

  public static final String SERVICE_NAME = "google.cloud.pubsublite.v1.CursorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest,
          com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
      getStreamingCommitCursorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamingCommitCursor",
      requestType = com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest.class,
      responseType = com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest,
          com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
      getStreamingCommitCursorMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest,
            com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
        getStreamingCommitCursorMethod;
    if ((getStreamingCommitCursorMethod = CursorServiceGrpc.getStreamingCommitCursorMethod)
        == null) {
      synchronized (CursorServiceGrpc.class) {
        if ((getStreamingCommitCursorMethod = CursorServiceGrpc.getStreamingCommitCursorMethod)
            == null) {
          CursorServiceGrpc.getStreamingCommitCursorMethod =
              getStreamingCommitCursorMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest,
                          com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "StreamingCommitCursor"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new CursorServiceMethodDescriptorSupplier("StreamingCommitCursor"))
                      .build();
        }
      }
    }
    return getStreamingCommitCursorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CommitCursorRequest,
          com.google.cloud.pubsublite.proto.CommitCursorResponse>
      getCommitCursorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CommitCursor",
      requestType = com.google.cloud.pubsublite.proto.CommitCursorRequest.class,
      responseType = com.google.cloud.pubsublite.proto.CommitCursorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CommitCursorRequest,
          com.google.cloud.pubsublite.proto.CommitCursorResponse>
      getCommitCursorMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.CommitCursorRequest,
            com.google.cloud.pubsublite.proto.CommitCursorResponse>
        getCommitCursorMethod;
    if ((getCommitCursorMethod = CursorServiceGrpc.getCommitCursorMethod) == null) {
      synchronized (CursorServiceGrpc.class) {
        if ((getCommitCursorMethod = CursorServiceGrpc.getCommitCursorMethod) == null) {
          CursorServiceGrpc.getCommitCursorMethod =
              getCommitCursorMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.CommitCursorRequest,
                          com.google.cloud.pubsublite.proto.CommitCursorResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CommitCursor"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.CommitCursorRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.CommitCursorResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new CursorServiceMethodDescriptorSupplier("CommitCursor"))
                      .build();
        }
      }
    }
    return getCommitCursorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest,
          com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
      getListPartitionCursorsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListPartitionCursors",
      requestType = com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest,
          com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
      getListPartitionCursorsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest,
            com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
        getListPartitionCursorsMethod;
    if ((getListPartitionCursorsMethod = CursorServiceGrpc.getListPartitionCursorsMethod) == null) {
      synchronized (CursorServiceGrpc.class) {
        if ((getListPartitionCursorsMethod = CursorServiceGrpc.getListPartitionCursorsMethod)
            == null) {
          CursorServiceGrpc.getListPartitionCursorsMethod =
              getListPartitionCursorsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest,
                          com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListPartitionCursors"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new CursorServiceMethodDescriptorSupplier("ListPartitionCursors"))
                      .build();
        }
      }
    }
    return getListPartitionCursorsMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static CursorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CursorServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<CursorServiceStub>() {
          @java.lang.Override
          public CursorServiceStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CursorServiceStub(channel, callOptions);
          }
        };
    return CursorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CursorServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CursorServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<CursorServiceBlockingStub>() {
          @java.lang.Override
          public CursorServiceBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CursorServiceBlockingStub(channel, callOptions);
          }
        };
    return CursorServiceBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static CursorServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CursorServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<CursorServiceFutureStub>() {
          @java.lang.Override
          public CursorServiceFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CursorServiceFutureStub(channel, callOptions);
          }
        };
    return CursorServiceFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to manage committed
   * cursors while receiving messsages. A cursor represents a subscriber's
   * progress within a topic partition for a given subscription.
   * </pre>
   */
  public abstract static class CursorServiceImplBase implements io.grpc.BindableService {

    /**
     *
     *
     * <pre>
     * Establishes a stream with the server for managing committed cursors.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<
            com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest>
        streamingCommitCursor(
            io.grpc.stub.StreamObserver<
                    com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
                responseObserver) {
      return asyncUnimplementedStreamingCall(getStreamingCommitCursorMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates the committed cursor.
     * </pre>
     */
    public void commitCursor(
        com.google.cloud.pubsublite.proto.CommitCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.CommitCursorResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getCommitCursorMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns all committed cursor information for a subscription.
     * </pre>
     */
    public void listPartitionCursors(
        com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListPartitionCursorsMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getStreamingCommitCursorMethod(),
              asyncBidiStreamingCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest,
                      com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>(
                      this, METHODID_STREAMING_COMMIT_CURSOR)))
          .addMethod(
              getCommitCursorMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.CommitCursorRequest,
                      com.google.cloud.pubsublite.proto.CommitCursorResponse>(
                      this, METHODID_COMMIT_CURSOR)))
          .addMethod(
              getListPartitionCursorsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest,
                      com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>(
                      this, METHODID_LIST_PARTITION_CURSORS)))
          .build();
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to manage committed
   * cursors while receiving messsages. A cursor represents a subscriber's
   * progress within a topic partition for a given subscription.
   * </pre>
   */
  public static final class CursorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CursorServiceStub> {
    private CursorServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CursorServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CursorServiceStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Establishes a stream with the server for managing committed cursors.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<
            com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest>
        streamingCommitCursor(
            io.grpc.stub.StreamObserver<
                    com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>
                responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getStreamingCommitCursorMethod(), getCallOptions()),
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates the committed cursor.
     * </pre>
     */
    public void commitCursor(
        com.google.cloud.pubsublite.proto.CommitCursorRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.CommitCursorResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCommitCursorMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns all committed cursor information for a subscription.
     * </pre>
     */
    public void listPartitionCursors(
        com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListPartitionCursorsMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to manage committed
   * cursors while receiving messsages. A cursor represents a subscriber's
   * progress within a topic partition for a given subscription.
   * </pre>
   */
  public static final class CursorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CursorServiceBlockingStub> {
    private CursorServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CursorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CursorServiceBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Updates the committed cursor.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.CommitCursorResponse commitCursor(
        com.google.cloud.pubsublite.proto.CommitCursorRequest request) {
      return blockingUnaryCall(getChannel(), getCommitCursorMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns all committed cursor information for a subscription.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse listPartitionCursors(
        com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListPartitionCursorsMethod(), getCallOptions(), request);
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to manage committed
   * cursors while receiving messsages. A cursor represents a subscriber's
   * progress within a topic partition for a given subscription.
   * </pre>
   */
  public static final class CursorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CursorServiceFutureStub> {
    private CursorServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CursorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CursorServiceFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Updates the committed cursor.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.CommitCursorResponse>
        commitCursor(com.google.cloud.pubsublite.proto.CommitCursorRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCommitCursorMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns all committed cursor information for a subscription.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>
        listPartitionCursors(
            com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListPartitionCursorsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_COMMIT_CURSOR = 0;
  private static final int METHODID_LIST_PARTITION_CURSORS = 1;
  private static final int METHODID_STREAMING_COMMIT_CURSOR = 2;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CursorServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CursorServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_COMMIT_CURSOR:
          serviceImpl.commitCursor(
              (com.google.cloud.pubsublite.proto.CommitCursorRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.CommitCursorResponse>)
                  responseObserver);
          break;
        case METHODID_LIST_PARTITION_CURSORS:
          serviceImpl.listPartitionCursors(
              (com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse>)
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
        case METHODID_STREAMING_COMMIT_CURSOR:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.streamingCommitCursor(
                  (io.grpc.stub.StreamObserver<
                          com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse>)
                      responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private abstract static class CursorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CursorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.CursorProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CursorService");
    }
  }

  private static final class CursorServiceFileDescriptorSupplier
      extends CursorServiceBaseDescriptorSupplier {
    CursorServiceFileDescriptorSupplier() {}
  }

  private static final class CursorServiceMethodDescriptorSupplier
      extends CursorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CursorServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (CursorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new CursorServiceFileDescriptorSupplier())
                      .addMethod(getStreamingCommitCursorMethod())
                      .addMethod(getCommitCursorMethod())
                      .addMethod(getListPartitionCursorsMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
