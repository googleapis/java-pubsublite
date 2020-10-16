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
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

// CommitState tracks the states of commits across stream retries. It is thread compatible.
//
// It ensures that commit futures sent on past streams which will never get a response on the
// current stream will be completed when any later commit response is sent on the stream.
class CommitState {
  private static class FutureWithOffset {
    SettableApiFuture<Void> future = SettableApiFuture.create();
    Offset offset;
  }

  private final Queue<FutureWithOffset> pastConnectionFutures = new ArrayDeque<>();
  private final Queue<FutureWithOffset> currentConnectionFutures = new ArrayDeque<>();

  // Moves all but one of the outstanding futures to pastConnectionFutures, and returns the offset
  // that should be committed on the stream if it exists.
  Optional<Offset> reinitializeAndReturnToSend() {
    if (currentConnectionFutures.isEmpty()) return Optional.empty();
    while (currentConnectionFutures.size() > 1) {
      pastConnectionFutures.add(currentConnectionFutures.remove());
    }
    return Optional.of(currentConnectionFutures.peek().offset);
  }

  // Return a future that will be done when a completion is received on the stream. Called before
  // sending a commit request on the stream.
  ApiFuture<Void> addCommit(Offset offset) {
    FutureWithOffset futureWithOffset = new FutureWithOffset();
    futureWithOffset.offset = offset;
    currentConnectionFutures.add(futureWithOffset);
    return futureWithOffset.future;
  }

  void complete(long numComplete) throws CheckedApiException {
    if (numComplete > currentConnectionFutures.size()) {
      CheckedApiException error =
          new CheckedApiException(
              String.format(
                  "Received %s completions, which is more than the commits outstanding for this"
                      + " stream.",
                  numComplete),
              Code.FAILED_PRECONDITION);
      abort(error);
      throw error;
    }
    while (!pastConnectionFutures.isEmpty()) {
      // Past futures refer to commits sent chronologically before the current stream, and thus they
      // are now overridden by this stream's completions.
      pastConnectionFutures.remove().future.set(null);
    }
    for (int i = 0; i < numComplete; i++) {
      currentConnectionFutures.remove().future.set(null);
    }
  }

  void abort(CheckedApiException error) {
    while (!pastConnectionFutures.isEmpty()) {
      pastConnectionFutures.remove().future.setException(error);
    }
    while (!currentConnectionFutures.isEmpty()) {
      currentConnectionFutures.remove().future.setException(error);
    }
  }

  boolean isEmpty() {
    return pastConnectionFutures.isEmpty() && currentConnectionFutures.isEmpty();
  }
}
