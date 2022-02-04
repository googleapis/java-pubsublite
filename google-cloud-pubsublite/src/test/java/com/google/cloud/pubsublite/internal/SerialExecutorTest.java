/*
 * Copyright 2022 Google LLC
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
import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class SerialExecutorTest {
  private final SerialExecutor executor = new SerialExecutor(SystemExecutors.getFuturesExecutor());

  @Test
  public void serializesTasks() throws Exception {
    final int numTasks = 100;
    List<Integer> receivedSequences = new ArrayList<>();
    CountDownLatch tasksDone = new CountDownLatch(numTasks);
    for (int i = 0; i < numTasks; i++) {
      int sequence = i;
      executor.execute(
          () -> {
            synchronized (receivedSequences) {
              receivedSequences.add(sequence);
            }
            tasksDone.countDown();
          });
    }
    assertThat(tasksDone.await(30, SECONDS)).isTrue();

    for (int i = 0; i < receivedSequences.size(); i++) {
      assertThat(receivedSequences.get(i)).isEqualTo(i);
    }
  }

  @Test
  public void closeDiscardsTasks() throws Exception {
    List<Integer> receivedSequences = new ArrayList<>();
    CountDownLatch tasksDone = new CountDownLatch(1);
    for (int i = 0; i < 10; i++) {
      int sequence = i;
      executor.execute(
          () -> {
            synchronized (receivedSequences) {
              receivedSequences.add(sequence);
            }
            tasksDone.countDown();
            executor.close();
          });
    }
    assertThat(tasksDone.await(10, SECONDS)).isTrue();

    assertThat(receivedSequences).containsExactly(0);
  }
}
