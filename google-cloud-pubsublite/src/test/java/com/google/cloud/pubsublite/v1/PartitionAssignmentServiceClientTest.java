/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsublite.v1;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@javax.annotation.Generated("by GAPIC")
public class PartitionAssignmentServiceClientTest {
  private static MockAdminService mockAdminService;
  private static MockCursorService mockCursorService;
  private static MockPublisherService mockPublisherService;
  private static MockSubscriberService mockSubscriberService;
  private static MockPartitionAssignmentService mockPartitionAssignmentService;
  private static MockTopicStatsService mockTopicStatsService;
  private static MockServiceHelper serviceHelper;
  private PartitionAssignmentServiceClient client;
  private LocalChannelProvider channelProvider;

  @BeforeClass
  public static void startStaticServer() {
    mockAdminService = new MockAdminService();
    mockCursorService = new MockCursorService();
    mockPublisherService = new MockPublisherService();
    mockSubscriberService = new MockSubscriberService();
    mockPartitionAssignmentService = new MockPartitionAssignmentService();
    mockTopicStatsService = new MockTopicStatsService();
    serviceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(),
            Arrays.<MockGrpcService>asList(
                mockAdminService,
                mockCursorService,
                mockPublisherService,
                mockSubscriberService,
                mockPartitionAssignmentService,
                mockTopicStatsService));
    serviceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    serviceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    serviceHelper.reset();
    channelProvider = serviceHelper.createChannelProvider();
    PartitionAssignmentServiceSettings settings =
        PartitionAssignmentServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = PartitionAssignmentServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  @SuppressWarnings("all")
  public void assignPartitionsTest() throws Exception {
    PartitionAssignment expectedResponse = PartitionAssignment.newBuilder().build();
    mockPartitionAssignmentService.addResponse(expectedResponse);
    PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();

    MockStreamObserver<PartitionAssignment> responseObserver = new MockStreamObserver<>();

    BidiStreamingCallable<PartitionAssignmentRequest, PartitionAssignment> callable =
        client.assignPartitionsCallable();
    ApiStreamObserver<PartitionAssignmentRequest> requestObserver =
        callable.bidiStreamingCall(responseObserver);

    requestObserver.onNext(request);
    requestObserver.onCompleted();

    List<PartitionAssignment> actualResponses = responseObserver.future().get();
    Assert.assertEquals(1, actualResponses.size());
    Assert.assertEquals(expectedResponse, actualResponses.get(0));
  }

  @Test
  @SuppressWarnings("all")
  public void assignPartitionsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockPartitionAssignmentService.addException(exception);
    PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();

    MockStreamObserver<PartitionAssignment> responseObserver = new MockStreamObserver<>();

    BidiStreamingCallable<PartitionAssignmentRequest, PartitionAssignment> callable =
        client.assignPartitionsCallable();
    ApiStreamObserver<PartitionAssignmentRequest> requestObserver =
        callable.bidiStreamingCall(responseObserver);

    requestObserver.onNext(request);

    try {
      List<PartitionAssignment> actualResponses = responseObserver.future().get();
      Assert.fail("No exception thrown");
    } catch (ExecutionException e) {
      Assert.assertTrue(e.getCause() instanceof InvalidArgumentException);
      InvalidArgumentException apiException = (InvalidArgumentException) e.getCause();
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }
}
