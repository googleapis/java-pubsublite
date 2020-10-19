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

import static com.google.cloud.pubsublite.internal.StatusExceptionMatcher.assertFutureThrowsCode;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CommitStateTest {
  private final CommitState state = new CommitState();

  @Test
  public void multipleSentCompletedInOrder() throws Exception {
    ApiFuture<Void> future1 = state.addCommit(Offset.of(10));
    ApiFuture<Void> future2 = state.addCommit(Offset.of(1));
    ApiFuture<Void> future3 = state.addCommit(Offset.of(87));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    state.complete(1);

    assertThat(future1.isDone()).isTrue();
    assertThat(future2.isDone()).isFalse();
    assertThat(future3.isDone()).isFalse();

    state.complete(2);

    assertThat(future2.isDone()).isTrue();
    assertThat(future3.isDone()).isTrue();
  }

  @Test
  public void completeMoreThanAddedError() {
    ApiFuture<Void> future = state.addCommit(Offset.of(10));

    assertThat(future.isDone()).isFalse();

    StatusException e = assertThrows(StatusException.class, () -> state.complete(2));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.FAILED_PRECONDITION);

    assertFutureThrowsCode(future, Code.FAILED_PRECONDITION);
  }

  @Test
  public void reinitializeCompletesAllAfterSingleCompletion() throws Exception {
    ApiFuture<Void> future1 = state.addCommit(Offset.of(10));
    ApiFuture<Void> future2 = state.addCommit(Offset.of(99));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    assertThat(state.reinitializeAndReturnToSend()).hasValue(Offset.of(99));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    state.complete(1);

    assertThat(future1.isDone()).isTrue();
    assertThat(future2.isDone()).isTrue();
  }

  @Test
  public void reinitializeFailsAllAfterExcessCompletion() throws Exception {
    ApiFuture<Void> future1 = state.addCommit(Offset.of(10));
    ApiFuture<Void> future2 = state.addCommit(Offset.of(99));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    assertThat(state.reinitializeAndReturnToSend()).hasValue(Offset.of(99));

    assertThat(future1.isDone()).isFalse();
    assertThat(future2.isDone()).isFalse();

    StatusException e = assertThrows(StatusException.class, () -> state.complete(2));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.FAILED_PRECONDITION);

    assertFutureThrowsCode(future1, Code.FAILED_PRECONDITION);
    assertFutureThrowsCode(future2, Code.FAILED_PRECONDITION);
  }
}
