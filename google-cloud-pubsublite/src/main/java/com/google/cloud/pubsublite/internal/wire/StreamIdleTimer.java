/*
 * Copyright 2022 Google LLC
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

import static com.google.common.collect.Comparators.min;

import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import java.time.Duration;
import java.util.concurrent.Future;
import javax.annotation.concurrent.GuardedBy;

/** An approximate timer used to detect idle streams. */
class StreamIdleTimer implements AutoCloseable {
  /** Handles a timeout. */
  interface Handler {
    void onTimeout();
  }

  private static final long POLL_DIVISOR = 4;
  private static final Duration MAX_POLL_INTERVAL = Duration.ofMinutes(1);

  @VisibleForTesting
  static Duration getDelay(Duration timeout) {
    return min(MAX_POLL_INTERVAL, timeout.dividedBy(POLL_DIVISOR));
  }

  private final Duration timeout;
  private final Handler handler;
  private final Future<?> task;

  @GuardedBy("this")
  private final Stopwatch stopwatch;

  /**
   * Creates a started timer.
   *
   * @param timeout Call the handler after this duration has elapsed. The call may be delayed up to
   *     (timeout / POLL_DIVISOR) after the timeout duration.
   * @param handler Called after the timeout has expired and the timer is running.
   */
  StreamIdleTimer(Duration timeout, Handler handler) {
    this(timeout, handler, Ticker.systemTicker(), AlarmFactory.create(getDelay(timeout)));
  }

  @VisibleForTesting
  StreamIdleTimer(Duration timeout, Handler handler, Ticker ticker, AlarmFactory alarmFactory) {
    this.timeout = timeout;
    this.handler = handler;
    this.stopwatch = Stopwatch.createStarted(ticker);
    this.task = alarmFactory.newAlarm(this::onPoll);
  }

  @Override
  public void close() throws Exception {
    task.cancel(false);
  }

  /** Restart the timer from zero. */
  public synchronized void restart() {
    stopwatch.reset().start();
  }

  private synchronized void onPoll() {
    if (stopwatch.elapsed().compareTo(timeout) > 0) {
      SystemExecutors.getFuturesExecutor().execute(handler::onTimeout);
    }
  }
}
