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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
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
import com.google.cloud.pubsublite.internal.wire.StreamFactories.SubscribeStreamFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeResponse;
import com.google.cloud.pubsublite.proto.MessageResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.SeekResponse;
import com.google.cloud.pubsublite.proto.SequencedMessage;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class ConnectedSubscriberImplTest {
  private static SubscribeRequest initialRequest() {
    return SubscribeRequest.newBuilder()
        .setInitial(
            InitialSubscribeRequest.newBuilder()
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

  private static final ConnectedSubscriberImpl.Factory FACTORY =
      new ConnectedSubscriberImpl.Factory();

  private static final Offset INITIAL_OFFSET = Offset.of(9000);

  @Mock SubscribeStreamFactory streamFactory;

  @Mock private ClientStream<SubscribeRequest> mockRequestStream;

  @Mock private ResponseObserver<List<SequencedMessage>> mockOutputStream;

  private Optional<ResponseObserver<SubscribeResponse>> leakedResponseStream = Optional.empty();

  private ConnectedSubscriberImpl subscriber;

  public ConnectedSubscriberImplTest() {}

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    when(streamFactory.New(any()))
        .then(
            args -> {
              Preconditions.checkArgument(!leakedResponseStream.isPresent());
              ResponseObserver<SubscribeResponse> responseObserver = args.getArgument(0);
              leakedResponseStream = Optional.of(responseObserver);
              return mockRequestStream;
            });
  }

  @After
  public void tearDown() {
    if (leakedResponseStream.isPresent()) {
      leakedResponseStream.get().onComplete();
    }
  }

  private Answer<Void> AnswerWith(SubscribeResponse response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onResponse(response);
      verify(mockRequestStream).send(initialRequest());
      return null;
    };
  }

  private Answer<Void> AnswerWith(SubscribeResponse.Builder response) {
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
  public void construct_SendsInitialThenResponse() {
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder()
                    .setInitial(InitialSubscribeResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() {
    doAnswer(AnswerWith(Code.INTERNAL)).when(mockRequestStream).send(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsMessageResponseError() {
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder().setMessages(MessageResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void construct_SendsSeekResponseError() {
    doAnswer(AnswerWith(SubscribeResponse.newBuilder().setSeek(SeekResponse.getDefaultInstance())))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedSubscriberImpl subscriber =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  private void initialize() {
    doAnswer(
            AnswerWith(
                SubscribeResponse.newBuilder()
                    .setInitial(
                        InitialSubscribeResponse.newBuilder()
                            .setCursor(Cursor.newBuilder().setOffset(INITIAL_OFFSET.value())))))
        .when(mockRequestStream)
        .send(initialRequest());
    subscriber = FACTORY.New(streamFactory, mockOutputStream, initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() {
    initialize();
    subscriber.close();
    verify(mockRequestStream).closeSend();
    leakedResponseStream
        .get()
        .onResponse(
            SubscribeResponse.newBuilder()
                .setMessages(
                    MessageResponse.newBuilder().addMessages(messageWithOffset(Offset.of(20))))
                .build());
    verify(mockOutputStream, never()).onResponse(any());
  }

  @Test
  public void emptyMessagesResponse_Abort() {
    initialize();
    SubscribeResponse.Builder builder =
        SubscribeResponse.newBuilder().setMessages(MessageResponse.getDefaultInstance());
    leakedResponseStream.get().onResponse(builder.build());
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  private SequencedMessage messageWithOffset(Offset offset) {
    return SequencedMessage.newBuilder()
        .setMessage(PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")))
        .setPublishTime(Timestamps.EPOCH)
        .setCursor(Cursor.newBuilder().setOffset(offset.value()))
        .setSizeBytes(123)
        .build();
  }

  @Test
  public void outOfOrderMessagesResponse_Abort() {
    initialize();
    SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    leakedResponseStream.get().onResponse(builder.build());
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void validMessagesResponse() {
    initialize();
    SubscribeResponse.Builder builder = SubscribeResponse.newBuilder();
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(10)));
    builder.getMessagesBuilder().addMessages(messageWithOffset(Offset.of(11)));
    leakedResponseStream.get().onResponse(builder.build());
    verify(mockOutputStream)
        .onResponse(
            ImmutableList.of(messageWithOffset(Offset.of(10)), messageWithOffset(Offset.of(11))));
  }

  @Test
  public void allowFlowRequest() {
    initialize();
    FlowControlRequest request =
        FlowControlRequest.newBuilder().setAllowedBytes(2).setAllowedMessages(3).build();
    subscriber.allowFlow(request);
    verify(mockRequestStream).send(SubscribeRequest.newBuilder().setFlowControl(request).build());
  }

  @Test
  public void seekResponse_Aborts() {
    initialize();
    leakedResponseStream
        .get()
        .onResponse(
            SubscribeResponse.newBuilder()
                .setSeek(SeekResponse.newBuilder().setCursor(Cursor.newBuilder().setOffset(10)))
                .build());
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    verify(mockRequestStream)
        .closeSendWithError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }
}
