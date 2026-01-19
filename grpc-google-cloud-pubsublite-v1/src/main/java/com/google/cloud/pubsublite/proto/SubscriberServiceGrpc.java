/*
 * Copyright 2026 Google LLC
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
 * The service that a subscriber client application uses to receive messages
 * from subscriptions.
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class SubscriberServiceGrpc {

  private SubscriberServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME =
      "google.cloud.pubsublite.v1.SubscriberService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.SubscribeRequest,
          com.google.cloud.pubsublite.proto.SubscribeResponse>
      getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = com.google.cloud.pubsublite.proto.SubscribeRequest.class,
      responseType = com.google.cloud.pubsublite.proto.SubscribeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.SubscribeRequest,
          com.google.cloud.pubsublite.proto.SubscribeResponse>
      getSubscribeMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.SubscribeRequest,
            com.google.cloud.pubsublite.proto.SubscribeResponse>
        getSubscribeMethod;
    if ((getSubscribeMethod = SubscriberServiceGrpc.getSubscribeMethod) == null) {
      synchronized (SubscriberServiceGrpc.class) {
        if ((getSubscribeMethod = SubscriberServiceGrpc.getSubscribeMethod) == null) {
          SubscriberServiceGrpc.getSubscribeMethod =
              getSubscribeMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.SubscribeRequest,
                          com.google.cloud.pubsublite.proto.SubscribeResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Subscribe"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.SubscribeRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.SubscribeResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new SubscriberServiceMethodDescriptorSupplier("Subscribe"))
                      .build();
        }
      }
    }
    return getSubscribeMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static SubscriberServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceStub>() {
          @java.lang.Override
          public SubscriberServiceStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new SubscriberServiceStub(channel, callOptions);
          }
        };
    return SubscriberServiceStub.newStub(factory, channel);
  }

  /** Creates a new blocking-style stub that supports all types of calls on the service */
  public static SubscriberServiceBlockingV2Stub newBlockingV2Stub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceBlockingV2Stub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceBlockingV2Stub>() {
          @java.lang.Override
          public SubscriberServiceBlockingV2Stub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new SubscriberServiceBlockingV2Stub(channel, callOptions);
          }
        };
    return SubscriberServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SubscriberServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceBlockingStub>() {
          @java.lang.Override
          public SubscriberServiceBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new SubscriberServiceBlockingStub(channel, callOptions);
          }
        };
    return SubscriberServiceBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static SubscriberServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<SubscriberServiceFutureStub>() {
          @java.lang.Override
          public SubscriberServiceFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new SubscriberServiceFutureStub(channel, callOptions);
          }
        };
    return SubscriberServiceFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public interface AsyncService {

    /**
     *
     *
     * <pre>
     * Establishes a stream with the server for receiving messages.
     * </pre>
     */
    default io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.SubscribeRequest>
        subscribe(
            io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.SubscribeResponse>
                responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(
          getSubscribeMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SubscriberService.
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public abstract static class SubscriberServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return SubscriberServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SubscriberService.
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public static final class SubscriberServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SubscriberServiceStub> {
    private SubscriberServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriberServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriberServiceStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Establishes a stream with the server for receiving messages.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.SubscribeRequest>
        subscribe(
            io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.SubscribeResponse>
                responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SubscriberService.
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public static final class SubscriberServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<SubscriberServiceBlockingV2Stub> {
    private SubscriberServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriberServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriberServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Establishes a stream with the server for receiving messages.
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<
            com.google.cloud.pubsublite.proto.SubscribeRequest,
            com.google.cloud.pubsublite.proto.SubscribeResponse>
        subscribe() {
      return io.grpc.stub.ClientCalls.blockingBidiStreamingCall(
          getChannel(), getSubscribeMethod(), getCallOptions());
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service SubscriberService.
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public static final class SubscriberServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SubscriberServiceBlockingStub> {
    private SubscriberServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriberServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriberServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SubscriberService.
   *
   * <pre>
   * The service that a subscriber client application uses to receive messages
   * from subscriptions.
   * </pre>
   */
  public static final class SubscriberServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SubscriberServiceFutureStub> {
    private SubscriberServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscriberServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SubscriberServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;

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
        case METHODID_SUBSCRIBE:
          return (io.grpc.stub.StreamObserver<Req>)
              serviceImpl.subscribe(
                  (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.SubscribeResponse>)
                      responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
            getSubscribeMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.SubscribeRequest,
                    com.google.cloud.pubsublite.proto.SubscribeResponse>(
                    service, METHODID_SUBSCRIBE)))
        .build();
  }

  private abstract static class SubscriberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SubscriberServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.SubscriberProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SubscriberService");
    }
  }

  private static final class SubscriberServiceFileDescriptorSupplier
      extends SubscriberServiceBaseDescriptorSupplier {
    SubscriberServiceFileDescriptorSupplier() {}
  }

  private static final class SubscriberServiceMethodDescriptorSupplier
      extends SubscriberServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    SubscriberServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (SubscriberServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new SubscriberServiceFileDescriptorSupplier())
                      .addMethod(getSubscribeMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
