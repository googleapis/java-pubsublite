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

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.grpc.testing.MockStreamObserver;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.common.collect.Lists;
import com.google.protobuf.AbstractMessage;
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
public class CursorServiceClientTest {
  private static MockAdminService mockAdminService;
  private static MockCursorService mockCursorService;
  private static MockPublisherService mockPublisherService;
  private static MockSubscriberService mockSubscriberService;
  private static MockPartitionAssignmentService mockPartitionAssignmentService;
  private static MockTopicStatsService mockTopicStatsService;
  private static MockServiceHelper serviceHelper;
  private CursorServiceClient client;
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
    CursorServiceSettings settings =
        CursorServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = CursorServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  @SuppressWarnings("all")
  public void streamingCommitCursorTest() throws Exception {
    StreamingCommitCursorResponse expectedResponse =
        StreamingCommitCursorResponse.newBuilder().build();
    mockCursorService.addResponse(expectedResponse);
    StreamingCommitCursorRequest request = StreamingCommitCursorRequest.newBuilder().build();

    MockStreamObserver<StreamingCommitCursorResponse> responseObserver = new MockStreamObserver<>();

    BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse> callable =
        client.streamingCommitCursorCallable();
    ApiStreamObserver<StreamingCommitCursorRequest> requestObserver =
        callable.bidiStreamingCall(responseObserver);

    requestObserver.onNext(request);
    requestObserver.onCompleted();

    List<StreamingCommitCursorResponse> actualResponses = responseObserver.future().get();
    Assert.assertEquals(1, actualResponses.size());
    Assert.assertEquals(expectedResponse, actualResponses.get(0));
  }

  @Test
  @SuppressWarnings("all")
  public void streamingCommitCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);
    StreamingCommitCursorRequest request = StreamingCommitCursorRequest.newBuilder().build();

    MockStreamObserver<StreamingCommitCursorResponse> responseObserver = new MockStreamObserver<>();

    BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse> callable =
        client.streamingCommitCursorCallable();
    ApiStreamObserver<StreamingCommitCursorRequest> requestObserver =
        callable.bidiStreamingCall(responseObserver);

    requestObserver.onNext(request);

    try {
      List<StreamingCommitCursorResponse> actualResponses = responseObserver.future().get();
      Assert.fail("No exception thrown");
    } catch (ExecutionException e) {
      Assert.assertTrue(e.getCause() instanceof InvalidArgumentException);
      InvalidArgumentException apiException = (InvalidArgumentException) e.getCause();
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  @SuppressWarnings("all")
  public void commitCursorTest() {
    CommitCursorResponse expectedResponse = CommitCursorResponse.newBuilder().build();
    mockCursorService.addResponse(expectedResponse);

    CommitCursorRequest request = CommitCursorRequest.newBuilder().build();

    CommitCursorResponse actualResponse = client.commitCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockCursorService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CommitCursorRequest actualRequest = (CommitCursorRequest) actualRequests.get(0);

    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void commitCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);

    try {
      CommitCursorRequest request = CommitCursorRequest.newBuilder().build();

      client.commitCursor(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }

  @Test
  @SuppressWarnings("all")
  public void listPartitionCursorsTest() {
    String nextPageToken = "";
    PartitionCursor partitionCursorsElement = PartitionCursor.newBuilder().build();
    List<PartitionCursor> partitionCursors = Arrays.asList(partitionCursorsElement);
    ListPartitionCursorsResponse expectedResponse =
        ListPartitionCursorsResponse.newBuilder()
            .setNextPageToken(nextPageToken)
            .addAllPartitionCursors(partitionCursors)
            .build();
    mockCursorService.addResponse(expectedResponse);

    SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    ListPartitionCursorsPagedResponse pagedListResponse = client.listPartitionCursors(parent);

    List<PartitionCursor> resources = Lists.newArrayList(pagedListResponse.iterateAll());
    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getPartitionCursorsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockCursorService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListPartitionCursorsRequest actualRequest = (ListPartitionCursorsRequest) actualRequests.get(0);

    Assert.assertEquals(parent, SubscriptionName.parse(actualRequest.getParent()));
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void listPartitionCursorsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);

    try {
      SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

      client.listPartitionCursors(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }
}
