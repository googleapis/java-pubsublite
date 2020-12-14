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

import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.util.Timestamps;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.stubbing.Answer;

public class BlockingPullSubscriberImplTest {
  private final SubscriberFactory underlyingFactory = mock(SubscriberFactory.class);
  private final Subscriber underlying = mock(Subscriber.class);
  private final Offset initialOffset = Offset.of(5);
  private final SeekRequest initialSeek =
      SeekRequest.newBuilder()
          .setCursor(Cursor.newBuilder().setOffset(initialOffset.value()))
          .build();
  private final FlowControlSettings flowControlSettings =
      FlowControlSettings.builder().setBytesOutstanding(10).setMessagesOutstanding(20).build();
  // Initialized in setUp.
  private BlockingPullSubscriberImpl subscriber;
  private Consumer<ImmutableList<SequencedMessage>> messageConsumer;
  private ApiService.Listener errorListener;
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  @Before
  public void setUp() throws Exception {
    when(underlying.startAsync()).thenReturn(underlying);
    SeekRequest seek =
        SeekRequest.newBuilder()
            .setCursor(Cursor.newBuilder().setOffset(initialOffset.value()).build())
            .build();
    when(underlying.seek(seek)).thenReturn(ApiFutures.immediateFuture(initialOffset));
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

    subscriber =
        new BlockingPullSubscriberImpl(underlyingFactory, flowControlSettings, initialSeek);

    InOrder inOrder = inOrder(underlyingFactory, underlying);
    inOrder.verify(underlyingFactory).newSubscriber(any());
    inOrder.verify(underlying).addListener(any(), any());
    inOrder.verify(underlying).startAsync();
    inOrder.verify(underlying).awaitRunning();
    inOrder.verify(underlying).seek(seek);
    inOrder.verify(underlying).allowFlow(flow);

    assertThat(messageConsumer).isNotNull();
    assertThat(errorListener).isNotNull();
  }

  @Test
  public void blockingPullAfterErrorThrows() {
    errorListener.failed(null, new CheckedApiException(StatusCode.Code.INTERNAL));
    CheckedApiException e = assertThrows(CheckedApiException.class, subscriber::pull);
    assertThat(StatusCode.Code.INTERNAL).isEqualTo(e.code());
  }

  @Test
  public void blockingPullBeforeErrorThrows() throws Exception {
    Future<?> future =
        executorService.submit(
            () -> assertThrows(CheckedApiException.class, () -> subscriber.pull()));
    Thread.sleep(1000);
    assertThat(future.isDone()).isFalse();

    errorListener.failed(null, new CheckedApiException(StatusCode.Code.INTERNAL));
    future.get();
  }

  @Test
  public void closeStops() {
    when(underlying.stopAsync()).thenReturn(underlying);
    subscriber.close();
    verify(underlying).stopAsync();
    verify(underlying).awaitTerminated();
  }

  @Test
  public void blockingPullMessage() throws Exception {
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    Future<?> future =
        executorService.submit(
            () -> {
              try {
                assertThat(subscriber.pull()).isEqualTo(message);
                FlowControlRequest flowControlRequest =
                    FlowControlRequest.newBuilder()
                        .setAllowedMessages(1)
                        .setAllowedBytes(30)
                        .build();
                verify(underlying).allowFlow(flowControlRequest);
              } catch (CheckedApiException | InterruptedException e) {
                throw new IllegalStateException(e);
              }
            });
    Thread.sleep(1000);
    assertThat(future.isDone()).isFalse();

    messageConsumer.accept(ImmutableList.of(message));
    future.get();
  }

  @Test
  public void pullMessageWithTimeoutAfterErrorThrows() {
    errorListener.failed(null, new CheckedApiException(StatusCode.Code.INTERNAL));
    CheckedApiException e =
        assertThrows(CheckedApiException.class, () -> subscriber.pull(5, TimeUnit.SECONDS));
    assertThat(StatusCode.Code.INTERNAL).isEqualTo(e.code());
  }

  @Test
  public void pullMessageWithTimeoutBeforeErrorThrows() throws Exception {
    Future<?> future =
        executorService.submit(
            () ->
                assertThrows(
                    CheckedApiException.class, () -> subscriber.pull(5, TimeUnit.SECONDS)));
    Thread.sleep(1000);
    assertThat(future.isDone()).isFalse();

    errorListener.failed(null, new CheckedApiException(StatusCode.Code.INTERNAL));
    future.get();
  }

  @Test
  public void pullMessageWithTimeoutNoMessage() throws Exception {
    assertThat(Optional.empty()).isEqualTo(subscriber.pull(200, TimeUnit.MILLISECONDS));
  }

  @Test
  public void pullMessageWithTimeout() throws Exception {
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    Future<?> future =
        executorService.submit(
            () -> {
              try {
                assertThat(Optional.of(message))
                    .isEqualTo(subscriber.pull(500, TimeUnit.MILLISECONDS));
                FlowControlRequest flowControlRequest =
                    FlowControlRequest.newBuilder()
                        .setAllowedMessages(1)
                        .setAllowedBytes(30)
                        .build();
                verify(underlying).allowFlow(flowControlRequest);
              } catch (CheckedApiException | InterruptedException e) {
                throw new IllegalStateException(e);
              }
            });
    Thread.sleep(300);
    assertThat(future.isDone()).isFalse();

    messageConsumer.accept(ImmutableList.of(message));
    future.get();
  }
}
