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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService.Listener;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.MessageTransforms;
import com.google.cloud.pubsublite.cloudpubsub.NackHandler;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class SinglePartitionSubscriberTest {
  @Mock private MessageReceiver receiver;
  @Captor private ArgumentCaptor<AckReplyConsumer> ackConsumerCaptor;
  private final MessageTransformer<SequencedMessage, PubsubMessage> transformer =
      MessageTransforms.toCpsSubscribeTransformer();

  abstract static class AckSetTrackerFakeService extends FakeApiService implements AckSetTracker {}

  @Spy private AckSetTrackerFakeService ackSetTracker;
  @Mock private NackHandler nackHandler;
  @Mock private SubscriberFactory subscriberFactory;

  abstract static class SubscriberFakeService extends FakeApiService implements Subscriber {}

  @Spy private SubscriberFakeService wireSubscriber;
  @Mock private Listener permanentErrorHandler;
  private SinglePartitionSubscriber subscriber;

  private static final Offset OFFSET = Offset.of(1);
  private static final long BYTE_SIZE = 1392;
  private static final SequencedMessage MESSAGE =
      SequencedMessage.of(
          Message.builder().setData(ByteString.copyFromUtf8("abc")).build(),
          Timestamps.EPOCH,
          OFFSET,
          BYTE_SIZE);

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(subscriberFactory.newSubscriber(any())).thenReturn(wireSubscriber);
    subscriber =
        new SinglePartitionSubscriber(
            receiver,
            transformer,
            ackSetTracker,
            nackHandler,
            subscriberFactory,
            FlowControlSettings.builder()
                .setMessagesOutstanding(100000)
                .setBytesOutstanding(1000000)
                .build());
    subscriber.startAsync().awaitRunning();
    verify(subscriberFactory).newSubscriber(any());
    verify(ackSetTracker).startAsync();
    verify(wireSubscriber).startAsync();
    subscriber.addListener(permanentErrorHandler, MoreExecutors.directExecutor());
  }

  @Test
  public void ackSetTrackerFailure() {
    ackSetTracker.fail(new CheckedApiException(Code.INVALID_ARGUMENT));
    verify(ackSetTracker).stopAsync();
    verify(wireSubscriber).stopAsync();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void wireSubscriberFailure() {
    wireSubscriber.fail(new CheckedApiException(Code.INVALID_ARGUMENT));
    verify(ackSetTracker).stopAsync();
    verify(wireSubscriber).stopAsync();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }

  @Test
  public void singleMessageAck() throws CheckedApiException {
    Runnable ack = mock(Runnable.class);
    when(ackSetTracker.track(MESSAGE)).thenReturn(ack);
    subscriber.onMessages(ImmutableList.of(MESSAGE));
    verify(ackSetTracker).track(MESSAGE);
    verify(receiver)
        .receiveMessage(eq(transformer.transform(MESSAGE)), ackConsumerCaptor.capture());
    ackConsumerCaptor.getValue().ack();
    verify(ack).run();
    verify(wireSubscriber)
        .allowFlow(
            FlowControlRequest.newBuilder()
                .setAllowedMessages(1)
                .setAllowedBytes(BYTE_SIZE)
                .build());
  }

  @Test
  public void multiMessageAck() throws CheckedApiException {
    Runnable ack1 = mock(Runnable.class);
    Runnable ack2 = mock(Runnable.class);
    long bytes2 = 111;
    SequencedMessage message2 =
        SequencedMessage.fromProto(
            MESSAGE
                .toProto()
                .toBuilder()
                .setSizeBytes(bytes2)
                .setPublishTime(Timestamps.fromMillis(System.currentTimeMillis()))
                .setCursor(Cursor.newBuilder().setOffset(OFFSET.value() + 1))
                .build());
    when(ackSetTracker.track(MESSAGE)).thenReturn(ack1);
    when(ackSetTracker.track(message2)).thenReturn(ack2);
    subscriber.onMessages(ImmutableList.of(MESSAGE, message2));
    verify(ackSetTracker).track(MESSAGE);
    verify(ackSetTracker).track(message2);
    verify(receiver)
        .receiveMessage(eq(transformer.transform(MESSAGE)), ackConsumerCaptor.capture());
    verify(receiver)
        .receiveMessage(eq(transformer.transform(message2)), ackConsumerCaptor.capture());

    ackConsumerCaptor.getAllValues().get(1).ack();
    verify(ack2).run();
    verify(wireSubscriber)
        .allowFlow(
            FlowControlRequest.newBuilder().setAllowedMessages(1).setAllowedBytes(bytes2).build());

    ackConsumerCaptor.getAllValues().get(0).ack();
    verify(ack1).run();
    verify(wireSubscriber)
        .allowFlow(
            FlowControlRequest.newBuilder()
                .setAllowedMessages(1)
                .setAllowedBytes(BYTE_SIZE)
                .build());
  }

  @Test
  public void singleMessageNackHandlerSuccessFuture() throws CheckedApiException {
    Runnable ack = mock(Runnable.class);
    when(ackSetTracker.track(MESSAGE)).thenReturn(ack);
    subscriber.onMessages(ImmutableList.of(MESSAGE));
    verify(ackSetTracker).track(MESSAGE);
    verify(receiver)
        .receiveMessage(eq(transformer.transform(MESSAGE)), ackConsumerCaptor.capture());
    when(nackHandler.nack(transformer.transform(MESSAGE)))
        .thenReturn(ApiFutures.immediateFuture(null));
    ackConsumerCaptor.getValue().nack();
    verify(ack).run();
    verify(wireSubscriber)
        .allowFlow(
            FlowControlRequest.newBuilder()
                .setAllowedMessages(1)
                .setAllowedBytes(BYTE_SIZE)
                .build());
  }

  @Test
  public void singleMessageNackHandlerFailedFuture() throws CheckedApiException {

    Runnable ack = mock(Runnable.class);
    when(ackSetTracker.track(MESSAGE)).thenReturn(ack);
    subscriber.onMessages(ImmutableList.of(MESSAGE));
    verify(ackSetTracker).track(MESSAGE);
    verify(receiver)
        .receiveMessage(eq(transformer.transform(MESSAGE)), ackConsumerCaptor.capture());
    when(nackHandler.nack(transformer.transform(MESSAGE)))
        .thenReturn(
            ApiFutures.immediateFailedFuture(new CheckedApiException(Code.INVALID_ARGUMENT)));
    ackConsumerCaptor.getValue().nack();
    verify(ackSetTracker).stopAsync();
    verify(wireSubscriber).stopAsync();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }
}
