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
import static org.mockito.Mockito.inOrder;
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
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.InitialPublishResponse;
import com.google.cloud.pubsublite.proto.MessagePublishRequest;
import com.google.cloud.pubsublite.proto.MessagePublishResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class BatchPublisherImplTest {
  private static PublishRequest initialRequest() {
    return PublishRequest.newBuilder()
        .setInitialRequest(
            InitialPublishRequest.newBuilder()
                .setTopic(
                    TopicPath.newBuilder()
                        .setProject(ProjectNumber.of(1864654))
                        .setName(TopicName.of("some_topic"))
                        .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                        .build()
                        .toString())
                .setPartition(1024)
                .build())
        .build();
  }

  private static final BatchPublisherImpl.Factory FACTORY = new BatchPublisherImpl.Factory();

  @Mock private PublishStreamFactory streamFactory;

  @Mock private ClientStream<PublishRequest> mockRequestStream;

  @Mock private ResponseObserver<Offset> mockOutputStream;

  private Optional<ResponseObserver<PublishResponse>> leakedResponseStream = Optional.empty();

  public BatchPublisherImplTest() {}

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    doAnswer(
            (Answer<ClientStream<PublishRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  ResponseObserver<PublishResponse> ResponseObserver = args.getArgument(0);
                  leakedResponseStream = Optional.of(ResponseObserver);
                  return mockRequestStream;
                })
        .when(streamFactory)
        .New(any());
  }

  @After
  public void tearDown() {
    if (leakedResponseStream.isPresent()) {
      leakedResponseStream.get().onComplete();
    }
  }

  private class OffsetAnswer implements Answer<Void> {
    private final Offset offset;

    OffsetAnswer(Offset offset) {
      this.offset = offset;
    }

    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream
          .get()
          .onResponse(
              PublishResponse.newBuilder()
                  .setMessageResponse(
                      MessagePublishResponse.newBuilder()
                          .setStartCursor(Cursor.newBuilder().setOffset(offset.value())))
                  .build());
      verify(mockRequestStream).send(initialRequest());
      return null;
    }
  }

  @Test
  public void construct_SendsInitialThenResponse() {
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream
                      .get()
                      .onResponse(
                          PublishResponse.newBuilder()
                              .setInitialResponse(InitialPublishResponse.getDefaultInstance())
                              .build());
                  return null;
                })
        .when(mockRequestStream)
        .send(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() {
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream.get().onError(new CheckedApiException(Code.INTERNAL));
                  leakedResponseStream = Optional.empty();
                  return null;
                })
        .when(mockRequestStream)
        .send(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.INTERNAL)));
      verifyNoMoreInteractions(mockOutputStream);
    }
  }

  @Test
  public void construct_SendsMessagePublishResponseError() {
    doAnswer(new OffsetAnswer(Offset.of(10))).when(mockRequestStream).send(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  private BatchPublisherImpl initialize() {
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream
                      .get()
                      .onResponse(
                          PublishResponse.newBuilder()
                              .setInitialResponse(InitialPublishResponse.getDefaultInstance())
                              .build());
                  return null;
                })
        .when(mockRequestStream)
        .send(initialRequest());
    return FACTORY.New(streamFactory, mockOutputStream, initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() throws Exception {
    BatchPublisher publisher = initialize();
    publisher.close();
    verify(mockRequestStream).closeSend();
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    verify(mockOutputStream, never()).onResponse(any());
  }

  @Test
  public void duplicateInitial_Abort() {
    BatchPublisher unusedPublisher = initialize();
    PublishResponse.Builder builder = PublishResponse.newBuilder();
    builder.getInitialResponseBuilder();
    leakedResponseStream.get().onResponse(builder.build());
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  private static PublishRequest messagePublishRequest(PubSubMessage... messages) {
    return PublishRequest.newBuilder()
        .setMessagePublishRequest(
            MessagePublishRequest.newBuilder().addAllMessages(Arrays.asList(messages)))
        .build();
  }

  @Test
  public void offsetResponseInOrder_Ok() {
    BatchPublisher publisher = initialize();
    doAnswer(new OffsetAnswer(Offset.of(10)))
        .when(mockRequestStream)
        .send(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    doAnswer(new OffsetAnswer(Offset.of(20)))
        .when(mockRequestStream)
        .send(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    publisher.publish(
        ImmutableList.of(
            PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));

    InOrder requests = inOrder(mockRequestStream);
    requests
        .verify(mockRequestStream)
        .send(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    requests
        .verify(mockRequestStream)
        .send(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    InOrder outputs = inOrder(mockOutputStream);
    outputs.verify(mockOutputStream).onResponse(Offset.of(10));
    outputs.verify(mockOutputStream).onResponse(Offset.of(20));
    verifyNoMoreInteractions(mockRequestStream);
    verifyNoMoreInteractions(mockOutputStream);
  }

  @Test
  public void offsetResponseOutOfOrder_Exception() {
    BatchPublisher publisher = initialize();
    doAnswer(new OffsetAnswer(Offset.of(10)))
        .when(mockRequestStream)
        .send(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    doAnswer(new OffsetAnswer(Offset.of(5)))
        .when(mockRequestStream)
        .send(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    publisher.publish(
        ImmutableList.of(
            PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));

    InOrder requests = inOrder(mockRequestStream);
    requests
        .verify(mockRequestStream)
        .send(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    requests
        .verify(mockRequestStream)
        .send(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    requests.verify(mockRequestStream).closeSendWithError(argThat(new ApiExceptionMatcher()));
    InOrder outputs = inOrder(mockOutputStream);
    outputs.verify(mockOutputStream).onResponse(Offset.of(10));
    outputs
        .verify(mockOutputStream)
        .onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
    verifyNoMoreInteractions(mockRequestStream, mockOutputStream);

    leakedResponseStream = Optional.empty();
  }
}
