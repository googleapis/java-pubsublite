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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkState;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class BatchingCommitterTest {
  abstract static class FakeCommitter extends FakeApiService implements Committer {}

  @Spy
  FakeCommitter underlying;
  @Mock
  AlarmFactory alarmFactory;

  private BatchingCommitter committer;

  private Runnable flushAlarm;

  @Before
  public void setUp() {
    initMocks(this);
    when(alarmFactory.newAlarm(any())).thenAnswer(args -> {
      flushAlarm = args.getArgument(0);
      return SettableApiFuture.create();
    });
    committer = new BatchingCommitter(underlying, alarmFactory);
    checkState(flushAlarm != null);
    committer.startAsync().awaitRunning();
  }

  @Test
  public void batchesRequests() throws Exception {
    ApiFuture<Void> f1 = committer.commitOffset(Offset.of(1));
    ApiFuture<Void> f2 = committer.commitOffset(Offset.of(2));
    ApiFuture<Void> f3 = committer.commitOffset(Offset.of(3));
    verify(underlying, times(0)).commitOffset(any());
    assertThat(f1.isDone()).isFalse();
    assertThat(f2.isDone()).isFalse();
    assertThat(f3.isDone()).isFalse();
    SettableApiFuture<Void> underlyingFuture3 = SettableApiFuture.create();
    when(underlying.commitOffset(Offset.of(3))).thenReturn(underlyingFuture3);
    flushAlarm.run();
    verify(underlying, times(1)).commitOffset(Offset.of(3));
    assertThat(f1.isDone()).isFalse();
    assertThat(f2.isDone()).isFalse();
    assertThat(f3.isDone()).isFalse();
    ApiFuture<Void> f4 = committer.commitOffset(Offset.of(4));
    SettableApiFuture<Void> underlyingFuture4 = SettableApiFuture.create();
    when(underlying.commitOffset(Offset.of(4))).thenReturn(underlyingFuture4);
    flushAlarm.run();
    verify(underlying, times(1)).commitOffset(Offset.of(4));
    assertThat(f4.isDone()).isFalse();
    underlyingFuture3.set(null);
    f1.get();
    f2.get();
    f3.get();
    assertThat(f4.isDone()).isFalse();
    underlyingFuture4.setException(new Exception("Some error"));
    assertThrows(Exception.class, f4::get);
  }

  @Test
  public void waitUntilEmptyFlushes() throws Exception {
    ApiFuture<Void> f1 = committer.commitOffset(Offset.of(1));
    verify(underlying, times(0)).commitOffset(any());
    when(underlying.commitOffset(Offset.of(1))).thenReturn(ApiFutures.immediateFuture(null));
    committer.waitUntilEmpty();
    verify(underlying).waitUntilEmpty();
    f1.get();
  }

  @Test
  public void shutdownFlushes() throws Exception {
    ApiFuture<Void> f1 = committer.commitOffset(Offset.of(1));
    verify(underlying, times(0)).commitOffset(any());
    when(underlying.commitOffset(Offset.of(1))).thenReturn(ApiFutures.immediateFuture(null));
    committer.stopAsync().awaitTerminated();
    verify(underlying).commitOffset(Offset.of(1));
    f1.get();
  }
}
