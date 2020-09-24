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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Constants;
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceBlockingStub;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc.CursorServiceImplBase;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.common.collect.ImmutableMap;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class CursorClientImplTest {

  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
  private static final CloudRegion REGION = CloudRegion.of("us-east1");

  private static SubscriptionPath path() {
    try {
      return SubscriptionPath.newBuilder()
          .setName(SubscriptionName.of("a"))
          .setProject(ProjectNumber.of(4))
          .setLocation(CloudZone.of(REGION, 'a'))
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static Partition partition() {
    try {
      return Partition.of(83);
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
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

  private static ListPartitionCursorsResponse listResponse() throws StatusException {
    return ListPartitionCursorsResponse.newBuilder()
        .addPartitionCursors(partitionCursorOf(Partition.of(1), Offset.of(111)))
        .addPartitionCursors(partitionCursorOf(Partition.of(2), Offset.of(222)))
        .build();
  }

  private static Map<Partition, Offset> listMap() throws StatusException {
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

  private final CursorServiceImplBase serviceImpl =
      mock(CursorServiceImplBase.class, delegatesTo(new CursorServiceImplBase() {}));

  private CursorClientImpl client;

  @Before
  public void setUp() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(
        InProcessServerBuilder.forName(serverName)
            .directExecutor()
            .addService(serviceImpl)
            .build()
            .start());
    ManagedChannel channel =
        grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    CursorServiceBlockingStub stub = CursorServiceGrpc.newBlockingStub(channel);
    client = new CursorClientImpl(REGION, stub, Constants.DEFAULT_RETRY_SETTINGS);
  }

  @After
  public void tearDown() throws Exception {
    client.shutdownNow();
    Preconditions.checkArgument(client.awaitTermination(10, SECONDS));
  }

  @Test
  public void region_isConstructedRegion() {
    assertThat(client.region()).isEqualTo(REGION);
  }

  private static <T> Answer<Void> answerWith(T response) {
    return TestUtil.answerWith(response);
  }

  private static Answer<Void> answerWith(Status status) {
    return TestUtil.answerWith(status);
  }

  private static Answer<Void> inOrder(Answer<Void>... answers) {
    return TestUtil.inOrder(answers);
  }

  @Test
  public void listPartitionCursors_Ok() throws Exception {
    doAnswer(answerWith(listResponse()))
        .when(serviceImpl)
        .listPartitionCursors(eq(listRequest()), any());
    assertThat(client.listPartitionCursors(path()).get()).isEqualTo(listMap());
  }

  @Test
  public void listPartitionCursors_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .listPartitionCursors(eq(listRequest()), any());

    ApiFuture<Map<Partition, Offset>> future = client.listPartitionCursors(path());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void listPartitionCursors_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(listResponse())))
          .when(serviceImpl)
          .listPartitionCursors(eq(listRequest()), any());

      assertThat(client.listPartitionCursors(path()).get()).isEqualTo(listMap());
    }
  }

  @Test
  public void listPartitionCursors_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(listResponse())))
        .when(serviceImpl)
        .listPartitionCursors(eq(listRequest()), any());

    assertThat(client.listPartitionCursors(path()).get()).isEqualTo(listMap());
  }

  @Test
  public void commitCursor_Ok() throws Exception {
    doAnswer(answerWith(CommitCursorResponse.getDefaultInstance()))
        .when(serviceImpl)
        .commitCursor(eq(commitRequest()), any());
    client.commitCursor(path(), partition(), commitOffset()).get();
  }

  @Test
  public void commitCursor_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .commitCursor(eq(commitRequest()), any());

    ApiFuture<Void> future = client.commitCursor(path(), partition(), commitOffset());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void commitCursor_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      doAnswer(
              inOrder(
                  answerWith(Status.fromCode(code)),
                  answerWith(CommitCursorResponse.getDefaultInstance())))
          .when(serviceImpl)
          .commitCursor(eq(commitRequest()), any());

      client.commitCursor(path(), partition(), commitOffset()).get();
    }
  }

  @Test
  public void commitCursor_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(CommitCursorResponse.getDefaultInstance())))
        .when(serviceImpl)
        .commitCursor(eq(commitRequest()), any());

    client.commitCursor(path(), partition(), commitOffset()).get();
  }
}
