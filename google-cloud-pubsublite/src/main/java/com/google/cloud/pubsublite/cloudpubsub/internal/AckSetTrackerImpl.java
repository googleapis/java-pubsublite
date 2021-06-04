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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkArgument;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AckSetTrackerImpl extends TrivialProxyService implements AckSetTracker {
  // Receipt represents an unacked message. It can be cleared, which will cause the ack to be
  // ignored.
  private static class Receipt implements Runnable {
    private final CloseableMonitor m = new CloseableMonitor();
    private final AtomicBoolean wasAcked = new AtomicBoolean(false);
    final Offset offset;

    @GuardedBy("m.monitor")
    private Optional<AckSetTrackerImpl> tracker;

    Receipt(Offset offset, AckSetTrackerImpl tracker) {
      this.offset = offset;
      this.tracker = Optional.of(tracker);
    }

    void clear() {
      try (CloseableMonitor.Hold h = m.enter()) {
        tracker = Optional.empty();
      }
    }

    @Override
    public void run() {
      try (CloseableMonitor.Hold h = m.enter()) {
        if (!tracker.isPresent()) {
          return;
        }
      }
      if (wasAcked.getAndSet(true)) {
        CheckedApiException e =
            new CheckedApiException("Duplicate acks are not allowed.", Code.FAILED_PRECONDITION);
        tracker.get().onPermanentError(e);
        throw e.underlying;
      }
      tracker.get().onAck(offset);
    }
  }

  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private final Committer committer;

  @GuardedBy("monitor.monitor")
  private final Deque<Receipt> receipts = new ArrayDeque<>();

  @GuardedBy("monitor.monitor")
  private final PriorityQueue<Offset> acks = new PriorityQueue<>();

  public AckSetTrackerImpl(Committer committer) throws ApiException {
    super(committer);
    this.committer = committer;
  }

  // AckSetTracker implementation.
  @Override
  public Runnable track(SequencedMessage message) throws CheckedApiException {
    final Offset messageOffset = message.offset();
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkArgument(
          receipts.isEmpty() || receipts.peekLast().offset.value() < messageOffset.value());
      Receipt receipt = new Receipt(messageOffset, this);
      receipts.addLast(receipt);
      return receipt;
    }
  }

  @Override
  public void waitUntilCommitted() throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      receipts.forEach(Receipt::clear);
      receipts.clear();
      acks.clear();
      committer.waitUntilEmpty();
    }
  }

  private void onAck(Offset offset) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      acks.add(offset);
      Optional<Offset> prefixAckedOffset = Optional.empty();
      while (!receipts.isEmpty()
          && !acks.isEmpty()
          && receipts.peekFirst().offset.value() == acks.peek().value()) {
        prefixAckedOffset = Optional.of(acks.remove());
        receipts.removeFirst();
      }
      // Convert from last acked to first unacked.
      if (prefixAckedOffset.isPresent()) {
        ApiFuture<?> future =
            committer.commitOffset(Offset.of(prefixAckedOffset.get().value() + 1));
        ExtractStatus.addFailureHandler(future, this::onPermanentError);
      }
    }
  }
}
