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

import static com.google.cloud.pubsublite.internal.wire.ApiServiceUtils.autoCloseableAsApiService;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import com.google.common.io.BaseEncoding;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.HashSet;
import java.util.Set;

public class AssignerImpl extends ProxyService
    implements Assigner, RetryingConnectionObserver<PartitionAssignment> {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private final PartitionAssignmentRequest initialRequest;
  private final String uuidHex;

  @GuardedBy("this")
  private final RetryingConnection<PartitionAssignmentRequest, ConnectedAssigner> connection;

  @GuardedBy("this")
  private final PartitionAssignmentReceiver receiver;

  @VisibleForTesting
  AssignerImpl(
      StreamFactory<PartitionAssignmentRequest, PartitionAssignment> streamFactory,
      ConnectedAssignerFactory factory,
      InitialPartitionAssignmentRequest initialRequest,
      PartitionAssignmentReceiver receiver)
      throws ApiException {
    this.initialRequest =
        PartitionAssignmentRequest.newBuilder().setInitial(initialRequest).build();
    this.uuidHex =
        BaseEncoding.base16().lowerCase().encode(initialRequest.getClientId().toByteArray());
    this.receiver = receiver;
    this.connection =
        new RetryingConnectionImpl<>(streamFactory, factory, this, this.initialRequest);
    addServices(this.connection);
  }

  public AssignerImpl(
      PartitionAssignmentServiceClient client,
      InitialPartitionAssignmentRequest initialRequest,
      PartitionAssignmentReceiver receiver)
      throws ApiException {
    this(
        stream -> client.assignPartitionsCallable().splitCall(stream),
        new ConnectedAssignerImpl.Factory(),
        initialRequest,
        receiver);
    addServices(autoCloseableAsApiService(client));
  }

  @Override
  public synchronized void triggerReinitialize(CheckedApiException streamError) {
    connection.reinitialize(initialRequest);
  }

  private static Set<Partition> toSet(PartitionAssignment assignment) throws ApiException {
    Set<Partition> partitions = new HashSet<>();
    for (long partition : assignment.getPartitionsList()) {
      partitions.add(Partition.of(partition));
    }
    return partitions;
  }

  @Override
  public synchronized void onClientResponse(PartitionAssignment value) throws CheckedApiException {
    Set<Partition> partitions = toSet(value);
    logger.atFine().log("Subscriber with uuid %s received assignment: %s", uuidHex, partitions);
    receiver.handleAssignment(partitions);
    logger.atInfo().log("Subscriber with uuid %s handled assignment: %s", uuidHex, partitions);
    connection.modifyConnection(connectionOr -> connectionOr.ifPresent(ConnectedAssigner::ack));
  }
}
