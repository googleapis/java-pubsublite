/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.proto.InitialCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.common.base.Preconditions;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class ConnectedCommitterImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static StreamingCommitCursorRequest initialRequest() {
    try {
      return StreamingCommitCursorRequest.newBuilder()
          .setInitial(
              InitialCommitCursorRequest.newBuilder()
                  .setSubscription(
                      SubscriptionPaths.newBuilder()
                          .setProjectNumber(ProjectNumber.of(12345))
                          .setZone(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                          .setSubscriptionName(SubscriptionName.of("some_subscription"))
                          .build()
                          .value())
                  .setPartition(1024))
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static final ConnectedCommitterImpl.Factory FACTORY =
      new ConnectedCommitterImpl.Factory();

  private CursorServiceGrpc.CursorServiceStub stub;

  @SuppressWarnings("unchecked")
  private final StreamObserver<StreamingCommitCursorRequest> mockRequestStream =
      mock(StreamObserver.class);

  @SuppressWarnings("unchecked")
  private final StreamObserver<SequencedCommitCursorResponse> mockOutputStream =
      mock(StreamObserver.class);

  private final CursorServiceGrpc.CursorServiceImplBase serviceImpl =
      mock(
          CursorServiceGrpc.CursorServiceImplBase.class,
          delegatesTo(new CursorServiceGrpc.CursorServiceImplBase() {}));

  private Optional<StreamObserver<StreamingCommitCursorResponse>> leakedResponseStream =
      Optional.empty();

  private ConnectedCommitter committer;

  public ConnectedCommitterImplTest() {}

  @Before
  public void setUp() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(serviceImpl)
            .build()
            .start());
    ManagedChannel channel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    stub = CursorServiceGrpc.newStub(channel);

    doAnswer(
            (Answer<StreamObserver<StreamingCommitCursorRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  StreamObserver<StreamingCommitCursorResponse> responseObserver =
                      args.getArgument(0);
                  leakedResponseStream = Optional.of(responseObserver);
                  return mockRequestStream;
                })
        .when(serviceImpl)
        .streamingCommitCursor(any());
  }

  @After
  public void tearDown() {
    leakedResponseStream.ifPresent(StreamObserver::onCompleted);
  }

  private Answer<Void> AnswerWith(StreamingCommitCursorResponse response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onNext(response);
      return null;
    };
  }

  private Answer<Void> AnswerWith(StreamingCommitCursorResponse.Builder response) {
    return AnswerWith(response.build());
  }

  private Answer<Void> AnswerWith(Status error) {
    Preconditions.checkArgument(!error.isOk());
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onError(error.asRuntimeException());
      leakedResponseStream = Optional.empty();
      verify(mockRequestStream).onError(argThat(new StatusExceptionMatcher(error.getCode())));
      verifyNoMoreInteractions(mockOutputStream);
      return null;
    };
  }

  @Test
  public void construct_SendsInitialThenResponse() throws Exception {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setInitial(InitialCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(stub::streamingCommitCursor, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() throws Exception {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(AnswerWith(Status.INTERNAL)).when(mockRequestStream).onNext(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(stub::streamingCommitCursor, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsCommitResponseError() throws Exception {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setCommit(SequencedCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(stub::streamingCommitCursor, mockOutputStream, initialRequest())) {
      verify(mockOutputStream)
          .onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  private void initialize() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setInitial(InitialCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    committer = FACTORY.New(stub::streamingCommitCursor, mockOutputStream, initialRequest());
    verify(mockRequestStream).onNext(initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() throws Exception {
    initialize();
    committer.close();
    verify(mockRequestStream).onCompleted();
    committer.commit(Offset.of(10));
    verify(mockOutputStream, never()).onNext(any());
  }

  @Test
  public void duplicateInitial_Abort() {
    initialize();
    StreamingCommitCursorResponse.Builder builder = StreamingCommitCursorResponse.newBuilder();
    builder.getInitialBuilder();
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void commitRequestProxied() {
    initialize();
    StreamingCommitCursorRequest.Builder builder = StreamingCommitCursorRequest.newBuilder();
    builder.getCommitBuilder().setCursor(Cursor.newBuilder().setOffset(154));
    committer.commit(Offset.of(154));
    verify(mockRequestStream).onNext(builder.build());
  }

  @Test
  public void commitResponseProxied() {
    initialize();
    leakedResponseStream
        .get()
        .onNext(
            StreamingCommitCursorResponse.newBuilder()
                .setCommit(SequencedCommitCursorResponse.newBuilder().setAcknowledgedCommits(10))
                .build());
    verify(mockOutputStream)
        .onNext(SequencedCommitCursorResponse.newBuilder().setAcknowledgedCommits(10).build());
  }
}
