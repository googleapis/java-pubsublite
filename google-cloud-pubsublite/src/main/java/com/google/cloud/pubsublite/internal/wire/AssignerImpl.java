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

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceStub;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.StatusException;
import java.util.HashSet;
import java.util.Set;

public class AssignerImpl extends TrivialProxyService
    implements Assigner, RetryingConnectionObserver<PartitionAssignment> {
  @GuardedBy("monitor.monitor")
  private final RetryingConnection<ConnectedAssigner> connection;

  @GuardedBy("monitor.monitor")
  private final PartitionAssignmentReceiver receiver;

  private final CloseableMonitor monitor = new CloseableMonitor();

  @VisibleForTesting
  AssignerImpl(
      PartitionAssignmentServiceStub stub,
      ConnectedAssignerFactory factory,
      InitialPartitionAssignmentRequest initialRequest,
      PartitionAssignmentReceiver receiver)
      throws StatusException {
    this.receiver = receiver;
    this.connection =
        new RetryingConnectionImpl<>(
            stub::assignPartitions,
            factory,
            PartitionAssignmentRequest.newBuilder().setInitial(initialRequest).build(),
            this);
    addServices(this.connection);
  }

  @Override
  public void triggerReinitialize() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      connection.reinitialize();
    }
  }

  private static Set<Partition> toSet(PartitionAssignment assignment) throws StatusException {
    Set<Partition> partitions = new HashSet<>();
    for (long partition : assignment.getPartitionsList()) {
      partitions.add(Partition.of(partition));
    }
    return partitions;
  }

  @Override
  public void onClientResponse(PartitionAssignment value) throws StatusException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      receiver.handleAssignment(toSet(value));
      connection.modifyConnection(connectionOr -> connectionOr.ifPresent(ConnectedAssigner::ack));
    }
  }
}
