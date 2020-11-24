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

package com.google.cloud.pubsublite.beam;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class MemoryLimitingPullSubscriber implements PullSubscriber<SequencedMessage> {
  private final PullSubscriberFactory factory;
  private final MemoryLimiter limiter;
  private final FlowControlSettings baseSettings;

  private final AlarmFactory.Alarm alarm;

  @GuardedBy("this")
  private @Nullable MemoryLease lease;

  @GuardedBy("this")
  private @Nullable PullSubscriber<SequencedMessage> subscriber;

  @GuardedBy("this")
  private SeekRequest seekForRestart;

  @GuardedBy("this")
  private Optional<CheckedApiException> permanentError = Optional.empty();

  MemoryLimitingPullSubscriber(
      PullSubscriberFactory factory,
      MemoryLimiter limiter,
      FlowControlSettings baseSettings,
      SeekRequest initialSeek,
      AlarmFactory alarmFactory) {
    this.factory = factory;
    this.limiter = limiter;
    this.baseSettings = baseSettings;
    this.seekForRestart = initialSeek;
    refresh();
    this.alarm = alarmFactory.newAlarm(Duration.ofMinutes(5), this::refresh);
  }

  private synchronized FlowControlSettings getSettings() {
    // Must allow at least 1 MiB to allow all potential messages.
    long bytesAllowed =
        Math.max(1 << 20, Math.min(this.lease.byteCount(), baseSettings.bytesOutstanding()));
    return FlowControlSettings.builder()
        .setMessagesOutstanding(baseSettings.messagesOutstanding())
        .setBytesOutstanding(bytesAllowed)
        .build();
  }

  @Override
  public synchronized List<SequencedMessage> pull() throws CheckedApiException {
    if (permanentError.isPresent()) throw permanentError.get();
    try {
      List<SequencedMessage> messages = subscriber.pull();
      if (!messages.isEmpty()) {
        long next = Iterables.getLast(messages).offset().value() + 1;
        seekForRestart =
            SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(next)).build();
      }
      return messages;
    } catch (CheckedApiException e) {
      permanentError = Optional.of(e);
      throw e;
    }
  }

  @Override
  public synchronized Optional<Offset> nextOffset() {
    if (permanentError.isPresent()) throw permanentError.get().underlying;
    return subscriber.nextOffset();
  }

  @Override
  public void close() throws CheckedApiException {
    this.alarm.close();
    tryClose();
    if (permanentError.isPresent()) throw permanentError.get();
  }

  private synchronized void tryClose() {
    try {
      if (this.subscriber != null) {
        this.subscriber.close();
      }
    } catch (Throwable t) {
      this.permanentError = Optional.of(ExtractStatus.toCanonical(t));
    } finally {
      this.subscriber = null;
      if (this.lease != null) {
        this.lease.close();
        this.lease = null;
      }
    }
  }

  private synchronized void refresh() {
    tryClose();
    if (this.permanentError.isPresent()) return;
    this.lease = limiter.acquireMemory(baseSettings.bytesOutstanding());
    try {
      this.subscriber = factory.newPullSubscriber(seekForRestart, getSettings());
    } catch (CheckedApiException e) {
      this.permanentError = Optional.of(e);
    }
  }
}
