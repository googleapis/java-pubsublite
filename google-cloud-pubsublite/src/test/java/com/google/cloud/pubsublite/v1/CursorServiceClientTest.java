/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.common.collect.Lists;
import com.google.protobuf.AbstractMessage;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class CursorServiceClientTest {
  private static MockCursorService mockCursorService;
  private static MockServiceHelper mockServiceHelper;
  private LocalChannelProvider channelProvider;
  private CursorServiceClient client;

  @BeforeClass
  public static void startStaticServer() {
    mockCursorService = new MockCursorService();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockCursorService));
    mockServiceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    mockServiceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    mockServiceHelper.reset();
    channelProvider = mockServiceHelper.createChannelProvider();
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
  public void streamingCommitCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
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
      InvalidArgumentException apiException = ((InvalidArgumentException) e.getCause());
      Assert.assertEquals(StatusCode.Code.INVALID_ARGUMENT, apiException.getStatusCode().getCode());
    }
  }

  @Test
  public void commitCursorTest() throws Exception {
    CommitCursorResponse expectedResponse = CommitCursorResponse.newBuilder().build();
    mockCursorService.addResponse(expectedResponse);

    CommitCursorRequest request =
        CommitCursorRequest.newBuilder()
            .setSubscription("subscription341203229")
            .setPartition(-1799810326)
            .setCursor(Cursor.newBuilder().build())
            .build();

    CommitCursorResponse actualResponse = client.commitCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockCursorService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CommitCursorRequest actualRequest = ((CommitCursorRequest) actualRequests.get(0));

    Assert.assertEquals(request.getSubscription(), actualRequest.getSubscription());
    Assert.assertEquals(request.getPartition(), actualRequest.getPartition());
    Assert.assertEquals(request.getCursor(), actualRequest.getCursor());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void commitCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);

    try {
      CommitCursorRequest request =
          CommitCursorRequest.newBuilder()
              .setSubscription("subscription341203229")
              .setPartition(-1799810326)
              .setCursor(Cursor.newBuilder().build())
              .build();
      client.commitCursor(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listPartitionCursorsTest() throws Exception {
    PartitionCursor responsesElement = PartitionCursor.newBuilder().build();
    ListPartitionCursorsResponse expectedResponse =
        ListPartitionCursorsResponse.newBuilder()
            .setNextPageToken("")
            .addAllPartitionCursors(Arrays.asList(responsesElement))
            .build();
    mockCursorService.addResponse(expectedResponse);

    SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    ListPartitionCursorsPagedResponse pagedListResponse = client.listPartitionCursors(parent);

    List<PartitionCursor> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getPartitionCursorsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockCursorService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListPartitionCursorsRequest actualRequest =
        ((ListPartitionCursorsRequest) actualRequests.get(0));

    Assert.assertEquals(parent.toString(), actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listPartitionCursorsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);

    try {
      SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
      client.listPartitionCursors(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void listPartitionCursorsTest2() throws Exception {
    PartitionCursor responsesElement = PartitionCursor.newBuilder().build();
    ListPartitionCursorsResponse expectedResponse =
        ListPartitionCursorsResponse.newBuilder()
            .setNextPageToken("")
            .addAllPartitionCursors(Arrays.asList(responsesElement))
            .build();
    mockCursorService.addResponse(expectedResponse);

    String parent = "parent-995424086";

    ListPartitionCursorsPagedResponse pagedListResponse = client.listPartitionCursors(parent);

    List<PartitionCursor> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getPartitionCursorsList().get(0), resources.get(0));

    List<AbstractMessage> actualRequests = mockCursorService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ListPartitionCursorsRequest actualRequest =
        ((ListPartitionCursorsRequest) actualRequests.get(0));

    Assert.assertEquals(parent, actualRequest.getParent());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void listPartitionCursorsExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockCursorService.addException(exception);

    try {
      String parent = "parent-995424086";
      client.listPartitionCursors(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
