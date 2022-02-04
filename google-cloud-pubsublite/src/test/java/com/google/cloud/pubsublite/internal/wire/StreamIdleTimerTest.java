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

package com.google.cloud.pubsublite.internal.wire;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.common.testing.FakeTicker;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class StreamIdleTimerTest {
  private static final Duration TIMEOUT = Duration.ofSeconds(60);

  @Mock private AlarmFactory mockAlarmFactory;

  private final SettableApiFuture<Void> taskFuture = SettableApiFuture.create();
  private final FakeTicker fakeTicker = new FakeTicker();
  private final CountDownLatch handlerCalled = new CountDownLatch(1);
  private StreamIdleTimer timer;
  private Runnable leakedPoller;

  @Before
  public void setUp() {
    initMocks(this);
    when(mockAlarmFactory.newAlarm(any()))
        .thenAnswer(
            args -> {
              leakedPoller = args.getArgument(0);
              return taskFuture;
            });
    timer = new StreamIdleTimer(TIMEOUT, handlerCalled::countDown, fakeTicker, mockAlarmFactory);
    verify(mockAlarmFactory).newAlarm(any());
    assertThat(leakedPoller).isNotNull();
  }

  @After
  public void shutdown() {
    verifyNoMoreInteractions(mockAlarmFactory);
  }

  @Test
  public void delayComputedCorrectly() {
    assertThat(StreamIdleTimer.getDelay(Duration.ofSeconds(60))).isEqualTo(Duration.ofSeconds(15));
    assertThat(StreamIdleTimer.getDelay(Duration.ofMinutes(5))).isEqualTo(Duration.ofMinutes(1));
  }

  @Test
  public void timeoutHandlerCalledCorrectly() throws Exception {
    // Timeout has not expired.
    fakeTicker.advance(TIMEOUT.minusSeconds(1));
    leakedPoller.run();
    assertThat(handlerCalled.await(10, MILLISECONDS)).isFalse();

    // Timeout now expired.
    fakeTicker.advance(Duration.ofSeconds(2));
    leakedPoller.run();
    assertThat(handlerCalled.await(30, SECONDS)).isTrue();
  }

  @Test
  public void restartResetsTimer() throws Exception {
    // Timer restarted just before it expires.
    fakeTicker.advance(TIMEOUT.minusSeconds(1));
    timer.restart();

    fakeTicker.advance(Duration.ofSeconds(2));
    leakedPoller.run();
    assertThat(handlerCalled.await(10, MILLISECONDS)).isFalse();

    // Timeout now expired.
    fakeTicker.advance(TIMEOUT);
    leakedPoller.run();
    assertThat(handlerCalled.await(30, SECONDS)).isTrue();
  }

  @Test
  public void closeCancelsTask() throws Exception {
    fakeTicker.advance(TIMEOUT.minusSeconds(1));
    leakedPoller.run();

    timer.close();

    assertThat(taskFuture.isCancelled()).isTrue();
    assertThat(handlerCalled.await(10, MILLISECONDS)).isFalse();
  }
}
