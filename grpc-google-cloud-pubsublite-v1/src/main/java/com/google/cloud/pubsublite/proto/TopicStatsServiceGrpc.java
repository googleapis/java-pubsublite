package com.google.cloud.pubsublite.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 *
 *
 * <pre>
 * This service allows users to get stats about messages in their topic.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.30.2)",
    comments = "Source: google/cloud/pubsublite/v1/topic_stats.proto")
public final class TopicStatsServiceGrpc {

  private TopicStatsServiceGrpc() {}

  public static final String SERVICE_NAME = "google.cloud.pubsublite.v1.TopicStatsService";

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
  public abstract static class TopicStatsServiceImplBase implements io.grpc.BindableService {

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
      asyncUnimplementedUnaryCall(getComputeMessageStatsMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getComputeMessageStatsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest,
                      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse>(
                      this, METHODID_COMPUTE_MESSAGE_STATS)))
          .build();
    }
  }

  /**
   *
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
      asyncUnaryCall(
          getChannel().newCall(getComputeMessageStatsMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   *
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
      return blockingUnaryCall(
          getChannel(), getComputeMessageStatsMethod(), getCallOptions(), request);
    }
  }

  /**
   *
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
      return futureUnaryCall(
          getChannel().newCall(getComputeMessageStatsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_COMPUTE_MESSAGE_STATS = 0;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TopicStatsServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TopicStatsServiceImplBase serviceImpl, int methodId) {
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
    private final String methodName;

    TopicStatsServiceMethodDescriptorSupplier(String methodName) {
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
                      .build();
        }
      }
    }
    return result;
  }
}
