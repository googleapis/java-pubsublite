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

import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentAck;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;

public class ConnectedAssignerImpl
    extends SingleConnection<PartitionAssignmentRequest, PartitionAssignment, PartitionAssignment>
    implements ConnectedAssigner {
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  boolean outstanding = false;

  private ConnectedAssignerImpl(
      StreamFactory<PartitionAssignmentRequest, PartitionAssignment> streamFactory,
      StreamObserver<PartitionAssignment> clientStream,
      PartitionAssignmentRequest initialRequest) {
    super(streamFactory, clientStream);
    initialize(initialRequest);
  }

  static class Factory implements ConnectedAssignerFactory {
    @Override
    public ConnectedAssigner New(
        StreamFactory<PartitionAssignmentRequest, PartitionAssignment> streamFactory,
        StreamObserver<PartitionAssignment> clientStream,
        PartitionAssignmentRequest initialRequest) {
      return new ConnectedAssignerImpl(streamFactory, clientStream, initialRequest);
    }
  }

  // SingleConnection implementation.
  @Override
  protected Status handleInitialResponse(PartitionAssignment response) {
    // The assignment stream is server-initiated by sending a PartitionAssignment. The
    // initial response from the server is handled identically to other responses.
    return handleStreamResponse(response);
  }

  @Override
  protected Status handleStreamResponse(PartitionAssignment response) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkState(
          !outstanding,
          "Received assignment from the server while there was an assignment outstanding.");
      outstanding = true;
    } catch (StatusException e) {
      return e.getStatus();
    }
    sendToClient(response);
    return Status.OK;
  }

  // ConnectedAssigner implementation.
  @Override
  public void ack() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      checkState(outstanding, "Client acknowledged when there was no request outstanding.");
      outstanding = false;
    } catch (StatusException e) {
      setError(e.getStatus());
    }
    sendToStream(
        PartitionAssignmentRequest.newBuilder()
            .setAck(PartitionAssignmentAck.getDefaultInstance())
            .build());
  }
}
