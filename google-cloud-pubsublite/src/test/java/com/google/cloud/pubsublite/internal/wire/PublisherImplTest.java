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

import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertThrowableMatches;
import static com.google.cloud.pubsublite.internal.testing.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.proto.InitialPublishRequest;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.protobuf.ByteString;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class PublisherImplTest {
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

  @Mock private StreamFactory<PublishRequest, PublishResponse> unusedStreamFactory;
  @Mock private BatchPublisher mockBatchPublisher;
  @Mock private BatchPublisherFactory mockPublisherFactory;
  @Mock private AlarmFactory alarmFactory;

  private PublisherImpl publisher;
  private Future<Void> errorOccurredFuture;
  private ResponseObserver<Offset> leakedOffsetStream;
  private Runnable leakedBatchAlarm;

  @Before
  public void setUp() throws CheckedApiException {
    initMocks(this);
    doAnswer(
            args -> {
              leakedOffsetStream = args.getArgument(1);
              return mockBatchPublisher;
            })
        .when(mockPublisherFactory)
        .New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    when(alarmFactory.newAlarm(any()))
        .thenAnswer(
            args -> {
              leakedBatchAlarm = args.getArgument(0);
              return SettableApiFuture.create();
            });
    publisher =
        new PublisherImpl(
            unusedStreamFactory,
            mockPublisherFactory,
            alarmFactory,
            INITIAL_PUBLISH_REQUEST.getInitialRequest(),
            BATCHING_SETTINGS_THAT_NEVER_FIRE);
    errorOccurredFuture = whenFailed(publisher);
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
                  leakedOffsetStream.onResponse(Offset.of(10));
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
                  leakedOffsetStream.onResponse(Offset.of(10));
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
    leakedOffsetStream.onError(new CheckedApiException(Code.FAILED_PRECONDITION).underlying);
    assertThrows(IllegalStateException.class, publisher::awaitTerminated);
    errorOccurredFuture.get();
    assertThrowableMatches(publisher.failureCause(), Code.FAILED_PRECONDITION);
    Message message = Message.builder().build();
    Future<Offset> future = publisher.publish(message);
    ExecutionException e = assertThrows(ExecutionException.class, future::get);
    Optional<CheckedApiException> statusOr = ExtractStatus.extract(e.getCause());
    assertThat(statusOr.isPresent()).isTrue();
    assertThat(statusOr.get().code()).isEqualTo(Code.FAILED_PRECONDITION);

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
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    Future<Offset> future3 = publisher.publish(message3);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(11));
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(12));
    assertThat(future3.isDone()).isTrue();
    assertThat(future3.get()).isEqualTo(Offset.of(12));

    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void retryableError_RecreatesAndRetriesAll() throws Exception {
    startPublisher();
    Message message1 =
        Message.builder()
            .setData(ByteString.copyFrom(new byte[(int) Constants.MAX_PUBLISH_BATCH_BYTES - 20]))
            .build();
    Message message2 =
        Message.builder()
            .setData(ByteString.copyFromUtf8(String.join("", Collections.nCopies(21, "a"))))
            .build();
    Future<Offset> future1 = publisher.publish(message1);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message1.toProto())));
    leakedBatchAlarm.run();
    Future<Offset> future2 = publisher.publish(message2);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message2.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    BatchPublisher mockBatchPublisher2 = mock(BatchPublisher.class);
    doReturn(mockBatchPublisher2)
        .when(mockPublisherFactory)
        .New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    leakedOffsetStream.onError(new CheckedApiException(Code.UNKNOWN));

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

    leakedOffsetStream.onResponse(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(50));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(50));

    verifyNoMoreInteractions(mockBatchPublisher, mockBatchPublisher2);
  }

  @Test
  public void retryableError_RebatchesProperly() throws Exception {
    startPublisher();
    Message message1 = Message.builder().setData(ByteString.copyFromUtf8("message1")).build();
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("message2")).build();
    Message message3 =
        Message.builder()
            .setData(ByteString.copyFrom(new byte[(int) Constants.MAX_PUBLISH_BATCH_BYTES - 20]))
            .build();
    Message message4 =
        Message.builder()
            .setData(ByteString.copyFromUtf8(String.join("", Collections.nCopies(21, "a"))))
            .build();
    List<Message> remaining =
        IntStream.range(0, (int) Constants.MAX_PUBLISH_BATCH_COUNT)
            .mapToObj(x -> Message.builder().setData(ByteString.copyFromUtf8("clone-" + x)).build())
            .collect(Collectors.toList());

    Future<Offset> future1 = publisher.publish(message1);
    Future<Offset> future2 = publisher.publish(message2);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    leakedBatchAlarm.run();
    Future<Offset> future3 = publisher.publish(message3);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));
    Future<Offset> future4 = publisher.publish(message4);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message4.toProto())));
    List<Future<Offset>> remainingFutures =
        remaining.stream().map(publisher::publish).collect(Collectors.toList());
    leakedBatchAlarm.run();

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();
    assertThat(future4.isDone()).isFalse();
    for (Future<Offset> future : remainingFutures) {
      assertThat(future.isDone()).isFalse();
    }

    BatchPublisher mockBatchPublisher2 = mock(BatchPublisher.class);
    doReturn(mockBatchPublisher2)
        .when(mockPublisherFactory)
        .New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    leakedOffsetStream.onError(new CheckedApiException(Code.UNKNOWN));

    // wait for retry to complete
    Thread.sleep(500);

    verify(mockBatchPublisher).close();
    verify(mockPublisherFactory, times(2)).New(any(), any(), eq(INITIAL_PUBLISH_REQUEST));
    InOrder order = inOrder(mockBatchPublisher2);
    order
        .verify(mockBatchPublisher2)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    order
        .verify(mockBatchPublisher2)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));
    ImmutableList.Builder<PubSubMessage> expectedRebatch = ImmutableList.builder();
    expectedRebatch.add(message4.toProto());
    for (int i = 0; i < (Constants.MAX_PUBLISH_BATCH_COUNT - 1); ++i) {
      expectedRebatch.add(remaining.get(i).toProto());
    }
    order
        .verify(mockBatchPublisher2)
        .publish((Collection<PubSubMessage>) argThat(contains(expectedRebatch.build().toArray())));
    order
        .verify(mockBatchPublisher2)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(Iterables.getLast(remaining).toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();
    assertThat(future4.isDone()).isFalse();
    for (Future<Offset> future : remainingFutures) {
      assertThat(future.isDone()).isFalse();
    }

    leakedOffsetStream.onResponse(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(11));
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(50));
    assertThat(future3.isDone()).isTrue();
    assertThat(future3.get()).isEqualTo(Offset.of(50));
    assertThat(future4.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(100));
    assertThat(future4.isDone()).isTrue();
    assertThat(future4.get()).isEqualTo(Offset.of(100));
    for (int i = 0; i < (Constants.MAX_PUBLISH_BATCH_COUNT - 1); ++i) {
      Future<Offset> future = remainingFutures.get(i);
      assertThat(future.isDone()).isTrue();
      assertThat(future.get()).isEqualTo(Offset.of(100 + 1 + i));
    }
    Future<Offset> lastFuture = Iterables.getLast(remainingFutures);
    assertThat(lastFuture.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(10000));
    assertThat(lastFuture.isDone()).isTrue();
    assertThat(lastFuture.get()).isEqualTo(Offset.of(10000));
  }

  @Test
  public void invalidOffsetSequence_SetsPermanentException() throws Exception {
    startPublisher();
    Message message1 = Message.builder().build();
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("data")).build();
    Message message3 = Message.builder().setData(ByteString.copyFromUtf8("other_data")).build();
    Future<Offset> future1 = publisher.publish(message1);
    Future<Offset> future2 = publisher.publish(message2);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish(
            (Collection<PubSubMessage>) argThat(hasItems(message1.toProto(), message2.toProto())));
    Future<Offset> future3 = publisher.publish(message3);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message3.toProto())));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(10));
    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(10));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(11));
    assertThat(future3.isDone()).isFalse();

    leakedOffsetStream.onResponse(Offset.of(11));
    assertThrows(IllegalStateException.class, publisher::awaitTerminated);
    assertThat(future3.isDone()).isTrue();
    assertThrows(Exception.class, future3::get);
    errorOccurredFuture.get();
    assertThrowableMatches(publisher.failureCause(), Code.FAILED_PRECONDITION);

    verifyNoMoreInteractions(mockBatchPublisher);
  }

  @Test
  public void cancelOutstandingPublishes_terminatesFutures() throws Exception {
    startPublisher();

    // Publish a message and flush to stream.
    Message message1 = Message.builder().setData(ByteString.copyFromUtf8("data")).build();
    Future<Offset> future1 = publisher.publish(message1);
    leakedBatchAlarm.run();
    verify(mockBatchPublisher)
        .publish((Collection<PubSubMessage>) argThat(hasItems(message1.toProto())));

    // Publish another message but do not flush to stream yet.
    Message message2 = Message.builder().setData(ByteString.copyFromUtf8("other_data")).build();
    Future<Offset> future2 = publisher.publish(message2);

    // Cancel outstanding publishes and verify that both futures complete with a cancelled status.
    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    publisher.cancelOutstandingPublishes();
    assertThat(future1.isDone()).isTrue();
    ExecutionException e1 = assertThrows(ExecutionException.class, future1::get);
    assertThat(ExtractStatus.extract(e1.getCause()).get().code()).isEqualTo(Code.CANCELLED);
    assertThat(future2.isDone()).isTrue();
    ExecutionException e2 = assertThrows(ExecutionException.class, future2::get);
    assertThat(ExtractStatus.extract(e2.getCause()).get().code()).isEqualTo(Code.CANCELLED);
  }
}
