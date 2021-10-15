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

import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertFutureThrowsCode;
import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertThrowableMatches;
import static com.google.cloud.pubsublite.internal.testing.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class CommitterImplTest {
  private static StreamingCommitCursorRequest initialRequest() {
    return StreamingCommitCursorRequest.newBuilder()
        .setInitial(
            InitialCommitCursorRequest.newBuilder()
                .setSubscription(
                    SubscriptionPath.newBuilder()
                        .setProject(ProjectNumber.of(12345))
                        .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                        .setName(SubscriptionName.of("some_subscription"))
                        .build()
                        .toString())
                .setPartition(1024))
        .build();
  }

  @Mock
  private StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      unusedStreamFactory;

  @Mock private ConnectedCommitter mockConnectedCommitter;
  @Mock private ConnectedCommitterFactory mockCommitterFactory;

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  private Committer committer;
  private ResponseObserver<SequencedCommitCursorResponse> leakedResponseObserver;

  SequencedCommitCursorResponse ResponseWithCount(long count) {
    return SequencedCommitCursorResponse.newBuilder().setAcknowledgedCommits(count).build();
  }

  @Before
  public void setUp() throws CheckedApiException {
    initMocks(this);
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return mockConnectedCommitter;
            })
        .when(mockCommitterFactory)
        .New(any(), any(), eq(initialRequest()));
    committer =
        new CommitterImpl(unusedStreamFactory, mockCommitterFactory, initialRequest().getInitial());
    committer.startAsync().awaitRunning();
    verify(mockCommitterFactory).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void construct_CallsFactoryNew() {
    verifyNoMoreInteractions(mockCommitterFactory);
    verifyNoInteractions(mockConnectedCommitter);
  }

  @Test
  public void stopWaitsForCommit() throws Exception {
    Offset commitOffset = Offset.of(101);
    ApiFuture<Void> future = committer.commitOffset(commitOffset);
    verify(mockConnectedCommitter).commit(commitOffset);

    CountDownLatch latch = new CountDownLatch(1);
    Future<?> closeFuture =
        executorService.submit(
            () -> {
              latch.countDown();
              committer.stopAsync();
              committer.awaitTerminated();
              assertThat(future.isDone()).isTrue();
            });

    latch.await();
    Thread.sleep(100);
    assertThat(future.isDone()).isFalse();
    leakedResponseObserver.onResponse(ResponseWithCount(1));
    closeFuture.get();

    assertFutureThrowsCode(committer.commitOffset(Offset.of(1)), Code.FAILED_PRECONDITION);
  }

  @Test
  public void responseMoreThanSentError() throws Exception {
    Future<Void> failed = whenFailed(committer);
    ApiFuture<Void> future = committer.commitOffset(Offset.of(10));
    leakedResponseObserver.onResponse(ResponseWithCount(2));
    failed.get();
    assertThrowableMatches(committer.failureCause(), Code.FAILED_PRECONDITION);
    assertFutureThrowsCode(future, Code.FAILED_PRECONDITION);
  }

  @Test
  public void multipleSentCompletedInOrder() {
    ApiFuture<Void> future1 = committer.commitOffset(Offset.of(10));
    ApiFuture<Void> future2 = committer.commitOffset(Offset.of(1));
    ApiFuture<Void> future3 = committer.commitOffset(Offset.of(87));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedResponseObserver.onResponse(ResponseWithCount(1));

    assertThat(future1.isDone()).isTrue();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    leakedResponseObserver.onResponse(ResponseWithCount(2));

    assertThat(future2.isDone()).isTrue();
    assertThat(future3.isDone()).isTrue();

    assertThat(committer.isRunning()).isTrue();
  }

  @Test
  public void stopInCommitCallback() throws Exception {
    ApiFuture<Void> future = committer.commitOffset(Offset.of(10));
    Future<Void> failed = whenFailed(committer);
    leakedResponseObserver.onError(new CheckedApiException(Code.FAILED_PRECONDITION).underlying);
    assertFutureThrowsCode(future, Code.FAILED_PRECONDITION);
    failed.get();
    assertThrowableMatches(committer.failureCause(), Code.FAILED_PRECONDITION);
  }

  @Test
  public void waitUntilEmptyReturnsWhenEmpty() throws Exception {
    ApiFuture<Void> future1 = committer.commitOffset(Offset.of(10));
    ApiFuture<Void> future2 = committer.commitOffset(Offset.of(1));

    Future<?> waitFuture =
        executorService.submit(
            () -> {
              try {
                committer.waitUntilEmpty();
              } catch (Throwable e) {
                throw new IllegalStateException(e);
              }
            });
    assertThat(waitFuture.isDone()).isFalse();

    // Still not done after 1 commit response.
    leakedResponseObserver.onResponse(ResponseWithCount(1));
    assertThat(waitFuture.isDone()).isFalse();
    assertThat(future1.isDone()).isTrue();
    assertThat(future2.isDone()).isFalse();

    leakedResponseObserver.onResponse(ResponseWithCount(1));
    assertThat(future1.isDone()).isTrue();
    assertThat(future2.isDone()).isTrue();
    waitFuture.get(30, TimeUnit.SECONDS);
  }

  @Test
  public void waitUntilEmptyReturnsOnShutdown() throws Exception {
    Future<?> unusedFuture = committer.commitOffset(Offset.of(10));

    CountDownLatch latch = new CountDownLatch(1);
    Future<?> waitFuture =
        executorService.submit(
            () -> {
              try {
                latch.await();
                committer.waitUntilEmpty();
              } catch (Throwable e) {
                throw new IllegalStateException(e);
              }
            });
    assertThat(waitFuture.isDone()).isFalse();

    Future<?> stopFuture =
        executorService.submit(
            () -> {
              latch.countDown();
              committer.stopAsync().awaitTerminated();
            });

    leakedResponseObserver.onResponse(ResponseWithCount(1));
    stopFuture.get(30, TimeUnit.SECONDS);
    waitFuture.get(30, TimeUnit.SECONDS);
  }

  @Test
  public void waitUntilEmptyThrowsOnPermanentError() throws Exception {
    Future<?> unusedFuture = committer.commitOffset(Offset.of(10));

    Future<?> waitFuture =
        executorService.submit(
            () -> assertThrows(CheckedApiException.class, () -> committer.waitUntilEmpty()));
    assertThat(waitFuture.isDone()).isFalse();

    Future<Void> failed = whenFailed(committer);
    leakedResponseObserver.onResponse(ResponseWithCount(2));
    failed.get(30, TimeUnit.SECONDS);

    waitFuture.get(30, TimeUnit.SECONDS);
  }
}
