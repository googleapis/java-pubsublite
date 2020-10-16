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

import static com.google.cloud.pubsublite.internal.wire.RetryingConnectionHelpers.whenFailed;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.core.ApiService.Listener;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class AssignerImplTest {
  private static PartitionAssignmentRequest initialRequest() {
    return PartitionAssignmentRequest.newBuilder()
        .setInitial(
            InitialPartitionAssignmentRequest.newBuilder()
                .setSubscription(
                    SubscriptionPath.newBuilder()
                        .setProject(ProjectNumber.of(12345))
                        .setLocation(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                        .setName(SubscriptionName.of("some_subscription"))
                        .build()
                        .toString()))
        .build();
  }

  private static final PartitionAssignment ASSIGNMENT =
      PartitionAssignment.newBuilder().addPartitions(1).addPartitions(7).build();

  private Set<Partition> partitions() {
    Set<Partition> partitions = new HashSet<>();
    for (long partition : ASSIGNMENT.getPartitionsList()) {
      partitions.add(Partition.of(partition));
    }
    return partitions;
  }

  @Mock private StreamFactory<PartitionAssignmentRequest, PartitionAssignment> unusedStreamFactory;

  @Mock private ConnectedAssigner connectedAssigner;
  @Mock private ConnectedAssignerFactory assignerFactory;

  @Mock private PartitionAssignmentReceiver receiver;
  @Mock private Listener permanentErrorHandler;

  private Assigner assigner;
  private ResponseObserver<PartitionAssignment> leakedResponseObserver;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    doAnswer(
            args -> {
              leakedResponseObserver = args.getArgument(1);
              return connectedAssigner;
            })
        .when(assignerFactory)
        .New(any(), any(), eq(initialRequest()));
    assigner =
        new AssignerImpl(
            unusedStreamFactory, assignerFactory, initialRequest().getInitial(), receiver);
    assigner.addListener(permanentErrorHandler, MoreExecutors.directExecutor());
    assigner.startAsync().awaitRunning();
    verify(assignerFactory).New(any(), any(), eq(initialRequest()));
  }

  @Test
  public void construct_CallsFactoryNew() {
    verifyNoMoreInteractions(assignerFactory);
    verifyNoMoreInteractions(connectedAssigner);
  }

  @Test
  public void delivery_CallsReceiver() {
    leakedResponseObserver.onResponse(ASSIGNMENT);
    verify(receiver).handleAssignment(partitions());
  }

  @Test
  public void responseObserverFailure_Fails() throws Exception {
    Future<Void> failed = whenFailed(permanentErrorHandler);
    leakedResponseObserver.onError(new CheckedApiException(Code.INVALID_ARGUMENT));
    failed.get();
    verify(permanentErrorHandler)
        .failed(any(), argThat(new ApiExceptionMatcher(Code.INVALID_ARGUMENT)));
  }
}
