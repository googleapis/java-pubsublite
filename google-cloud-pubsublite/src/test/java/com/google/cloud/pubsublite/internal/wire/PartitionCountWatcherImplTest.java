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

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import java.time.Duration;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

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

  PartitionCountWatcher.Factory watcherFactory;
  @Mock AdminClient mockClient;
  @Mock Consumer<Long> mockConsumer;

  @Before
  public void setUp() {
    initMocks(this);
    watcherFactory =
        new PartitionCountWatcherImpl.Factory(path(), mockClient, Duration.ofMillis(10));
  }

  @Test
  public void testFirstCallFails() {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(StatusCode.Code.FAILED_PRECONDITION).underlying));
    PartitionCountWatcher watcher = watcherFactory.newWatcher(mockConsumer);
    watcher.startAsync();
    assertThrows(IllegalStateException.class, watcher::awaitTerminated);
    ApiExceptionMatcher.assertThrowableMatches(
        watcher.failureCause(), StatusCode.Code.FAILED_PRECONDITION);
    verify(mockClient, times(1)).getTopicPartitionCount(path());
  }

  @Test
  public void testCallsHandlerOnStart() {
    when(mockClient.getTopicPartitionCount(path())).thenReturn(ApiFutures.immediateFuture(1L));
    PartitionCountWatcher watcher = watcherFactory.newWatcher(mockConsumer);
    watcher.startAsync();
    verify(mockConsumer, after(1000)).accept(1L);
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void testHandlerCalledOnUpdates() {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(ApiFutures.immediateFuture(2L));
    PartitionCountWatcher watcher = watcherFactory.newWatcher(mockConsumer);
    watcher.startAsync();
    verify(mockClient, after(1000).atLeast(3)).getTopicPartitionCount(path());
    verify(mockConsumer, after(1000)).accept(1L);
    verify(mockConsumer, after(1000)).accept(2L);
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void testFailuresAfterFirstSuccessIgnored() {
    when(mockClient.getTopicPartitionCount(path()))
        .thenReturn(ApiFutures.immediateFuture(1L))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(StatusCode.Code.FAILED_PRECONDITION)))
        .thenReturn(ApiFutures.immediateFuture(2L));
    PartitionCountWatcher watcher = watcherFactory.newWatcher(mockConsumer);
    watcher.startAsync();
    verify(mockClient, after(1000).atLeast(3)).getTopicPartitionCount(path());
    verify(mockConsumer, after(1000)).accept(1L);
    verify(mockConsumer, after(1000)).accept(2L);
    verifyNoMoreInteractions(mockConsumer);
  }

  @Test
  public void testStopPreventsFutureCalls() {
    when(mockClient.getTopicPartitionCount(path())).thenReturn(ApiFutures.immediateFuture(1L));
    PartitionCountWatcher watcher = watcherFactory.newWatcher(mockConsumer);
    watcher.startAsync();
    watcher.stopAsync();
    watcher.awaitTerminated();
    verify(mockClient, after(1000).atLeast(1)).getTopicPartitionCount(path());
    Mockito.reset(mockClient);
    verify(mockClient, after(20).never()).getTopic(any());
  }
}
