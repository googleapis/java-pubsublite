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

package com.google.cloud.pubsublite.internal.wire;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

// A thread compatible batcher which preserves message order.
class SerialBatcher {
  private final long byteLimit;
  private final long messageLimit;
  private long byteCount = 0L;
  private Deque<UnbatchedMessage> messages = new ArrayDeque<>();

  @AutoValue
  public abstract static class UnbatchedMessage {
    public abstract PubSubMessage message();

    public abstract SettableApiFuture<Offset> future();

    public static UnbatchedMessage of(PubSubMessage message, SettableApiFuture<Offset> future) {
      return new AutoValue_SerialBatcher_UnbatchedMessage(message, future);
    }
  }

  SerialBatcher(long byteLimit, long messageLimit) {
    this.byteLimit = byteLimit;
    this.messageLimit = messageLimit;
  }

  // Callers should always call shouldFlush() after add, and flush() if that returns true.
  ApiFuture<Offset> add(PubSubMessage message) {
    byteCount += message.getSerializedSize();
    SettableApiFuture<Offset> future = SettableApiFuture.create();
    messages.add(UnbatchedMessage.of(message, future));
    return future;
  }

  boolean shouldFlush() {
    return byteCount >= byteLimit || messages.size() >= messageLimit;
  }

  // If callers satisfy the conditions on add, one of two things will be true after a call to flush.
  // Either, there will be 0-many messages remaining and they will be within the limits, or
  // there will be 1 message remaining.
  //
  // This means, an isolated call to flush will always return all messages in the batcher.
  Collection<UnbatchedMessage> flush() {
    Deque<UnbatchedMessage> toReturn = messages;
    messages = new ArrayDeque<>();
    while ((byteCount > byteLimit || toReturn.size() > messageLimit) && toReturn.size() > 1) {
      messages.addFirst(toReturn.removeLast());
      byteCount -= toReturn.peekLast().message().getSerializedSize();
    }
    byteCount = messages.stream().mapToLong(value -> value.message().getSerializedSize()).sum();
    // Validate the postcondition.
    Preconditions.checkState(
        messages.size() == 1 || (byteCount <= byteLimit && messages.size() <= messageLimit),
        "Postcondition violation in SerialBatcher::flush. The caller is likely not calling flush"
            + " after calling add.");
    return toReturn;
  }
}
