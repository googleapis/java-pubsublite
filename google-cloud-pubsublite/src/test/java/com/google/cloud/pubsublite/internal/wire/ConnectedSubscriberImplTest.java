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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeResponse;
import com.google.cloud.pubsublite.proto.MessageResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekResponse;
import com.google.cloud.pubsublite.proto.SequencedMessage;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
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
public class ConnectedSubscriberImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static SubscribeRequest initialRequest() {
    try {
      return SubscribeRequest.newBuilder()
          .setInitial(
              InitialSubscribeRequest.newBuilder()
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

  private static final ConnectedSubscriberImpl.Factory FACTORY =
      new ConnectedSubscriberImpl.Factory();

  private static final Offset INITIAL_OFFSET = Offset.of(9000);

  private SubscriberServiceGrpc.SubscriberServiceStub stub;

  @SuppressWarnings("unchecked")
  private final StreamObserver<SubscribeRequest> mockRequestStream = mock(StreamObserver.class);

  @SuppressWarnings("unchecked")
  private final StreamObserver<Response> mockOutputStream = mock(StreamObserver.class);

  private final SubscriberServiceGrpc.SubscriberServiceImplBase serviceImpl =
      mock(
          SubscriberServiceGrpc.SubscriberServiceImplBase.class,
          delegatesTo(new SubscriberServiceGrpc.SubscriberServiceImplBase() {}));

  private Optional<StreamObserver<SubscribeResponse>> leakedResponseStream = Optional.empty();

  private ConnectedSubscriberImpl subscriber;

  public ConnectedSubscriberImplTest() {}

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
    stub = SubscriberServiceGrpc.newStub(channel);

    doAnswer(
            (Answer<StreamObserver<SubscribeRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  StreamObserver<SubscribeResponse> responseObserver = args.getArgument(0);
                  leakedResponseStream = Optional.of(responseObserver);
                  return mockRequestStream;
                })
        .when(serviceImpl)
        .subscribe(any());
    Preconditions.checkNotNull(serviceImpl);
  }

  @After
  public void tearDown() {
    if (leakedResponseStream.isPresent()) {
      leakedResponseStream.get().onCompleted();
    }
  }

  private Answer<Void> AnswerWith(SubscribeResponse response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onNext(response);
      verify(mockRequestStream).onNext(initialRequest());
      return null;
    };
  }

