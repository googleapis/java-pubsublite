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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.TestResetSignal;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.SubscribeStreamFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.util.Timestamps;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class SubscriberImplTest {
  private static final InitialSubscribeRequest BASE_INITIAL_SUBSCRIBE_REQUEST =
      InitialSubscribeRequest.newBuilder()
          .setSubscription(
              SubscriptionPath.newBuilder()
                  .setProject(ProjectNumber.of(12345))
                  .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                  .setName(SubscriptionName.of("some_subscription"))
                  .build()
                  .toString())
          .setPartition(1024)
          .build();
  private static final SeekRequest INITIAL_LOCATION =
      SeekRequest.newBuilder().setNamedTarget(NamedTarget.COMMITTED_CURSOR).build();

  private static final CheckedApiException DUPLICATE_CONNECTION_SIGNAL =
      new CheckedApiException(
          StatusProto.toStatusRuntimeException(
              Status.newBuilder()
                  .setCode(Code.ABORTED.ordinal())
                  .addDetails(
                      Any.pack(
                          ErrorInfo.newBuilder()
                              .setReason("DUPLICATE_SUBSCRIBER_CONNECTIONS")
                              .setDomain("pubsublite.googleapis.com")
                              .build()))
                  .build()),
          Code.ABORTED);

  private static SubscribeRequest initialRequest() {
    return SubscribeRequest.newBuilder()
        .setInitial(BASE_INITIAL_SUBSCRIBE_REQUEST.toBuilder().setInitialLocation(INITIAL_LOCATION))
        .build();
  }

  @Mock private SubscribeStreamFactory unusedStreamFactory;
  @Mock private ConnectedSubscriber mockConnectedSubscriber1;
  @Mock private ConnectedSubscriber mockConnectedSubscriber2;
  @Mock private ConnectedSubscriberFactory mockSubscriberFactory;
  @Mock private AlarmFactory alarmFactory;

  @Mock private Consumer<List<SequencedMessage>> mockMessageConsumer;
  @Mock private SubscriberResetHandler mockResetHandler;

  private SubscriberImpl subscriber;
  private ResponseObserver<List<SequencedMessage>> leakedResponseObserver;
  private Runnable leakedFlowControlAlarm;

  private CountDownLatch countdownMessageBatches(int count) {
    CountDownLatch received = new CountDownLatch(count);
    doAnswer(
            args -> {
              received.countDown();
              return null;
            })
        .when(mockMessageConsumer)
        .accept(any());
    return received;
  }

  @Before
  public void setUp() throws CheckedApiException {
    initMocks(this);
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber1;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(initialRequest()));
    when(alarmFactory.newAlarm(any()))
        .thenAnswer(
            args -> {
              leakedFlowControlAlarm = args.getArgument(0);
              return SettableApiFuture.create();
            });
    subscriber =
        new SubscriberImpl(
            unusedStreamFactory,
            mockSubscriberFactory,
            alarmFactory,
            BASE_INITIAL_SUBSCRIBE_REQUEST,
            INITIAL_LOCATION,
            mockMessageConsumer,
            mockResetHandler,
            true);
    subscriber.startAsync().awaitRunning();
  }

  @Test
  public void invalidFlowThrows() {
    assertThrows(
        CheckedApiException.class,
        () -> subscriber.allowFlow(FlowControlRequest.newBuilder().setAllowedBytes(-1).build()));
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  FlowControlRequest bigFlowControlRequest() {
    return FlowControlRequest.newBuilder()
        .setAllowedBytes(Long.MAX_VALUE)
        .setAllowedMessages(Long.MAX_VALUE)
        .build();
  }

  @Test
  public void anyFlowAllowedAndProxies() throws CheckedApiException {
    subscriber.allowFlow(bigFlowControlRequest());
    verify(mockConnectedSubscriber1).allowFlow(bigFlowControlRequest());
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void batchesFlowControlRequests() throws Exception {
    FlowControlRequest initialFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(10000).setAllowedMessages(1000).build();
    subscriber.allowFlow(initialFlowRequest);
    verify(mockConnectedSubscriber1).allowFlow(initialFlowRequest);

    FlowControlRequest deltaFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(10).build();
    subscriber.allowFlow(deltaFlowRequest);
    subscriber.allowFlow(deltaFlowRequest);
    verifyNoMoreInteractions(mockConnectedSubscriber1);

    leakedFlowControlAlarm.run();
    FlowControlRequest expectedBatchFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(200).setAllowedMessages(20).build();
    verify(mockConnectedSubscriber1).allowFlow(expectedBatchFlowRequest);

    leakedFlowControlAlarm.run();
    verifyNoMoreInteractions(mockConnectedSubscriber1);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void messagesEmpty_IsError() throws Exception {
    Future<Void> failed = whenFailed(subscriber);
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onResponse(ImmutableList.of());
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), Code.INVALID_ARGUMENT);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void messagesUnordered_IsError() throws Exception {
    Future<Void> failed = whenFailed(subscriber);
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onResponse(
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10)));
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), Code.INVALID_ARGUMENT);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void messageBatchesOutOfOrder_IsError() throws Exception {
    Future<Void> failed = whenFailed(subscriber);
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 0));
    leakedResponseObserver.onResponse(messages);
    leakedResponseObserver.onResponse(messages);
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), Code.FAILED_PRECONDITION);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void messagesOrdered_Ok() throws Exception {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    CountDownLatch messagesReceived = countdownMessageBatches(1);
    leakedResponseObserver.onResponse(messages);
    assertThat(messagesReceived.await(10, SECONDS)).isTrue();

    verify(mockMessageConsumer).accept(messages);
    assertThat(subscriber.isRunning()).isTrue();
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void messageResponseSubtracts() throws Exception {
    Future<Void> failed = whenFailed(subscriber);
    FlowControlRequest request =
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build();
    subscriber.allowFlow(request);
    verify(mockConnectedSubscriber1).allowFlow(request);
    ImmutableList<SequencedMessage> messages1 =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 98),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(2), 1));
    ImmutableList<SequencedMessage> messages2 =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(3), 2));
    CountDownLatch messagesReceived = countdownMessageBatches(1);
    leakedResponseObserver.onResponse(messages1);
    assertThat(messagesReceived.await(10, SECONDS)).isTrue();
    verify(mockMessageConsumer).accept(messages1);
    assertThat(subscriber.isRunning()).isTrue();
    leakedResponseObserver.onResponse(messages2);
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), Code.FAILED_PRECONDITION);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void reinitialize_reconnectsToNextOffset() throws Exception {
    subscriber.allowFlow(
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    CountDownLatch messagesReceived = countdownMessageBatches(1);
    leakedResponseObserver.onResponse(messages);
    assertThat(messagesReceived.await(10, SECONDS)).isTrue();
    verify(mockMessageConsumer).accept(messages);

    final SubscribeRequest nextOffsetRequest =
        SubscribeRequest.newBuilder()
            .setInitial(
                BASE_INITIAL_SUBSCRIBE_REQUEST
                    .toBuilder()
                    .setInitialLocation(
                        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2))))
            .build();
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber2;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(nextOffsetRequest));
    subscriber.triggerReinitialize(new CheckedApiException(Code.ABORTED));
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(nextOffsetRequest));
    verify(mockConnectedSubscriber2)
        .allowFlow(
            FlowControlRequest.newBuilder().setAllowedBytes(80).setAllowedMessages(98).build());
  }

  @Test
  public void reinitialize_retriesDuplicateConnectionByDefault() {
    subscriber.triggerReinitialize(DUPLICATE_CONNECTION_SIGNAL);
    verify(mockSubscriberFactory, times(2)).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void reinitialize_doesntRetryDuplicateConnectionIfDisabled() throws Exception {
    subscriber =
        new SubscriberImpl(
            unusedStreamFactory,
            mockSubscriberFactory,
            alarmFactory,
            BASE_INITIAL_SUBSCRIBE_REQUEST,
            INITIAL_LOCATION,
            mockMessageConsumer,
            mockResetHandler,
            false);
    Future<Void> failed = whenFailed(subscriber);
    subscriber.startAsync().awaitRunning();
    subscriber.triggerReinitialize(DUPLICATE_CONNECTION_SIGNAL);
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), DUPLICATE_CONNECTION_SIGNAL.code());
  }

  @Test
  public void reinitialize_handlesSuccessfulReset() throws Exception {
    subscriber.allowFlow(
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    CountDownLatch messagesReceived = countdownMessageBatches(1);
    leakedResponseObserver.onResponse(messages);
    assertThat(messagesReceived.await(10, SECONDS)).isTrue();
    verify(mockMessageConsumer).accept(messages);

    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber2;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(initialRequest()));

    // If the RESET signal is received and subscriber reset is handled, the subscriber should read
    // from the committed cursor upon reconnect.
    when(mockResetHandler.handleReset()).thenReturn(true);
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    verify(mockSubscriberFactory, times(2)).New(any(), any(), eq(initialRequest()));
    verify(mockConnectedSubscriber2)
        .allowFlow(
            FlowControlRequest.newBuilder().setAllowedBytes(80).setAllowedMessages(98).build());
  }

  @Test
  public void reinitialize_handlesIgnoredReset() throws Exception {
    subscriber.allowFlow(
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    CountDownLatch messagesReceived = countdownMessageBatches(1);
    leakedResponseObserver.onResponse(messages);
    assertThat(messagesReceived.await(10, SECONDS)).isTrue();
    verify(mockMessageConsumer).accept(messages);

    final SubscribeRequest nextOffsetRequest =
        SubscribeRequest.newBuilder()
            .setInitial(
                BASE_INITIAL_SUBSCRIBE_REQUEST
                    .toBuilder()
                    .setInitialLocation(
                        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2))))
            .build();
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber2;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(nextOffsetRequest));

    // If the RESET signal is received and subscriber reset is ignored, the subscriber should read
    // from the next offset upon reconnect.
    when(mockResetHandler.handleReset()).thenReturn(false);
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(nextOffsetRequest));
    verify(mockConnectedSubscriber2)
        .allowFlow(
            FlowControlRequest.newBuilder().setAllowedBytes(80).setAllowedMessages(98).build());
  }

  @Test
  public void reinitialize_handlesResetFailure() throws Exception {
    Future<Void> failed = whenFailed(subscriber);
    // If the RESET signal is received and subscriber reset fails, the subscriber should permanently
    // shut down.
    doThrow(new CheckedApiException(Code.UNAVAILABLE)).when(mockResetHandler).handleReset();
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    failed.get();
    assertThrowableMatches(subscriber.failureCause(), Code.UNAVAILABLE);
    verify(mockSubscriberFactory, times(1)).New(any(), any(), eq(initialRequest()));
  }
}
