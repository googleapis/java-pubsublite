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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SystemExecutors {
  private SystemExecutors() {}

  public static ScheduledExecutorService newDaemonExecutor(String prefix) {
    return Executors.newScheduledThreadPool(
        Math.max(4, Runtime.getRuntime().availableProcessors()),
        new ThreadFactoryBuilder().setDaemon(true).setNameFormat(prefix + "-%d").build());
  }

  private static final Lazy<ScheduledExecutorService> ALARM_EXECUTOR =
      new Lazy<>(() -> newDaemonExecutor("pubsub-lite-alarms"));
  // An executor for alarms.
  public static ScheduledExecutorService getAlarmExecutor() {
    return ALARM_EXECUTOR.get();
  }
}
