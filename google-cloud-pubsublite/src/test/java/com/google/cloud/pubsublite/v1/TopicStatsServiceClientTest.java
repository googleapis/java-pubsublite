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
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.protobuf.AbstractMessage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@javax.annotation.Generated("by GAPIC")
public class TopicStatsServiceClientTest {
  private static MockAdminService mockAdminService;
  private static MockCursorService mockCursorService;
  private static MockPublisherService mockPublisherService;
  private static MockSubscriberService mockSubscriberService;
  private static MockPartitionAssignmentService mockPartitionAssignmentService;
  private static MockTopicStatsService mockTopicStatsService;
  private static MockServiceHelper serviceHelper;
  private TopicStatsServiceClient client;
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
  @SuppressWarnings("all")
  public void computeMessageStatsTest() {
    long messageCount = 1229303081L;
    long messageBytes = 1229929933L;
    ComputeMessageStatsResponse expectedResponse =
        ComputeMessageStatsResponse.newBuilder()
            .setMessageCount(messageCount)
            .setMessageBytes(messageBytes)
            .build();
    mockTopicStatsService.addResponse(expectedResponse);

    TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
    long partition = 1799810326L;
    ComputeMessageStatsRequest request =
        ComputeMessageStatsRequest.newBuilder()
            .setTopic(topic.toString())
            .setPartition(partition)
            .build();

    ComputeMessageStatsResponse actualResponse = client.computeMessageStats(request);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTopicStatsService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    ComputeMessageStatsRequest actualRequest = (ComputeMessageStatsRequest) actualRequests.get(0);

    Assert.assertEquals(topic, TopicName.parse(actualRequest.getTopic()));
    Assert.assertEquals(partition, actualRequest.getPartition());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  @SuppressWarnings("all")
  public void computeMessageStatsExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(Status.INVALID_ARGUMENT);
    mockTopicStatsService.addException(exception);

    try {
      TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
      long partition = 1799810326L;
      ComputeMessageStatsRequest request =
          ComputeMessageStatsRequest.newBuilder()
              .setTopic(topic.toString())
              .setPartition(partition)
              .build();

      client.computeMessageStats(request);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception
    }
  }
}
