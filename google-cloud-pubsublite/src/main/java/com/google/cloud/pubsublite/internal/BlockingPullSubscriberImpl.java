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

import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
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
import java.util.concurrent.Future;

public class BlockingPullSubscriberImpl implements BlockingPullSubscriber {

  private final Subscriber underlying;

  @GuardedBy("this")
  private Optional<CheckedApiException> error = Optional.empty();

  @GuardedBy("this")
  private Deque<SequencedMessage> messages = new ArrayDeque<>();

  @GuardedBy("this")
  private Optional<SettableApiFuture<Void>> notification = Optional.empty();

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
    } catch (InterruptedException | ExecutionException e) {
      throw ExtractStatus.toCanonical(e);
    }
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(settings.messagesOutstanding())
            .setAllowedBytes(settings.bytesOutstanding())
            .build());
  }

  private synchronized void fail(CheckedApiException e) {
    error = Optional.of(e);
    if (notification.isPresent()) {
      notification.get().setException(e);
      notification = Optional.empty();
    }
  }

  private synchronized void addMessages(Collection<SequencedMessage> new_messages) {
    messages.addAll(new_messages);
    if (notification.isPresent()) {
      notification.get().set(null);
      notification = Optional.empty();
    }
  }

  @Override
  public synchronized Future<Void> onData() {
    if (error.isPresent()) {
      return ApiFutures.immediateFailedFuture(error.get());
    }
    if (!messages.isEmpty()) {
      return ApiFutures.immediateFuture(null);
    }
    notification = Optional.of(SettableApiFuture.create());
    return notification.get();
  }

  @Override
  public synchronized Optional<SequencedMessage> messageIfAvailable() throws CheckedApiException {
    if (error.isPresent()) {
      throw error.get();
    }
    if (!messages.isEmpty()) {
        return Optional.of(Objects.requireNonNull(messages.pollFirst()));
    }
    return Optional.empty();
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}
