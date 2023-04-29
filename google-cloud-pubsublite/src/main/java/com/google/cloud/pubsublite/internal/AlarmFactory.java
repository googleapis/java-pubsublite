/*
 * Copyright 2021 Google LLC
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

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.concurrent.Future;

// An alarm factory comes with a builtin delay and constructs a future which runs that delay after
// it finishes.
public interface AlarmFactory {
  Future<?> newAlarm(Runnable runnable);

  // Get around lack of interface support for private static members in java 8
  final class Internal {
    private static final GoogleLogger LOGGER = GoogleLogger.forEnclosingClass();
  };

  static AlarmFactory create(Duration duration) {
    return runnable ->
        SystemExecutors.getAlarmExecutor()
            .scheduleWithFixedDelay(
                () -> {
                  try {
                    runnable.run();
                  } catch (Throwable t) {
                    Internal.LOGGER.atSevere().withCause(t).log("Alarm leaked exception.");
                  }
                },
                0,
                duration.toNanos(),
                NANOSECONDS);
  }

  /** Runnable is executed by an unbounded pool, although the alarm pool is bounded. */
  static AlarmFactory createUnbounded(Duration duration) {
    AlarmFactory underlying = create(duration);
    return runnable ->
        underlying.newAlarm(() -> SystemExecutors.getFuturesExecutor().execute(runnable));
  }
}
