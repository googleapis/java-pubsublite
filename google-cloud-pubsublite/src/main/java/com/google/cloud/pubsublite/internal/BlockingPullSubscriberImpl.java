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
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.wire.Subscriber;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.SettableFuture;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BlockingPullSubscriberImpl implements BlockingPullSubscriber {

  private final Subscriber underlying;

  @GuardedBy("monitor.monitor")
  private Optional<CheckedApiException> error = Optional.empty();

  @GuardedBy("monitor.monitor")
  private Deque<SequencedMessage> messages = new ArrayDeque<>();

  private final CloseableMonitor monitor = new CloseableMonitor();
  private final Monitor.Guard notEmptyOrErr =
      new Monitor.Guard(monitor.monitor) {
        @Override
        public boolean isSatisfied() {
          return !messages.isEmpty() || error.isPresent();
        }
      };
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  public BlockingPullSubscriberImpl(
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
        executorService);
    underlying.startAsync().awaitRunning();
    try {
      underlying.seek(initialSeek).get();
    } catch (InterruptedException | ExecutionException e) {
      throw ExtractStatus.toCanonical(e);
    }
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(settings.messagesOutstanding())
            .setAllowedBytes(settings.bytesOutstanding())
            .build());
  }

  private void fail(CheckedApiException e) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      error = Optional.of(e);
    }
  }

  private void addMessages(Collection<SequencedMessage> new_messages) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      messages.addAll(new_messages);
    }
  }

  @Override
  public Future<Void> onData() {
    return onData(Long.MAX_VALUE, TimeUnit.SECONDS);
  }

  @Override
  public Future<Void> onData(long time, TimeUnit unit) {
    SettableFuture<Void> toReturn = SettableFuture.create();
    executorService.submit(
        () -> {
          CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(notEmptyOrErr, time, unit);
          if (!h.satisfied()) {
            toReturn.setException(new TimeoutException("No data available"));
          }
          try (CloseableMonitor.Hold hold = h) {
            if (error.isPresent()) {
              toReturn.setException(error.get());
            } else {
              toReturn.set(null);
            }
          }
        });
    return toReturn;
  }

  @Override
  public Optional<SequencedMessage> messageIfAvailable() throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (!messages.isEmpty()) {
        return Optional.of(Objects.requireNonNull(messages.pollFirst()));
      }
      if (error.isPresent()) {
        throw error.get();
      }
      return Optional.empty();
    }
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}
