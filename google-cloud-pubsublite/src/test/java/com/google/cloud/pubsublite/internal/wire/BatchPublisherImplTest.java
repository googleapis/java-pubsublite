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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.InitialPublishResponse;
import com.google.cloud.pubsublite.proto.MessagePublishRequest;
import com.google.cloud.pubsublite.proto.MessagePublishResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
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
import java.util.Arrays;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class BatchPublisherImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static PublishRequest initialRequest() {
    try {
      return PublishRequest.newBuilder()
          .setInitialRequest(
              InitialPublishRequest.newBuilder()
                  .setTopic(
                      TopicPaths.newBuilder()
                          .setProject(ProjectNumber.of(1864654))
                          .setTopicName(TopicName.of("some_topic"))
                          .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                          .build()
                          .toString())
                  .setPartition(1024)
                  .build())
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static final BatchPublisherImpl.Factory FACTORY = new BatchPublisherImpl.Factory();

  private PublisherServiceGrpc.PublisherServiceStub stub;

  @SuppressWarnings("unchecked")
  private final StreamObserver<PublishRequest> mockRequestStream = mock(StreamObserver.class);

  @SuppressWarnings("unchecked")
  private final StreamObserver<Offset> mockOutputStream = mock(StreamObserver.class);

  private final PublisherServiceGrpc.PublisherServiceImplBase serviceImpl =
      mock(
          PublisherServiceGrpc.PublisherServiceImplBase.class,
          delegatesTo(new PublisherServiceGrpc.PublisherServiceImplBase() {}));

  private Optional<StreamObserver<PublishResponse>> leakedResponseStream = Optional.empty();

  public BatchPublisherImplTest() {}

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
    stub = PublisherServiceGrpc.newStub(channel);

    doAnswer(
            (Answer<StreamObserver<PublishRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  StreamObserver<PublishResponse> responseObserver = args.getArgument(0);
                  leakedResponseStream = Optional.of(responseObserver);
                  return mockRequestStream;
                })
        .when(serviceImpl)
        .publish(any());
  }

  @After
  public void tearDown() {
    if (leakedResponseStream.isPresent()) {
      leakedResponseStream.get().onCompleted();
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
          .onNext(
              PublishResponse.newBuilder()
                  .setMessageResponse(
                      MessagePublishResponse.newBuilder()
                          .setStartCursor(Cursor.newBuilder().setOffset(offset.value())))
                  .build());
      verify(mockRequestStream).onNext(initialRequest());
      return null;
    }
  }

  @Test
  public void construct_SendsInitialThenResponse() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream
                      .get()
                      .onNext(
                          PublishResponse.newBuilder()
                              .setInitialResponse(InitialPublishResponse.getDefaultInstance())
                              .build());
                  return null;
                })
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(stub::publish, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream.get().onError(Status.fromCode(Code.INTERNAL).asException());
                  leakedResponseStream = Optional.empty();
                  return null;
                })
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(stub::publish, mockOutputStream, initialRequest())) {
      verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.INTERNAL)));
      verifyNoMoreInteractions(mockOutputStream);
    }
  }

  @Test
  public void construct_SendsMessagePublishResponseError() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(new OffsetAnswer(Offset.of(10))).when(mockRequestStream).onNext(initialRequest());
    try (BatchPublisherImpl publisher =
        FACTORY.New(stub::publish, mockOutputStream, initialRequest())) {
      verify(mockOutputStream)
          .onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
      verifyNoMoreInteractions(mockOutputStream);
    }
    leakedResponseStream = Optional.empty();
  }

  private BatchPublisherImpl initialize() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(
            (Answer<Void>)
                args -> {
                  Preconditions.checkArgument(leakedResponseStream.isPresent());
                  leakedResponseStream
                      .get()
                      .onNext(
                          PublishResponse.newBuilder()
                              .setInitialResponse(InitialPublishResponse.getDefaultInstance())
                              .build());
                  return null;
                })
        .when(mockRequestStream)
        .onNext(initialRequest());
    return FACTORY.New(stub::publish, mockOutputStream, initialRequest());
  }

  @Test
  public void responseAfterClose_Dropped() throws Exception {
    BatchPublisher publisher = initialize();
    publisher.close();
    verify(mockRequestStream).onCompleted();
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    verify(mockOutputStream, never()).onNext(any());
  }

  @Test
  public void duplicateInitial_Abort() {
    BatchPublisher unusedPublisher = initialize();
    PublishResponse.Builder builder = PublishResponse.newBuilder();
    builder.getInitialResponseBuilder();
    leakedResponseStream.get().onNext(builder.build());
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
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
        .onNext(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    doAnswer(new OffsetAnswer(Offset.of(20)))
        .when(mockRequestStream)
        .onNext(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    publisher.publish(
        ImmutableList.of(
            PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));

    InOrder requests = inOrder(mockRequestStream);
    requests
        .verify(mockRequestStream)
        .onNext(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    requests
        .verify(mockRequestStream)
        .onNext(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    InOrder outputs = inOrder(mockOutputStream);
    outputs.verify(mockOutputStream).onNext(Offset.of(10));
    outputs.verify(mockOutputStream).onNext(Offset.of(20));
    verifyNoMoreInteractions(mockRequestStream);
    verifyNoMoreInteractions(mockOutputStream);
  }

  @Test
  public void offsetResponseOutOfOrder_Exception() {
    BatchPublisher publisher = initialize();
    doAnswer(new OffsetAnswer(Offset.of(10)))
        .when(mockRequestStream)
        .onNext(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    doAnswer(new OffsetAnswer(Offset.of(5)))
        .when(mockRequestStream)
        .onNext(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    publisher.publish(ImmutableList.of(PubSubMessage.getDefaultInstance()));
    publisher.publish(
        ImmutableList.of(
            PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));

    InOrder requests = inOrder(mockRequestStream);
    requests
        .verify(mockRequestStream)
        .onNext(messagePublishRequest(PubSubMessage.getDefaultInstance()));
    requests
        .verify(mockRequestStream)
        .onNext(
            messagePublishRequest(
                PubSubMessage.newBuilder().setData(ByteString.copyFromUtf8("abc")).build()));
    requests.verify(mockRequestStream).onError(argThat(new StatusExceptionMatcher()));
    InOrder outputs = inOrder(mockOutputStream);
    outputs.verify(mockOutputStream).onNext(Offset.of(10));
    outputs
        .verify(mockOutputStream)
        .onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    verifyNoMoreInteractions(mockRequestStream, mockOutputStream);

    leakedResponseStream = Optional.empty();
  }
}
