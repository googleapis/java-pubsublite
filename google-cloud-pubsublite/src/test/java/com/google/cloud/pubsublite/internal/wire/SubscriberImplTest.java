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

import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertFutureThrowsCode;
import static com.google.cloud.pubsublite.internal.wire.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService.Listener;
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
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.TestResetSignal;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.util.Timestamps;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class SubscriberImplTest {
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

  @Mock private StreamFactory<SubscribeRequest, SubscribeResponse> unusedStreamFactory;
  @Mock private ConnectedSubscriber mockConnectedSubscriber;
  @Mock private ConnectedSubscriberFactory mockSubscriberFactory;

  @Mock Consumer<ImmutableList<SequencedMessage>> mockMessageConsumer;
  @Mock SubscriberResetHandler mockResetHandler;

  private final Listener permanentErrorHandler = mock(Listener.class);

  private SubscriberImpl subscriber;
  private ResponseObserver<Response> leakedResponseObserver;

  @Before
  public void setUp() throws CheckedApiException {
    initMocks(this);
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(initialRequest()));
    subscriber =
        new SubscriberImpl(
            unusedStreamFactory,
            mockSubscriberFactory,
            initialRequest().getInitial(),
            mockMessageConsumer,
            mockResetHandler);
    subscriber.addListener(permanentErrorHandler, MoreExecutors.directExecutor());
    subscriber.startAsync().awaitRunning();
    verify(mockSubscriberFactory).New(any(), any(), eq(initialRequest()));
    verifyNoMoreInteractions(mockSubscriberFactory);
  }

  @Test
  public void stopAbortsSeek() throws Exception {
    ApiFuture<Offset> future =
        subscriber.seek(
            SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.HEAD).build());
    assertThat(subscriber.seekInFlight()).isTrue();

    subscriber.stopAsync();
    subscriber.awaitTerminated();
    assertFutureThrowsCode(future, Code.ABORTED);
  }

  @Test
  public void invalidFlowThrows() {
    assertThrows(
        CheckedApiException.class,
        () -> subscriber.allowFlow(FlowControlRequest.newBuilder().setAllowedBytes(-1).build()));
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
    verify(mockConnectedSubscriber).allowFlow(bigFlowControlRequest());
  }

  @Test
  public void batchesFlowControlRequests() throws Exception {
    CountDownLatch allowFlowLatch = new CountDownLatch(2);
    doAnswer(
            (Answer<Void>)
                args -> {
                  allowFlowLatch.countDown();
                  return null;
                })
        .when(mockConnectedSubscriber)
        .allowFlow(any());

    FlowControlRequest initialFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(10000).setAllowedMessages(1000).build();
    subscriber.allowFlow(initialFlowRequest);
    verify(mockConnectedSubscriber).allowFlow(initialFlowRequest);

    FlowControlRequest deltaFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(10).build();
    subscriber.allowFlow(deltaFlowRequest);
    subscriber.allowFlow(deltaFlowRequest);
    verifyZeroInteractions(mockConnectedSubscriber);

    allowFlowLatch.await(SubscriberImpl.FLOW_REQUESTS_FLUSH_INTERVAL_MS * 4, MILLISECONDS);
    FlowControlRequest expectedBatchFlowRequest =
        FlowControlRequest.newBuilder().setAllowedBytes(200).setAllowedMessages(20).build();
    verify(mockConnectedSubscriber).allowFlow(expectedBatchFlowRequest);

    subscriber.processBatchFlowRequest();
    verifyNoMoreInteractions(mockConnectedSubscriber);
  }

  @Test
  public void messagesEmpty_IsError() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onResponse(Response.ofMessages(ImmutableList.of()));
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void messagesUnordered_IsError() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onResponse(
        Response.ofMessages(
            ImmutableList.of(
                SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10),
                SequencedMessage.of(
                    Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10))));
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void messageBatchesOutOfOrder_IsError() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 0));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
  }

  @Test
  public void messagesOrdered_Ok() throws Exception {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));

    verify(mockMessageConsumer).accept(messages);
    verify(permanentErrorHandler, times(0)).failed(any(), any());
  }

  @Test
  public void messageResponseSubtracts() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    FlowControlRequest request =
        FlowControlRequest.newBuilder().setAllowedBytes(100).setAllowedMessages(100).build();
    subscriber.allowFlow(request);
    verify(mockConnectedSubscriber).allowFlow(request);
    ImmutableList<SequencedMessage> messages1 =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 98),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(2), 1));
    ImmutableList<SequencedMessage> messages2 =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(3), 2));
    leakedResponseObserver.onResponse(Response.ofMessages(messages1));
    verify(mockMessageConsumer).accept(messages1);
    verify(permanentErrorHandler, times(0)).failed(any(), any());
    leakedResponseObserver.onResponse(Response.ofMessages(messages2));
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
  }

  @Test
  public void reinitialize_resendsInFlightSeek() {
    Offset offset = Offset.of(1);
    SeekRequest seekRequest =
        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(offset.value())).build();
    ApiFuture<Offset> future = subscriber.seek(seekRequest);
    assertThat(subscriber.seekInFlight()).isTrue();

    subscriber.triggerReinitialize(new CheckedApiException(Code.ABORTED));
    verify(mockConnectedSubscriber, times(2)).seek(seekRequest);

    leakedResponseObserver.onResponse(Response.ofSeekOffset(offset));
    assertTrue(future.isDone());
    assertThat(subscriber.seekInFlight()).isFalse();
  }

  @Test
  public void reinitialize_sendsNextOffsetSeek() throws Exception {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));
    verify(mockMessageConsumer).accept(messages);

    subscriber.triggerReinitialize(new CheckedApiException(Code.ABORTED));
    verify(mockConnectedSubscriber)
        .seek(SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2)).build());
    assertThat(subscriber.seekInFlight()).isFalse();
    leakedResponseObserver.onResponse(Response.ofSeekOffset(Offset.of(2)));
  }

  @Test
  public void reinitialize_handlesSuccessfulReset() throws Exception {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));
    verify(mockMessageConsumer).accept(messages);

    // If the RESET signal is received and subscriber reset is handled, the subscriber should read
    // from the committed cursor upon reconnect.
    when(mockResetHandler.handleReset()).thenReturn(true);
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    verify(mockConnectedSubscriber, never()).seek(any());
  }

  @Test
  public void reinitialize_resetCancelsClientSeek() throws Exception {
    SeekRequest seekRequest =
        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2)).build();
    ApiFuture<Offset> seekFuture = subscriber.seek(seekRequest);
    assertThat(subscriber.seekInFlight()).isTrue();
    verify(mockConnectedSubscriber, times(1)).seek(seekRequest);

    // If the RESET signal is received and subscriber reset is handled, the subscriber should cancel
    // client seeks.
    when(mockResetHandler.handleReset()).thenReturn(true);
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    assertThat(subscriber.seekInFlight()).isFalse();
    assertFutureThrowsCode(seekFuture, Code.ABORTED);
  }

  @Test
  public void reinitialize_handlesIgnoredReset() throws Exception {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onResponse(Response.ofMessages(messages));
    verify(mockMessageConsumer).accept(messages);

    // If the RESET signal is received and subscriber reset is ignored, the subscriber should read
    // from the next offset upon reconnect.
    when(mockResetHandler.handleReset()).thenReturn(false);
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    verify(mockConnectedSubscriber)
        .seek(SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2)).build());
    leakedResponseObserver.onResponse(Response.ofSeekOffset(Offset.of(2)));
  }

  @Test
  public void reinitialize_handlesResetFailure() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    // If the RESET signal is received and subscriber reset fails, the subscriber should permanently
    // shut down.
    doThrow(new CheckedApiException(Code.UNAVAILABLE)).when(mockResetHandler).handleReset();
    subscriber.triggerReinitialize(TestResetSignal.newCheckedException());
    failed.get();
    verify(permanentErrorHandler).failed(any(), argThat(new ApiExceptionMatcher(Code.UNAVAILABLE)));
  }
}
