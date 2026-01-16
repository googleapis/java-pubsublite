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
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceImplBase;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.protobuf.AbstractMessage;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Generated;

@BetaApi
@Generated("by gapic-generator-java")
public class MockCursorServiceImpl extends CursorServiceImplBase {
  private List<AbstractMessage> requests;
  private Queue<Object> responses;

  public MockCursorServiceImpl() {
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
  public StreamObserver<StreamingCommitCursorRequest> streamingCommitCursor(
      final StreamObserver<StreamingCommitCursorResponse> responseObserver) {
    StreamObserver<StreamingCommitCursorRequest> requestObserver =
        new StreamObserver<StreamingCommitCursorRequest>() {
          @Override
          public void onNext(StreamingCommitCursorRequest value) {
            requests.add(value);
            final Object response = responses.remove();
            if (response instanceof StreamingCommitCursorResponse) {
              responseObserver.onNext(((StreamingCommitCursorResponse) response));
            } else if (response instanceof Exception) {
              responseObserver.onError(((Exception) response));
            } else {
              responseObserver.onError(
                  new IllegalArgumentException(
                      String.format(
                          "Unrecognized response type %s for method StreamingCommitCursor, expected"
                              + " %s or %s",
                          response == null ? "null" : response.getClass().getName(),
                          StreamingCommitCursorResponse.class.getName(),
                          Exception.class.getName())));
            }
          }

          @Override
          public void onError(Throwable t) {
            responseObserver.onError(t);
          }

          @Override
          public void onCompleted() {
            responseObserver.onCompleted();
          }
        };
    return requestObserver;
  }

  @Override
  public void commitCursor(
      CommitCursorRequest request, StreamObserver<CommitCursorResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof CommitCursorResponse) {
      requests.add(request);
      responseObserver.onNext(((CommitCursorResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method CommitCursor, expected %s or %s",
                  response == null ? "null" : response.getClass().getName(),
                  CommitCursorResponse.class.getName(),
                  Exception.class.getName())));
    }
  }

  @Override
  public void listPartitionCursors(
      ListPartitionCursorsRequest request,
      StreamObserver<ListPartitionCursorsResponse> responseObserver) {
    Object response = responses.poll();
    if (response instanceof ListPartitionCursorsResponse) {
      requests.add(request);
      responseObserver.onNext(((ListPartitionCursorsResponse) response));
      responseObserver.onCompleted();
    } else if (response instanceof Exception) {
      responseObserver.onError(((Exception) response));
    } else {
      responseObserver.onError(
          new IllegalArgumentException(
              String.format(
                  "Unrecognized response type %s for method ListPartitionCursors, expected %s or"
                      + " %s",
                  response == null ? "null" : response.getClass().getName(),
                  ListPartitionCursorsResponse.class.getName(),
                  Exception.class.getName())));
    }
  }
}
