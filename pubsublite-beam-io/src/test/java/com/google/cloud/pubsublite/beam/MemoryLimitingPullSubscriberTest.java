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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.util.Timestamps;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class MemoryLimitingPullSubscriberTest {
  @Mock PullSubscriberFactory subscriberFactory;
  @Mock PullSubscriber<SequencedMessage> underlying;
  @Mock MemoryLimiter limiter;
  @Mock MemoryLease lease;
  private static final FlowControlSettings BASE_FLOW_CONTROL =
      FlowControlSettings.builder()
          .setBytesOutstanding(1 << 25)
          .setMessagesOutstanding(1000)
          .build();
  private static final FlowControlSettings SMALLER_FLOW_CONTROL =
      BASE_FLOW_CONTROL
          .toBuilder()
          .setBytesOutstanding(BASE_FLOW_CONTROL.bytesOutstanding() - 1)
          .build();
  private static final SeekRequest INITIAL =
      SeekRequest.newBuilder().setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR).build();
  @Mock AlarmFactory alarmFactory;
  @Mock AlarmFactory.Alarm alarm;
  Runnable alarmRunnable;
  PullSubscriber<SequencedMessage> subscriber;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(limiter.acquireMemory(BASE_FLOW_CONTROL.bytesOutstanding())).thenReturn(lease);
    when(lease.byteCount()).thenReturn(SMALLER_FLOW_CONTROL.bytesOutstanding());
    when(subscriberFactory.newPullSubscriber(INITIAL, SMALLER_FLOW_CONTROL)).thenReturn(underlying);
    when(alarmFactory.newAlarm(eq(Duration.ofMinutes(5)), any()))
        .then(
            args -> {
              alarmRunnable = args.getArgument(1, Runnable.class);
              return alarm;
            });
    subscriber =
        new MemoryLimitingPullSubscriber(subscriberFactory, limiter, BASE_FLOW_CONTROL, INITIAL);
    verify(limiter).acquireMemory(BASE_FLOW_CONTROL.bytesOutstanding());
    verify(subscriberFactory).newPullSubscriber(INITIAL, SMALLER_FLOW_CONTROL);
    assertThat(alarmRunnable).isNotNull();
  }

  @Test
  public void createDestroy() {}

  @Test
  public void exceptionInPullFails() throws Exception {
    when(underlying.pull()).thenThrow(new CheckedApiException(Code.INTERNAL));
    assertThrows(CheckedApiException.class, () -> subscriber.pull());
    verify(underlying).pull();
    assertThrows(ApiException.class, () -> subscriber.nextOffset());
    verify(underlying, times(0)).nextOffset();
    assertThrows(CheckedApiException.class, () -> subscriber.close());
    verify(lease).close();
    verify(underlying).close();
  }

  @Test
  public void exceptionInCloseFails() throws Exception {
    doThrow(new CheckedApiException(Code.INTERNAL)).when(underlying).close();
    alarmRunnable.run();
    assertThrows(CheckedApiException.class, () -> subscriber.pull());
    verify(underlying, times(0)).pull();
    assertThrows(ApiException.class, () -> subscriber.nextOffset());
    verify(underlying, times(0)).nextOffset();
    assertThrows(CheckedApiException.class, () -> subscriber.close());
    verify(lease, times(1)).close();
    verify(underlying, times(1)).close();
  }

  @Test
  public void alarmRecreates() throws Exception {
    when(lease.byteCount()).thenReturn(BASE_FLOW_CONTROL.bytesOutstanding());
    alarmRunnable.run();
    verify(lease).close();
    verify(underlying).close();
    verify(limiter, times(2)).acquireMemory(BASE_FLOW_CONTROL.bytesOutstanding());
    verify(subscriberFactory).newPullSubscriber(INITIAL, BASE_FLOW_CONTROL);
  }

  @Test
  public void lowLimitAtLeast1MiB() throws Exception {
    when(lease.byteCount()).thenReturn(7L);
    alarmRunnable.run();
    verify(lease, times(1)).close();
    verify(underlying, times(1)).close();
    verify(limiter, times(2)).acquireMemory(BASE_FLOW_CONTROL.bytesOutstanding());
    verify(subscriberFactory, times(1))
        .newPullSubscriber(
            INITIAL, BASE_FLOW_CONTROL.toBuilder().setBytesOutstanding(1 << 20).build());
  }

  @Test
  public void seekModifiedAfterReceivingMessages() throws Exception {
    SequencedMessage m1 =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(3), 1);
    SequencedMessage m2 =
        SequencedMessage.of(Message.builder().build(), Timestamps.EPOCH, Offset.of(8), 1);
    when(underlying.pull()).thenReturn(ImmutableList.of(m1, m2));
    assertThat(subscriber.pull()).containsExactly(m1, m2);
    alarmRunnable.run();
    verify(lease, times(1)).close();
    verify(underlying, times(1)).close();
    verify(limiter, times(2)).acquireMemory(BASE_FLOW_CONTROL.bytesOutstanding());
    verify(subscriberFactory, times(1))
        .newPullSubscriber(
            SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(9)).build(),
            SMALLER_FLOW_CONTROL);
  }
}
