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

package com.google.cloud.pubsublite.spark;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingPullSubscriber implements AutoCloseable {

  private final Subscriber underlying;

  private final Lock lock = new ReentrantLock();
  private final Condition notEmpty = lock.newCondition();

  @GuardedBy("lock")
  private Optional<CheckedApiException> error = Optional.empty();

  @GuardedBy("lock")
  private Deque<SequencedMessage> messages = new ArrayDeque<>();

  public BlockingPullSubscriber(
      SubscriberFactory factory, FlowControlSettings settings, SeekRequest initialSeek)
      throws CheckedApiException {
    underlying = factory.newSubscriber(this::addMessages);
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

  private void fail(CheckedApiException e) {
    lock.lock();
    try {
      error = Optional.of(e);
    } finally {
      lock.unlock();
    }
  }

  private void addMessages(Collection<SequencedMessage> new_messages) {
    lock.lock();
    try {
      messages.addAll(new_messages);
      if (!messages.isEmpty()) {
        notEmpty.signal();
      }
    } finally {
      lock.unlock();
    }
  }

  // Blocking call until there is at least one message to return.
  public SequencedMessage pull() throws InterruptedException, CheckedApiException {
    lock.lock();
    try {
      if (error.isPresent()) {
        throw error.get();
      }
      while (messages.isEmpty()) {
        notEmpty.await();
      }
      SequencedMessage msg = Objects.requireNonNull(messages.pollFirst());
      underlying.allowFlow(
          FlowControlRequest.newBuilder()
              .setAllowedBytes(msg.byteSize())
              .setAllowedMessages(1)
              .build());
      return msg;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}
