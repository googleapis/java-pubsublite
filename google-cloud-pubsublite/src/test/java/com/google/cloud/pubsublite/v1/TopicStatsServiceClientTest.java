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

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TimeTarget;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class TopicStatsServiceClientTest {
  private static MockServiceHelper mockServiceHelper;
  private static MockTopicStatsService mockTopicStatsService;
  private LocalChannelProvider channelProvider;
  private TopicStatsServiceClient client;

  @BeforeClass
  public static void startStaticServer() {
    mockTopicStatsService = new MockTopicStatsService();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockTopicStatsService));
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
    TopicStatsServiceSettings settings =
        TopicStatsServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = TopicStatsServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  public void computeMessageStatsTest() throws Exception {
    ComputeMessageStatsResponse expectedResponse =
        ComputeMessageStatsResponse.newBuilder()
            .setMessageCount(-1229303081)
            .setMessageBytes(-1229929933)
            .setMinimumPublishTime(Timestamp.newBuilder().build())
            .setMinimumEventTime(Timestamp.newBuilder().build())
            .build();
    mockTopicStatsService.addResponse(expectedResponse);

    ComputeMessageStatsRequest request =
        ComputeMessageStatsRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .setStartCursor(Cursor.newBuilder().build())
            .setEndCursor(Cursor.newBuilder().build())
            .build();

    ComputeMessageStatsResponse actualResponse = client.computeMessageStats(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTopicStatsService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComputeMessageStatsRequest actualRequest = ((ComputeMessageStatsRequest) actualRequests.get(0));

    Assert.assertEquals(request.getTopic(), actualRequest.getTopic());
    Assert.assertEquals(request.getPartition(), actualRequest.getPartition());
    Assert.assertEquals(request.getStartCursor(), actualRequest.getStartCursor());
    Assert.assertEquals(request.getEndCursor(), actualRequest.getEndCursor());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void computeMessageStatsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTopicStatsService.addException(exception);

    try {
      ComputeMessageStatsRequest request =
          ComputeMessageStatsRequest.newBuilder()
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartition(-1799810326)
              .setStartCursor(Cursor.newBuilder().build())
              .setEndCursor(Cursor.newBuilder().build())
              .build();
      client.computeMessageStats(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void computeHeadCursorTest() throws Exception {
    ComputeHeadCursorResponse expectedResponse =
        ComputeHeadCursorResponse.newBuilder().setHeadCursor(Cursor.newBuilder().build()).build();
    mockTopicStatsService.addResponse(expectedResponse);

    ComputeHeadCursorRequest request =
        ComputeHeadCursorRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .build();

    ComputeHeadCursorResponse actualResponse = client.computeHeadCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTopicStatsService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComputeHeadCursorRequest actualRequest = ((ComputeHeadCursorRequest) actualRequests.get(0));

    Assert.assertEquals(request.getTopic(), actualRequest.getTopic());
    Assert.assertEquals(request.getPartition(), actualRequest.getPartition());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void computeHeadCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTopicStatsService.addException(exception);

    try {
      ComputeHeadCursorRequest request =
          ComputeHeadCursorRequest.newBuilder()
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartition(-1799810326)
              .build();
      client.computeHeadCursor(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void computeTimeCursorTest() throws Exception {
    ComputeTimeCursorResponse expectedResponse =
        ComputeTimeCursorResponse.newBuilder().setCursor(Cursor.newBuilder().build()).build();
    mockTopicStatsService.addResponse(expectedResponse);

    ComputeTimeCursorRequest request =
        ComputeTimeCursorRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .setTarget(TimeTarget.newBuilder().build())
            .build();

    ComputeTimeCursorResponse actualResponse = client.computeTimeCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTopicStatsService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComputeTimeCursorRequest actualRequest = ((ComputeTimeCursorRequest) actualRequests.get(0));

    Assert.assertEquals(request.getTopic(), actualRequest.getTopic());
    Assert.assertEquals(request.getPartition(), actualRequest.getPartition());
    Assert.assertEquals(request.getTarget(), actualRequest.getTarget());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void computeTimeCursorExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTopicStatsService.addException(exception);

    try {
      ComputeTimeCursorRequest request =
          ComputeTimeCursorRequest.newBuilder()
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartition(-1799810326)
              .setTarget(TimeTarget.newBuilder().build())
              .build();
      client.computeTimeCursor(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
