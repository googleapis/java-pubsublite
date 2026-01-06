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

package com.google.cloud.pubsublite.v1;

import com.google.api.core.BetaApi;
import com.google.cloud.pubsublite.proto.AdminServiceGrpc.AdminServiceImplBase;
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
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.longrunning.Operation;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockAdminServiceImpl extends AdminServiceImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockAdminServiceImpl() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  public List<AbstractMessage> getRequests() {
    return requests;
  }

  public void addResponse(AbstractMessage response) {
    responses.add(response);
  }

  public void setResponses(List<AbstractMessage> responses) {
    this.responses = new LinkedList<Object>(responses);
  }

  public void addException(Exception exception) {
    responses.add(exception);
  }

  public void reset() {
    requests = new ArrayList<>();
    responses = new LinkedList<>();
  }

  @Override
  public void createTopic(CreateTopicRequest request, StreamObserver<Topic> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Topic) {
      requests.add(request);
      responseObserver.onNext(((Topic) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateTopic, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Topic.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getTopic(GetTopicRequest request, StreamObserver<Topic> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Topic) {
      requests.add(request);
      responseObserver.onNext(((Topic) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetTopic, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Topic.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getTopicPartitions(
      GetTopicPartitionsRequest request, StreamObserver<TopicPartitions> responseObserver) {
    Object response = responses.poll();
    if (response instanceof TopicPartitions) {
      requests.add(request);
      responseObserver.onNext(((TopicPartitions) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetTopicPartitions, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  TopicPartitions.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listTopics(
      ListTopicsRequest request, StreamObserver<ListTopicsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListTopicsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListTopicsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListTopics, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListTopicsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateTopic(UpdateTopicRequest request, StreamObserver<Topic> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Topic) {
      requests.add(request);
      responseObserver.onNext(((Topic) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateTopic, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Topic.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteTopic(DeleteTopicRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteTopic, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listTopicSubscriptions(
      ListTopicSubscriptionsRequest request,
      StreamObserver<ListTopicSubscriptionsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListTopicSubscriptionsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListTopicSubscriptionsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListTopicSubscriptions, expected %s or"
                      + " %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListTopicSubscriptionsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void createSubscription(
      CreateSubscriptionRequest request, StreamObserver<Subscription> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Subscription) {
      requests.add(request);
      responseObserver.onNext(((Subscription) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateSubscription, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Subscription.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getSubscription(
      GetSubscriptionRequest request, StreamObserver<Subscription> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Subscription) {
      requests.add(request);
      responseObserver.onNext(((Subscription) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetSubscription, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Subscription.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listSubscriptions(
      ListSubscriptionsRequest request,
      StreamObserver<ListSubscriptionsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListSubscriptionsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListSubscriptionsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListSubscriptions, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListSubscriptionsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateSubscription(
      UpdateSubscriptionRequest request, StreamObserver<Subscription> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Subscription) {
      requests.add(request);
      responseObserver.onNext(((Subscription) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateSubscription, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Subscription.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteSubscription(
      DeleteSubscriptionRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteSubscription, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void seekSubscription(
      SeekSubscriptionRequest request, StreamObserver<Operation> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Operation) {
      requests.add(request);
      responseObserver.onNext(((Operation) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method SeekSubscription, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Operation.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void createReservation(
      CreateReservationRequest request, StreamObserver<Reservation> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Reservation) {
      requests.add(request);
      responseObserver.onNext(((Reservation) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CreateReservation, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Reservation.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void getReservation(
      GetReservationRequest request, StreamObserver<Reservation> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Reservation) {
      requests.add(request);
      responseObserver.onNext(((Reservation) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method GetReservation, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Reservation.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listReservations(
      ListReservationsRequest request, StreamObserver<ListReservationsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListReservationsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListReservationsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListReservations, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListReservationsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void updateReservation(
      UpdateReservationRequest request, StreamObserver<Reservation> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Reservation) {
      requests.add(request);
      responseObserver.onNext(((Reservation) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method UpdateReservation, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Reservation.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void deleteReservation(
      DeleteReservationRequest request, StreamObserver<Empty> responseObserver) {
    Object response = responses.poll();
    if (response instanceof Empty) {
      requests.add(request);
      responseObserver.onNext(((Empty) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method DeleteReservation, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  Empty.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listReservationTopics(
      ListReservationTopicsRequest request,
      StreamObserver<ListReservationTopicsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListReservationTopicsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListReservationTopicsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListReservationTopics, expected %s or"
                      + " %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListReservationTopicsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
