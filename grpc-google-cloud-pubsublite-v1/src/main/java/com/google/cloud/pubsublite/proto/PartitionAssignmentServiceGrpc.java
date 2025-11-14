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
 * The service that a subscriber client application uses to determine which
 * partitions it should connect to.
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class PartitionAssignmentServiceGrpc {

  private PartitionAssignmentServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME =
      "google.cloud.pubsublite.v1.PartitionAssignmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
          com.google.cloud.pubsublite.proto.PartitionAssignment>
      getAssignPartitionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AssignPartitions",
      requestType = com.google.cloud.pubsublite.proto.PartitionAssignmentRequest.class,
      responseType = com.google.cloud.pubsublite.proto.PartitionAssignment.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
          com.google.cloud.pubsublite.proto.PartitionAssignment>
      getAssignPartitionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
            com.google.cloud.pubsublite.proto.PartitionAssignment>
        getAssignPartitionsMethod;
    if ((getAssignPartitionsMethod = PartitionAssignmentServiceGrpc.getAssignPartitionsMethod)
        == null) {
      synchronized (PartitionAssignmentServiceGrpc.class) {
        if ((getAssignPartitionsMethod = PartitionAssignmentServiceGrpc.getAssignPartitionsMethod)
            == null) {
          PartitionAssignmentServiceGrpc.getAssignPartitionsMethod =
              getAssignPartitionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
                          com.google.cloud.pubsublite.proto.PartitionAssignment>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AssignPartitions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.PartitionAssignmentRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.PartitionAssignment
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new PartitionAssignmentServiceMethodDescriptorSupplier(
                              "AssignPartitions"))
                      .build();
        }
      }
    }
    return getAssignPartitionsMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static PartitionAssignmentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceStub>() {
          @java.lang.Override
          public PartitionAssignmentServiceStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new PartitionAssignmentServiceStub(channel, callOptions);
          }
        };
    return PartitionAssignmentServiceStub.newStub(factory, channel);
  }

  /** Creates a new blocking-style stub that supports all types of calls on the service */
  public static PartitionAssignmentServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceBlockingV2Stub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceBlockingV2Stub>() {
          @java.lang.Override
          public PartitionAssignmentServiceBlockingV2Stub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new PartitionAssignmentServiceBlockingV2Stub(channel, callOptions);
          }
        };
    return PartitionAssignmentServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PartitionAssignmentServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceBlockingStub>() {
          @java.lang.Override
          public PartitionAssignmentServiceBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new PartitionAssignmentServiceBlockingStub(channel, callOptions);
          }
        };
    return PartitionAssignmentServiceBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static PartitionAssignmentServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<PartitionAssignmentServiceFutureStub>() {
          @java.lang.Override
          public PartitionAssignmentServiceFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new PartitionAssignmentServiceFutureStub(channel, callOptions);
          }
        };
    return PartitionAssignmentServiceFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public interface AsyncService {

    /**
     *
     *
     * <pre>
     * Assign partitions for this client to handle for the specified subscription.
     * The client must send an InitialPartitionAssignmentRequest first.
     * The server will then send at most one unacknowledged PartitionAssignment
     * outstanding on the stream at a time.
     * The client should send a PartitionAssignmentAck after updating the
     * partitions it is connected to to reflect the new assignment.
     * </pre>
     */
    default io.grpc.stub.StreamObserver<
            com.google.cloud.pubsublite.proto.PartitionAssignmentRequest>
        assignPartitions(
            io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PartitionAssignment>
                responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(
          getAssignPartitionsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PartitionAssignmentService.
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public abstract static class PartitionAssignmentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return PartitionAssignmentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PartitionAssignmentService.
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public static final class PartitionAssignmentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PartitionAssignmentServiceStub> {
    private PartitionAssignmentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PartitionAssignmentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PartitionAssignmentServiceStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Assign partitions for this client to handle for the specified subscription.
     * The client must send an InitialPartitionAssignmentRequest first.
     * The server will then send at most one unacknowledged PartitionAssignment
     * outstanding on the stream at a time.
     * The client should send a PartitionAssignmentAck after updating the
     * partitions it is connected to to reflect the new assignment.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PartitionAssignmentRequest>
        assignPartitions(
            io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PartitionAssignment>
                responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getAssignPartitionsMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PartitionAssignmentService.
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public static final class PartitionAssignmentServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<PartitionAssignmentServiceBlockingV2Stub> {
    private PartitionAssignmentServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PartitionAssignmentServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PartitionAssignmentServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Assign partitions for this client to handle for the specified subscription.
     * The client must send an InitialPartitionAssignmentRequest first.
     * The server will then send at most one unacknowledged PartitionAssignment
     * outstanding on the stream at a time.
     * The client should send a PartitionAssignmentAck after updating the
     * partitions it is connected to to reflect the new assignment.
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<
            com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
            com.google.cloud.pubsublite.proto.PartitionAssignment>
        assignPartitions() {
      return io.grpc.stub.ClientCalls.blockingBidiStreamingCall(
          getChannel(), getAssignPartitionsMethod(), getCallOptions());
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service
   * PartitionAssignmentService.
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public static final class PartitionAssignmentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PartitionAssignmentServiceBlockingStub> {
    private PartitionAssignmentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PartitionAssignmentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PartitionAssignmentServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service
   * PartitionAssignmentService.
   *
   * <pre>
   * The service that a subscriber client application uses to determine which
   * partitions it should connect to.
   * </pre>
   */
  public static final class PartitionAssignmentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PartitionAssignmentServiceFutureStub> {
    private PartitionAssignmentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PartitionAssignmentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PartitionAssignmentServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_ASSIGN_PARTITIONS = 0;

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
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ASSIGN_PARTITIONS:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.assignPartitions(
                  (io.grpc.stub.StreamObserver<
                          com.google.cloud.pubsublite.proto.PartitionAssignment>)
                      responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
            getAssignPartitionsMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.PartitionAssignmentRequest,
                    com.google.cloud.pubsublite.proto.PartitionAssignment>(
                    service, METHODID_ASSIGN_PARTITIONS)))
        .build();
  }

  private abstract static class PartitionAssignmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PartitionAssignmentServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.SubscriberProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PartitionAssignmentService");
    }
  }

  private static final class PartitionAssignmentServiceFileDescriptorSupplier
      extends PartitionAssignmentServiceBaseDescriptorSupplier {
    PartitionAssignmentServiceFileDescriptorSupplier() {}
  }

  private static final class PartitionAssignmentServiceMethodDescriptorSupplier
      extends PartitionAssignmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PartitionAssignmentServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (PartitionAssignmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new PartitionAssignmentServiceFileDescriptorSupplier())
                      .addMethod(getAssignPartitionsMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
