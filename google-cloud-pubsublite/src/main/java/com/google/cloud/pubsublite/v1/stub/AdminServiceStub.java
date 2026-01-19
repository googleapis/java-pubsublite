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
import com.google.api.gax.rpc.OperationCallable;
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
import com.google.longrunning.stub.OperationsStub;
import com.google.protobuf.Empty;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the AdminService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class AdminServiceStub implements BackgroundResource {

  public OperationsStub getOperationsStub() {
    throw new UnsupportedOperationException("Not implemented: getOperationsStub()");
  }

  public UnaryCallable<CreateTopicRequest, Topic> createTopicCallable() {
    throw new UnsupportedOperationException("Not implemented: createTopicCallable()");
  }

  public UnaryCallable<GetTopicRequest, Topic> getTopicCallable() {
    throw new UnsupportedOperationException("Not implemented: getTopicCallable()");
  }

  public UnaryCallable<GetTopicPartitionsRequest, TopicPartitions> getTopicPartitionsCallable() {
    throw new UnsupportedOperationException("Not implemented: getTopicPartitionsCallable()");
  }

  public UnaryCallable<ListTopicsRequest, ListTopicsPagedResponse> listTopicsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listTopicsPagedCallable()");
  }

  public UnaryCallable<ListTopicsRequest, ListTopicsResponse> listTopicsCallable() {
    throw new UnsupportedOperationException("Not implemented: listTopicsCallable()");
  }

  public UnaryCallable<UpdateTopicRequest, Topic> updateTopicCallable() {
    throw new UnsupportedOperationException("Not implemented: updateTopicCallable()");
  }

  public UnaryCallable<DeleteTopicRequest, Empty> deleteTopicCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteTopicCallable()");
  }

  public UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsPagedCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: listTopicSubscriptionsPagedCallable()");
  }

  public UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsCallable() {
    throw new UnsupportedOperationException("Not implemented: listTopicSubscriptionsCallable()");
  }

  public UnaryCallable<CreateSubscriptionRequest, Subscription> createSubscriptionCallable() {
    throw new UnsupportedOperationException("Not implemented: createSubscriptionCallable()");
  }

  public UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable() {
    throw new UnsupportedOperationException("Not implemented: getSubscriptionCallable()");
  }

  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsPagedResponse>
      listSubscriptionsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listSubscriptionsPagedCallable()");
  }

  public UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsCallable() {
    throw new UnsupportedOperationException("Not implemented: listSubscriptionsCallable()");
  }

  public UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable() {
    throw new UnsupportedOperationException("Not implemented: updateSubscriptionCallable()");
  }

  public UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteSubscriptionCallable()");
  }

  public OperationCallable<SeekSubscriptionRequest, SeekSubscriptionResponse, OperationMetadata>
      seekSubscriptionOperationCallable() {
    throw new UnsupportedOperationException("Not implemented: seekSubscriptionOperationCallable()");
  }

  public UnaryCallable<SeekSubscriptionRequest, Operation> seekSubscriptionCallable() {
    throw new UnsupportedOperationException("Not implemented: seekSubscriptionCallable()");
  }

  public UnaryCallable<CreateReservationRequest, Reservation> createReservationCallable() {
    throw new UnsupportedOperationException("Not implemented: createReservationCallable()");
  }

  public UnaryCallable<GetReservationRequest, Reservation> getReservationCallable() {
    throw new UnsupportedOperationException("Not implemented: getReservationCallable()");
  }

  public UnaryCallable<ListReservationsRequest, ListReservationsPagedResponse>
      listReservationsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listReservationsPagedCallable()");
  }

  public UnaryCallable<ListReservationsRequest, ListReservationsResponse>
      listReservationsCallable() {
    throw new UnsupportedOperationException("Not implemented: listReservationsCallable()");
  }

  public UnaryCallable<UpdateReservationRequest, Reservation> updateReservationCallable() {
    throw new UnsupportedOperationException("Not implemented: updateReservationCallable()");
  }

  public UnaryCallable<DeleteReservationRequest, Empty> deleteReservationCallable() {
    throw new UnsupportedOperationException("Not implemented: deleteReservationCallable()");
  }

  public UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsPagedResponse>
      listReservationTopicsPagedCallable() {
    throw new UnsupportedOperationException(
        "Not implemented: listReservationTopicsPagedCallable()");
  }

  public UnaryCallable<ListReservationTopicsRequest, ListReservationTopicsResponse>
      listReservationTopicsCallable() {
    throw new UnsupportedOperationException("Not implemented: listReservationTopicsCallable()");
  }

  @Override
  public abstract void close();
}
