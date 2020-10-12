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

import static com.google.cloud.pubsublite.internal.StatusExceptionMatcher.assertFutureThrowsCode;
import static com.google.cloud.pubsublite.internal.wire.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService.Listener;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.InitialSubscribeRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc.SubscriberServiceStub;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.util.Timestamps;
import io.grpc.ManagedChannel;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class SubscriberImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static SubscribeRequest initialRequest() {
    try {
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
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private final ConnectedSubscriber mockConnectedSubscriber = mock(ConnectedSubscriber.class);
  private final ConnectedSubscriberFactory mockSubscriberFactory =
      mock(ConnectedSubscriberFactory.class);

  @SuppressWarnings("unchecked")
  private final Consumer<ImmutableList<SequencedMessage>> mockMessageConsumer =
      mock(Consumer.class);

  private final Listener permanentErrorHandler = mock(Listener.class);

  private SubscriberImpl subscriber;
  private StreamObserver<Response> leakedResponseObserver;

  @Before
  public void setUp() throws StatusException {
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedSubscriber;
            })
        .when(mockSubscriberFactory)
        .New(any(), any(), eq(initialRequest()));
    ManagedChannel channel =
        grpcCleanup.register(
            InProcessChannelBuilder.forName("localhost:12345").directExecutor().build());
    SubscriberServiceStub unusedStub = SubscriberServiceGrpc.newStub(channel);
    subscriber =
        new SubscriberImpl(
            unusedStub, mockSubscriberFactory, initialRequest().getInitial(), mockMessageConsumer);
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
        StatusRuntimeException.class,
        () -> subscriber.allowFlow(FlowControlRequest.newBuilder().setAllowedBytes(-1).build()));
  }

  FlowControlRequest bigFlowControlRequest() {
    return FlowControlRequest.newBuilder()
        .setAllowedBytes(Long.MAX_VALUE)
        .setAllowedMessages(Long.MAX_VALUE)
        .build();
  }

  @Test
  public void anyFlowAllowedAndProxies() {
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
  public void messagesEmpty_IsError() {
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onNext(Response.ofMessages(ImmutableList.of()));
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    verify(permanentErrorHandler)
        .failed(any(), argThat(new StatusExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void messagesUnordered_IsError() {
    subscriber.allowFlow(bigFlowControlRequest());
    leakedResponseObserver.onNext(
        Response.ofMessages(
            ImmutableList.of(
                SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10),
                SequencedMessage.of(
                    Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10))));
    assertThrows(IllegalStateException.class, subscriber::awaitTerminated);
    verify(permanentErrorHandler)
        .failed(any(), argThat(new StatusExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void messageBatchesOutOfOrder_IsError() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 0));
    leakedResponseObserver.onNext(Response.ofMessages(messages));
    leakedResponseObserver.onNext(Response.ofMessages(messages));
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
  }

  @Test
  public void messagesOrdered_Ok() {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onNext(Response.ofMessages(messages));

    verify(mockMessageConsumer).accept(messages);
    verify(permanentErrorHandler, times(0)).failed(any(), any());
  }

  @Test
  public void messageResponseSubtracts() {
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
    leakedResponseObserver.onNext(Response.ofMessages(messages1));
    verify(mockMessageConsumer).accept(messages1);
    verify(permanentErrorHandler, times(0)).failed(any(), any());
    leakedResponseObserver.onNext(Response.ofMessages(messages2));
    verify(permanentErrorHandler)
        .failed(any(), argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
  }

  @Test
  public void reinitialize_resendsInFlightSeek() {
    Offset offset = Offset.of(1);
    SeekRequest seekRequest =
        SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(offset.value())).build();
    ApiFuture<Offset> future = subscriber.seek(seekRequest);
    assertThat(subscriber.seekInFlight()).isTrue();

    subscriber.triggerReinitialize();
    verify(mockConnectedSubscriber, times(2)).seek(seekRequest);

    leakedResponseObserver.onNext(Response.ofSeekOffset(offset));
    assertTrue(future.isDone());
    assertThat(subscriber.seekInFlight()).isFalse();
  }

  @Test
  public void reinitialize_sendsNextOffsetSeek() {
    subscriber.allowFlow(bigFlowControlRequest());
    ImmutableList<SequencedMessage> messages =
        ImmutableList.of(
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(0), 10),
            SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(1), 10));
    leakedResponseObserver.onNext(Response.ofMessages(messages));
    verify(mockMessageConsumer).accept(messages);

    subscriber.triggerReinitialize();
    verify(mockConnectedSubscriber)
        .seek(SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(2)).build());
    assertThat(subscriber.seekInFlight()).isFalse();
    leakedResponseObserver.onNext(Response.ofSeekOffset(Offset.of(2)));
  }
}
