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
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class BufferingPullSubscriber implements PullSubscriber<SequencedMessage> {
  private final Subscriber underlying;
  private final AtomicReference<StatusException> error = new AtomicReference<>();
  private final LinkedBlockingQueue<SequencedMessage> messages = new LinkedBlockingQueue<>();

  public BufferingPullSubscriber(SubscriberFactory factory, FlowControlSettings settings)
      throws StatusException {
    underlying = factory.New(messages::addAll);
    underlying.addListener(
        new Listener() {
          @Override
          public void failed(State state, Throwable throwable) {
            error.set(ExtractStatus.toCanonical(throwable));
          }
        },
        MoreExecutors.directExecutor());
    underlying.startAsync().awaitRunning();
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(settings.messagesOutstanding())
            .setAllowedBytes(settings.bytesOutstanding())
            .build());
  }

  public BufferingPullSubscriber(
      SubscriberFactory factory, FlowControlSettings settings, Offset initialLocation)
      throws StatusException {
    this(factory, settings);
    try {
      underlying
          .seek(
              SeekRequest.newBuilder()
                  .setCursor(Cursor.newBuilder().setOffset(initialLocation.value()))
                  .build())
          .get();
    } catch (InterruptedException e) {
      throw ExtractStatus.toCanonical(e);
    } catch (ExecutionException e) {
      throw ExtractStatus.toCanonical(e.getCause());
    }
  }

  @Override
  public ImmutableList<SequencedMessage> pull() throws StatusException {
    @Nullable StatusException maybeError = error.get();
    if (maybeError != null) {
      throw maybeError;
    }
    ArrayList<SequencedMessage> collection = new ArrayList<>();
    messages.drainTo(collection);
    long bytes = collection.stream().mapToLong(SequencedMessage::byteSize).sum();
    underlying.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedBytes(bytes)
            .setAllowedMessages(collection.size())
            .build());
    return ImmutableList.copyOf(collection);
  }

  @Override
  public void close() {
    underlying.stopAsync().awaitTerminated();
  }
}
