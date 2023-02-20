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

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.proto.InitialCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class ConnectedCommitterImplTest {
  private static StreamingCommitCursorRequest initialRequest() {
    return StreamingCommitCursorRequest.newBuilder()
        .setInitial(
            InitialCommitCursorRequest.newBuilder()
                .setSubscription(
                    SubscriptionPath.newBuilder()
                        .setProject(ProjectNumber.of(12345))
                        .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                        .setName(SubscriptionName.of("some_subscription"))
                        .build()
                        .toString())
                .setPartition(1024))
        .build();
  }

  private static final ConnectedCommitterImpl.Factory FACTORY =
      new ConnectedCommitterImpl.Factory();

  @Mock
  private StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory;

  @Mock private ClientStream<StreamingCommitCursorRequest> mockRequestStream;

  @Mock private ResponseObserver<SequencedCommitCursorResponse> mockOutputStream;

  private Optional<ResponseObserver<StreamingCommitCursorResponse>> leakedResponseStream =
      Optional.empty();

  private ConnectedCommitter committer;

  public ConnectedCommitterImplTest() {}

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    doAnswer(
            (Answer<ClientStream<StreamingCommitCursorRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  ResponseObserver<StreamingCommitCursorResponse> responseObserver =
                      args.getArgument(0);
                  leakedResponseStream = Optional.of(responseObserver);
                  return mockRequestStream;
                })
        .when(streamFactory)
        .New(any());
  }

  @After
  public void tearDown() {
    leakedResponseStream.ifPresent(ResponseObserver::onComplete);
  }

  private Answer<Void> AnswerWith(StreamingCommitCursorResponse response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onResponse(response);
      return null;
    };
  }

  private Answer<Void> AnswerWith(StreamingCommitCursorResponse.Builder response) {
    return AnswerWith(response.build());
  }

  private Answer<Void> AnswerWith(Code error) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onError(new CheckedApiException(error).underlying);
      leakedResponseStream = Optional.empty();
      verify(mockRequestStream).closeSendWithError(argThat(new ApiExceptionMatcher(error)));
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(error)));
      verifyNoMoreInteractions(mockOutputStream);
      return null;
    };
  }

  @Test
  public void construct_SendsInitialThenResponse() throws Exception {
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setInitial(InitialCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() throws Exception {
    doAnswer(AnswerWith(Code.INTERNAL)).when(mockRequestStream).send(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsCommitResponseError() throws Exception {
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setCommit(SequencedCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedCommitter committer =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  private void initialize() {
    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setInitial(InitialCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    committer = FACTORY.New(streamFactory, mockOutputStream, initialRequest());
    verify(mockRequestStream).send(initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() throws Exception {
    initialize();
    committer.close();
    verify(mockRequestStream).closeSend();
    committer.commit(Offset.of(10));
    verify(mockOutputStream, never()).onResponse(any());
  }

  @Test
  public void duplicateInitial_Abort() {
    initialize();
    StreamingCommitCursorResponse.Builder builder = StreamingCommitCursorResponse.newBuilder();
    builder.getInitialBuilder();
    leakedResponseStream.get().onResponse(builder.build());
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void commitRequestProxied() {
    initialize();
    StreamingCommitCursorRequest.Builder builder = StreamingCommitCursorRequest.newBuilder();
    builder.getCommitBuilder().setCursor(Cursor.newBuilder().setOffset(154));
    committer.commit(Offset.of(154));
    verify(mockRequestStream).send(builder.build());
  }

  @Test
  public void commitResponseProxied() {
    initialize();
    leakedResponseStream
        .get()
        .onResponse(
            StreamingCommitCursorResponse.newBuilder()
                .setCommit(SequencedCommitCursorResponse.newBuilder().setAcknowledgedCommits(10))
                .build());
    verify(mockOutputStream)
        .onResponse(SequencedCommitCursorResponse.newBuilder().setAcknowledgedCommits(10).build());
  }

  @Test
  public void receiveTimeout_closesConnection() throws Exception {
    CountDownLatch requestClosed = new CountDownLatch(1);
    doAnswer(
            args -> {
              requestClosed.countDown();
              return null;
            })
        .when(mockRequestStream)
        .closeSendWithError(any());
    CountDownLatch outputClosed = new CountDownLatch(1);
    doAnswer(
            args -> {
              outputClosed.countDown();
              return null;
            })
        .when(mockOutputStream)
        .onError(any());

    doAnswer(
            AnswerWith(
                StreamingCommitCursorResponse.newBuilder()
                    .setInitial(InitialCommitCursorResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    committer =
        new ConnectedCommitterImpl(
            streamFactory, mockOutputStream, initialRequest(), Duration.ofMillis(100));
    verify(mockRequestStream).send(initialRequest());

    // No subsequent stream responses should close the stream.
    assertThat(requestClosed.await(30, SECONDS)).isTrue();
    assertThat(outputClosed.await(30, SECONDS)).isTrue();

    verify(mockRequestStream).closeSendWithError(argThat(new ApiExceptionMatcher(Code.ABORTED)));
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.ABORTED)));

    committer.close();
    verifyNoMoreInteractions(mockRequestStream);
    verifyNoMoreInteractions(mockOutputStream);
  }

  @Test
  public void initializationTimeout_closesConnection() throws Exception {
    CountDownLatch connectionClosed = new CountDownLatch(1);
    doAnswer(
            args -> {
              connectionClosed.countDown();
              return null;
            })
        .when(mockOutputStream)
        .onError(any());

    // No initial response should close the stream.
    committer =
        new ConnectedCommitterImpl(
            streamFactory, mockOutputStream, initialRequest(), Duration.ofMillis(100));

    assertThat(connectionClosed.await(30, SECONDS)).isTrue();

    verify(mockRequestStream).send(initialRequest());
    verify(mockRequestStream).closeSendWithError(argThat(new ApiExceptionMatcher(Code.ABORTED)));
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.ABORTED)));

    committer.close();
    verifyNoMoreInteractions(mockRequestStream);
    verifyNoMoreInteractions(mockOutputStream);
  }
}
