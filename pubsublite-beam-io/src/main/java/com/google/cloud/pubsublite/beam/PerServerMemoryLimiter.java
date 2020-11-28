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

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class PerServerMemoryLimiter {
  private PerServerMemoryLimiter() {}

  private static MemoryLimiter limiter;
  private static Optional<Long> totalBytes;

  private static Future<?> alarm =
      Executors.newSingleThreadScheduledExecutor()
          .scheduleAtFixedRate(PerServerMemoryLimiter::printDebug, 15, 15, TimeUnit.SECONDS);

  private static synchronized void printDebug() {
    if (limiter != null) {
      System.err.println(limiter.toString());
    }
  }

  private static synchronized void init() {
    if (totalBytes.isPresent()) {
      limiter = new MemoryLimiterImpl(totalBytes.get());
    } else {
      limiter = new NoOpMemoryLimiter();
    }
  }

  static synchronized MemoryLimiter getLimiter(Optional<Long> totalBytesSetting) {
    if (totalBytesSetting.isPresent()) {
      Preconditions.checkArgument(totalBytesSetting.get() > 0);
    }
    if (limiter == null) {
      totalBytes = totalBytesSetting;
      init();
    }
    Preconditions.checkArgument(
        totalBytes.equals(totalBytesSetting),
        "getLimiter can only be called with one setting per-server for the total byte count.");
    return limiter;
  }
}
