package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.internal.FakeApiService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class RoutingPublisherTest {
  abstract static class FakePublisher extends FakeApiService
      implements Publisher<PublishMetadata> {}

  @Spy private FakePublisher publisher0;
  @Spy private FakePublisher publisher1;
  @Mock private RoutingPolicy routingPolicy;

  private RoutingPublisher routing;

  @Before
  public void setUp() throws StatusException {
    initMocks(this);
    this.routing =
        new RoutingPublisher(
            ImmutableMap.of(Partition.of(0), publisher0, Partition.of(1), publisher1),
            routingPolicy);
    this.routing.startAsync().awaitRunning();
  }

  @Test
  public void flushFlushesAll() throws Exception {
    routing.flush();
    verify(publisher0, times(1)).flush();
    verify(publisher1, times(1)).flush();
    this.routing.stopAsync().awaitTerminated();
  }

  @Test
  public void publishValidRoute() throws Exception {
    Message message = Message.builder().setKey(ByteString.copyFromUtf8("abc")).build();
    when(routingPolicy.route(message.key())).thenReturn(Partition.of(1));
    routing.publish(message);
    verify(publisher1, times(1)).publish(message);
    this.routing.stopAsync().awaitTerminated();
  }

  @Test
  public void publishInvalidRoute() throws Exception {
    Message message = Message.builder().setKey(ByteString.copyFromUtf8("abc")).build();
    when(routingPolicy.route(message.key())).thenReturn(Partition.of(77));
    ApiFuture<PublishMetadata> fut = routing.publish(message);
    StatusExceptionMatcher.assertFutureThrowsCode(fut, Code.FAILED_PRECONDITION);
  }
}
