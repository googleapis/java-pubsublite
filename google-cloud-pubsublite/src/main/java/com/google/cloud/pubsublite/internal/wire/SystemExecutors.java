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

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.internal.Lazy;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class SystemExecutors {
  private SystemExecutors() {}

  private static ThreadFactory newDaemonThreadFactory(String prefix) {
    return new ThreadFactoryBuilder().setDaemon(true).setNameFormat(prefix + "-%d").build();
  }

  public static ScheduledExecutorService newDaemonExecutor(String prefix) {
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
        4, newDaemonThreadFactory(prefix));
    // Remove scheduled tasks from the executor as soon as they are cancelled.
    executor.setRemoveOnCancelPolicy(true);
    return executor;
  }

  private static final Lazy<ScheduledExecutorService> ALARM_EXECUTOR =
      new Lazy<>(() -> newDaemonExecutor("pubsub-lite-alarms"));
  // An executor for alarms.
  public static ScheduledExecutorService getAlarmExecutor() {
    return ALARM_EXECUTOR.get();
  }

  private static Executor newDaemonThreadPool(String prefix) {
    return Executors.newCachedThreadPool(newDaemonThreadFactory(prefix));
  }

  private static final Lazy<Executor> FUTURES_EXECUTOR =
      new Lazy<>(() -> newDaemonThreadPool("pubsub-lite-futures"));
  // An executor for future handling.
  public static Executor getFuturesExecutor() {
    return FUTURES_EXECUTOR.get();
  }
}
