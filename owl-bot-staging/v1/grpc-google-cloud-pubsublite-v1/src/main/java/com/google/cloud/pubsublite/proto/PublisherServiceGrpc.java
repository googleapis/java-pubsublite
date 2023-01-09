package com.google.cloud.pubsublite.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The service that a publisher client application uses to publish messages to
 * topics. Published messages are retained by the service for the duration of
 * the retention period configured for the respective topic, and are delivered
 * to subscriber clients upon request (via the `SubscriberService`).
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler",
    comments = "Source: google/cloud/pubsublite/v1/publisher.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PublisherServiceGrpc {

  private PublisherServiceGrpc() {}

  public static final String SERVICE_NAME = "google.cloud.pubsublite.v1.PublisherService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.cloud.pubsublite.proto.PublishRequest,
      com.google.cloud.pubsublite.proto.PublishResponse> getPublishMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Publish",
      requestType = com.google.cloud.pubsublite.proto.PublishRequest.class,
      responseType = com.google.cloud.pubsublite.proto.PublishResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.google.cloud.pubsublite.proto.PublishRequest,
      com.google.cloud.pubsublite.proto.PublishResponse> getPublishMethod() {
    io.grpc.MethodDescriptor<com.google.cloud.pubsublite.proto.PublishRequest, com.google.cloud.pubsublite.proto.PublishResponse> getPublishMethod;
    if ((getPublishMethod = PublisherServiceGrpc.getPublishMethod) == null) {
      synchronized (PublisherServiceGrpc.class) {
        if ((getPublishMethod = PublisherServiceGrpc.getPublishMethod) == null) {
          PublisherServiceGrpc.getPublishMethod = getPublishMethod =
              io.grpc.MethodDescriptor.<com.google.cloud.pubsublite.proto.PublishRequest, com.google.cloud.pubsublite.proto.PublishResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Publish"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.cloud.pubsublite.proto.PublishRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.cloud.pubsublite.proto.PublishResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PublisherServiceMethodDescriptorSupplier("Publish"))
              .build();
        }
      }
    }
    return getPublishMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PublisherServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PublisherServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PublisherServiceStub>() {
        @java.lang.Override
        public PublisherServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PublisherServiceStub(channel, callOptions);
        }
      };
    return PublisherServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PublisherServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PublisherServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PublisherServiceBlockingStub>() {
        @java.lang.Override
        public PublisherServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PublisherServiceBlockingStub(channel, callOptions);
        }
      };
    return PublisherServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PublisherServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PublisherServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PublisherServiceFutureStub>() {
        @java.lang.Override
        public PublisherServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PublisherServiceFutureStub(channel, callOptions);
        }
      };
    return PublisherServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The service that a publisher client application uses to publish messages to
   * topics. Published messages are retained by the service for the duration of
   * the retention period configured for the respective topic, and are delivered
   * to subscriber clients upon request (via the `SubscriberService`).
   * </pre>
   */
  public static abstract class PublisherServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Establishes a stream with the server for publishing messages. Once the
     * stream is initialized, the client publishes messages by sending publish
     * requests on the stream. The server responds with a PublishResponse for each
     * PublishRequest sent by the client, in the same order that the requests
     * were sent. Note that multiple PublishRequests can be in flight
     * simultaneously, but they will be processed by the server in the order that
     * they are sent by the client on a given stream.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PublishRequest> publish(
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PublishResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getPublishMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPublishMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                com.google.cloud.pubsublite.proto.PublishRequest,
                com.google.cloud.pubsublite.proto.PublishResponse>(
                  this, METHODID_PUBLISH)))
          .build();
    }
  }

  /**
   * <pre>
   * The service that a publisher client application uses to publish messages to
   * topics. Published messages are retained by the service for the duration of
   * the retention period configured for the respective topic, and are delivered
   * to subscriber clients upon request (via the `SubscriberService`).
   * </pre>
   */
  public static final class PublisherServiceStub extends io.grpc.stub.AbstractAsyncStub<PublisherServiceStub> {
    private PublisherServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PublisherServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PublisherServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Establishes a stream with the server for publishing messages. Once the
     * stream is initialized, the client publishes messages by sending publish
     * requests on the stream. The server responds with a PublishResponse for each
     * PublishRequest sent by the client, in the same order that the requests
     * were sent. Note that multiple PublishRequests can be in flight
     * simultaneously, but they will be processed by the server in the order that
     * they are sent by the client on a given stream.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PublishRequest> publish(
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PublishResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getPublishMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * <pre>
   * The service that a publisher client application uses to publish messages to
   * topics. Published messages are retained by the service for the duration of
   * the retention period configured for the respective topic, and are delivered
   * to subscriber clients upon request (via the `SubscriberService`).
   * </pre>
   */
  public static final class PublisherServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<PublisherServiceBlockingStub> {
    private PublisherServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PublisherServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PublisherServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * <pre>
   * The service that a publisher client application uses to publish messages to
   * topics. Published messages are retained by the service for the duration of
   * the retention period configured for the respective topic, and are delivered
   * to subscriber clients upon request (via the `SubscriberService`).
   * </pre>
   */
  public static final class PublisherServiceFutureStub extends io.grpc.stub.AbstractFutureStub<PublisherServiceFutureStub> {
    private PublisherServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PublisherServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PublisherServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_PUBLISH = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PublisherServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PublisherServiceImplBase serviceImpl, int methodId) {
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
        case METHODID_PUBLISH:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.publish(
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.PublishResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PublisherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PublisherServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.PublisherProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PublisherService");
    }
  }

  private static final class PublisherServiceFileDescriptorSupplier
      extends PublisherServiceBaseDescriptorSupplier {
    PublisherServiceFileDescriptorSupplier() {}
  }

  private static final class PublisherServiceMethodDescriptorSupplier
      extends PublisherServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PublisherServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (PublisherServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PublisherServiceFileDescriptorSupplier())
              .addMethod(getPublishMethod())
              .build();
        }
      }
    }
    return result;
  }
}