  private Answer<Void> AnswerWith(SubscribeResponse.Builder response) {
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
  public void construct_SendsInitialThenResponse() {
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder()
                    .setInitial(InitialSubscribeResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(stub::subscribe, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() {
    doAnswer(AnswerWith(Status.INTERNAL)).when(mockRequestStream).onNext(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(stub::subscribe, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsMessageResponseError() {
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder().setMessages(MessageResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(stub::subscribe, mockOutputStream, initialRequest())) {
      verify(mockOutputStream)
          .onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void construct_SendsSeekResponseError() {
    doAnswer(AnswerWith(SubscribeResponse.newBuilder().setSeek(SeekResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(stub::subscribe, mockOutputStream, initialRequest())) {
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
                SubscribeResponse.newBuilder()
                    .setInitial(
                        InitialSubscribeResponse.newBuilder()
                            .setCursor(Cursor.newBuilder().setOffset(INITIAL_OFFSET.value())))))
        .when(mockRequestStream)
        .onNext(initialRequest());
    subscriber = FACTORY.New(stub::subscribe, mockOutputStream, initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() {
    initialize();
    subscriber.close();
    verify(mockRequestStream).onCompleted();
    subscriber.seek(SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.HEAD).build());
    verify(mockOutputStream, never()).onNext(any());
  }

  @Test
  public void duplicateInitial_Abort() {
    initialize();
    SubscribeResponse.Builder builder =
        SubscribeResponse.newBuilder().setInitial(InitialSubscribeResponse.getDefaultInstance());
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void emptyMessagesResponse_Abort() {
    initialize();
    SubscribeResponse.Builder builder =
        SubscribeResponse.newBuilder().setMessages(MessageResponse.getDefaultInstance());
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  private SequencedMessage messageWithOffset(Offset offset) {
    return SequencedMessage.newBuilder()
        .setMessage(PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")))
        .setCursor(Cursor.newBuilder().setOffset(offset.value()))
        .build();
  }

  @Test
  public void outOfOrderMessagesResponse_Abort() {
    initialize();
    SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void validMessagesResponse() {
    initialize();
    SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(11)));
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream)
        .onNext(
            Response.ofMessages(
                ImmutableList.of(
                    com.google.cloud.pubsublite.SequencedMessage.fromProto(
                        messageWithOffset(Offset.of(10))),
                    com.google.cloud.pubsublite.SequencedMessage.fromProto(
                        messageWithOffset(Offset.of(11))))));
  }

  @Test
  public void allowFlowRequest() {
    initialize();
    FlowControlRequest request =
        FlowControlRequest.newBuilder().setAllowedBytes(2).setAllowedMessages(3).build();
    subscriber.allowFlow(request);
    verify(mockRequestStream).onNext(SubscribeRequest.newBuilder().setFlowControl(request).build());
  }

  @Test
  public void seekToOffsetRequest() {
    initialize();
    SeekRequest request =
        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(10)).build();
    subscriber.seek(request);
    verify(mockRequestStream).onNext(SubscribeRequest.newBuilder().setSeek(request).build());
  }

  @Test
  public void seekToHeadRequest() {
    initialize();
    SeekRequest request =
        SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.HEAD).build();
    subscriber.seek(request);
    verify(mockRequestStream).onNext(SubscribeRequest.newBuilder().setSeek(request).build());
  }

  @Test
  public void seekToCommitRequest() {
    initialize();
    SeekRequest request =
        SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR).build();
    subscriber.seek(request);
    verify(mockRequestStream).onNext(SubscribeRequest.newBuilder().setSeek(request).build());
  }

  SeekRequest validSeekRequest() {
    return SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.HEAD).build();
  }

  @Test
  public void seekRequestWhileSeekInFlight() {
    initialize();
    subscriber.seek(validSeekRequest());
    verify(mockRequestStream)
        .onNext(SubscribeRequest.newBuilder().setSeek(validSeekRequest()).build());
    subscriber.seek(validSeekRequest());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    verify(mockRequestStream).onError(argThat(new StatusExceptionMatcher(Code.CANCELLED)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void seekRequestResponseRequest() {
    initialize();
    SubscribeRequest request = SubscribeRequest.newBuilder().setSeek(validSeekRequest()).build();
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder()
                    .setSeek(SeekResponse.newBuilder().setCursor(Cursor.newBuilder().setOffset(10)))
                    .build()))
        .when(mockRequestStream)
        .onNext(request);
    subscriber.seek(validSeekRequest());
    verify(mockRequestStream).onNext(request);
    verify(mockOutputStream).onNext(Response.ofSeekOffset(Offset.of(10)));
    subscriber.seek(
        SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR).build());
    verify(mockRequestStream)
        .onNext(
            SubscribeRequest.newBuilder()
                .setSeek(
                    SeekRequest.newBuilder()
                        .setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR)
                        .build())
                .build());
  }

  @Test
  public void messagesWithOutstandingSeekDropped() {
    initialize();
    SubscribeRequest request = SubscribeRequest.newBuilder().setSeek(validSeekRequest()).build();
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder()
                    .setMessages(
                        MessageResponse.newBuilder()
                            .addMessages(messageWithOffset(Offset.of(11)))
                            .build())
                    .build()))
        .when(mockRequestStream)
        .onNext(request);
    subscriber.seek(validSeekRequest());
    verify(mockRequestStream).onNext(request);
    verify(mockOutputStream, times(0))
        .onNext(
            Response.ofMessages(
                ImmutableList.of(
                    com.google.cloud.pubsublite.SequencedMessage.fromProto(
                        messageWithOffset(Offset.of(11))))));
  }

  @Test
  public void seekResponseWithoutRequest_Aborts() {
    initialize();
    leakedResponseStream
        .get()
        .onNext(
            SubscribeResponse.newBuilder()
                .setSeek(SeekResponse.newBuilder().setCursor(Cursor.newBuilder().setOffset(10)))
                .build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    verify(mockRequestStream).onError(argThat(new StatusExceptionMatcher(Code.CANCELLED)));
    leakedResponseStream = Optional.empty();
  }
}
