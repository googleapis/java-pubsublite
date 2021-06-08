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
package com.google.cloud.pubsublite.internal.wire;

import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.protobuf.ByteString;
import java.time.Duration;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;

@RunWith(JUnit4.class)
public class PartitionCountWatchingPublisherTest {
  abstract static class FakePublisher extends FakeApiService
      implements Publisher<MessageMetadata> {}

  abstract static class FakeConfigWatcher extends FakeApiService implements PartitionCountWatcher {}

  private static final Duration PERIOD = Duration.ofMinutes(1);
  private static final CloudRegion REGION = example(CloudRegion.class);

  private static TopicPath path() {
    return example(TopicPath.class);
  }

  @Mock PartitionPublisherFactory mockPublisherFactory;
  @Spy private FakePublisher publisher0;
  @Spy private FakePublisher publisher1;
  @Spy private FakePublisher publisher2;

  @Mock RoutingPolicy.Factory mockRoutingPolicyFactory;
  @Mock RoutingPolicy mockRoutingPolicy;

  Consumer<Long> leakedConsumer;
  @Spy FakeConfigWatcher fakeConfigWatcher;

  Publisher<MessageMetadata> publisher;

  @Before
  public void setUp() {
    initMocks(this);
    doReturn(ApiFutures.immediateFuture(MessageMetadata.of(Partition.of(0), Offset.of(0))))
        .when(publisher0)
        .publish(any());
    doReturn(ApiFutures.immediateFuture(MessageMetadata.of(Partition.of(1), Offset.of(0))))
        .when(publisher1)
        .publish(any());
    doReturn(ApiFutures.immediateFuture(MessageMetadata.of(Partition.of(2), Offset.of(0))))
        .when(publisher2)
        .publish(any());
    when(mockPublisherFactory.newPublisher(Partition.of(0))).thenReturn(publisher0);
    when(mockPublisherFactory.newPublisher(Partition.of(1))).thenReturn(publisher1);
    when(mockPublisherFactory.newPublisher(Partition.of(2))).thenReturn(publisher2);
    when(mockRoutingPolicyFactory.newPolicy(anyLong())).thenReturn(mockRoutingPolicy);
    doAnswer(
            (invocation) -> {
              leakedConsumer.accept(2L);
              invocation.callRealMethod();
              return null;
            })
        .when(fakeConfigWatcher)
        .startAsync();
    publisher =
        new PartitionCountWatchingPublisher(
            mockPublisherFactory,
            mockRoutingPolicyFactory,
            c -> {
              leakedConsumer = c;
              return fakeConfigWatcher;
            });
    publisher.startAsync();
    publisher.awaitRunning();

    verify(mockRoutingPolicyFactory).newPolicy(2L);
  }

  @Test
  public void testPublishWithKey() throws Exception {
    Message message0 = Message.builder().setKey(ByteString.copyFromUtf8("0")).build();
    Message message1 = Message.builder().setKey(ByteString.copyFromUtf8("1")).build();
    when(mockRoutingPolicy.route(message0.key())).thenReturn(Partition.of(0));
    when(mockRoutingPolicy.route(message1.key())).thenReturn(Partition.of(1));

    Future<?> unusedFuture0 = publisher.publish(message0);
    Future<?> unusedFuture1 = publisher.publish(message1);

    verify(publisher0).publish(message0);
    verify(publisher1).publish(message1);
  }

  @Test
  public void testPublishWithoutKey() throws Exception {
    Message messageA = Message.builder().setData(ByteString.copyFromUtf8("a")).build();
    Message messageB = Message.builder().setData(ByteString.copyFromUtf8("b")).build();

    when(mockRoutingPolicy.routeWithoutKey())
        .thenReturn(Partition.of(0))
        .thenReturn(Partition.of(1));

    Future<?> unusedFutureA = publisher.publish(messageA);
    Future<?> unusedFutureB = publisher.publish(messageB);

    verify(publisher0).publish(messageA);
    verify(publisher1).publish(messageB);
  }

  @Test
  public void testPublishWithBadRouting() throws Exception {
    Message message = Message.builder().build();

    when(mockRoutingPolicy.routeWithoutKey()).thenReturn(Partition.of(4));
    Future<?> unusedFuture = publisher.publish(message);

    ApiExceptionMatcher.assertThrowableMatches(
        publisher.failureCause(), StatusCode.Code.FAILED_PRECONDITION);
    assertThrows(IllegalStateException.class, publisher::flush);
    assertThrows(IllegalStateException.class, () -> publisher.publish(Message.builder().build()));
  }

