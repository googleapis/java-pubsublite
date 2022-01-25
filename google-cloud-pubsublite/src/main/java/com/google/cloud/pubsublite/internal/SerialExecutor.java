/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.concurrent.GuardedBy;

/** An executor that runs tasks sequentially. */
public final class SerialExecutor implements AutoCloseable, Executor {
  private final Executor executor;
  private final AtomicBoolean isShutdown = new AtomicBoolean(false);

  @GuardedBy("this")
  private final Queue<Runnable> tasks;

  @GuardedBy("this")
  private boolean isTaskActive;

  public SerialExecutor(Executor executor) {
    this.executor = executor;
    this.tasks = new ArrayDeque<>();
    this.isTaskActive = false;
  }

  /**
   * Shuts down the executor. No subsequent tasks will run, but any running task will be left to
   * complete.
   */
  @Override
  public void close() {
    isShutdown.set(true);
  }

  @Override
  public synchronized void execute(Runnable r) {
    if (isShutdown.get()) {
      return;
    }
    tasks.add(
        () -> {
          if (isShutdown.get()) {
            return;
          }
          try {
            r.run();
          } finally {
            scheduleNextTask();
          }
        });
    if (!isTaskActive) {
      scheduleNextTask();
    }
  }

  private synchronized void scheduleNextTask() {
    isTaskActive = !tasks.isEmpty();
    if (isTaskActive) {
      executor.execute(tasks.poll());
    }
  }
}
