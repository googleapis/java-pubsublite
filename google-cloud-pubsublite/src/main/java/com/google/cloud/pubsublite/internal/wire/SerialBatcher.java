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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// A thread compatible batcher which preserves message order.
class SerialBatcher {
  private final long byteLimit;
  private final long messageLimit;
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

  ApiFuture<Offset> add(PubSubMessage message) {
    SettableApiFuture<Offset> future = SettableApiFuture.create();
    messages.add(UnbatchedMessage.of(message, future));
    return future;
  }

  List<List<UnbatchedMessage>> flush() {
    List<List<UnbatchedMessage>> toReturn = new ArrayList<>();
    List<UnbatchedMessage> currentBatch = new ArrayList<>();
    toReturn.add(currentBatch);
    long currentBatchBytes = 0;
    for (UnbatchedMessage message : messages) {
      long newBatchBytes = currentBatchBytes + message.message().getSerializedSize();
      if (currentBatch.size() + 1 > messageLimit || newBatchBytes > byteLimit) {
        // If we would be pushed over the limit, create a new batch.
        currentBatch = new ArrayList<>();
        toReturn.add(currentBatch);
        newBatchBytes = message.message().getSerializedSize();
      }
      currentBatchBytes = newBatchBytes;
      currentBatch.add(message);
    }
    messages = new ArrayDeque<>();
    return toReturn;
  }
}
