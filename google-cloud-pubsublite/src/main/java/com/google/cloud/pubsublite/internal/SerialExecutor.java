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

import com.google.common.util.concurrent.Monitor.Guard;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

/** An executor that runs tasks sequentially. */
public final class SerialExecutor implements AutoCloseable, Executor {
  private final Executor executor;

  private final CloseableMonitor monitor = new CloseableMonitor();
  private final Guard isInactive =
      new Guard(monitor.monitor) {
        @Override
        public boolean isSatisfied() {
          return !isTaskActive;
        }
      };

  @GuardedBy("monitor.monitor")
  private final Queue<Runnable> tasks;

  @GuardedBy("monitor.monitor")
  private boolean isTaskActive;

  @GuardedBy("monitor.monitor")
  private boolean isShutdown;

  public SerialExecutor(Executor executor) {
    this.executor = executor;
    this.tasks = new ArrayDeque<>();
    this.isTaskActive = false;
    this.isShutdown = false;
  }

  /** Waits until there are no active tasks. */
  public void waitUntilInactive() {
    try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(isInactive)) {}
  }

  /**
   * Shuts down the executor. No subsequent tasks will run, but any running task will be left to
   * complete.
   */
  @Override
  public void close() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      isShutdown = true;
    }
  }

  @Override
  public void execute(Runnable r) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (isShutdown) {
        return;
      }
      tasks.add(
          () -> {
            try {
              if (shouldExecuteTask()) {
                r.run();
              }
            } finally {
              scheduleNextTask();
            }
          });
      if (!isTaskActive) {
        scheduleNextTask();
      }
    }
  }

  private boolean shouldExecuteTask() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      return !isShutdown;
    }
  }

  private void scheduleNextTask() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      isTaskActive = !tasks.isEmpty() && !isShutdown;
      if (isTaskActive) {
        executor.execute(tasks.poll());
      }
    }
  }
}
