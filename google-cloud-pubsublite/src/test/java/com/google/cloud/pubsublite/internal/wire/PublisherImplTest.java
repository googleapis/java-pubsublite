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

import static com.google.cloud.pubsublite.internal.wire.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.google.api.core.ApiService.Listener;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc.PublisherServiceStub;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class PublisherImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static final PublishRequest INITIAL_PUBLISH_REQUEST =
      PublishRequest.newBuilder()
          .setInitialRequest(InitialPublishRequest.newBuilder().setPartition(7).build())
          .build();
  private static final BatchingSettings BATCHING_SETTINGS_THAT_NEVER_FIRE =
      BatchingSettings.newBuilder()
          .setIsEnabled(true)
          .setDelayThreshold(Duration.ofDays(10))
          .setRequestByteThreshold(1000000L)
          .setElementCountThreshold(1000000L)
          .build();

  private final BatchPublisher mockBatchPublisher = mock(BatchPublisher.class);
  private final BatchPublisherFactory mockPublisherFactory = mock(BatchPublisherFactory.class);
  private final Listener permanentErrorHandler = mock(Listener.class);
  private Future<Void> errorOccurredFuture;

  private PublisherImpl publisher;
  private StreamObserver<Offset> leakedOffsetStream;

  @Before
  public void setUp() throws StatusException {
    doAnswer(
            args -> {
              leakedOffsetStream = args.getArgument(1);
              return mockBatchPublisher;
            })
        .when(mockPublisherFactory)
        .New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    errorOccurredFuture = whenFailed(permanentErrorHandler);
    ManagedChannel channel =
        grpcCleanup.register(
            InProcessChannelBuilder.forName("localhost:12345").directExecutor().build());
    PublisherServiceStub unusedStub = PublisherServiceGrpc.newStub(channel);
    publisher =
        new PublisherImpl(
            unusedStub,
            mockPublisherFactory,
            INITIAL_PUBLISH_REQUEST.getInitialRequest(),
            BATCHING_SETTINGS_THAT_NEVER_FIRE);
    publisher.addListener(permanentErrorHandler, MoreExecutors.directExecutor());
  }

  private void startPublisher() {
    publisher.startAsync().awaitRunning();

    assertThat(leakedOffsetStream).isNotNull();
    verify(mockPublisherFactory).New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
  }

  @Test
  public void construct_CallsFactoryNew() {
    startPublisher();
    verifyNoMoreInteractions(mockPublisherFactory);
    verifyZeroInteractions(mockBatchPublisher);
  }

  @Test
  public void construct_FlushSendsBatched() throws Exception {
    startPublisher();
    Message message = Message.builder().build();
    Future<Offset> future = publisher.publish(message);

    doAnswer(
            (Answer<Void>)
                args -> {
                  leakedOffsetStream.onNext(Offset.of(10));
                  return null;
                })
        .when(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message.toProto())));

    publisher.flush();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message.toProto())));
    assertThat(future.get()).isEqualTo(Offset.of(10));
    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void construct_CloseSendsBatched() throws Exception {
    startPublisher();
    Message message = Message.builder().build();
    Future<Offset> future = publisher.publish(message);

    doAnswer(
            (Answer<Void>)
                args -> {
                  leakedOffsetStream.onNext(Offset.of(10));
                  return null;
                })
        .when(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message.toProto())));

    publisher.stopAsync().awaitTerminated();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message.toProto())));
    assertThat(future.get()).isEqualTo(Offset.of(10));
    verify(mockBatchPublisher).close();
    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void publishBeforeStart_IsPermanentError() throws Exception {
    Message message = Message.builder().build();
    assertThrows(IllegalStateException.class, () -> publisher.publish(message));
    assertThrows(IllegalStateException.class, () -> publisher.startAsync().awaitRunning());
    verifyZeroInteractions(mockPublisherFactory);
    verifyZeroInteractions(mockBatchPublisher);
  }

  @Test
  public void publishAfterError_IsError() throws Exception {
    startPublisher();
    leakedOffsetStream.onError(Status.PERMISSION_DENIED.asRuntimeException());
    assertThrows(IllegalStateException.class, publisher::awaitTerminated);
    errorOccurredFuture.get();
    verify(permanentErrorHandler).failed(any(), ArgumentMatchers.<StatusRuntimeException>any());
    Message message = Message.builder().build();
    Future<Offset> future = publisher.publish(message);
    ExecutionException e = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(e.getCause());
    assertThat(statusOr.isPresent()).isTrue();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);

    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void multipleBatches_Ok() throws Exception {
    startPublisher();
    Message message1 = Message.builder().build();
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("data")).build();
    Message message3 = Message.builder().setData(ByteString.copyFromUtf8("other_data")).build();
    Future<Offset> future1 = publisher.publish(message1);
    Future<Offset> future2 = publisher.publish(message2);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    Future<Offset> future3 = publisher.publish(message3);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(11));
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(12));
    assertThat(future3.isDone()).isTrue();
    assertThat(future3.get()).isEqualTo(Offset.of(12));

    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void retryableError_RecreatesAndRetriesAll() throws Exception {
    startPublisher();
    Message message1 = Message.builder().setData(ByteString.copyFromUtf8("message1")).build();
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("message2")).build();
    Future<Offset> future1 = publisher.publish(message1);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message1.toProto())));
    publisher.flushToStream();
    Future<Offset> future2 = publisher.publish(message2);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message2.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    BatchPublisher mockBatchPublisher2 = mock(BatchPublisher.class);
    doReturn(mockBatchPublisher2)
        .when(mockPublisherFactory)
        .New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    leakedOffsetStream.onError(Status.UNKNOWN.asRuntimeException());

    // wait for retry to complete
    Thread.sleep(500);

    verify(mockBatchPublisher).close();
    verifyNoMoreInteractions(mockBatchPublisher);
    verify(mockPublisherFactory, times(2)).New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    verify(mockBatchPublisher2)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message1.toProto())));
    verify(mockBatchPublisher2)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message2.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(50));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(50));

    verifyNoMoreInteractions(mockBatchPublisher, mockBatchPublisher2);
  }

  @Test
  public void invalidOffsetSequence_SetsPermanentException() throws Exception {
    startPublisher();
    Message message1 = Message.builder().build();
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("data")).build();
    Message message3 = Message.builder().setData(ByteString.copyFromUtf8("other_data")).build();
    Future<Offset> future1 = publisher.publish(message1);
    Future<Offset> future2 = publisher.publish(message2);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    Future<Offset> future3 = publisher.publish(message3);
    publisher.flushToStream();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(11));
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onNext(Offset.of(11));
    assertThrows(IllegalStateException.class, publisher::awaitTerminated);
    assertThat(future3.isDone()).isTrue();
    assertThrows(Exception.class, future3::get);
    errorOccurredFuture.get();
    verify(permanentErrorHandler).failed(any(), any());

    verifyNoMoreInteractions(mockBatchPublisher);
  }
}
