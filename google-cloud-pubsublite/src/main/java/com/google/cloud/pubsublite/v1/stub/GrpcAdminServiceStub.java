/*
 * Copyright 2026 Google LLC
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

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationTopicsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.OperationCallable;
import com.google.api.gax.rpc.RequestParamsBuilder;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CreateReservationRequest;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteReservationRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetReservationRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsResponse;
import com.google.cloud.pubsublite.proto.ListReservationsRequest;
import com.google.cloud.pubsublite.proto.ListReservationsResponse;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.longrunning.Operation;
import com.google.longrunning.stub.GrpcOperationsStub;
import com.google.protobuf.Empty;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the AdminService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public class GrpcAdminServiceStub extends AdminServiceStub {
  private static final MethodDescriptor<CreateTopicRequest, Topic> createTopicMethodDescriptor =
      MethodDescriptor.<CreateTopicRequest, Topic>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.cloud.pubsublite.v1.AdminService/CreateTopic")
          .setRequestMarshaller(ProtoUtils.marshaller(CreateTopicRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Topic.getDefaultInstance()))
          .setSampledToLocalTracing(true)
          .build();

  private static final MethodDescriptor<GetTopicRequest, Topic> getTopicMethodDescriptor =
      MethodDescriptor.<GetTopicRequest, Topic>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.cloud.pubsublite.v1.AdminService/GetTopic")
          .setRequestMarshaller(ProtoUtils.marshaller(GetTopicRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Topic.getDefaultInstance()))
          .setSampledToLocalTracing(true)
          .build();

  private static final MethodDescriptor<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsMethodDescriptor =
          MethodDescriptor.<GetTopicPartitionsRequest, TopicPartitions>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/GetTopicPartitions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetTopicPartitionsRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(TopicPartitions.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ListTopicsRequest, ListTopicsResponse>
      listTopicsMethodDescriptor =
          MethodDescriptor.<ListTopicsRequest, ListTopicsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/ListTopics")
              .setRequestMarshaller(ProtoUtils.marshaller(ListTopicsRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(ListTopicsResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<UpdateTopicRequest, Topic> updateTopicMethodDescriptor =
      MethodDescriptor.<UpdateTopicRequest, Topic>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.cloud.pubsublite.v1.AdminService/UpdateTopic")
          .setRequestMarshaller(ProtoUtils.marshaller(UpdateTopicRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Topic.getDefaultInstance()))
          .setSampledToLocalTracing(true)
          .build();

  private static final MethodDescriptor<DeleteTopicRequest, Empty> deleteTopicMethodDescriptor =
      MethodDescriptor.<DeleteTopicRequest, Empty>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName("google.cloud.pubsublite.v1.AdminService/DeleteTopic")
          .setRequestMarshaller(ProtoUtils.marshaller(DeleteTopicRequest.getDefaultInstance()))
          .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
          .setSampledToLocalTracing(true)
          .build();

  private static final MethodDescriptor<
          ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsMethodDescriptor =
          MethodDescriptor
              .<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/ListTopicSubscriptions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListTopicSubscriptionsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListTopicSubscriptionsResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<CreateSubscriptionRequest, Subscription>
      createSubscriptionMethodDescriptor =
          MethodDescriptor.<CreateSubscriptionRequest, Subscription>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/CreateSubscription")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(CreateSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Subscription.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<GetSubscriptionRequest, Subscription>
      getSubscriptionMethodDescriptor =
          MethodDescriptor.<GetSubscriptionRequest, Subscription>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/GetSubscription")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Subscription.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsMethodDescriptor =
          MethodDescriptor.<ListSubscriptionsRequest, ListSubscriptionsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/ListSubscriptions")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListSubscriptionsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListSubscriptionsResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<UpdateSubscriptionRequest, Subscription>
      updateSubscriptionMethodDescriptor =
          MethodDescriptor.<UpdateSubscriptionRequest, Subscription>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/UpdateSubscription")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(UpdateSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Subscription.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<DeleteSubscriptionRequest, Empty>
      deleteSubscriptionMethodDescriptor =
          MethodDescriptor.<DeleteSubscriptionRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/DeleteSubscription")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(DeleteSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<SeekSubscriptionRequest, Operation>
      seekSubscriptionMethodDescriptor =
          MethodDescriptor.<SeekSubscriptionRequest, Operation>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/SeekSubscription")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(SeekSubscriptionRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Operation.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<CreateReservationRequest, Reservation>
      createReservationMethodDescriptor =
          MethodDescriptor.<CreateReservationRequest, Reservation>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/CreateReservation")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(CreateReservationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Reservation.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<GetReservationRequest, Reservation>
      getReservationMethodDescriptor =
          MethodDescriptor.<GetReservationRequest, Reservation>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/GetReservation")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GetReservationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Reservation.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ListReservationsRequest, ListReservationsResponse>
      listReservationsMethodDescriptor =
          MethodDescriptor.<ListReservationsRequest, ListReservationsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/ListReservations")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListReservationsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListReservationsResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<UpdateReservationRequest, Reservation>
      updateReservationMethodDescriptor =
          MethodDescriptor.<UpdateReservationRequest, Reservation>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/UpdateReservation")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(UpdateReservationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Reservation.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<DeleteReservationRequest, Empty>
      deleteReservationMethodDescriptor =
          MethodDescriptor.<DeleteReservationRequest, Empty>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/DeleteReservation")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(DeleteReservationRequest.getDefaultInstance()))
              .setResponseMarshaller(ProtoUtils.marshaller(Empty.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private static final MethodDescriptor<ListReservationTopicsRequest, ListReservationTopicsResponse>
      listReservationTopicsMethodDescriptor =
          MethodDescriptor.<ListReservationTopicsRequest, ListReservationTopicsResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName("google.cloud.pubsublite.v1.AdminService/ListReservationTopics")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(ListReservationTopicsRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(ListReservationTopicsResponse.getDefaultInstance()))
              .setSampledToLocalTracing(true)
              .build();

  private final UnaryCallable<CreateTopicRequest, Topic> createTopicCallable;
  private final UnaryCallable<GetTopicRequest, Topic> getTopicCallable;
  private final UnaryCallable<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsCallable;
  private final UnaryCallable<ListTopicsRequest, ListTopicsResponse> listTopicsCallable;
  private final UnaryCallable<ListTopicsRequest, ListTopicsPagedResponse> listTopicsPagedCallable;
  private final UnaryCallable<UpdateTopicRequest, Topic> updateTopicCallable;
  private final UnaryCallable<DeleteTopicRequest, Empty> deleteTopicCallable;
  private final UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsCallable;
  private final UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsPagedCallable;
  private final UnaryCallable<CreateSubscriptionRequest, Subscription> createSubscriptionCallable;
  private final UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable;
  private final UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsCallable;
  private final UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsPagedResponse>
      listSubscriptionsPagedCallable;
  private final UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable;
  private final UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable;
  private final UnaryCallable<SeekSubscriptionRequest, Operation> seekSubscriptionCallable;
  private final OperationCallable<
          SeekSubscriptionRequest, SeekSubscriptionResponse, OperationMetadata>
      seekSubscriptionOperationCallable;
  private final UnaryCallable<CreateReservationRequest, Reservation> createReservationCallable;
  private final UnaryCallable<GetReservationRequest, Reservation> getReservationCallable;
  private final UnaryCallable<ListReservationsRequest, ListReservationsResponse>
      listReservationsCallable;
  private final UnaryCallable<ListReservationsRequest, ListReservationsPagedResponse>
      listReservationsPagedCallable;
  private final UnaryCallable<UpdateReservationRequest, Reservation> updateReservationCallable;
  private final UnaryCallable<DeleteReservationRequest, Empty> deleteReservationCallable;
  private final UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsResponse>
      listReservationTopicsCallable;
  private final UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsPagedResponse>
      listReservationTopicsPagedCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcAdminServiceStub create(AdminServiceStubSettings settings)
      throws IOException {
    return new GrpcAdminServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcAdminServiceStub create(ClientContext clientContext) throws IOException {
    return new GrpcAdminServiceStub(AdminServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcAdminServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcAdminServiceStub(
        AdminServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcAdminServiceStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcAdminServiceStub(AdminServiceStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcAdminServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcAdminServiceStub, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcAdminServiceStub(
      AdminServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<CreateTopicRequest, Topic> createTopicTransportSettings =
        GrpcCallSettings.<CreateTopicRequest, Topic>newBuilder()
            .setMethodDescriptor(createTopicMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("parent", String.valueOf(request.getParent()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetTopicRequest, Topic> getTopicTransportSettings =
        GrpcCallSettings.<GetTopicRequest, Topic>newBuilder()
            .setMethodDescriptor(getTopicMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetTopicPartitionsRequest, TopicPartitions>
        getTopicPartitionsTransportSettings =
            GrpcCallSettings.<GetTopicPartitionsRequest, TopicPartitions>newBuilder()
                .setMethodDescriptor(getTopicPartitionsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("name", String.valueOf(request.getName()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<ListTopicsRequest, ListTopicsResponse> listTopicsTransportSettings =
        GrpcCallSettings.<ListTopicsRequest, ListTopicsResponse>newBuilder()
            .setMethodDescriptor(listTopicsMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("parent", String.valueOf(request.getParent()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<UpdateTopicRequest, Topic> updateTopicTransportSettings =
        GrpcCallSettings.<UpdateTopicRequest, Topic>newBuilder()
            .setMethodDescriptor(updateTopicMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("topic.name", String.valueOf(request.getTopic().getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<DeleteTopicRequest, Empty> deleteTopicTransportSettings =
        GrpcCallSettings.<DeleteTopicRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteTopicMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
        listTopicSubscriptionsTransportSettings =
            GrpcCallSettings
                .<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>newBuilder()
                .setMethodDescriptor(listTopicSubscriptionsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("name", String.valueOf(request.getName()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<CreateSubscriptionRequest, Subscription> createSubscriptionTransportSettings =
        GrpcCallSettings.<CreateSubscriptionRequest, Subscription>newBuilder()
            .setMethodDescriptor(createSubscriptionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("parent", String.valueOf(request.getParent()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetSubscriptionRequest, Subscription> getSubscriptionTransportSettings =
        GrpcCallSettings.<GetSubscriptionRequest, Subscription>newBuilder()
            .setMethodDescriptor(getSubscriptionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ListSubscriptionsRequest, ListSubscriptionsResponse>
        listSubscriptionsTransportSettings =
            GrpcCallSettings.<ListSubscriptionsRequest, ListSubscriptionsResponse>newBuilder()
                .setMethodDescriptor(listSubscriptionsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<UpdateSubscriptionRequest, Subscription> updateSubscriptionTransportSettings =
        GrpcCallSettings.<UpdateSubscriptionRequest, Subscription>newBuilder()
            .setMethodDescriptor(updateSubscriptionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(
                      "subscription.name", String.valueOf(request.getSubscription().getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<DeleteSubscriptionRequest, Empty> deleteSubscriptionTransportSettings =
        GrpcCallSettings.<DeleteSubscriptionRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteSubscriptionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<SeekSubscriptionRequest, Operation> seekSubscriptionTransportSettings =
        GrpcCallSettings.<SeekSubscriptionRequest, Operation>newBuilder()
            .setMethodDescriptor(seekSubscriptionMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<CreateReservationRequest, Reservation> createReservationTransportSettings =
        GrpcCallSettings.<CreateReservationRequest, Reservation>newBuilder()
            .setMethodDescriptor(createReservationMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("parent", String.valueOf(request.getParent()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<GetReservationRequest, Reservation> getReservationTransportSettings =
        GrpcCallSettings.<GetReservationRequest, Reservation>newBuilder()
            .setMethodDescriptor(getReservationMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ListReservationsRequest, ListReservationsResponse>
        listReservationsTransportSettings =
            GrpcCallSettings.<ListReservationsRequest, ListReservationsResponse>newBuilder()
                .setMethodDescriptor(listReservationsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("parent", String.valueOf(request.getParent()));
                      return builder.build();
                    })
                .build();
    GrpcCallSettings<UpdateReservationRequest, Reservation> updateReservationTransportSettings =
        GrpcCallSettings.<UpdateReservationRequest, Reservation>newBuilder()
            .setMethodDescriptor(updateReservationMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add(
                      "reservation.name", String.valueOf(request.getReservation().getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<DeleteReservationRequest, Empty> deleteReservationTransportSettings =
        GrpcCallSettings.<DeleteReservationRequest, Empty>newBuilder()
            .setMethodDescriptor(deleteReservationMethodDescriptor)
            .setParamsExtractor(
                request -> {
                  RequestParamsBuilder builder = RequestParamsBuilder.create();
                  builder.add("name", String.valueOf(request.getName()));
                  return builder.build();
                })
            .build();
    GrpcCallSettings<ListReservationTopicsRequest, ListReservationTopicsResponse>
        listReservationTopicsTransportSettings =
            GrpcCallSettings
                .<ListReservationTopicsRequest, ListReservationTopicsResponse>newBuilder()
                .setMethodDescriptor(listReservationTopicsMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      RequestParamsBuilder builder = RequestParamsBuilder.create();
                      builder.add("name", String.valueOf(request.getName()));
                      return builder.build();
                    })
                .build();

    this.createTopicCallable =
        callableFactory.createUnaryCallable(
            createTopicTransportSettings, settings.createTopicSettings(), clientContext);
    this.getTopicCallable =
        callableFactory.createUnaryCallable(
            getTopicTransportSettings, settings.getTopicSettings(), clientContext);
    this.getTopicPartitionsCallable =
        callableFactory.createUnaryCallable(
            getTopicPartitionsTransportSettings,
            settings.getTopicPartitionsSettings(),
            clientContext);
    this.listTopicsCallable =
        callableFactory.createUnaryCallable(
            listTopicsTransportSettings, settings.listTopicsSettings(), clientContext);
    this.listTopicsPagedCallable =
        callableFactory.createPagedCallable(
            listTopicsTransportSettings, settings.listTopicsSettings(), clientContext);
    this.updateTopicCallable =
        callableFactory.createUnaryCallable(
            updateTopicTransportSettings, settings.updateTopicSettings(), clientContext);
    this.deleteTopicCallable =
        callableFactory.createUnaryCallable(
            deleteTopicTransportSettings, settings.deleteTopicSettings(), clientContext);
    this.listTopicSubscriptionsCallable =
        callableFactory.createUnaryCallable(
            listTopicSubscriptionsTransportSettings,
            settings.listTopicSubscriptionsSettings(),
            clientContext);
    this.listTopicSubscriptionsPagedCallable =
        callableFactory.createPagedCallable(
            listTopicSubscriptionsTransportSettings,
            settings.listTopicSubscriptionsSettings(),
            clientContext);
    this.createSubscriptionCallable =
        callableFactory.createUnaryCallable(
            createSubscriptionTransportSettings,
            settings.createSubscriptionSettings(),
            clientContext);
    this.getSubscriptionCallable =
        callableFactory.createUnaryCallable(
            getSubscriptionTransportSettings, settings.getSubscriptionSettings(), clientContext);
    this.listSubscriptionsCallable =
        callableFactory.createUnaryCallable(
            listSubscriptionsTransportSettings,
            settings.listSubscriptionsSettings(),
            clientContext);
    this.listSubscriptionsPagedCallable =
        callableFactory.createPagedCallable(
            listSubscriptionsTransportSettings,
            settings.listSubscriptionsSettings(),
            clientContext);
    this.updateSubscriptionCallable =
        callableFactory.createUnaryCallable(
            updateSubscriptionTransportSettings,
            settings.updateSubscriptionSettings(),
            clientContext);
    this.deleteSubscriptionCallable =
        callableFactory.createUnaryCallable(
            deleteSubscriptionTransportSettings,
            settings.deleteSubscriptionSettings(),
            clientContext);
    this.seekSubscriptionCallable =
        callableFactory.createUnaryCallable(
            seekSubscriptionTransportSettings, settings.seekSubscriptionSettings(), clientContext);
    this.seekSubscriptionOperationCallable =
        callableFactory.createOperationCallable(
            seekSubscriptionTransportSettings,
            settings.seekSubscriptionOperationSettings(),
            clientContext,
            operationsStub);
    this.createReservationCallable =
        callableFactory.createUnaryCallable(
            createReservationTransportSettings,
            settings.createReservationSettings(),
            clientContext);
    this.getReservationCallable =
        callableFactory.createUnaryCallable(
            getReservationTransportSettings, settings.getReservationSettings(), clientContext);
    this.listReservationsCallable =
        callableFactory.createUnaryCallable(
            listReservationsTransportSettings, settings.listReservationsSettings(), clientContext);
    this.listReservationsPagedCallable =
        callableFactory.createPagedCallable(
            listReservationsTransportSettings, settings.listReservationsSettings(), clientContext);
    this.updateReservationCallable =
        callableFactory.createUnaryCallable(
            updateReservationTransportSettings,
            settings.updateReservationSettings(),
            clientContext);
    this.deleteReservationCallable =
        callableFactory.createUnaryCallable(
            deleteReservationTransportSettings,
            settings.deleteReservationSettings(),
            clientContext);
    this.listReservationTopicsCallable =
        callableFactory.createUnaryCallable(
            listReservationTopicsTransportSettings,
            settings.listReservationTopicsSettings(),
            clientContext);
    this.listReservationTopicsPagedCallable =
        callableFactory.createPagedCallable(
            listReservationTopicsTransportSettings,
            settings.listReservationTopicsSettings(),
            clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public UnaryCallable<CreateTopicRequest, Topic> createTopicCallable() {
    return createTopicCallable;
  }

  @Override
  public UnaryCallable<GetTopicRequest, Topic> getTopicCallable() {
    return getTopicCallable;
  }

  @Override
  public UnaryCallable<GetTopicPartitionsRequest, TopicPartitions> getTopicPartitionsCallable() {
    return getTopicPartitionsCallable;
  }

  @Override
  public UnaryCallable<ListTopicsRequest, ListTopicsResponse> listTopicsCallable() {
    return listTopicsCallable;
  }

  @Override
  public UnaryCallable<ListTopicsRequest, ListTopicsPagedResponse> listTopicsPagedCallable() {
    return listTopicsPagedCallable;
  }

  @Override
  public UnaryCallable<UpdateTopicRequest, Topic> updateTopicCallable() {
    return updateTopicCallable;
  }

  @Override
  public UnaryCallable<DeleteTopicRequest, Empty> deleteTopicCallable() {
    return deleteTopicCallable;
  }

  @Override
  public UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsCallable() {
    return listTopicSubscriptionsCallable;
  }

  @Override
  public UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsPagedCallable() {
    return listTopicSubscriptionsPagedCallable;
  }

  @Override
  public UnaryCallable<CreateSubscriptionRequest, Subscription> createSubscriptionCallable() {
    return createSubscriptionCallable;
  }

  @Override
  public UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable() {
    return getSubscriptionCallable;
  }

  @Override
  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsCallable() {
    return listSubscriptionsCallable;
  }

  @Override
  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsPagedResponse>
      listSubscriptionsPagedCallable() {
    return listSubscriptionsPagedCallable;
  }

  @Override
  public UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable() {
    return updateSubscriptionCallable;
  }

  @Override
  public UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable() {
    return deleteSubscriptionCallable;
  }

  @Override
  public UnaryCallable<SeekSubscriptionRequest, Operation> seekSubscriptionCallable() {
    return seekSubscriptionCallable;
  }

  @Override
  public OperationCallable<SeekSubscriptionRequest, SeekSubscriptionResponse, OperationMetadata>
      seekSubscriptionOperationCallable() {
    return seekSubscriptionOperationCallable;
  }

  @Override
  public UnaryCallable<CreateReservationRequest, Reservation> createReservationCallable() {
    return createReservationCallable;
  }

  @Override
  public UnaryCallable<GetReservationRequest, Reservation> getReservationCallable() {
    return getReservationCallable;
  }

  @Override
  public UnaryCallable<ListReservationsRequest, ListReservationsResponse>
      listReservationsCallable() {
    return listReservationsCallable;
  }

  @Override
  public UnaryCallable<ListReservationsRequest, ListReservationsPagedResponse>
      listReservationsPagedCallable() {
    return listReservationsPagedCallable;
  }

  @Override
  public UnaryCallable<UpdateReservationRequest, Reservation> updateReservationCallable() {
    return updateReservationCallable;
  }

  @Override
  public UnaryCallable<DeleteReservationRequest, Empty> deleteReservationCallable() {
    return deleteReservationCallable;
  }

  @Override
  public UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsResponse>
      listReservationTopicsCallable() {
    return listReservationTopicsCallable;
  }

  @Override
  public UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsPagedResponse>
      listReservationTopicsPagedCallable() {
    return listReservationTopicsPagedCallable;
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
