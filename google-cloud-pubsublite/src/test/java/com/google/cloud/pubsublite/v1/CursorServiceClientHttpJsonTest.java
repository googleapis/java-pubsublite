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

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.v1.stub.HttpJsonCursorServiceStub;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class CursorServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static CursorServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonCursorServiceStub.getMethodDescriptors(),
            CursorServiceSettings.getDefaultEndpoint());
    CursorServiceSettings settings =
        CursorServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                CursorServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = CursorServiceClient.create(settings);
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
  public void streamingCommitCursorUnsupportedMethodTest() throws Exception {
    // The streamingCommitCursor() method is not supported in REST transport.
    // This empty test is generated for technical reasons.
  }

  @Test
  public void commitCursorTest() throws Exception {
    CommitCursorResponse expectedResponse = CommitCursorResponse.newBuilder().build();
    mockService.addResponse(expectedResponse);

    CommitCursorRequest request =
        CommitCursorRequest.newBuilder()
            .setSubscription(
                "projects/project-3634/locations/location-3634/subscriptions/subscription-3634")
            .setPartition(-1799810326)
            .setCursor(Cursor.newBuilder().build())
            .build();

    CommitCursorResponse actualResponse = client.commitCursor(request);
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
  public void commitCursorExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      CommitCursorRequest request =
          CommitCursorRequest.newBuilder()
              .setSubscription(
                  "projects/project-3634/locations/location-3634/subscriptions/subscription-3634")
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
    mockService.addResponse(expectedResponse);

    SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");

    ListPartitionCursorsPagedResponse pagedListResponse = client.listPartitionCursors(parent);

    List<PartitionCursor> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getPartitionCursorsList().get(0), resources.get(0));

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
  public void listPartitionCursorsExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String parent = "projects/project-8485/locations/location-8485/subscriptions/subscription-8485";

    ListPartitionCursorsPagedResponse pagedListResponse = client.listPartitionCursors(parent);

    List<PartitionCursor> resources = Lists.newArrayList(pagedListResponse.iterateAll());

    Assert.assertEquals(1, resources.size());
    Assert.assertEquals(expectedResponse.getPartitionCursorsList().get(0), resources.get(0));

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
  public void listPartitionCursorsExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String parent =
          "projects/project-8485/locations/location-8485/subscriptions/subscription-8485";
      client.listPartitionCursors(parent);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}
