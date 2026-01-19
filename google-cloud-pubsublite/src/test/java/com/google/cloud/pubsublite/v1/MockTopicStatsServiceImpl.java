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
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc.TopicStatsServiceImplBase;
import com.google.protobuf.AbstractMessage;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockTopicStatsServiceImpl extends TopicStatsServiceImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockTopicStatsServiceImpl() {
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
  public void computeMessageStats(
      ComputeMessageStatsRequest request,
      StreamObserver<ComputeMessageStatsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ComputeMessageStatsResponse) {
      requests.add(request);
      responseObserver.onNext(((ComputeMessageStatsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ComputeMessageStats, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ComputeMessageStatsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void computeHeadCursor(
      ComputeHeadCursorRequest request,
      StreamObserver<ComputeHeadCursorResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ComputeHeadCursorResponse) {
      requests.add(request);
      responseObserver.onNext(((ComputeHeadCursorResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ComputeHeadCursor, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ComputeHeadCursorResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void computeTimeCursor(
      ComputeTimeCursorRequest request,
      StreamObserver<ComputeTimeCursorResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ComputeTimeCursorResponse) {
      requests.add(request);
      responseObserver.onNext(((ComputeTimeCursorResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ComputeTimeCursor, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  ComputeTimeCursorResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
