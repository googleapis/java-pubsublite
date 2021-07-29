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
package com.google.cloud.pubsublite.internal.wire;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class PartitionCountWatcherImplTest {
  private static final CloudRegion REGION = CloudRegion.of("us-east1");

  private static TopicPath path() {
    return TopicPath.newBuilder()
        .setName(TopicName.of("a"))
        .setProject(ProjectNumber.of(4))
        .setLocation(CloudZone.of(REGION, 'a'))
        .build();
  }

  @Mock AdminClient mockClient;
  @Mock AlarmFactory alarmFactory;
  @Mock Consumer<Long> mockConsumer;
  PartitionCountWatcher watcher;
  final SettableApiFuture<Void> alarmFuture = SettableApiFuture.create();

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    PartitionCountWatcherImpl.Factory watcherFactory =
        new PartitionCountWatcherImpl.Factory(path(), mockClient, alarmFactory);
    watcher = watcherFactory.newWatcher(mockConsumer);
  }

  @Test
  public void testFirstCallFails() {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(StatusCode.Code.FAILED_PRECONDITION).underlying));
    watcher.startAsync();
    assertThrows(IllegalStateException.class, watcher::awaitTerminated);
    ApiExceptionMatcher.assertThrowableMatches(
        watcher.failureCause(), StatusCode.Code.FAILED_PRECONDITION);
    verify(mockClient, times(1)).getTopicPartitionCount(path());
  }

  @Test
  public void testConsumerExcepts() {
    when(mockClient.getTopicPartitionCount(path())).thenReturn(ApiFutures.immediateFuture(1L));
    doThrow(new IllegalArgumentException("bad batching settings")).when(mockConsumer).accept(1L);
    watcher.startAsync();
    assertThrows(IllegalStateException.class, watcher::awaitTerminated);
    ApiExceptionMatcher.assertThrowableMatches(watcher.failureCause(), StatusCode.Code.INTERNAL);
    verify(mockClient, times(1)).getTopicPartitionCount(path());
  }

  Runnable startAndLeakAlarm() throws Exception {
    SettableApiFuture<Runnable> toLeak = SettableApiFuture.create();
    when(alarmFactory.newAlarm(any()))
        .thenAnswer(
            args -> {
              toLeak.set(args.getArgument(0));
              return alarmFuture;
            });
    watcher.startAsync();
    return toLeak.get();
  }

  @Test
  public void testCallsHandlerOnStart() throws Exception {
    when(mockClient.getTopicPartitionCount(path())).thenReturn(ApiFutures.immediateFuture(1L));
    Runnable unusedAlarm = startAndLeakAlarm();
    verify(mockConsumer).accept(1L);
    verifyNoMoreInteractions(mockConsumer);
    assertThat(watcher.isRunning()).isTrue();
  }

  @Test
  public void testHandlerCalledOnUpdates() throws Exception {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(ApiFutures.immediateFuture(2L));
    Runnable alarm = startAndLeakAlarm();
    alarm.run();
    alarm.run();
    verify(mockClient, times(3)).getTopicPartitionCount(path());
    verify(mockConsumer, times(1)).accept(1L);
    verify(mockConsumer, times(1)).accept(2L);
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void testFailuresAfterFirstSuccessIgnored() throws Exception {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(StatusCode.Code.FAILED_PRECONDITION)))
        .thenReturn(ApiFutures.immediateFuture(2L));
    Runnable alarm = startAndLeakAlarm();
    alarm.run();
    alarm.run();
    verify(mockClient, times(3)).getTopicPartitionCount(path());
    verify(mockConsumer, times(1)).accept(1L);
    verify(mockConsumer, times(1)).accept(2L);
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void testStopStopsAlarm() throws Exception {
    when(mockClient.getTopicPartitionCount(path())).thenReturn(ApiFutures.immediateFuture(1L));
    Runnable unusedAlarm = startAndLeakAlarm();
    watcher.awaitRunning();
    watcher.stopAsync().awaitTerminated();
    assertThat(alarmFuture.isCancelled()).isTrue();
  }
}
