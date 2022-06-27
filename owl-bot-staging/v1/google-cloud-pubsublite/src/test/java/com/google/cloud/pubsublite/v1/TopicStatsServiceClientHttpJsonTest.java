/*
 * Copyright 2022 Google LLC
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
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TimeTarget;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.v1.stub.HttpJsonTopicStatsServiceStub;
import com.google.protobuf.Timestamp;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class TopicStatsServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static TopicStatsServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonTopicStatsServiceStub.getMethodDescriptors(),
            TopicStatsServiceSettings.getDefaultEndpoint());
    TopicStatsServiceSettings settings =
        TopicStatsServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                TopicStatsServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = TopicStatsServiceClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
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
    mockService.addResponse(expectedResponse);

    ComputeMessageStatsRequest request =
        ComputeMessageStatsRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .setStartCursor(Cursor.newBuilder().build())
            .setEndCursor(Cursor.newBuilder().build())
            .build();

    ComputeMessageStatsResponse actualResponse = client.computeMessageStats(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void computeMessageStatsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    ComputeHeadCursorRequest request =
        ComputeHeadCursorRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .build();

    ComputeHeadCursorResponse actualResponse = client.computeHeadCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void computeHeadCursorExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    ComputeTimeCursorRequest request =
        ComputeTimeCursorRequest.newBuilder()
            .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
            .setPartition(-1799810326)
            .setTarget(TimeTarget.newBuilder().build())
            .build();

    ComputeTimeCursorResponse actualResponse = client.computeTimeCursor(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void computeTimeCursorExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
