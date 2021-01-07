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
import com.google.cloud.pubsublite.internal.wire.SinglePartitionSubscriberFactory;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.util.Timestamps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.stubbing.Answer;

public class BlockingPullSubscriberImplTest {
  private final SinglePartitionSubscriberFactory underlyingFactory =
      mock(SinglePartitionSubscriberFactory.class);
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
  public void closeStops() {
    when(underlying.stopAsync()).thenReturn(underlying);
    subscriber.close();
    verify(underlying).stopAsync();
    verify(underlying).awaitTerminated();
  }

  @Test
  public void onDataAfterErrorThrows() {
    CheckedApiException expected = new CheckedApiException(StatusCode.Code.INTERNAL);
    errorListener.failed(null, expected);
    ExecutionException e = assertThrows(ExecutionException.class, () -> subscriber.onData().get());
    assertThat(expected).isEqualTo(e.getCause());
  }

  @Test
  public void onDataBeforeErrorThrows() throws Exception {
    CheckedApiException expected = new CheckedApiException(StatusCode.Code.INTERNAL);
    Future<?> future = subscriber.onData();
    Thread.sleep(1000);
    assertThat(future.isDone()).isFalse();

    errorListener.failed(null, expected);
    ExecutionException e = assertThrows(ExecutionException.class, future::get);
    assertThat(expected).isEqualTo(e.getCause());
  }

  @Test
  public void onDataSuccess() throws Exception {
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    Future<?> future = executorService.submit(() -> subscriber.onData().get());
    messageConsumer.accept(ImmutableList.of(message));
    future.get();
  }

  @Test
  public void pullMessage() throws Exception {
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    messageConsumer.accept(ImmutableList.of(message));
    assertThat(Optional.of(message)).isEqualTo(subscriber.messageIfAvailable());
  }

  @Test
  public void pullMessageNoMessage() throws Exception {
    assertThat(Optional.empty()).isEqualTo(subscriber.messageIfAvailable());
  }

  @Test
  public void pullMessageWhenError() {
    CheckedApiException expected = new CheckedApiException(StatusCode.Code.INTERNAL);
    errorListener.failed(null, expected);
    CheckedApiException e =
        assertThrows(CheckedApiException.class, () -> subscriber.messageIfAvailable());
    assertThat(expected).isEqualTo(e);
  }

  @Test
  public void pullMessagePrioritizeErrorOverExistingMessage() {
    CheckedApiException expected = new CheckedApiException(StatusCode.Code.INTERNAL);
    errorListener.failed(null, expected);
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    messageConsumer.accept(ImmutableList.of(message));

    CheckedApiException e =
        assertThrows(CheckedApiException.class, () -> subscriber.messageIfAvailable());
    assertThat(expected).isEqualTo(e);
  }

  // Not guaranteed to fail if subscriber is not thread safe, investigate if this becomes
  // flaky.
  @Test
  public void onlyOneMessageDeliveredWhenMultiCalls() throws Exception {
    SequencedMessage message =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(12), 30);
    messageConsumer.accept(ImmutableList.of(message));

    AtomicInteger count = new AtomicInteger(0);
    CountDownLatch latch = new CountDownLatch(1);
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      futures.add(
          executorService.submit(
              () -> {
                try {
                  latch.await();
                  if (subscriber.messageIfAvailable().isPresent()) {
                    count.incrementAndGet();
                  }
                } catch (Exception e) {
                  throw new IllegalStateException(e);
                }
              }));
    }
    latch.countDown();
    for (Future<?> f : futures) {
      f.get();
    }
    assertThat(1).isEqualTo(count.get());
  }
}
