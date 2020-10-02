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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.cloudpubsub.KeyExtractor;
import com.google.cloud.pubsublite.cloudpubsub.MessageTransforms;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.StatusExceptionMatcher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class WrappingPublisherTest {
  abstract static class FakePublisher extends FakeApiService
      implements Publisher<PublishMetadata> {}

  @Spy private FakePublisher underlying;

  private WrappingPublisher publisher;

  @Before
  public void setUp() throws StatusException {
    initMocks(this);
    publisher =
        new WrappingPublisher(
            underlying, MessageTransforms.fromCpsPublishTransformer(KeyExtractor.DEFAULT));
    publisher.startAsync().awaitRunning();
    verify(underlying).startAsync();
  }

  @After
  public void tearDown() {
    if (publisher.isRunning()) {
      publisher.stopAsync().awaitTerminated();
      verify(underlying).stopAsync();
    }
  }

  @Test
  public void validPublish() throws Exception {
    PubsubMessage message = PubsubMessage.newBuilder().setOrderingKey("abc").build();
    Message wireMessage = Message.builder().setKey(ByteString.copyFromUtf8("abc")).build();
    SettableApiFuture<PublishMetadata> metadataFuture = SettableApiFuture.create();
    when(underlying.publish(wireMessage)).thenReturn(metadataFuture);
    ApiFuture<String> published = publisher.publish(message);
    verify(underlying).publish(wireMessage);
    assertThat(published.isDone()).isFalse();
    PublishMetadata metadata = PublishMetadata.of(Partition.of(3), Offset.of(88));
    metadataFuture.set(metadata);
    assertThat(published.isDone()).isTrue();
    assertThat(published.get()).isEqualTo(metadata.encode());
  }

  @Test
  public void badTimestampCannotBeTransformed() {
    PubsubMessage message =
        PubsubMessage.newBuilder()
            .setOrderingKey("abc")
            .putAttributes(
                MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                "Not a valid encoded timestamp")
            .build();

    ApiFuture<String> published = publisher.publish(message);
    verify(underlying, times(0)).publish(any());
    StatusExceptionMatcher.assertFutureThrowsCode(published, Code.INVALID_ARGUMENT);
    assertThat(publisher.isRunning()).isFalse();
  }
}
