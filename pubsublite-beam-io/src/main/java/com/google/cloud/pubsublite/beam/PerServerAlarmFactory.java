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

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class PerServerAlarmFactory implements AlarmFactory {
  static PerServerAlarmFactory instance;

  private final ScheduledExecutorService exec;

  private PerServerAlarmFactory() {
    exec = Executors.newSingleThreadScheduledExecutor();
  }

  @Override
  public Alarm newAlarm(Duration alarmPeriod, Runnable alarm) {
    ScheduledFuture<?> future =
        exec.scheduleAtFixedRate(
            alarm, alarmPeriod.toMillis(), alarmPeriod.toMillis(), TimeUnit.MILLISECONDS);
    return () -> {
      try {
        future.cancel(false);
        future.get();
      } catch (Throwable t) {
      }
    };
  }

  static synchronized AlarmFactory getInstance() {
    if (instance == null) {
      instance = new PerServerAlarmFactory();
    }
    return instance;
  }
}