  @Test
  public void testChildPublisherFailure() throws Exception {
    publisher0.fail(new CheckedApiException(StatusCode.Code.FAILED_PRECONDITION));

    ApiExceptionMatcher.assertThrowableMatches(
        publisher.failureCause(), StatusCode.Code.FAILED_PRECONDITION);
    assertThrows(IllegalStateException.class, publisher::flush);
    assertThrows(IllegalStateException.class, () -> publisher.publish(Message.builder().build()));
  }

  @Test
  public void testFlush() throws Exception {
    publisher.flush();
    verify(publisher0).flush();
    verify(publisher1).flush();
  }

  @Test
  public void testCancelOutstandingPublishes() throws Exception {
    publisher.cancelOutstandingPublishes();
    verify(publisher0).cancelOutstandingPublishes();
    verify(publisher1).cancelOutstandingPublishes();
  }

  @Test
  public void testIncreaseSucceeds() throws Exception {
    leakedConsumer.accept(3L);
    verify(mockRoutingPolicyFactory).newPolicy(3L);

    Message message0 = Message.builder().setKey(ByteString.copyFromUtf8("0")).build();
    Message message1 = Message.builder().setKey(ByteString.copyFromUtf8("1")).build();
    Message message2 = Message.builder().setKey(ByteString.copyFromUtf8("2")).build();
    when(mockRoutingPolicy.route(message0.key())).thenReturn(Partition.of(0));
    when(mockRoutingPolicy.route(message1.key())).thenReturn(Partition.of(1));
    when(mockRoutingPolicy.route(message2.key())).thenReturn(Partition.of(2));

    Future<?> unusedFuture0 = publisher.publish(message0);
    Future<?> unusedFuture1 = publisher.publish(message1);
    Future<?> unusedFuture2 = publisher.publish(message2);

    verify(publisher0).publish(message0);
    verify(publisher1).publish(message1);
    verify(publisher2).publish(message2);
  }

  @Test
  public void testDecreaseIgnored() throws Exception {
    leakedConsumer.accept(1L);

    Message message0 = Message.builder().setKey(ByteString.copyFromUtf8("0")).build();
    Message message1 = Message.builder().setKey(ByteString.copyFromUtf8("1")).build();
    when(mockRoutingPolicy.route(message0.key())).thenReturn(Partition.of(0));
    when(mockRoutingPolicy.route(message1.key())).thenReturn(Partition.of(1));

    Future<?> unusedFuture0 = publisher.publish(message0);
    Future<?> unusedFuture1 = publisher.publish(message1);

    verify(publisher0).publish(message0);
    verify(publisher1).publish(message1);
  }

  @Test
  public void testNoopConfigUpdate() throws Exception {
    leakedConsumer.accept(2L);
    verifyNoMoreInteractions(mockRoutingPolicyFactory);

    Message message0 = Message.builder().setKey(ByteString.copyFromUtf8("0")).build();
    Message message1 = Message.builder().setKey(ByteString.copyFromUtf8("1")).build();
    when(mockRoutingPolicy.route(message0.key())).thenReturn(Partition.of(0));
    when(mockRoutingPolicy.route(message1.key())).thenReturn(Partition.of(1));

    Future<?> unusedFuture0 = publisher.publish(message0);
    Future<?> unusedFuture1 = publisher.publish(message1);

    verify(publisher0).publish(message0);
    verify(publisher1).publish(message1);
  }

  @Test
  public void testStopWorksProperly() {
    publisher.stopAsync();
    publisher.awaitTerminated();

    leakedConsumer.accept(3L);

    assertThrows(IllegalStateException.class, publisher::flush);
    assertThrows(IllegalStateException.class, () -> publisher.publish(Message.builder().build()));
  }

  @Test
  public void testStopAfterIncrease() throws Exception {
    leakedConsumer.accept(3L);
    verify(mockRoutingPolicyFactory).newPolicy(3L);

    publisher.stopAsync();
    publisher.awaitTerminated();

    leakedConsumer.accept(4L);

    assertThrows(IllegalStateException.class, publisher::flush);
    assertThrows(IllegalStateException.class, () -> publisher.publish(Message.builder().build()));
  }
}
