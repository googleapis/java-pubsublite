/*
 * Copyright 2020 Google LLC
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
package com.google.cloud.pubsublite.v1.stub;

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.protobuf.Empty;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Base stub class for Pub/Sub Lite API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator")
@BetaApi("A restructuring of stub classes is planned, so this may break in the future")
public abstract class AdminServiceStub implements BackgroundResource {

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

  @Override
  public abstract void close();
}
