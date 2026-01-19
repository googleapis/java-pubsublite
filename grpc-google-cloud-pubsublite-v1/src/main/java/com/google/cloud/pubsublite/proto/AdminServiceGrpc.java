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
 * The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class AdminServiceGrpc {

  private AdminServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "google.cloud.pubsublite.v1.AdminService";

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

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.SeekSubscriptionRequest,
          com.google.longrunning.Operation>
      getSeekSubscriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SeekSubscription",
      requestType = com.google.cloud.pubsublite.proto.SeekSubscriptionRequest.class,
      responseType = com.google.longrunning.Operation.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.SeekSubscriptionRequest,
          com.google.longrunning.Operation>
      getSeekSubscriptionMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.SeekSubscriptionRequest,
            com.google.longrunning.Operation>
        getSeekSubscriptionMethod;
    if ((getSeekSubscriptionMethod = AdminServiceGrpc.getSeekSubscriptionMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getSeekSubscriptionMethod = AdminServiceGrpc.getSeekSubscriptionMethod) == null) {
          AdminServiceGrpc.getSeekSubscriptionMethod =
              getSeekSubscriptionMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.SeekSubscriptionRequest,
                          com.google.longrunning.Operation>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SeekSubscription"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.SeekSubscriptionRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.longrunning.Operation.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("SeekSubscription"))
                      .build();
        }
      }
    }
    return getSeekSubscriptionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getCreateReservationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateReservation",
      requestType = com.google.cloud.pubsublite.proto.CreateReservationRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Reservation.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.CreateReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getCreateReservationMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.CreateReservationRequest,
            com.google.cloud.pubsublite.proto.Reservation>
        getCreateReservationMethod;
    if ((getCreateReservationMethod = AdminServiceGrpc.getCreateReservationMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getCreateReservationMethod = AdminServiceGrpc.getCreateReservationMethod) == null) {
          AdminServiceGrpc.getCreateReservationMethod =
              getCreateReservationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.CreateReservationRequest,
                          com.google.cloud.pubsublite.proto.Reservation>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateReservation"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.CreateReservationRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Reservation.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("CreateReservation"))
                      .build();
        }
      }
    }
    return getCreateReservationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getGetReservationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetReservation",
      requestType = com.google.cloud.pubsublite.proto.GetReservationRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Reservation.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.GetReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getGetReservationMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.GetReservationRequest,
            com.google.cloud.pubsublite.proto.Reservation>
        getGetReservationMethod;
    if ((getGetReservationMethod = AdminServiceGrpc.getGetReservationMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getGetReservationMethod = AdminServiceGrpc.getGetReservationMethod) == null) {
          AdminServiceGrpc.getGetReservationMethod =
              getGetReservationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.GetReservationRequest,
                          com.google.cloud.pubsublite.proto.Reservation>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetReservation"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.GetReservationRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Reservation.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("GetReservation"))
                      .build();
        }
      }
    }
    return getGetReservationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListReservationsRequest,
          com.google.cloud.pubsublite.proto.ListReservationsResponse>
      getListReservationsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListReservations",
      requestType = com.google.cloud.pubsublite.proto.ListReservationsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListReservationsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListReservationsRequest,
          com.google.cloud.pubsublite.proto.ListReservationsResponse>
      getListReservationsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListReservationsRequest,
            com.google.cloud.pubsublite.proto.ListReservationsResponse>
        getListReservationsMethod;
    if ((getListReservationsMethod = AdminServiceGrpc.getListReservationsMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getListReservationsMethod = AdminServiceGrpc.getListReservationsMethod) == null) {
          AdminServiceGrpc.getListReservationsMethod =
              getListReservationsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListReservationsRequest,
                          com.google.cloud.pubsublite.proto.ListReservationsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListReservations"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListReservationsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListReservationsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("ListReservations"))
                      .build();
        }
      }
    }
    return getListReservationsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getUpdateReservationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateReservation",
      requestType = com.google.cloud.pubsublite.proto.UpdateReservationRequest.class,
      responseType = com.google.cloud.pubsublite.proto.Reservation.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.UpdateReservationRequest,
          com.google.cloud.pubsublite.proto.Reservation>
      getUpdateReservationMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.UpdateReservationRequest,
            com.google.cloud.pubsublite.proto.Reservation>
        getUpdateReservationMethod;
    if ((getUpdateReservationMethod = AdminServiceGrpc.getUpdateReservationMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getUpdateReservationMethod = AdminServiceGrpc.getUpdateReservationMethod) == null) {
          AdminServiceGrpc.getUpdateReservationMethod =
              getUpdateReservationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.UpdateReservationRequest,
                          com.google.cloud.pubsublite.proto.Reservation>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateReservation"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.UpdateReservationRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.Reservation.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("UpdateReservation"))
                      .build();
        }
      }
    }
    return getUpdateReservationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteReservationRequest, com.google.protobuf.Empty>
      getDeleteReservationMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteReservation",
      requestType = com.google.cloud.pubsublite.proto.DeleteReservationRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.DeleteReservationRequest, com.google.protobuf.Empty>
      getDeleteReservationMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.DeleteReservationRequest, com.google.protobuf.Empty>
        getDeleteReservationMethod;
    if ((getDeleteReservationMethod = AdminServiceGrpc.getDeleteReservationMethod) == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getDeleteReservationMethod = AdminServiceGrpc.getDeleteReservationMethod) == null) {
          AdminServiceGrpc.getDeleteReservationMethod =
              getDeleteReservationMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.DeleteReservationRequest,
                          com.google.protobuf.Empty>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteReservation"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.DeleteReservationRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.protobuf.Empty.getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("DeleteReservation"))
                      .build();
        }
      }
    }
    return getDeleteReservationMethod;
  }

  private static volatile io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListReservationTopicsRequest,
          com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
      getListReservationTopicsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListReservationTopics",
      requestType = com.google.cloud.pubsublite.proto.ListReservationTopicsRequest.class,
      responseType = com.google.cloud.pubsublite.proto.ListReservationTopicsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<
          com.google.cloud.pubsublite.proto.ListReservationTopicsRequest,
          com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
      getListReservationTopicsMethod() {
    io.grpc.MethodDescriptor<
            com.google.cloud.pubsublite.proto.ListReservationTopicsRequest,
            com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
        getListReservationTopicsMethod;
    if ((getListReservationTopicsMethod = AdminServiceGrpc.getListReservationTopicsMethod)
        == null) {
      synchronized (AdminServiceGrpc.class) {
        if ((getListReservationTopicsMethod = AdminServiceGrpc.getListReservationTopicsMethod)
            == null) {
          AdminServiceGrpc.getListReservationTopicsMethod =
              getListReservationTopicsMethod =
                  io.grpc.MethodDescriptor
                      .<com.google.cloud.pubsublite.proto.ListReservationTopicsRequest,
                          com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
                          newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "ListReservationTopics"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListReservationTopicsRequest
                                  .getDefaultInstance()))
                      .setResponseMarshaller(
                          io.grpc.protobuf.ProtoUtils.marshaller(
                              com.google.cloud.pubsublite.proto.ListReservationTopicsResponse
                                  .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AdminServiceMethodDescriptorSupplier("ListReservationTopics"))
                      .build();
        }
      }
    }
    return getListReservationTopicsMethod;
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

  /** Creates a new blocking-style stub that supports all types of calls on the service */
  public static AdminServiceBlockingV2Stub newBlockingV2Stub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AdminServiceBlockingV2Stub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AdminServiceBlockingV2Stub>() {
          @java.lang.Override
          public AdminServiceBlockingV2Stub newStub(
              io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AdminServiceBlockingV2Stub(channel, callOptions);
          }
        };
    return AdminServiceBlockingV2Stub.newStub(factory, channel);
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
  public interface AsyncService {

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    default void createTopic(
        com.google.cloud.pubsublite.proto.CreateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getCreateTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    default void getTopic(
        com.google.cloud.pubsublite.proto.GetTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    default void getTopicPartitions(
        com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.TopicPartitions>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getGetTopicPartitionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of topics for the given project.
     * </pre>
     */
    default void listTopics(
        com.google.cloud.pubsublite.proto.ListTopicsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListTopicsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListTopicsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    default void updateTopic(
        com.google.cloud.pubsublite.proto.UpdateTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Topic> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getUpdateTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    default void deleteTopic(
        com.google.cloud.pubsublite.proto.DeleteTopicRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getDeleteTopicMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * </pre>
     */
    default void listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListTopicSubscriptionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new subscription.
     * </pre>
     */
    default void createSubscription(
        com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getCreateSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the subscription configuration.
     * </pre>
     */
    default void getSubscription(
        com.google.cloud.pubsublite.proto.GetSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getGetSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    default void listSubscriptions(
        com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListSubscriptionsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified subscription.
     * </pre>
     */
    default void updateSubscription(
        com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Subscription>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getUpdateSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified subscription.
     * </pre>
     */
    default void deleteSubscription(
        com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getDeleteSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Performs an out-of-band seek for a subscription to a specified target,
     * which may be timestamps or named positions within the message backlog.
     * Seek translates these targets to cursors for each partition and
     * orchestrates subscribers to start consuming messages from these seek
     * cursors.
     * If an operation is returned, the seek has been registered and subscribers
     * will eventually receive messages from the seek cursors (i.e. eventual
     * consistency), as long as they are using a minimum supported client library
     * version and not a system that tracks cursors independently of Pub/Sub Lite
     * (e.g. Apache Beam, Dataflow, Spark). The seek operation will fail for
     * unsupported clients.
     * If clients would like to know when subscribers react to the seek (or not),
     * they can poll the operation. The seek operation will succeed and complete
     * once subscribers are ready to receive messages from the seek cursors for
     * all partitions of the topic. This means that the seek operation will not
     * complete until all subscribers come online.
     * If the previous seek operation has not yet completed, it will be aborted
     * and the new invocation of seek will supersede it.
     * </pre>
     */
    default void seekSubscription(
        com.google.cloud.pubsublite.proto.SeekSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getSeekSubscriptionMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new reservation.
     * </pre>
     */
    default void createReservation(
        com.google.cloud.pubsublite.proto.CreateReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getCreateReservationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the reservation configuration.
     * </pre>
     */
    default void getReservation(
        com.google.cloud.pubsublite.proto.GetReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getGetReservationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of reservations for the given project.
     * </pre>
     */
    default void listReservations(
        com.google.cloud.pubsublite.proto.ListReservationsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListReservationsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListReservationsMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified reservation.
     * </pre>
     */
    default void updateReservation(
        com.google.cloud.pubsublite.proto.UpdateReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getUpdateReservationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified reservation.
     * </pre>
     */
    default void deleteReservation(
        com.google.cloud.pubsublite.proto.DeleteReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getDeleteReservationMethod(), responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the topics attached to the specified reservation.
     * </pre>
     */
    default void listReservationTopics(
        com.google.cloud.pubsublite.proto.ListReservationTopicsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
            responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(
          getListReservationTopicsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AdminService.
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public abstract static class AdminServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return AdminServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AdminService.
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteTopicMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * </pre>
     */
    public void listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request,
        io.grpc.stub.StreamObserver<
                com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
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
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Performs an out-of-band seek for a subscription to a specified target,
     * which may be timestamps or named positions within the message backlog.
     * Seek translates these targets to cursors for each partition and
     * orchestrates subscribers to start consuming messages from these seek
     * cursors.
     * If an operation is returned, the seek has been registered and subscribers
     * will eventually receive messages from the seek cursors (i.e. eventual
     * consistency), as long as they are using a minimum supported client library
     * version and not a system that tracks cursors independently of Pub/Sub Lite
     * (e.g. Apache Beam, Dataflow, Spark). The seek operation will fail for
     * unsupported clients.
     * If clients would like to know when subscribers react to the seek (or not),
     * they can poll the operation. The seek operation will succeed and complete
     * once subscribers are ready to receive messages from the seek cursors for
     * all partitions of the topic. This means that the seek operation will not
     * complete until all subscribers come online.
     * If the previous seek operation has not yet completed, it will be aborted
     * and the new invocation of seek will supersede it.
     * </pre>
     */
    public void seekSubscription(
        com.google.cloud.pubsublite.proto.SeekSubscriptionRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSeekSubscriptionMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Creates a new reservation.
     * </pre>
     */
    public void createReservation(
        com.google.cloud.pubsublite.proto.CreateReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateReservationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the reservation configuration.
     * </pre>
     */
    public void getReservation(
        com.google.cloud.pubsublite.proto.GetReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetReservationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of reservations for the given project.
     * </pre>
     */
    public void listReservations(
        com.google.cloud.pubsublite.proto.ListReservationsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListReservationsResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListReservationsMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified reservation.
     * </pre>
     */
    public void updateReservation(
        com.google.cloud.pubsublite.proto.UpdateReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateReservationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified reservation.
     * </pre>
     */
    public void deleteReservation(
        com.google.cloud.pubsublite.proto.DeleteReservationRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteReservationMethod(), getCallOptions()),
          request,
          responseObserver);
    }

    /**
     *
     *
     * <pre>
     * Lists the topics attached to the specified reservation.
     * </pre>
     */
    public void listReservationTopics(
        com.google.cloud.pubsublite.proto.ListReservationTopicsRequest request,
        io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
            responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListReservationTopicsMethod(), getCallOptions()),
          request,
          responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AdminService.
   *
   * <pre>
   * The service that a client application uses to manage topics and
   * subscriptions, such creating, listing, and deleting topics and subscriptions.
   * </pre>
   */
  public static final class AdminServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<AdminServiceBlockingV2Stub> {
    private AdminServiceBlockingV2Stub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AdminServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AdminServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     *
     *
     * <pre>
     * Creates a new topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic createTopic(
        com.google.cloud.pubsublite.proto.CreateTopicRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCreateTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the topic configuration.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic getTopic(
        com.google.cloud.pubsublite.proto.GetTopicRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the partition information for the requested topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.TopicPartitions getTopicPartitions(
        com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
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
        com.google.cloud.pubsublite.proto.ListTopicsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListTopicsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Topic updateTopic(
        com.google.cloud.pubsublite.proto.UpdateTopicRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getUpdateTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified topic.
     * </pre>
     */
    public com.google.protobuf.Empty deleteTopic(
        com.google.cloud.pubsublite.proto.DeleteTopicRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDeleteTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
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
        com.google.cloud.pubsublite.proto.CreateSubscriptionRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
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
        com.google.cloud.pubsublite.proto.GetSubscriptionRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of subscriptions for the given project.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListSubscriptionsResponse listSubscriptions(
        com.google.cloud.pubsublite.proto.ListSubscriptionsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
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
        com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
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
        com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDeleteSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Performs an out-of-band seek for a subscription to a specified target,
     * which may be timestamps or named positions within the message backlog.
     * Seek translates these targets to cursors for each partition and
     * orchestrates subscribers to start consuming messages from these seek
     * cursors.
     * If an operation is returned, the seek has been registered and subscribers
     * will eventually receive messages from the seek cursors (i.e. eventual
     * consistency), as long as they are using a minimum supported client library
     * version and not a system that tracks cursors independently of Pub/Sub Lite
     * (e.g. Apache Beam, Dataflow, Spark). The seek operation will fail for
     * unsupported clients.
     * If clients would like to know when subscribers react to the seek (or not),
     * they can poll the operation. The seek operation will succeed and complete
     * once subscribers are ready to receive messages from the seek cursors for
     * all partitions of the topic. This means that the seek operation will not
     * complete until all subscribers come online.
     * If the previous seek operation has not yet completed, it will be aborted
     * and the new invocation of seek will supersede it.
     * </pre>
     */
    public com.google.longrunning.Operation seekSubscription(
        com.google.cloud.pubsublite.proto.SeekSubscriptionRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSeekSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation createReservation(
        com.google.cloud.pubsublite.proto.CreateReservationRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCreateReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the reservation configuration.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation getReservation(
        com.google.cloud.pubsublite.proto.GetReservationRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of reservations for the given project.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListReservationsResponse listReservations(
        com.google.cloud.pubsublite.proto.ListReservationsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListReservationsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation updateReservation(
        com.google.cloud.pubsublite.proto.UpdateReservationRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getUpdateReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified reservation.
     * </pre>
     */
    public com.google.protobuf.Empty deleteReservation(
        com.google.cloud.pubsublite.proto.DeleteReservationRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDeleteReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the topics attached to the specified reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListReservationTopicsResponse listReservationTopics(
        com.google.cloud.pubsublite.proto.ListReservationTopicsRequest request)
        throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListReservationTopicsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service AdminService.
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateTopicMethod(), getCallOptions(), request);
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTopicMethod(), getCallOptions(), request);
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListTopicsMethod(), getCallOptions(), request);
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateTopicMethod(), getCallOptions(), request);
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteTopicMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse listTopicSubscriptions(
        com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetSubscriptionMethod(), getCallOptions(), request);
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
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
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Performs an out-of-band seek for a subscription to a specified target,
     * which may be timestamps or named positions within the message backlog.
     * Seek translates these targets to cursors for each partition and
     * orchestrates subscribers to start consuming messages from these seek
     * cursors.
     * If an operation is returned, the seek has been registered and subscribers
     * will eventually receive messages from the seek cursors (i.e. eventual
     * consistency), as long as they are using a minimum supported client library
     * version and not a system that tracks cursors independently of Pub/Sub Lite
     * (e.g. Apache Beam, Dataflow, Spark). The seek operation will fail for
     * unsupported clients.
     * If clients would like to know when subscribers react to the seek (or not),
     * they can poll the operation. The seek operation will succeed and complete
     * once subscribers are ready to receive messages from the seek cursors for
     * all partitions of the topic. This means that the seek operation will not
     * complete until all subscribers come online.
     * If the previous seek operation has not yet completed, it will be aborted
     * and the new invocation of seek will supersede it.
     * </pre>
     */
    public com.google.longrunning.Operation seekSubscription(
        com.google.cloud.pubsublite.proto.SeekSubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSeekSubscriptionMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation createReservation(
        com.google.cloud.pubsublite.proto.CreateReservationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the reservation configuration.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation getReservation(
        com.google.cloud.pubsublite.proto.GetReservationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of reservations for the given project.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListReservationsResponse listReservations(
        com.google.cloud.pubsublite.proto.ListReservationsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListReservationsMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.Reservation updateReservation(
        com.google.cloud.pubsublite.proto.UpdateReservationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified reservation.
     * </pre>
     */
    public com.google.protobuf.Empty deleteReservation(
        com.google.cloud.pubsublite.proto.DeleteReservationRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteReservationMethod(), getCallOptions(), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the topics attached to the specified reservation.
     * </pre>
     */
    public com.google.cloud.pubsublite.proto.ListReservationTopicsResponse listReservationTopics(
        com.google.cloud.pubsublite.proto.ListReservationTopicsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListReservationTopicsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AdminService.
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTopicMethod(), getCallOptions()), request);
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteTopicMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the subscriptions attached to the specified topic.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>
        listTopicSubscriptions(
            com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
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
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteSubscriptionMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Performs an out-of-band seek for a subscription to a specified target,
     * which may be timestamps or named positions within the message backlog.
     * Seek translates these targets to cursors for each partition and
     * orchestrates subscribers to start consuming messages from these seek
     * cursors.
     * If an operation is returned, the seek has been registered and subscribers
     * will eventually receive messages from the seek cursors (i.e. eventual
     * consistency), as long as they are using a minimum supported client library
     * version and not a system that tracks cursors independently of Pub/Sub Lite
     * (e.g. Apache Beam, Dataflow, Spark). The seek operation will fail for
     * unsupported clients.
     * If clients would like to know when subscribers react to the seek (or not),
     * they can poll the operation. The seek operation will succeed and complete
     * once subscribers are ready to receive messages from the seek cursors for
     * all partitions of the topic. This means that the seek operation will not
     * complete until all subscribers come online.
     * If the previous seek operation has not yet completed, it will be aborted
     * and the new invocation of seek will supersede it.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation>
        seekSubscription(com.google.cloud.pubsublite.proto.SeekSubscriptionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSeekSubscriptionMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Creates a new reservation.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Reservation>
        createReservation(com.google.cloud.pubsublite.proto.CreateReservationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateReservationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the reservation configuration.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Reservation>
        getReservation(com.google.cloud.pubsublite.proto.GetReservationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetReservationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Returns the list of reservations for the given project.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListReservationsResponse>
        listReservations(com.google.cloud.pubsublite.proto.ListReservationsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListReservationsMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Updates properties of the specified reservation.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.Reservation>
        updateReservation(com.google.cloud.pubsublite.proto.UpdateReservationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateReservationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Deletes the specified reservation.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty>
        deleteReservation(com.google.cloud.pubsublite.proto.DeleteReservationRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteReservationMethod(), getCallOptions()), request);
    }

    /**
     *
     *
     * <pre>
     * Lists the topics attached to the specified reservation.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<
            com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>
        listReservationTopics(
            com.google.cloud.pubsublite.proto.ListReservationTopicsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListReservationTopicsMethod(), getCallOptions()), request);
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
  private static final int METHODID_SEEK_SUBSCRIPTION = 12;
  private static final int METHODID_CREATE_RESERVATION = 13;
  private static final int METHODID_GET_RESERVATION = 14;
  private static final int METHODID_LIST_RESERVATIONS = 15;
  private static final int METHODID_UPDATE_RESERVATION = 16;
  private static final int METHODID_DELETE_RESERVATION = 17;
  private static final int METHODID_LIST_RESERVATION_TOPICS = 18;

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
        case METHODID_SEEK_SUBSCRIPTION:
          serviceImpl.seekSubscription(
              (com.google.cloud.pubsublite.proto.SeekSubscriptionRequest) request,
              (io.grpc.stub.StreamObserver<com.google.longrunning.Operation>) responseObserver);
          break;
        case METHODID_CREATE_RESERVATION:
          serviceImpl.createReservation(
              (com.google.cloud.pubsublite.proto.CreateReservationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>)
                  responseObserver);
          break;
        case METHODID_GET_RESERVATION:
          serviceImpl.getReservation(
              (com.google.cloud.pubsublite.proto.GetReservationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>)
                  responseObserver);
          break;
        case METHODID_LIST_RESERVATIONS:
          serviceImpl.listReservations(
              (com.google.cloud.pubsublite.proto.ListReservationsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ListReservationsResponse>)
                  responseObserver);
          break;
        case METHODID_UPDATE_RESERVATION:
          serviceImpl.updateReservation(
              (com.google.cloud.pubsublite.proto.UpdateReservationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.cloud.pubsublite.proto.Reservation>)
                  responseObserver);
          break;
        case METHODID_DELETE_RESERVATION:
          serviceImpl.deleteReservation(
              (com.google.cloud.pubsublite.proto.DeleteReservationRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_LIST_RESERVATION_TOPICS:
          serviceImpl.listReservationTopics(
              (com.google.cloud.pubsublite.proto.ListReservationTopicsRequest) request,
              (io.grpc.stub.StreamObserver<
                      com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>)
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
            getCreateTopicMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.CreateTopicRequest,
                    com.google.cloud.pubsublite.proto.Topic>(service, METHODID_CREATE_TOPIC)))
        .addMethod(
            getGetTopicMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.GetTopicRequest,
                    com.google.cloud.pubsublite.proto.Topic>(service, METHODID_GET_TOPIC)))
        .addMethod(
            getGetTopicPartitionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest,
                    com.google.cloud.pubsublite.proto.TopicPartitions>(
                    service, METHODID_GET_TOPIC_PARTITIONS)))
        .addMethod(
            getListTopicsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ListTopicsRequest,
                    com.google.cloud.pubsublite.proto.ListTopicsResponse>(
                    service, METHODID_LIST_TOPICS)))
        .addMethod(
            getUpdateTopicMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.UpdateTopicRequest,
                    com.google.cloud.pubsublite.proto.Topic>(service, METHODID_UPDATE_TOPIC)))
        .addMethod(
            getDeleteTopicMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.DeleteTopicRequest,
                    com.google.protobuf.Empty>(service, METHODID_DELETE_TOPIC)))
        .addMethod(
            getListTopicSubscriptionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest,
                    com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse>(
                    service, METHODID_LIST_TOPIC_SUBSCRIPTIONS)))
        .addMethod(
            getCreateSubscriptionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.CreateSubscriptionRequest,
                    com.google.cloud.pubsublite.proto.Subscription>(
                    service, METHODID_CREATE_SUBSCRIPTION)))
        .addMethod(
            getGetSubscriptionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.GetSubscriptionRequest,
                    com.google.cloud.pubsublite.proto.Subscription>(
                    service, METHODID_GET_SUBSCRIPTION)))
        .addMethod(
            getListSubscriptionsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ListSubscriptionsRequest,
                    com.google.cloud.pubsublite.proto.ListSubscriptionsResponse>(
                    service, METHODID_LIST_SUBSCRIPTIONS)))
        .addMethod(
            getUpdateSubscriptionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest,
                    com.google.cloud.pubsublite.proto.Subscription>(
                    service, METHODID_UPDATE_SUBSCRIPTION)))
        .addMethod(
            getDeleteSubscriptionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest,
                    com.google.protobuf.Empty>(service, METHODID_DELETE_SUBSCRIPTION)))
        .addMethod(
            getSeekSubscriptionMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.SeekSubscriptionRequest,
                    com.google.longrunning.Operation>(service, METHODID_SEEK_SUBSCRIPTION)))
        .addMethod(
            getCreateReservationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.CreateReservationRequest,
                    com.google.cloud.pubsublite.proto.Reservation>(
                    service, METHODID_CREATE_RESERVATION)))
        .addMethod(
            getGetReservationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.GetReservationRequest,
                    com.google.cloud.pubsublite.proto.Reservation>(
                    service, METHODID_GET_RESERVATION)))
        .addMethod(
            getListReservationsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ListReservationsRequest,
                    com.google.cloud.pubsublite.proto.ListReservationsResponse>(
                    service, METHODID_LIST_RESERVATIONS)))
        .addMethod(
            getUpdateReservationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.UpdateReservationRequest,
                    com.google.cloud.pubsublite.proto.Reservation>(
                    service, METHODID_UPDATE_RESERVATION)))
        .addMethod(
            getDeleteReservationMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.DeleteReservationRequest,
                    com.google.protobuf.Empty>(service, METHODID_DELETE_RESERVATION)))
        .addMethod(
            getListReservationTopicsMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
                new MethodHandlers<
                    com.google.cloud.pubsublite.proto.ListReservationTopicsRequest,
                    com.google.cloud.pubsublite.proto.ListReservationTopicsResponse>(
                    service, METHODID_LIST_RESERVATION_TOPICS)))
        .build();
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
    private final java.lang.String methodName;

    AdminServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
                      .addMethod(getSeekSubscriptionMethod())
                      .addMethod(getCreateReservationMethod())
                      .addMethod(getGetReservationMethod())
                      .addMethod(getListReservationsMethod())
                      .addMethod(getUpdateReservationMethod())
                      .addMethod(getDeleteReservationMethod())
                      .addMethod(getListReservationTopicsMethod())
                      .build();
        }
      }
    }
    return result;
  }
}
