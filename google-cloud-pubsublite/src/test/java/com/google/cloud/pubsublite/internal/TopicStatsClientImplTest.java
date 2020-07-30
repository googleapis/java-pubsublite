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
import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class TopicStatsClientImplTest {

  @Rule public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
  private static final CloudRegion REGION = CloudRegion.of("us-east1");

  private static TopicPath path() {
    try {
      return TopicPaths.newBuilder()
          .setTopicName(TopicName.of("a"))
          .setProjectNumber(ProjectNumber.of(4))
          .setZone(CloudZone.of(REGION, 'a'))
          .build();

    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private static Partition partition() {
    try {
      return Partition.of(0);
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
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

  private final TopicStatsServiceGrpc.TopicStatsServiceImplBase serviceImpl =
      mock(
          TopicStatsServiceGrpc.TopicStatsServiceImplBase.class,
          delegatesTo(new TopicStatsServiceGrpc.TopicStatsServiceImplBase() {}));

  private TopicStatsClientImpl client;

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
    TopicStatsServiceGrpc.TopicStatsServiceBlockingStub stub =
        TopicStatsServiceGrpc.newBlockingStub(channel);
    client =
        new TopicStatsClientImpl(REGION, stub, TopicStatsClientSettings.DEFAULT_RETRY_SETTINGS);
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
    return invocation -> {
      StreamObserver<T> responseObserver = invocation.getArgument(1);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return null;
    };
  }

  private static Answer<Void> answerWith(Status status) {
    return invocation -> {
      StreamObserver<?> responseObserver = invocation.getArgument(1);
      responseObserver.onError(status.asRuntimeException());
      return null;
    };
  }

  private static Answer<Void> inOrder(Answer<Void>... answers) {
    AtomicInteger count = new AtomicInteger(0);
    return invocation -> {
      int index = count.getAndIncrement();

      return answers[index].answer(invocation);
    };
  }

  @Test
  public void computeMessageStats_Ok() throws Exception {
    doAnswer(answerWith(response())).when(serviceImpl).computeMessageStats(eq(request()), any());
    assertThat(client.computeMessageStats(path(), partition(), start(), end()).get())
        .isEqualTo(response());
  }

  @Test
  public void computeMessageStats_NonRetryableError() {
    assertThat(ErrorCodes.IsRetryable(Code.FAILED_PRECONDITION)).isFalse();

    doAnswer(answerWith(Status.FAILED_PRECONDITION))
        .when(serviceImpl)
        .computeMessageStats(eq(request()), any());

    ApiFuture<ComputeMessageStatsResponse> future =
        client.computeMessageStats(path(), partition(), start(), end());
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Code.FAILED_PRECONDITION);
  }

  @Test
  public void computeMessageStats_RetryableError() throws Exception {
    for (Code code : ErrorCodes.RETRYABLE_CODES) {
      assertThat(ErrorCodes.IsRetryable(code)).isTrue();
      doAnswer(inOrder(answerWith(Status.fromCode(code)), answerWith(response())))
          .when(serviceImpl)
          .computeMessageStats(eq(request()), any());

      assertThat(client.computeMessageStats(path(), partition(), start(), end()).get())
          .isEqualTo(response());
    }
  }

  @Test
  public void computeMessageStats_MultipleRetryableErrors() throws Exception {
    assertThat(ErrorCodes.IsRetryable(Code.DEADLINE_EXCEEDED)).isTrue();
    doAnswer(
            inOrder(
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(Status.DEADLINE_EXCEEDED),
                answerWith(response())))
        .when(serviceImpl)
        .computeMessageStats(eq(request()), any());

    assertThat(client.computeMessageStats(path(), partition(), start(), end()).get())
        .isEqualTo(response());
  }
}
