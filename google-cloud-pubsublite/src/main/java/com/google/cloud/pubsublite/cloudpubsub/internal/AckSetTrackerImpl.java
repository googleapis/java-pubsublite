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
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AckSetTrackerImpl extends ProxyService implements AckSetTracker {
  private static final GoogleLogger LOGGER = GoogleLogger.forEnclosingClass();

  // Receipt represents an unacked message. If the tracker generation is incremented, the ack will
  // be ignored.
  private static class Receipt {
    final Offset offset;
    final long generation;

    private final AtomicBoolean wasAcked = new AtomicBoolean();

    private final AckSetTrackerImpl tracker;

    Receipt(Offset offset, long generation, AckSetTrackerImpl tracker) {
      this.offset = offset;
      this.generation = generation;
      this.tracker = tracker;
    }

    void onAck() throws ApiException {
      if (wasAcked.getAndSet(true)) {
        CheckedApiException e =
            new CheckedApiException("Duplicate acks are not allowed.", Code.FAILED_PRECONDITION);
        tracker.onPermanentError(e);
        throw e.underlying;
      }
      tracker.onAck(offset, generation);
    }
  }

  @GuardedBy("this")
  private final Committer committer;

  @GuardedBy("this")
  private final Deque<Receipt> receipts = new ArrayDeque<>();

  @GuardedBy("this")
  private final PriorityQueue<Offset> acks = new PriorityQueue<>();

  @GuardedBy("this")
  private long generation = 0L;

  @GuardedBy("this")
  private boolean shutdown = false;

  public AckSetTrackerImpl(Committer committer) throws ApiException {
    this.committer = committer;
    addServices(committer);
  }

  // AckSetTracker implementation.
  @Override
  public synchronized Runnable track(SequencedMessage message) throws CheckedApiException {
    checkArgument(
        receipts.isEmpty() || receipts.peekLast().offset.value() < message.offset().value());
    Receipt receipt = new Receipt(message.offset(), generation, this);
    receipts.addLast(receipt);
    return receipt::onAck;
  }

  @Override
  public synchronized void waitUntilCommitted() throws CheckedApiException {
    ++generation;
    receipts.clear();
    acks.clear();
    committer.waitUntilEmpty();
  }

  private synchronized void onAck(Offset offset, long generation) {
    if (shutdown) {
      LOGGER.atFine().log("Dropping ack after tracker shutdown.");
      return;
    }
    if (generation != this.generation) {
      LOGGER.atFine().log("Dropping ack from wrong generation (admin seek occurred).");
      return;
    }
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
      ApiFuture<?> future = committer.commitOffset(Offset.of(prefixAckedOffset.get().value() + 1));
      ExtractStatus.addFailureHandler(future, this::onPermanentError);
    }
  }

  @Override
  protected void start() throws CheckedApiException {}

  @Override
  protected synchronized void stop() throws CheckedApiException {
    shutdown = true;
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {}
}
