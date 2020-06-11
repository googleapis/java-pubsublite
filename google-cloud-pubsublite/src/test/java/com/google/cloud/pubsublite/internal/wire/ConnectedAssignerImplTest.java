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

import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentAck;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceImplBase;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceStub;
import com.google.common.base.Preconditions;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class ConnectedAssignerImplTest {
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

  private static final ConnectedAssignerImpl.Factory FACTORY = new ConnectedAssignerImpl.Factory();

  private PartitionAssignmentServiceStub stub;

  @Mock private StreamObserver<PartitionAssignmentRequest> mockRequestStream;

  @Mock private StreamObserver<PartitionAssignment> mockOutputStream;

  private final PartitionAssignmentServiceImplBase serviceImpl =
      mock(
          PartitionAssignmentServiceImplBase.class,
          delegatesTo(new PartitionAssignmentServiceImplBase() {}));

  private Optional<StreamObserver<PartitionAssignment>> leakedResponseStream = Optional.empty();

  private ConnectedAssigner assigner;

  public ConnectedAssignerImplTest() {}

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    String serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(serviceImpl)
            .build()
            .start());
    ManagedChannel channel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    stub = PartitionAssignmentServiceGrpc.newStub(channel);

    doAnswer(
            (Answer<StreamObserver<PartitionAssignmentRequest>>)
                args -> {
                  Preconditions.checkArgument(!leakedResponseStream.isPresent());
                  StreamObserver<PartitionAssignment> responseObserver = args.getArgument(0);
                  leakedResponseStream = Optional.of(responseObserver);
                  return mockRequestStream;
                })
        .when(serviceImpl)
        .assignPartitions(any());
  }

  @After
  public void tearDown() {
    leakedResponseStream.ifPresent(StreamObserver::onCompleted);
  }

  private Answer<Void> AnswerWith(PartitionAssignment response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onNext(response);
      return null;
    };
  }

  private Answer<Void> AnswerWith(PartitionAssignment.Builder response) {
    return AnswerWith(response.build());
  }

  private Answer<Void> AnswerWith(Status error) {
    Preconditions.checkArgument(!error.isOk());
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onError(error.asRuntimeException());
      leakedResponseStream = Optional.empty();
      verify(mockRequestStream).onError(argThat(new StatusExceptionMatcher(error.getCode())));
      verifyNoMoreInteractions(mockOutputStream);
      return null;
    };
  }

  @Test
  public void construct_SendsInitialThenResponse() throws Exception {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(AnswerWith(PartitionAssignment.newBuilder().addPartitions(7)))
        .when(mockRequestStream)
        .onNext(initialRequest());
    try (ConnectedAssigner assigner =
        FACTORY.New(stub::assignPartitions, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() throws Exception {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(AnswerWith(Status.INTERNAL)).when(mockRequestStream).onNext(initialRequest());
    try (ConnectedAssigner assigner =
        FACTORY.New(stub::assignPartitions, mockOutputStream, initialRequest())) {}
  }

  private void initialize() {
    Preconditions.checkNotNull(serviceImpl);
    doAnswer(AnswerWith(PartitionAssignment.newBuilder().addPartitions(1)))
        .when(mockRequestStream)
        .onNext(initialRequest());
    assigner = FACTORY.New(stub::assignPartitions, mockOutputStream, initialRequest());
    verify(mockRequestStream).onNext(initialRequest());
    clearInvocations(mockRequestStream);
    clearInvocations(mockOutputStream);
  }

  @Test
  public void ackAfterClose_Dropped() throws Exception {
    initialize();
    assigner.close();
    verify(mockRequestStream).onCompleted();
    assigner.ack();
    verifyNoMoreInteractions(mockRequestStream);
  }

  @Test
  public void ackRequestProxied() {
    initialize();
    PartitionAssignmentRequest request =
        PartitionAssignmentRequest.newBuilder()
            .setAck(PartitionAssignmentAck.getDefaultInstance())
            .build();
    assigner.ack();
    verify(mockRequestStream).onNext(request);
  }

  @Test
  public void assignmentResponseBeforeAckAborts() {
    initialize();
    PartitionAssignment response = PartitionAssignment.newBuilder().addPartitions(1).build();
    leakedResponseStream.get().onNext(response);
    verify(mockOutputStream).onError(argThat(new StatusExceptionMatcher(Code.FAILED_PRECONDITION)));
    leakedResponseStream = Optional.empty();
  }

  @Test
  public void assignmentResponseProxied() {
    initialize();
    PartitionAssignmentRequest request =
        PartitionAssignmentRequest.newBuilder()
            .setAck(PartitionAssignmentAck.getDefaultInstance())
            .build();
    assigner.ack();
    verify(mockRequestStream).onNext(request);
    PartitionAssignment response = PartitionAssignment.newBuilder().addPartitions(1).build();
    leakedResponseStream.get().onNext(response);
    verify(mockOutputStream).onNext(response);
  }
}
