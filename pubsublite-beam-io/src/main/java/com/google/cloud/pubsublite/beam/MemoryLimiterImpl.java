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

import com.google.errorprone.annotations.concurrent.GuardedBy;

class MemoryLimiterImpl implements MemoryLimiter {
  private final long totalMemory;

  @GuardedBy("this")
  private int tasks = 0;

  @GuardedBy("this")
  private long currentMemory;

  MemoryLimiterImpl(long totalMemory) {
    this.totalMemory = totalMemory;
    this.currentMemory = totalMemory;
  }

  private class LimiterLease implements MemoryLease {
    long amount;

    LimiterLease(long amount) {
      this.amount = amount;
    }

    @Override
    public long byteCount() {
      return amount;
    }

    @Override
    public void close() {
      release(amount);
    }
  }

  /**
   * Acquires however much memory is currently allowed for a single task, up to `desired`. Currently
   * acquires the minimum of (totalMemory/(tasks+1)), currentMemory*3/4 and `desired`.
   *
   * <p>This is a trade off between usage of the total capacity and availability of resources for
   * future tasks. Assuming that readers are recreated with some frequency (say, every 5 minutes as
   * a strawman) This should eventually trend towards an even distribution with each worker having
   * 1/(n+1) of the total memory capacity assuming that the number of workers per job stabilizes, as
   * 1/(n+1) is smaller than (2/(n+1)) * (3/4) = 3/(2(n+1)) and we take the minimum of the two
   * values.
   */
  @Override
  public synchronized MemoryLease acquireMemory(long desired) {
    ++tasks;
    long acquired = Math.min(desired, Math.min(totalMemory / (tasks + 1), currentMemory * 3 / 4));
    currentMemory -= acquired;
    System.err.println("Acquired " + desired + " bytes.");
    return new LimiterLease(acquired);
  }

  private synchronized void release(long amount) {
    currentMemory += amount;
    tasks -= 1;
  }

  @Override
  public synchronized String toString() {
    return "Tasks: " + tasks + " memory: " + currentMemory + "/" + totalMemory;
  }
}
