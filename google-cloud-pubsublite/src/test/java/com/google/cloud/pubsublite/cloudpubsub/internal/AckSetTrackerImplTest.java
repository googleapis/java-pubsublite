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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static com.google.api.core.ApiFutures.immediateFuture;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class AckSetTrackerImplTest {
  abstract static class FakeCommitter extends FakeApiService implements Committer {}

  @Spy private FakeCommitter committer;

  @Mock private Listener permanentErrorHandler;

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  AckSetTracker tracker;

  private static SequencedMessage messageForOffset(int offset) {
    return SequencedMessage.fromProto(
        com.google.cloud.pubsublite.proto.SequencedMessage.newBuilder()
            .setCursor(Cursor.newBuilder().setOffset(offset).build())
            .build());
  }

  @Before
  public void setUp() throws ApiException {
    initMocks(this);
    tracker = new AckSetTrackerImpl(committer);
    tracker.startAsync().awaitRunning();
    verify(committer).startAsync();
    tracker.addListener(permanentErrorHandler, MoreExecutors.directExecutor());
  }

  @After
  public void tearDown() {
    if (tracker.isRunning()) {
      tracker.stopAsync().awaitTerminated();
      verify(committer).stopAsync();
    }
  }

  @Test
  public void trackAndAggregateAcks() throws CheckedApiException {
    Runnable ack1 = tracker.track(messageForOffset(1));
    Runnable ack3 = tracker.track(messageForOffset(3));
    Runnable ack5 = tracker.track(messageForOffset(5));
    Runnable ignoredAck7 = tracker.track(messageForOffset(7));

    SettableApiFuture<Void> commitFuture = SettableApiFuture.create();
    // 6 = ackedOffset + 1
    when(committer.commitOffset(Offset.of(6))).thenReturn(commitFuture);

    ack3.run();
    verify(committer, times(0)).commitOffset(any());
    ack5.run();
    verify(committer, times(0)).commitOffset(any());
    ack1.run();
    verify(committer, times(1)).commitOffset(any());
    verify(committer).commitOffset(Offset.of(6));

    commitFuture.set(null);
    assertThat(tracker.isRunning()).isTrue();
  }

  @Test
  public void duplicateAckFails() throws CheckedApiException {
    Runnable ack1 = tracker.track(messageForOffset(1));

    SettableApiFuture<Void> commitFuture = SettableApiFuture.create();
    // 2 = ackedOffset + 1
    when(committer.commitOffset(Offset.of(2))).thenReturn(commitFuture);

    ack1.run();
    verify(committer, times(1)).commitOffset(any());
    verify(committer).commitOffset(Offset.of(2));

    commitFuture.set(null);
    assertThat(tracker.isRunning()).isTrue();

    assertThrows(ApiException.class, ack1::run);
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
  }

  @Test
  public void waitUntilEmptyReturnsWhenEmpty() throws Exception {
    Runnable ack = tracker.track(messageForOffset(1));

    Future<?> waitFuture =
        executorService.submit(
            () -> {
              try {
                tracker.waitUntilEmpty();
              } catch (Throwable e) {
                throw new IllegalStateException(e);
              }
            });
    assertThat(waitFuture.isDone()).isFalse();

    when(committer.commitOffset(Offset.of(2))).thenReturn(immediateFuture(null));
    ack.run();
    verify(committer).commitOffset(Offset.of(2));

    waitFuture.get(30, TimeUnit.SECONDS);
    verify(committer).waitUntilEmpty();
  }

  @Test
  public void waitUntilEmptyReturnsOnShutdown() throws Exception {
    tracker.track(messageForOffset(1));

    Future<?> waitFuture =
        executorService.submit(
            () -> {
              try {
                tracker.waitUntilEmpty();
              } catch (Throwable e) {
                throw new IllegalStateException(e);
              }
            });
    assertThat(waitFuture.isDone()).isFalse();

    tracker.stopAsync();
    verify(committer).stopAsync();

    waitFuture.get(30, TimeUnit.SECONDS);
    verify(committer).waitUntilEmpty();
  }
}
