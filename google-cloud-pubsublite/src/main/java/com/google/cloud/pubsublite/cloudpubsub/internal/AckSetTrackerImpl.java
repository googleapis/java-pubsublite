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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AckSetTrackerImpl extends ProxyService implements AckSetTracker {
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private final Committer committer;

  @GuardedBy("monitor.monitor")
  private final Deque<Offset> receipts = new ArrayDeque<>();

  @GuardedBy("monitor.monitor")
  private final PriorityQueue<Offset> acks = new PriorityQueue<>();

  public AckSetTrackerImpl(Committer committer) throws StatusException {
    addServices(committer);
    this.committer = committer;
  }

  // ProxyService implementation. Noop as this is a thin wrapper around committer.
  @Override
  protected void start() {}

  @Override
  protected void stop() {}

  @Override
  protected void handlePermanentError(StatusException error) {}

  // AckSetTracker implementation.
  @Override
  public Runnable track(SequencedMessage message) throws StatusException {
    final Offset messageOffset = message.offset();
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkArgument(receipts.isEmpty() || receipts.peekLast().value() < messageOffset.value());
      receipts.addLast(messageOffset);
    }
    return new Runnable() {
      private final AtomicBoolean wasAcked = new AtomicBoolean(false);

      @Override
      public void run() {
        if (wasAcked.getAndSet(true)) {
          Status s = Status.FAILED_PRECONDITION.withDescription("Duplicate acks are not allowed.");
          onPermanentError(s.asException());
          throw s.asRuntimeException();
        }
        onAck(messageOffset);
      }
    };
  }

  private void onAck(Offset offset) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      acks.add(offset);
      Optional<Offset> prefixAckedOffset = Optional.empty();
      while (!receipts.isEmpty()
          && !acks.isEmpty()
          && receipts.peekFirst().value() == acks.peek().value()) {
        prefixAckedOffset = Optional.of(acks.remove());
        receipts.removeFirst();
      }
      // Convert from last acked to first unacked.
      if (prefixAckedOffset.isPresent()) {
        ApiFuture<?> future =
            committer.commitOffset(Offset.create(prefixAckedOffset.get().value() + 1));
        ExtractStatus.addFailureHandler(future, this::onPermanentError);
      }
    }
  }
}
