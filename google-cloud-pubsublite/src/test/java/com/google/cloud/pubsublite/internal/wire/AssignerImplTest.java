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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.core.ApiService.Listener;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.internal.FakeApiService;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class AssignerImplTest {
  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  private static PartitionAssignmentRequest initialRequest() {
    try {
      return PartitionAssignmentRequest.newBuilder()
          .setInitial(
              InitialPartitionAssignmentRequest.newBuilder()
                  .setSubscription(
                      SubscriptionPaths.newBuilder()
                          .setProjectNumber(ProjectNumber.of(12345))
                          .setZone(CloudZone.of(CloudRegion.of("us-east1"), 'a'))
                          .setSubscriptionName(SubscriptionName.of("some_subscription"))
                          .build()
                          .value()))
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  abstract static class ConnectedAssignerFakeService extends FakeApiService
      implements ConnectedAssigner {}

  @Spy private ConnectedAssignerFakeService connectedAssigner;
  @Mock private ConnectedAssignerFactory assignerFactory;

  @Mock private PartitionAssignmentReceiver receiver;
  @Mock private Listener permanentErrorHandler;

  private Assigner assigner;
  private StreamObserver<PartitionAssignment> leakedResponseObserver;

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
    ManagedChannel channel =
        grpcCleanup.register(
            InProcessChannelBuilder.forName("localhost:12345").directExecutor().build());
    PartitionAssignmentServiceStub unusedStub = PartitionAssignmentServiceGrpc.newStub(channel);
    assigner =
        new AssignerImpl(unusedStub, assignerFactory, initialRequest().getInitial(), receiver);
  }

  @Test
  public void construct_CallsFactoryNew() {
    verifyNoMoreInteractions(assignerFactory);
    verifyNoMoreInteractions(connectedAssigner);
  }
}
