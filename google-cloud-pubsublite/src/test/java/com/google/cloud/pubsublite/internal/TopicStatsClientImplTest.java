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

package com.google.cloud.pubsublite.internal;

import static com.google.cloud.pubsublite.internal.ApiExceptionMatcher.assertFutureThrowsCode;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.cloud.pubsublite.v1.stub.TopicStatsServiceStub;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class TopicStatsClientImplTest {
  private static final CloudRegion REGION = CloudRegion.of("us-east1");

  private static TopicPath path() {
    return TopicPath.newBuilder()
        .setName(TopicName.of("a"))
        .setProject(ProjectNumber.of(4))
        .setLocation(CloudZone.of(REGION, 'a'))
        .build();
  }

  private static Partition partition() {
    return Partition.of(0);
  }

  private static Offset start() {
    return Offset.of(1);
  }

  private static Offset end() {
    return Offset.of(2);
  }

  private static ComputeMessageStatsRequest request() {
    return ComputeMessageStatsRequest.newBuilder()
        .setTopic(path().toString())
        .setPartition(partition().value())
        .setStartCursor(Cursor.newBuilder().setOffset(start().value()).build())
        .setEndCursor(Cursor.newBuilder().setOffset(end().value()).build())
        .build();
  }

  private static ComputeMessageStatsResponse response() {
    return ComputeMessageStatsResponse.newBuilder().setMessageBytes(1).setMessageCount(2).build();
  }

  @Mock TopicStatsServiceStub stub;
  @Mock UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse> computeCallable;

  private TopicStatsClientImpl client;

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    when(stub.computeMessageStatsCallable()).thenReturn(computeCallable);
    client = new TopicStatsClientImpl(REGION, TopicStatsServiceClient.create(stub));
  }

  @After
  public void tearDown() throws Exception {
    client.shutdownNow();
    verify(stub).shutdownNow();
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(REGION);
  }

  @Test
  public void computeMessageStats_Ok() throws Exception {
    when(computeCallable.futureCall(request())).thenReturn(ApiFutures.immediateFuture(response()));
    assertThat(client.computeMessageStats(path(), partition(), start(), end()).get())
        .isEqualTo(response());
  }

  @Test
  public void computeMessageStats_Error() {
    when(computeCallable.futureCall(request()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(Code.FAILED_PRECONDITION).underlying));
    assertFutureThrowsCode(
        client.computeMessageStats(path(), partition(), start(), end()), Code.FAILED_PRECONDITION);
  }
}
