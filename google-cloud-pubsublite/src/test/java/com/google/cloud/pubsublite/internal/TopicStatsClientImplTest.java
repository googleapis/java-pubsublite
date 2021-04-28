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
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TimeTarget;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;
import com.google.cloud.pubsublite.v1.stub.TopicStatsServiceStub;
import com.google.protobuf.Timestamp;
import java.io.IOException;
import java.util.Optional;
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

  private static Timestamp timestamp() {
    return Timestamp.newBuilder().setSeconds(1).setNanos(2).build();
  }

  private static Cursor cursor() {
    return Cursor.newBuilder().setOffset(45).build();
  }

  private static ComputeMessageStatsRequest messageStatsRequest() {
    return ComputeMessageStatsRequest.newBuilder()
        .setTopic(path().toString())
        .setPartition(partition().value())
        .setStartCursor(Cursor.newBuilder().setOffset(start().value()).build())
        .setEndCursor(Cursor.newBuilder().setOffset(end().value()).build())
        .build();
  }

  private static ComputeMessageStatsResponse messageStatsResponse() {
    return ComputeMessageStatsResponse.newBuilder().setMessageBytes(1).setMessageCount(2).build();
  }

  private static ComputeHeadCursorRequest headCursorRequest() {
    return ComputeHeadCursorRequest.newBuilder()
        .setTopic(path().toString())
        .setPartition(partition().value())
        .build();
  }

  private static ComputeHeadCursorResponse headCursorResponse() {
    return ComputeHeadCursorResponse.newBuilder().setHeadCursor(cursor()).build();
  }

  private static ComputeTimeCursorRequest publishTimeCursorRequest() {
    return ComputeTimeCursorRequest.newBuilder()
        .setTopic(path().toString())
        .setPartition(partition().value())
        .setTarget(TimeTarget.newBuilder().setPublishTime(timestamp()))
        .build();
  }

  private static ComputeTimeCursorRequest eventTimeCursorRequest() {
    return ComputeTimeCursorRequest.newBuilder()
        .setTopic(path().toString())
        .setPartition(partition().value())
        .setTarget(TimeTarget.newBuilder().setEventTime(timestamp()))
        .build();
  }

  private static ComputeTimeCursorResponse unsetTimeCursorResponse() {
    return ComputeTimeCursorResponse.getDefaultInstance();
  }

  private static ComputeTimeCursorResponse timeCursorResponse() {
    return ComputeTimeCursorResponse.newBuilder().setCursor(cursor()).build();
  }

  @Mock TopicStatsServiceStub stub;
  @Mock UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse> computeStatsCallable;

  @Mock
  UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse> computeHeadCursorCallable;

  @Mock
  UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse> computeTimeCursorCallable;

  private TopicStatsClientImpl client;

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    when(stub.computeMessageStatsCallable()).thenReturn(computeStatsCallable);
    when(stub.computeHeadCursorCallable()).thenReturn(computeHeadCursorCallable);
    when(stub.computeTimeCursorCallable()).thenReturn(computeTimeCursorCallable);
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
    when(computeStatsCallable.futureCall(messageStatsRequest()))
        .thenReturn(ApiFutures.immediateFuture(messageStatsResponse()));
    assertThat(client.computeMessageStats(path(), partition(), start(), end()).get())
        .isEqualTo(messageStatsResponse());
  }

  @Test
  public void computeMessageStats_Error() {
    when(computeStatsCallable.futureCall(messageStatsRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(Code.FAILED_PRECONDITION).underlying));
    assertFutureThrowsCode(
        client.computeMessageStats(path(), partition(), start(), end()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void computeHeadCursor_Ok() throws Exception {
    when(computeHeadCursorCallable.futureCall(headCursorRequest()))
        .thenReturn(ApiFutures.immediateFuture(headCursorResponse()));
    assertThat(client.computeHeadCursor(path(), partition()).get()).isEqualTo(cursor());
  }

  @Test
  public void computeHeadCursor_Error() {
    when(computeHeadCursorCallable.futureCall(headCursorRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(Code.FAILED_PRECONDITION).underlying));
    assertFutureThrowsCode(client.computeHeadCursor(path(), partition()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void computeCursorForPublishTime_OkPresent() throws Exception {
    when(computeTimeCursorCallable.futureCall(publishTimeCursorRequest()))
        .thenReturn(ApiFutures.immediateFuture(timeCursorResponse()));
    assertThat(client.computeCursorForPublishTime(path(), partition(), timestamp()).get())
        .isEqualTo(Optional.of(cursor()));
  }

  @Test
  public void computeCursorForPublishTime_OkUnset() throws Exception {
    when(computeTimeCursorCallable.futureCall(publishTimeCursorRequest()))
        .thenReturn(ApiFutures.immediateFuture(unsetTimeCursorResponse()));
    assertThat(client.computeCursorForPublishTime(path(), partition(), timestamp()).get())
        .isEqualTo(Optional.empty());
  }

  @Test
  public void computeCursorForPublishTime_Error() throws Exception {
    when(computeTimeCursorCallable.futureCall(publishTimeCursorRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(new CheckedApiException(Code.UNAVAILABLE).underlying));
    assertFutureThrowsCode(
        client.computeCursorForPublishTime(path(), partition(), timestamp()), Code.UNAVAILABLE);
  }

  @Test
  public void computeCursorForEventTime_OkPresent() throws Exception {
    when(computeTimeCursorCallable.futureCall(eventTimeCursorRequest()))
        .thenReturn(ApiFutures.immediateFuture(timeCursorResponse()));
    assertThat(client.computeCursorForEventTime(path(), partition(), timestamp()).get())
        .isEqualTo(Optional.of(cursor()));
  }

  @Test
  public void computeCursorForEventTime_OkUnset() throws Exception {
    when(computeTimeCursorCallable.futureCall(eventTimeCursorRequest()))
        .thenReturn(ApiFutures.immediateFuture(unsetTimeCursorResponse()));
    assertThat(client.computeCursorForEventTime(path(), partition(), timestamp()).get())
        .isEqualTo(Optional.empty());
  }

  @Test
  public void computeCursorForEventTime_Error() throws Exception {
    when(computeTimeCursorCallable.futureCall(eventTimeCursorRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(new CheckedApiException(Code.ABORTED).underlying));
    assertFutureThrowsCode(
        client.computeCursorForEventTime(path(), partition(), timestamp()), Code.ABORTED);
  }
}
