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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentAck;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class ConnectedAssignerImplTest {
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

  private static final ConnectedAssignerImpl.Factory FACTORY = new ConnectedAssignerImpl.Factory();

  @Mock private StreamFactory<PartitionAssignmentRequest, PartitionAssignment> streamFactory;

  @Mock private ClientStream<PartitionAssignmentRequest> mockRequestStream;

  @Mock private ResponseObserver<PartitionAssignment> mockOutputStream;

  private Optional<ResponseObserver<PartitionAssignment>> leakedResponseStream = Optional.empty();

  private ConnectedAssigner assigner;

  public ConnectedAssignerImplTest() {}

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    doAnswer(
        (Answer<ClientStream<PartitionAssignmentRequest>>)
            args -> {
              Preconditions.checkArgument(!leakedResponseStream.isPresent());
              ResponseObserver<PartitionAssignment> responseObserver = args.getArgument(0);
              leakedResponseStream = Optional.of(responseObserver);
              return mockRequestStream;
            })
        .when(streamFactory)
        .New(any());
  }

  @After
  public void tearDown() {
    leakedResponseStream.ifPresent(ResponseObserver::onComplete);
  }

  private Answer<Void> AnswerWith(PartitionAssignment response) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onResponse(response);
      return null;
    };
  }

  private Answer<Void> AnswerWith(PartitionAssignment.Builder response) {
    return AnswerWith(response.build());
  }

  private Answer<Void> AnswerWith(Code error) {
    return invocation -> {
      Preconditions.checkArgument(leakedResponseStream.isPresent());
      leakedResponseStream.get().onError(new CheckedApiException(error).underlying);
      leakedResponseStream = Optional.empty();
      verify(mockRequestStream).closeSendWithError(argThat(new ApiExceptionMatcher(error)));
      verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(error)));
      verifyNoMoreInteractions(mockOutputStream);
      return null;
    };
  }

  @Test
  public void construct_SendsInitialThenResponse() throws Exception {
    doAnswer(AnswerWith(PartitionAssignment.newBuilder().addPartitions(7)))
        .when(mockRequestStream)
        .send(initialRequest());
    try (ConnectedAssigner assigner =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_SendsInitialThenError() throws Exception {
    doAnswer(AnswerWith(Code.INTERNAL)).when(mockRequestStream).send(initialRequest());
    try (ConnectedAssigner assigner =
        FACTORY.New(streamFactory, mockOutputStream, initialRequest())) {}
  }

  @Test
  public void construct_noInitialResponse() throws Exception {
    assigner = FACTORY.New(streamFactory, mockOutputStream, initialRequest());
    verify(mockRequestStream).send(initialRequest());
  }

  private void initialize() {
    doAnswer(AnswerWith(PartitionAssignment.newBuilder().addPartitions(1)))
        .when(mockRequestStream)
        .send(initialRequest());
    assigner = FACTORY.New(streamFactory, mockOutputStream, initialRequest());
    verify(mockRequestStream).send(initialRequest());
    reset(mockRequestStream);
    reset(mockOutputStream);
  }

  @Test
  public void ackAfterClose_Dropped() throws Exception {
    initialize();
    assigner.close();
    verify(mockRequestStream).closeSend();
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
    verify(mockRequestStream).send(request);
  }

  @Test
  public void assignmentResponseBeforeAckAborts() {
    initialize();
    PartitionAssignment response = PartitionAssignment.newBuilder().addPartitions(1).build();
    leakedResponseStream.get().onResponse(response);
    verify(mockOutputStream).onError(argThat(new ApiExceptionMatcher(Code.FAILED_PRECONDITION)));
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
    verify(mockRequestStream).send(request);
    PartitionAssignment response = PartitionAssignment.newBuilder().addPartitions(1).build();
    leakedResponseStream.get().onResponse(response);
    verify(mockOutputStream).onResponse(response);
  }
}
