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

import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.StatusException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BufferingPullSubscriber implements PullSubscriber<SequencedMessage> {
  private final Subscriber underlying;

  @GuardedBy("this")
  private Optional<StatusException> error = Optional.empty();

  @GuardedBy("this")
  private Deque<SequencedMessage> messages = new ArrayDeque<>();

  @GuardedBy("this")
  private Optional<Offset> lastDelivered = Optional.empty();

  public BufferingPullSubscriber(SubscriberFactory factory, FlowControlSettings settings)
      throws StatusException {
    this(
        factory,
        settings,
        SeekRequest.newBuilder().setNamedTarget(NamedTarget.COMMITTED_CURSOR).build());
  }

  public BufferingPullSubscriber(
      SubscriberFactory factory, FlowControlSettings settings, SeekRequest initialSeek)
      throws StatusException {
    underlying = factory.New(this::addMessages);
    underlying.addListener(
        new Listener() {
          @Override
          public void failed(State state, Throwable throwable) {
            fail(ExtractStatus.toCanonical(throwable));
          }
        },
        MoreExecutors.directExecutor());
    underlying.startAsync().awaitRunning();
    try {
      underlying.seek(initialSeek).get();
    } catch (InterruptedException e) {
      throw ExtractStatus.toCanonical(e);
    } catch (ExecutionException e) {
      throw ExtractStatus.toCanonical(e.getCause());
    }
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(settings.messagesOutstanding())
            .setAllowedBytes(settings.bytesOutstanding())
            .build());
  }

  private synchronized void fail(StatusException e) {
    error = Optional.of(e);
  }

  private synchronized void addMessages(Collection<SequencedMessage> new_messages) {
    messages.addAll(new_messages);
  }

  @Override
  public synchronized List<SequencedMessage> pull() throws StatusException {
    if (error.isPresent()) {
      throw error.get();
    }
    if (messages.isEmpty()) {
      return ImmutableList.of();
    }
    Deque<SequencedMessage> collection = messages;
    messages = new ArrayDeque<>();
    long bytes = collection.stream().mapToLong(SequencedMessage::byteSize).sum();
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedBytes(bytes)
            .setAllowedMessages(collection.size())
            .build());
    lastDelivered = Optional.of(Iterables.getLast(collection).offset());
    return ImmutableList.copyOf(collection);
  }

  @Override
  public synchronized Optional<Offset> nextOffset() {
    return lastDelivered.map(offset -> Offset.of(offset.value() + 1));
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}