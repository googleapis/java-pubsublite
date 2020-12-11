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
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BlockingPullSubscriberImpl implements BlockingPullSubscriber {

  private final Subscriber underlying;

  @GuardedBy("monitor.monitor")
  private Optional<CheckedApiException> error = Optional.empty();

  @GuardedBy("monitor.monitor")
  private Deque<SequencedMessage> messages = new ArrayDeque<>();

  private final CloseableMonitor monitor = new CloseableMonitor();
  private final Monitor.Guard notEmtpyOrErr = new Monitor.Guard(monitor.monitor) {
    @Override
    public boolean isSatisfied() {
      return !messages.isEmpty() || error.isPresent();
    }
  };

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
  public SequencedMessage pull() throws InterruptedException, CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(notEmtpyOrErr)) {
      if (error.isPresent()) {
        throw error.get();
      }
      SequencedMessage msg = Objects.requireNonNull(messages.pollFirst());
      underlying.allowFlow(
          FlowControlRequest.newBuilder()
              .setAllowedBytes(msg.byteSize())
              .setAllowedMessages(1)
              .build());
      return msg;
    }
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}
