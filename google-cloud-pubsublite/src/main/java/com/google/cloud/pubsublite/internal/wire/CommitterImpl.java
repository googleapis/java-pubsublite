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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Monitor.Guard;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.Optional;

public class CommitterImpl extends ProxyService
    implements Committer, RetryingConnectionObserver<SequencedCommitCursorResponse> {
  private final StreamingCommitCursorRequest initialRequest;

  private final CloseableMonitor monitor = new CloseableMonitor();
  private final Guard isEmptyOrError =
      new Guard(monitor.monitor) {
        public boolean isSatisfied() {
          // Wait until the state is empty or a permanent error occurred.
          return state.isEmpty() || permanentError.isPresent();
        }
      };

  @GuardedBy("monitor.monitor")
  private final RetryingConnection<StreamingCommitCursorRequest, ConnectedCommitter> connection;

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  @GuardedBy("monitor.monitor")
  private Optional<CheckedApiException> permanentError = Optional.empty();

  @GuardedBy("monitor.monitor")
  private final CommitState state = new CommitState();

  @VisibleForTesting
  CommitterImpl(
      StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
      ConnectedCommitterFactory factory,
      InitialCommitCursorRequest initialRequest)
      throws ApiException {
    this.initialRequest =
        StreamingCommitCursorRequest.newBuilder().setInitial(initialRequest).build();
    this.connection =
        new RetryingConnectionImpl<>(streamFactory, factory, this, this.initialRequest);
    addServices(this.connection);
  }

  public CommitterImpl(
      StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> streamFactory,
      InitialCommitCursorRequest request)
      throws ApiException {
    this(streamFactory, new ConnectedCommitterImpl.Factory(), request);
  }

  // ProxyService implementation.
  @Override
  protected void handlePermanentError(CheckedApiException error) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      permanentError = Optional.of(error);
      shutdown = true;
      state.abort(error);
    }
  }

  @Override
  protected void stop() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
    }
    try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(isEmptyOrError)) {}
  }

  // RetryingConnectionObserver implementation.
  @Override
  public void triggerReinitialize(CheckedApiException streamError) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      connection.reinitialize(initialRequest);
      Optional<Offset> offsetOr = state.reinitializeAndReturnToSend();
      if (!offsetOr.isPresent()) return; // There are no outstanding commit requests.
      connection.modifyConnection(
          connectedCommitter -> {
            Preconditions.checkArgument(connectedCommitter.isPresent());
            connectedCommitter.get().commit(offsetOr.get());
          });
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  @Override
  public void onClientResponse(SequencedCommitCursorResponse value) throws CheckedApiException {
    Preconditions.checkArgument(value.getAcknowledgedCommits() > 0);
    try (CloseableMonitor.Hold h = monitor.enter()) {
      state.complete(value.getAcknowledgedCommits());
    }
  }

  // Committer implementation.
  @Override
  public ApiFuture<Void> commitOffset(Offset offset) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkState(!shutdown, "Committed after the stream shut down.");
      connection.modifyConnection(
          connectedCommitter ->
              connectedCommitter.ifPresent(committer -> committer.commit(offset)));
      return state.addCommit(offset);
    } catch (CheckedApiException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void waitUntilEmpty() throws CheckedApiException {
    try (CloseableMonitor.Hold h = monitor.enterWhenUninterruptibly(isEmptyOrError)) {
      if (permanentError.isPresent()) {
        throw permanentError.get();
      }
    }
  }
}
