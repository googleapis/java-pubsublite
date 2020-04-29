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

package com.google.cloud.pubsublite.internal;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.util.concurrent.Monitor;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CloseableMonitorTest {
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  boolean state = false;

  @Test
  public void enter() {
    assertThat(monitor.monitor.isOccupied()).isFalse();
    try (CloseableMonitor.Hold h = monitor.enter()) {
      assertThat(monitor.monitor.isOccupied()).isTrue();
    }
    assertThat(monitor.monitor.isOccupied()).isFalse();
  }

  @Test
  public void enterWhenUninterruptibly() {
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.execute(
        () -> {
          try (CloseableMonitor.Hold h = monitor.enter()) {
            state = true;
          }
        });

    try (CloseableMonitor.Hold h =
        monitor.enterWhenUninterruptibly(
            new Monitor.Guard(monitor.monitor) {
              @Override
              public boolean isSatisfied() {
                assertThat(monitor.monitor.isOccupied()).isTrue();
                return state;
              }
            })) {
      assertThat(monitor.monitor.isOccupied()).isTrue();
      assertThat(state).isTrue();
    }
  }
}
