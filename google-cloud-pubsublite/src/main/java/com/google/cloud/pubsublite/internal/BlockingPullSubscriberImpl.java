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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.StatusCode;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
  public synchronized ApiFuture<Void> onData() {
    if (error.isPresent()) {
      return ApiFutures.immediateFailedFuture(error.get());
    }
    if (!messages.isEmpty()) {
      return ApiFutures.immediateFuture(null);
    }
    if (!notification.isPresent()) {
      notification = Optional.of(SettableApiFuture.create());
    }
    return notification.get();
  }

  @Override
  public synchronized Optional<SequencedMessage> messageIfAvailable() throws CheckedApiException {
    if (error.isPresent()) {
      throw error.get();
    }
    if (messages.isEmpty()) {
      return Optional.empty();
    }
    SequencedMessage msg = messages.pollFirst();
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(1)
            .setAllowedBytes(msg.byteSize())
            .build());
    return Optional.of(msg);
  }

  @Override
  public void close() {
    synchronized (this) {
      if (!error.isPresent()) {
        error =
            Optional.of(
                new CheckedApiException(
                    "Subscriber client shut down", StatusCode.Code.UNAVAILABLE));
      }
    }
    underlying.stopAsync().awaitTerminated();
  }
}
