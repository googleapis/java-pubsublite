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

import com.google.common.util.concurrent.Monitor;
import com.google.errorprone.annotations.concurrent.LockMethod;
import com.google.errorprone.annotations.concurrent.UnlockMethod;
import java.util.concurrent.TimeUnit;

/** Wraps a Monitor with methods that can be used with try-with-resources. */
public class CloseableMonitor {
  public final Monitor monitor = new Monitor();

  /**
   * try-with-resources wrapper for enterWhenUninterruptibly. For example:
   *
   * <pre>
   * final Monitor.Guard guard = new Monitor.Guard(monitor.monitor) {
   *     &#64;Override
   *     public boolean isSatisfied() {
   *       assertThat(monitor.monitor.isOccupied()).isTrue();
   *       return state;
   *     }
   * };
   *
   * try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(guard)) {
   *   // Do stuff
   * }
   * // Monitor is automatically released
   * </pre>
   */
  @LockMethod("monitor")
  public Hold enterWhenUninterruptibly(Monitor.Guard condition) {
    monitor.enterWhenUninterruptibly(condition);
    return new Hold(ConditionSatisfaction.CONDITION_SATISFIED);
  }

  /**
   * try-with-resources wrapper for enterWhenUninterruptibly with timeout. For example:
   *
   * <pre>
   * final Monitor.Guard guard = new Monitor.Guard(monitor.monitor) {
   *     &#64;Override
   *     public boolean isSatisfied() {
   *       assertThat(monitor.monitor.isOccupied()).isTrue();
   *       return state;
   *     }
   * };
   *
   * CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(guard));
   * if (!h.isSatisfied()) {
   *   // Do stuff if condition not satisfied when timeout expired
   * } else {
   *   try (CloseableMonitor.Hold hold = h) {
   *     // Do stuff if condition satisfied
   *   }
   *   // Monitor is automatically released
   * }
   * </pre>
   */
  @LockMethod("monitor")
  public Hold enterWhenUninterruptibly(Monitor.Guard condition, long time, TimeUnit unit) {
    return new Hold(
        monitor.enterWhenUninterruptibly(condition, time, unit)
            ? ConditionSatisfaction.CONDITION_SATISFIED
            : ConditionSatisfaction.CONDITION_UNSATISFIED);
  }

  /**
   * try-with-resources wrapper for enter. For example...
   *
   * <pre>{@code
   * try (CloseableMonitor.Hold h = monitor.enter()) {
   *   // Do stuff
   * }
   * // Monitor is automatically released
   * }</pre>
   */
  @LockMethod("monitor")
  public Hold enter() {
    monitor.enter();
    return new Hold(ConditionSatisfaction.CONDITION_SATISFIED);
  }

  /**
   * This is meant for use in the try-with-resources pattern. It will call leave() on a Monitor when
   * it goes out of scope. This is cannot be constructed directly, but through usage of the static
   * utility methods above.
   */
  public class Hold implements AutoCloseable {

    private final ConditionSatisfaction satisfied;

    private Hold(ConditionSatisfaction satisfied) {
      this.satisfied = satisfied;
    }

    public boolean satisfied() {
      return satisfied == ConditionSatisfaction.CONDITION_SATISFIED;
    }

    @UnlockMethod("monitor")
    @Override
    public void close() {
      assert satisfied == ConditionSatisfaction.CONDITION_SATISFIED
          : "Hold should only be used in try-with-resources when condition satisfied";
      monitor.leave();
    }
  }

  public enum ConditionSatisfaction {
    CONDITION_SATISFIED,
    CONDITION_UNSATISFIED
  }
}
