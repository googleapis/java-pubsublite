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
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.stub.CursorServiceStub;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class CursorClientImplTest {
  private static final CloudRegion REGION = CloudRegion.of("us-east1");

  private static SubscriptionPath path() {
    return SubscriptionPath.newBuilder()
        .setName(SubscriptionName.of("a"))
        .setProject(ProjectNumber.of(4))
        .setLocation(CloudZone.of(REGION, 'a'))
        .build();
  }

  private static Partition partition() {
    return Partition.of(83);
  }

  private static ListPartitionCursorsRequest listRequest() {
    return ListPartitionCursorsRequest.newBuilder().setParent(path().toString()).build();
  }

  private static PartitionCursor partitionCursorOf(Partition partition, Offset offset) {
    return PartitionCursor.newBuilder()
        .setPartition(partition.value())
        .setCursor(Cursor.newBuilder().setOffset(offset.value()))
        .build();
  }

  private static ListPartitionCursorsResponse listResponse() throws CheckedApiException {
    return ListPartitionCursorsResponse.newBuilder()
        .addPartitionCursors(partitionCursorOf(Partition.of(1), Offset.of(111)))
        .addPartitionCursors(partitionCursorOf(Partition.of(2), Offset.of(222)))
        .build();
  }

  private static Map<Partition, Offset> listMap() throws CheckedApiException {
    return ImmutableMap.of(Partition.of(1), Offset.of(111), Partition.of(2), Offset.of(222));
  }

  private static Offset commitOffset() {
    return Offset.of(222);
  }

  private static CommitCursorRequest commitRequest() {
    return CommitCursorRequest.newBuilder()
        .setSubscription(path().toString())
        .setPartition(partition().value())
        .setCursor(Cursor.newBuilder().setOffset(commitOffset().value()))
        .build();
  }

  @Mock CursorServiceStub stub;
  @Mock UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCallable;
  @Mock UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse> listCallable;

  private CursorClientImpl client;

  @Before
  public void setUp() throws IOException {
    initMocks(this);
    when(stub.commitCursorCallable()).thenReturn(commitCallable);
    when(stub.listPartitionCursorsCallable()).thenReturn(listCallable);
    client = new CursorClientImpl(REGION, CursorServiceClient.create(stub));
  }

  @After
  public void tearDown() {
    client.shutdownNow();
    verify(stub).shutdownNow();
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(REGION);
  }

  @Test
  public void listPartitionCursors_Ok() throws Exception {
    when(listCallable.futureCall(listRequest()))
        .thenReturn(ApiFutures.immediateFuture(listResponse()));
    assertThat(client.listPartitionCursors(path()).get()).isEqualTo(listMap());
  }

  @Test
  public void listPartitionCursors_Error() {
    when(listCallable.futureCall(listRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(Code.FAILED_PRECONDITION).underlying));
    assertFutureThrowsCode(client.listPartitionCursors(path()), Code.FAILED_PRECONDITION);
  }

  @Test
  public void commitCursor_Ok() throws Exception {
    when(commitCallable.futureCall(commitRequest()))
        .thenReturn(ApiFutures.immediateFuture(CommitCursorResponse.getDefaultInstance()));
    Void unusedResponse = client.commitCursor(path(), partition(), commitOffset()).get();
  }

  @Test
  public void commitCursor_Error() {
    when(commitCallable.futureCall(commitRequest()))
        .thenReturn(
            ApiFutures.immediateFailedFuture(
                new CheckedApiException(Code.FAILED_PRECONDITION).underlying));
    assertFutureThrowsCode(
        client.commitCursor(path(), partition(), commitOffset()), Code.FAILED_PRECONDITION);
  }
}
