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
 * The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.30.2)",
    comments = "Source: google/cloud/pubsublite/v1/admin.proto")
public final class AdminServiceGrpc {

  private AdminServiceGrpc() {}

  public static final String SERVICE_NAME = "google.cloud.pubsublite.v1.AdminService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getCreateTopicMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateTopic",
      requestType = com.google.cloud.pubsublite.proto.CreateTopicRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Topic.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getCreateTopicMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.CreateTopicRequest,
            com.google.cloud.pubsublite.proto.Topic>
        getCreateTopicMethod;
    if ((getCreateTopicMethod = AdminServiceGrpc.getCreateTopicMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getCreateTopicMethod = AdminServiceGrpc.getCreateTopicMethod) == null) {
          AdminServiceGrpc.getCreateTopicMethod =
              getCreateTopicMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.CreateTopicRequest,
                          com.google.cloud.pubsublite.proto.Topic>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateTopic"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.CreateTopicRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Topic.getDefaultInstance()))
                      .setSchemaDescriptor(new AdminServiceMethodDescriptorSupplier("CreateTopic"))
                      .build();
        }
      }
    }
    return getCreateTopicMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getGetTopicMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTopic",
      requestType = com.google.cloud.pubsublite.proto.GetTopicRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Topic.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getGetTopicMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.GetTopicRequest,
            com.google.cloud.pubsublite.proto.Topic>
        getGetTopicMethod;
    if ((getGetTopicMethod = AdminServiceGrpc.getGetTopicMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getGetTopicMethod = AdminServiceGrpc.getGetTopicMethod) == null) {
          AdminServiceGrpc.getGetTopicMethod =
              getGetTopicMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.GetTopicRequest,
                          com.google.cloud.pubsublite.proto.Topic>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTopic"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.GetTopicRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Topic.getDefaultInstance()))
                      .setSchemaDescriptor(new AdminServiceMethodDescriptorSupplier("GetTopic"))
                      .build();
        }
      }
    }
    return getGetTopicMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
          com.google.cloud.pubsublite.proto.TopicPartitions>
      getGetTopicPartitionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTopicPartitions",
      requestType = com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.TopicPartitions.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
          com.google.cloud.pubsublite.proto.TopicPartitions>
      getGetTopicPartitionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
            com.google.cloud.pubsublite.proto.TopicPartitions>
        getGetTopicPartitionsMethod;
    if ((getGetTopicPartitionsMethod = AdminServiceGrpc.getGetTopicPartitionsMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getGetTopicPartitionsMethod = AdminServiceGrpc.getGetTopicPartitionsMethod) == null) {
          AdminServiceGrpc.getGetTopicPartitionsMethod =
              getGetTopicPartitionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
                          com.google.cloud.pubsublite.proto.TopicPartitions>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTopicPartitions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.TopicPartitions
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("GetTopicPartitions"))
                      .build();
        }
      }
    }
    return getGetTopicPartitionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListTopicsRequest,
          com.google.cloud.pubsublite.proto.ListTopicsResponse>
      getListTopicsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListTopics",
      requestType = com.google.cloud.pubsublite.proto.ListTopicsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListTopicsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListTopicsRequest,
          com.google.cloud.pubsublite.proto.ListTopicsResponse>
      getListTopicsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListTopicsRequest,
            com.google.cloud.pubsublite.proto.ListTopicsResponse>
        getListTopicsMethod;
    if ((getListTopicsMethod = AdminServiceGrpc.getListTopicsMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getListTopicsMethod = AdminServiceGrpc.getListTopicsMethod) == null) {
          AdminServiceGrpc.getListTopicsMethod =
              getListTopicsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListTopicsRequest,
                          com.google.cloud.pubsublite.proto.ListTopicsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListTopics"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListTopicsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListTopicsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(new AdminServiceMethodDescriptorSupplier("ListTopics"))
                      .build();
        }
      }
    }
    return getListTopicsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getUpdateTopicMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateTopic",
      requestType = com.google.cloud.pubsublite.proto.UpdateTopicRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Topic.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateTopicRequest,
          com.google.cloud.pubsublite.proto.Topic>
      getUpdateTopicMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.UpdateTopicRequest,
            com.google.cloud.pubsublite.proto.Topic>
        getUpdateTopicMethod;
    if ((getUpdateTopicMethod = AdminServiceGrpc.getUpdateTopicMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getUpdateTopicMethod = AdminServiceGrpc.getUpdateTopicMethod) == null) {
          AdminServiceGrpc.getUpdateTopicMethod =
              getUpdateTopicMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.UpdateTopicRequest,
                          com.google.cloud.pubsublite.proto.Topic>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateTopic"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.UpdateTopicRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Topic.getDefaultInstance()))
                      .setSchemaDescriptor(new AdminServiceMethodDescriptorSupplier("UpdateTopic"))
                      .build();
        }
      }
    }
    return getUpdateTopicMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteTopicRequest, com.google.protobuf.Empty>
      getDeleteTopicMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteTopic",
      requestType = com.google.cloud.pubsublite.proto.DeleteTopicRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteTopicRequest, com.google.protobuf.Empty>
      getDeleteTopicMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.DeleteTopicRequest, com.google.protobuf.Empty>
        getDeleteTopicMethod;
    if ((getDeleteTopicMethod = AdminServiceGrpc.getDeleteTopicMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getDeleteTopicMethod = AdminServiceGrpc.getDeleteTopicMethod) == null) {
          AdminServiceGrpc.getDeleteTopicMethod =
              getDeleteTopicMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.DeleteTopicRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteTopic"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.DeleteTopicRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(new AdminServiceMethodDescriptorSupplier("DeleteTopic"))
                      .build();
        }
      }
    }
    return getDeleteTopicMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
          com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
      getListTopicSubscriptionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListTopicSubscriptions",
      requestType = com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
          com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
      getListTopicSubscriptionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
        getListTopicSubscriptionsMethod;
    if ((getListTopicSubscriptionsMethod = AdminServiceGrpc.getListTopicSubscriptionsMethod)
        == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getListTopicSubscriptionsMethod = AdminServiceGrpc.getListTopicSubscriptionsMethod)
            == null) {
          AdminServiceGrpc.getListTopicSubscriptionsMethod =
              getListTopicSubscriptionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
                          com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListTopicSubscriptions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("ListTopicSubscriptions"))
                      .build();
        }
      }
    }
    return getListTopicSubscriptionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getCreateSubscriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateSubscription",
      requestType = com.google.cloud.pubsublite.proto.CreateSubscriptionRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Subscription.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getCreateSubscriptionMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
            com.google.cloud.pubsublite.proto.Subscription>
        getCreateSubscriptionMethod;
    if ((getCreateSubscriptionMethod = AdminServiceGrpc.getCreateSubscriptionMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getCreateSubscriptionMethod = AdminServiceGrpc.getCreateSubscriptionMethod) == null) {
          AdminServiceGrpc.getCreateSubscriptionMethod =
              getCreateSubscriptionMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
                          com.google.cloud.pubsublite.proto.Subscription>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateSubscription"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.CreateSubscriptionRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Subscription.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("CreateSubscription"))
                      .build();
        }
      }
    }
    return getCreateSubscriptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getGetSubscriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSubscription",
      requestType = com.google.cloud.pubsublite.proto.GetSubscriptionRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Subscription.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getGetSubscriptionMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
            com.google.cloud.pubsublite.proto.Subscription>
        getGetSubscriptionMethod;
    if ((getGetSubscriptionMethod = AdminServiceGrpc.getGetSubscriptionMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getGetSubscriptionMethod = AdminServiceGrpc.getGetSubscriptionMethod) == null) {
          AdminServiceGrpc.getGetSubscriptionMethod =
              getGetSubscriptionMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
                          com.google.cloud.pubsublite.proto.Subscription>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetSubscription"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.GetSubscriptionRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Subscription.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("GetSubscription"))
                      .build();
        }
      }
    }
    return getGetSubscriptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
          com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
      getListSubscriptionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListSubscriptions",
      requestType = com.google.cloud.pubsublite.proto.ListSubscriptionsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListSubscriptionsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
          com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
      getListSubscriptionsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
            com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
        getListSubscriptionsMethod;
    if ((getListSubscriptionsMethod = AdminServiceGrpc.getListSubscriptionsMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getListSubscriptionsMethod = AdminServiceGrpc.getListSubscriptionsMethod) == null) {
          AdminServiceGrpc.getListSubscriptionsMethod =
              getListSubscriptionsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
                          com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListSubscriptions"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListSubscriptionsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListSubscriptionsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("ListSubscriptions"))
                      .build();
        }
      }
    }
    return getListSubscriptionsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getUpdateSubscriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateSubscription",
      requestType = com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Subscription.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
          com.google.cloud.pubsublite.proto.Subscription>
      getUpdateSubscriptionMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
            com.google.cloud.pubsublite.proto.Subscription>
        getUpdateSubscriptionMethod;
    if ((getUpdateSubscriptionMethod = AdminServiceGrpc.getUpdateSubscriptionMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getUpdateSubscriptionMethod = AdminServiceGrpc.getUpdateSubscriptionMethod) == null) {
          AdminServiceGrpc.getUpdateSubscriptionMethod =
              getUpdateSubscriptionMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
                          com.google.cloud.pubsublite.proto.Subscription>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateSubscription"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Subscription.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("UpdateSubscription"))
                      .build();
        }
      }
    }
    return getUpdateSubscriptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest, com.google.protobuf.Empty>
      getDeleteSubscriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteSubscription",
      requestType = com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest, com.google.protobuf.Empty>
      getDeleteSubscriptionMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest, com.google.protobuf.Empty>
        getDeleteSubscriptionMethod;
    if ((getDeleteSubscriptionMethod = AdminServiceGrpc.getDeleteSubscriptionMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getDeleteSubscriptionMethod = AdminServiceGrpc.getDeleteSubscriptionMethod) == null) {
          AdminServiceGrpc.getDeleteSubscriptionMethod =
              getDeleteSubscriptionMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteSubscription"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("DeleteSubscription"))
                      .build();
        }
      }
    }
    return getDeleteSubscriptionMethod;
  }

  /** Creates a new async stub that supports all call types for the service */
  public static AdminServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AdminServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AdminServiceStub>() {
          @java.lang.Override
          public AdminServiceStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AdminServiceStub(channel, callOptions);
          }
        };
    return AdminServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AdminServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AdminServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AdminServiceBlockingStub>() {
          @java.lang.Override
          public AdminServiceBlockingStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AdminServiceBlockingStub(channel, callOptions);
          }
        };
    return AdminServiceBlockingStub.newStub(factory, channel);
  }

  /** Creates a new ListenableFuture-style stub that supports unary calls on the service */
  public static AdminServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AdminServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AdminServiceFutureStub>() {
          @java.lang.Override
          public AdminServiceFutureStub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AdminServiceFutureStub(channel, callOptions);
          }
        };
    return AdminServiceFutureStub.newStub(factory, channel);
  }

  /**
   *
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public abstract static class AdminServiceImplBase implements io.grpc.BindableService {

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    public void createTopic(
        com.google.cloud.pubsublite.proto.CreateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    public void getTopic(
        com.google.cloud.pubsublite.proto.GetTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnimplementedUnaryCall(getGetTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    public void getTopicPartitions(
        com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.TopicPartitions>
            responseObserver) {
      asyncUnimplementedUnaryCall(getGetTopicPartitionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of topics for the given project.
     * </pre>
     */
    public void listTopics(
        com.google.cloud.pubsublite.proto.ListTopicsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListTopicsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListTopicsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    public void updateTopic(
        com.google.cloud.pubsublite.proto.UpdateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    public void deleteTopic(
        com.google.cloud.pubsublite.proto.DeleteTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * (-- aip.dev/not-precedent: ListTopicSubscriptions returns only a list of
     * names rather than full Subscription messages, as ListSubscriptions does.
     * This is consistent with the Cloud Pub/Sub API.
     * --)
     * </pre>
     */
    public void listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListTopicSubscriptionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new subscription.
     * </pre>
     */
    public void createSubscription(
        com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnimplementedUnaryCall(getCreateSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the subscription configuration.
     * </pre>
     */
    public void getSubscription(
        com.google.cloud.pubsublite.proto.GetSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnimplementedUnaryCall(getGetSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    public void listSubscriptions(
        com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
            responseObserver) {
      asyncUnimplementedUnaryCall(getListSubscriptionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified subscription.
     * </pre>
     */
    public void updateSubscription(
        com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified subscription.
     * </pre>
     */
    public void deleteSubscription(
        com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteSubscriptionMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
              getCreateTopicMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.CreateTopicRequest,
                      com.google.cloud.pubsublite.proto.Topic>(this, METHODID_CREATE_TOPIC)))
          .addMethod(
              getGetTopicMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.GetTopicRequest,
                      com.google.cloud.pubsublite.proto.Topic>(this, METHODID_GET_TOPIC)))
          .addMethod(
              getGetTopicPartitionsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
                      com.google.cloud.pubsublite.proto.TopicPartitions>(
                      this, METHODID_GET_TOPIC_PARTITIONS)))
          .addMethod(
              getListTopicsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.ListTopicsRequest,
                      com.google.cloud.pubsublite.proto.ListTopicsResponse>(
                      this, METHODID_LIST_TOPICS)))
          .addMethod(
              getUpdateTopicMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.UpdateTopicRequest,
                      com.google.cloud.pubsublite.proto.Topic>(this, METHODID_UPDATE_TOPIC)))
          .addMethod(
              getDeleteTopicMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.DeleteTopicRequest,
                      com.google.protobuf.Empty>(this, METHODID_DELETE_TOPIC)))
          .addMethod(
              getListTopicSubscriptionsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
                      com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>(
                      this, METHODID_LIST_TOPIC_SUBSCRIPTIONS)))
          .addMethod(
              getCreateSubscriptionMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
                      com.google.cloud.pubsublite.proto.Subscription>(
                      this, METHODID_CREATE_SUBSCRIPTION)))
          .addMethod(
              getGetSubscriptionMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
                      com.google.cloud.pubsublite.proto.Subscription>(
                      this, METHODID_GET_SUBSCRIPTION)))
          .addMethod(
              getListSubscriptionsMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
                      com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>(
                      this, METHODID_LIST_SUBSCRIPTIONS)))
          .addMethod(
              getUpdateSubscriptionMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
                      com.google.cloud.pubsublite.proto.Subscription>(
                      this, METHODID_UPDATE_SUBSCRIPTION)))
          .addMethod(
              getDeleteSubscriptionMethod(),
              asyncUnaryCall(
                  new MethodHandlers<
                      com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest,
                      com.google.protobuf.Empty>(this, METHODID_DELETE_SUBSCRIPTION)))
          .build();
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public static final class AdminServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AdminServiceStub> {
    private AdminServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AdminServiceStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    public void createTopic(
        com.google.cloud.pubsublite.proto.CreateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateTopicMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    public void getTopic(
        com.google.cloud.pubsublite.proto.GetTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetTopicMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    public void getTopicPartitions(
        com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.TopicPartitions>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetTopicPartitionsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of topics for the given project.
     * </pre>
     */
    public void listTopics(
        com.google.cloud.pubsublite.proto.ListTopicsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListTopicsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListTopicsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    public void updateTopic(
        com.google.cloud.pubsublite.proto.UpdateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateTopicMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    public void deleteTopic(
        com.google.cloud.pubsublite.proto.DeleteTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteTopicMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * (-- aip.dev/not-precedent: ListTopicSubscriptions returns only a list of
     * names rather than full Subscription messages, as ListSubscriptions does.
     * This is consistent with the Cloud Pub/Sub API.
     * --)
     * </pre>
     */
    public void listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListTopicSubscriptionsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new subscription.
     * </pre>
     */
    public void createSubscription(
        com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the subscription configuration.
     * </pre>
     */
    public void getSubscription(
        com.google.cloud.pubsublite.proto.GetSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    public void listSubscriptions(
        com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListSubscriptionsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified subscription.
     * </pre>
     */
    public void updateSubscription(
        com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified subscription.
     * </pre>
     */
    public void deleteSubscription(
        com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public static final class AdminServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AdminServiceBlockingStub> {
    private AdminServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AdminServiceBlockingStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic createTopic(
        com.google.cloud.pubsublite.proto.CreateTopicRequest request) {
      return blockingUnaryCall(getChannel(), getCreateTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic getTopic(
        com.google.cloud.pubsublite.proto.GetTopicRequest request) {
      return blockingUnaryCall(getChannel(), getGetTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.TopicPartitions getTopicPartitions(
        com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetTopicPartitionsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of topics for the given project.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListTopicsResponse listTopics(
        com.google.cloud.pubsublite.proto.ListTopicsRequest request) {
      return blockingUnaryCall(getChannel(), getListTopicsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic updateTopic(
        com.google.cloud.pubsublite.proto.UpdateTopicRequest request) {
      return blockingUnaryCall(getChannel(), getUpdateTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    public com.google.protobuf.Empty deleteTopic(
        com.google.cloud.pubsublite.proto.DeleteTopicRequest request) {
      return blockingUnaryCall(getChannel(), getDeleteTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * (-- aip.dev/not-precedent: ListTopicSubscriptions returns only a list of
     * names rather than full Subscription messages, as ListSubscriptions does.
     * This is consistent with the Cloud Pub/Sub API.
     * --)
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListTopicSubscriptionsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new subscription.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Subscription createSubscription(
        com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the subscription configuration.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Subscription getSubscription(
        com.google.cloud.pubsublite.proto.GetSubscriptionRequest request) {
      return blockingUnaryCall(getChannel(), getGetSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListSubscriptionsResponse listSubscriptions(
        com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListSubscriptionsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified subscription.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Subscription updateSubscription(
        com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified subscription.
     * </pre>
     */
    public com.google.protobuf.Empty deleteSubscription(
        com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteSubscriptionMethod(), getCallOptions(), request);
    }
  }

  /**
   *
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public static final class AdminServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AdminServiceFutureStub> {
    private AdminServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AdminServiceFutureStub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Topic>
        createTopic(com.google.cloud.pubsublite.proto.CreateTopicRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateTopicMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Topic>
        getTopic(com.google.cloud.pubsublite.proto.GetTopicRequest request) {
      return futureUnaryCall(getChannel().newCall(getGetTopicMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.TopicPartitions>
        getTopicPartitions(com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetTopicPartitionsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of topics for the given project.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListTopicsResponse>
        listTopics(com.google.cloud.pubsublite.proto.ListTopicsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListTopicsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Topic>
        updateTopic(com.google.cloud.pubsublite.proto.UpdateTopicRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateTopicMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteTopic(com.google.cloud.pubsublite.proto.DeleteTopicRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteTopicMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * (-- aip.dev/not-precedent: ListTopicSubscriptions returns only a list of
     * names rather than full Subscription messages, as ListSubscriptions does.
     * This is consistent with the Cloud Pub/Sub API.
     * --)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
        listTopicSubscriptions(
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListTopicSubscriptionsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new subscription.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Subscription>
        createSubscription(com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateSubscriptionMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the subscription configuration.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Subscription>
        getSubscription(com.google.cloud.pubsublite.proto.GetSubscriptionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSubscriptionMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
        listSubscriptions(com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListSubscriptionsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified subscription.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Subscription>
        updateSubscription(com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateSubscriptionMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified subscription.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteSubscription(com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteSubscriptionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_TOPIC = 0;
  private static final int METHODID_GET_TOPIC = 1;
  private static final int METHODID_GET_TOPIC_PARTITIONS = 2;
  private static final int METHODID_LIST_TOPICS = 3;
  private static final int METHODID_UPDATE_TOPIC = 4;
  private static final int METHODID_DELETE_TOPIC = 5;
  private static final int METHODID_LIST_TOPIC_SUBSCRIPTIONS = 6;
  private static final int METHODID_CREATE_SUBSCRIPTION = 7;
  private static final int METHODID_GET_SUBSCRIPTION = 8;
  private static final int METHODID_LIST_SUBSCRIPTIONS = 9;
  private static final int METHODID_UPDATE_SUBSCRIPTION = 10;
  private static final int METHODID_DELETE_SUBSCRIPTION = 11;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
          io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AdminServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AdminServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_TOPIC:
          serviceImpl.createTopic(
              (com.google.cloud.pubsublite.proto.CreateTopicRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic>)
                  responseObserver);
          break;
        case METHODID_GET_TOPIC:
          serviceImpl.getTopic(
              (com.google.cloud.pubsublite.proto.GetTopicRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic>)
                  responseObserver);
          break;
        case METHODID_GET_TOPIC_PARTITIONS:
          serviceImpl.getTopicPartitions(
              (com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.TopicPartitions>)
                  responseObserver);
          break;
        case METHODID_LIST_TOPICS:
          serviceImpl.listTopics(
              (com.google.cloud.pubsublite.proto.ListTopicsRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListTopicsResponse>)
                  responseObserver);
          break;
        case METHODID_UPDATE_TOPIC:
          serviceImpl.updateTopic(
              (com.google.cloud.pubsublite.proto.UpdateTopicRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic>)
                  responseObserver);
          break;
        case METHODID_DELETE_TOPIC:
          serviceImpl.deleteTopic(
              (com.google.cloud.pubsublite.proto.DeleteTopicRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_LIST_TOPIC_SUBSCRIPTIONS:
          serviceImpl.listTopicSubscriptions(
              (com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>)
                  responseObserver);
          break;
        case METHODID_CREATE_SUBSCRIPTION:
          serviceImpl.createSubscription(
              (com.google.cloud.pubsublite.proto.CreateSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>)
                  responseObserver);
          break;
        case METHODID_GET_SUBSCRIPTION:
          serviceImpl.getSubscription(
              (com.google.cloud.pubsublite.proto.GetSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>)
                  responseObserver);
          break;
        case METHODID_LIST_SUBSCRIPTIONS:
          serviceImpl.listSubscriptions(
              (com.google.cloud.pubsublite.proto.ListSubscriptionsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>)
                  responseObserver);
          break;
        case METHODID_UPDATE_SUBSCRIPTION:
          serviceImpl.updateSubscription(
              (com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>)
                  responseObserver);
          break;
        case METHODID_DELETE_SUBSCRIPTION:
          serviceImpl.deleteSubscription(
              (com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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

  private abstract static class AdminServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
          io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AdminServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.google.cloud.pubsublite.proto.AdminProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AdminService");
    }
  }

  private static final class AdminServiceFileDescriptorSupplier
      extends AdminServiceBaseDescriptorSupplier {
    AdminServiceFileDescriptorSupplier() {}
  }

  private static final class AdminServiceMethodDescriptorSupplier
      extends AdminServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AdminServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (AdminServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor =
              result =
                  io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                      .setSchemaDescriptor(new AdminServiceFileDescriptorSupplier())
                      .addMethod(getCreateTopicMethod())
                      .addMethod(getGetTopicMethod())
                      .addMethod(getGetTopicPartitionsMethod())
                      .addMethod(getListTopicsMethod())
                      .addMethod(getUpdateTopicMethod())
                      .addMethod(getDeleteTopicMethod())
                      .addMethod(getListTopicSubscriptionsMethod())
                      .addMethod(getCreateSubscriptionMethod())
                      .addMethod(getGetSubscriptionMethod())
                      .addMethod(getListSubscriptionsMethod())
                      .addMethod(getUpdateSubscriptionMethod())
                      .addMethod(getDeleteSubscriptionMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
