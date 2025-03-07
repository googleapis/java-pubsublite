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

package com.google.cloud.pubsublite.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiService.Listener;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SequencedMessage;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.util.Timestamps;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class BufferingPullSubscriberTest {
  private final SubscriberFactory underlyingFactory = mock(SubscriberFactory.class);
  private final Subscriber underlying = mock(Subscriber.class);
  private final FlowControlSettings flowControlSettings =
      FlowControlSettings.builder().setBytesOutstanding(10).setMessagesOutstanding(20).build();
  // Initialized in setUp.
  private PullSubscriber<SequencedMessage> subscriber;
  private Consumer<ImmutableList<SequencedMessage>> messageConsumer;
  private Listener errorListener;

  @Before
  public void setUp() throws Exception {
    when(underlying.startAsync()).thenReturn(underlying);
    FlowControlRequest flow =
        FlowControlRequest.newBuilder()
            .setAllowedBytes(flowControlSettings.bytesOutstanding())
            .setAllowedMessages(flowControlSettings.messagesOutstanding())
            .build();
    when(underlyingFactory.newSubscriber(any()))
        .thenAnswer(
            args -> {
              messageConsumer = args.getArgument(0);
              return underlying;
            });
    doAnswer(
            (Answer<Void>)
                args -> {
                  errorListener = args.getArgument(0);
                  return null;
                })
        .when(underlying)
        .addListener(any(), any());

    subscriber = new BufferingPullSubscriber(underlyingFactory, flowControlSettings);

    InOrder inOrder = inOrder(underlyingFactory, underlying);
    inOrder.verify(underlyingFactory).newSubscriber(any());
    inOrder.verify(underlying).addListener(any(), any());
    inOrder.verify(underlying).startAsync();
    inOrder.verify(underlying).awaitRunning();
    inOrder.verify(underlying).allowFlow(flow);

    assertThat(messageConsumer).isNotNull();
    assertThat(errorListener).isNotNull();
  }

  @Test
  public void createDestroy() {}

  @Test
  public void pullAfterErrorThrows() {
    errorListener.failed(null, new CheckedApiException(Code.INTERNAL));
    CheckedApiException e = assertThrows(CheckedApiException.class, subscriber::pull);
    assertThat(e.code()).isEqualTo(Code.INTERNAL);
  }

  @Test
  public void emptyPull() throws CheckedApiException {
    assertThat(subscriber.pull()).isEmpty();
  }

  @Test
  public void pullEmptiesForNext() throws CheckedApiException {
    SequencedMessage message1 =
        SequencedMessage.newBuilder()
            .setPublishTime(Timestamps.EPOCH)
            .setCursor(Cursor.newBuilder().setOffset(10))
            .setSizeBytes(10)
            .build();
    SequencedMessage message2 =
        SequencedMessage.newBuilder()
            .setPublishTime(Timestamps.EPOCH)
            .setCursor(Cursor.newBuilder().setOffset(11))
            .setSizeBytes(10)
            .build();
    messageConsumer.accept(ImmutableList.of(message1, message2));
    assertThat(subscriber.pull()).containsExactly(message1, message2);
    assertThat(subscriber.pull()).isEmpty();
  }

  @Test
  public void multipleBatchesAggregatedReturnsTokens() throws CheckedApiException {
    SequencedMessage message1 =
        SequencedMessage.newBuilder()
            .setPublishTime(Timestamps.EPOCH)
            .setCursor(Cursor.newBuilder().setOffset(10))
            .setSizeBytes(10)
            .build();
    SequencedMessage message2 =
        SequencedMessage.newBuilder()
            .setPublishTime(Timestamps.EPOCH)
            .setCursor(Cursor.newBuilder().setOffset(11))
            .setSizeBytes(20)
            .build();
    SequencedMessage message3 =
        SequencedMessage.newBuilder()
            .setPublishTime(Timestamps.EPOCH)
            .setCursor(Cursor.newBuilder().setOffset(12))
            .setSizeBytes(30)
            .build();
    assertThat(subscriber.nextOffset()).isEmpty();
    messageConsumer.accept(ImmutableList.of(message1, message2));
    messageConsumer.accept(ImmutableList.of(message3));
    assertThat(subscriber.pull()).containsExactly(message1, message2, message3);
    assertThat(subscriber.nextOffset()).hasValue(Offset.of(13));
    assertThat(subscriber.pull()).isEmpty();

    FlowControlRequest flowControlRequest =
        FlowControlRequest.newBuilder().setAllowedMessages(3).setAllowedBytes(60).build();
    verify(underlying).allowFlow(flowControlRequest);
  }

  @Test
  public void closeStops() throws Exception {
    when(underlying.stopAsync()).thenReturn(underlying);
    subscriber.close();
    verify(underlying).stopAsync();
    verify(underlying).awaitTerminated();
  }
}
