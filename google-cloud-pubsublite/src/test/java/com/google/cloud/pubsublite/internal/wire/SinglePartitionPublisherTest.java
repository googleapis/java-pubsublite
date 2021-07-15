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
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class SinglePartitionPublisherTest {
  abstract static class FakeOffsetPublisher extends FakeApiService implements Publisher<Offset> {}

  @Spy private FakeOffsetPublisher underlying;

  private Publisher<MessageMetadata> pub;

  @Before
  public void setUp() {
    initMocks(this);

    TopicPath topic =
        TopicPath.newBuilder()
            .setName(TopicName.of("abc"))
            .setProject(ProjectNumber.of(123))
            .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
            .build();
    Partition partition = Partition.of(3);

    PublisherBuilder.Builder mockBuilder = mock(PublisherBuilder.Builder.class, RETURNS_SELF);
    when(mockBuilder.build()).thenReturn(underlying);

    when(mockBuilder.setTopic(topic)).thenReturn(mockBuilder);
    this.pub =
        SinglePartitionPublisherBuilder.newBuilder()
            .setTopic(topic)
            .setPartition(partition)
            .setUnderlyingBuilder(mockBuilder)
            .setBatchingSettings(BatchingSettings.newBuilder().setIsEnabled(false).build())
            .setServiceClient(mock(PublisherServiceClient.class))
            .build();
    this.pub.startAsync().awaitRunning();
  }

  @Test
  public void publishResultTransformed() throws Exception {
    SettableApiFuture<Offset> offsetFuture = SettableApiFuture.create();
    Message message = Message.builder().setData(ByteString.copyFromUtf8("xyz")).build();
    when(underlying.publish(message)).thenReturn(offsetFuture);
    ApiFuture<MessageMetadata> metadataFuture = pub.publish(message);
    assertThat(metadataFuture.isDone()).isFalse();
    offsetFuture.set(Offset.of(7));
    assertThat(metadataFuture.get()).isEqualTo(MessageMetadata.of(Partition.of(3), Offset.of(7)));
    pub.stopAsync().awaitTerminated();
  }

  @Test
  public void flushFlushesUnderlying() throws Exception {
    pub.flush();
    verify(underlying, times(1)).flush();
    pub.stopAsync().awaitTerminated();
  }

  @Test
  public void cancelOutstandingCancelsUnderlyingPublishes() throws Exception {
    pub.cancelOutstandingPublishes();
    verify(underlying, times(1)).cancelOutstandingPublishes();
    pub.stopAsync().awaitTerminated();
  }
}
