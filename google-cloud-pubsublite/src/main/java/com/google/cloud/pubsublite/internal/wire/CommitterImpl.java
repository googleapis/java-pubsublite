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

import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceStub;
import com.google.cloud.pubsublite.proto.InitialCommitCursorRequest;
import com.google.cloud.pubsublite.proto.SequencedCommitCursorResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Monitor.Guard;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.Optional;

public class CommitterImpl extends ProxyService
    implements Committer, RetryingConnectionObserver<SequencedCommitCursorResponse> {
  @GuardedBy("monitor.monitor")
  private final RetryingConnection<ConnectedCommitter> connection;

  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  @GuardedBy("monitor.monitor")
  private final CommitState state = new CommitState();

  @VisibleForTesting
  CommitterImpl(
      CursorServiceStub stub,
      ConnectedCommitterFactory factory,
      InitialCommitCursorRequest initialRequest)
      throws StatusException {
    this.connection =
        new RetryingConnectionImpl<>(
            stub::streamingCommitCursor,
            factory,
            StreamingCommitCursorRequest.newBuilder().setInitial(initialRequest).build(),
            this);
    addServices(this.connection);
  }

  public CommitterImpl(CursorServiceStub stub, InitialCommitCursorRequest request)
      throws StatusException {
    this(stub, new ConnectedCommitterImpl.Factory(), request);
  }

  // ProxyService implementation.
  @Override
  protected void handlePermanentError(StatusException error) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      state.abort(error);
    }
  }

  @Override
  protected void start() {}

  @Override
  protected void stop() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
    }
    try (CloseableMonitor.Hold h =
        monitor.enterWhenUninterruptibly(
            new Guard(monitor.monitor) {
              @Override
              public boolean isSatisfied() {
                // Wait until the state is empty. It will be made empty by a call to state.abort()
                // if an error occurs.
                return state.isEmpty();
              }
            })) {}
  }

  // RetryingConnectionObserver implementation.
  @Override
  public void triggerReinitialize() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      connection.reinitialize();
      Optional<Offset> offsetOr = state.reinitializeAndReturnToSend();
      if (!offsetOr.isPresent()) return; // There are no outstanding commit requests.
      connection.modifyConnection(
          connectedCommitter -> {
            Preconditions.checkArgument(connectedCommitter.isPresent());
            connectedCommitter.get().commit(offsetOr.get());
          });
    } catch (StatusException e) {
      onPermanentError(e);
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
    } catch (StatusException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public Status onClientResponse(SequencedCommitCursorResponse value) {
    Preconditions.checkArgument(value.getAcknowledgedCommits() > 0);
    try (CloseableMonitor.Hold h = monitor.enter()) {
      return state.complete(value.getAcknowledgedCommits());
    }
  }
}
