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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.ApiExceptionMatcher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class RoutingPublisherTest {
  abstract static class FakePublisher extends FakeApiService
      implements Publisher<MessageMetadata> {}

  @Spy private FakePublisher publisher0;
  @Spy private FakePublisher publisher1;
  @Mock private RoutingPolicy routingPolicy;

  private RoutingPublisher routing;

  @Before
  public void setUp() throws CheckedApiException {
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
  public void cancelOutstandingCancelsAll() throws Exception {
    routing.cancelOutstandingPublishes();
    verify(publisher0, times(1)).cancelOutstandingPublishes();
    verify(publisher1, times(1)).cancelOutstandingPublishes();
    this.routing.stopAsync().awaitTerminated();
  }

  @Ignore
  @Test
  public void publishValidRoute() throws Exception {
    PubSubMessage message =
        PubSubMessage.newBuilder().setKey(ByteString.copyFromUtf8("abc")).build();
    when(routingPolicy.route(message)).thenReturn(Partition.of(1));
    MessageMetadata meta = MessageMetadata.of(Partition.of(1), Offset.of(3));
    when(publisher1.publish(message)).thenReturn(ApiFutures.immediateFuture(meta));
    ApiFuture<MessageMetadata> fut = routing.publish(message);
    verify(publisher1, times(1)).publish(message);
    assertThat(fut.get()).isEqualTo(meta);
    this.routing.stopAsync().awaitTerminated();
  }

  @Test
  public void publishInvalidRoute() throws Exception {
    PubSubMessage message =
        PubSubMessage.newBuilder().setKey(ByteString.copyFromUtf8("abc")).build();
    when(routingPolicy.route(message)).thenReturn(Partition.of(77));
    ApiFuture<MessageMetadata> fut = routing.publish(message);
    ApiExceptionMatcher.assertFutureThrowsCode(fut, Code.FAILED_PRECONDITION);
  }
}
