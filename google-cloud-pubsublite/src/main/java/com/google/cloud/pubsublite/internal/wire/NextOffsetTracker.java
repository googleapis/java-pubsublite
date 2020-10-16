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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;
import static com.google.cloud.pubsublite.internal.wire.Predicates.isOrdered;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Optional;

// Tracks the next offset to be received if such an offset exists. Reset on client initiated seeks.
// Checks message ordering.
public class NextOffsetTracker {
  private Optional<Offset> nextOffset = Optional.empty();

  void onClientSeek(Offset clientSeekOffset) {
    nextOffset = Optional.of(clientSeekOffset);
  }

  void onMessages(Collection<SequencedMessage> messages) throws CheckedApiException {
    checkArgument(!messages.isEmpty());
    checkArgument(isOrdered(messages));
    Offset firstMessageOffset = messages.iterator().next().offset();
    checkState(
        !nextOffset.isPresent() || (nextOffset.get().value() <= firstMessageOffset.value()),
        String.format(
            "Received message with offset %s older than known cursor location %s.",
            firstMessageOffset, nextOffset));
    nextOffset = Optional.of(Offset.of(Iterables.getLast(messages).offset().value() + 1));
  }

  // Gives the SeekRequest that should be sent on restart, or empty if none should be sent because
  // the client has never received a message or seeked.
  Optional<SeekRequest> requestForRestart() {
    return nextOffset.map(
        offset ->
            SeekRequest.newBuilder()
                .setCursor(Cursor.newBuilder().setOffset(offset.value()))
                .build());
  }
}
