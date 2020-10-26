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

package com.google.cloud.pubsublite.cloudpubsub;

import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.v1.PublisherServiceClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class PublisherSettingsTest {
  TopicPath getPath() throws CheckedApiException {
    return TopicPath.newBuilder()
        .setProject(ProjectNumber.of(56))
        .setLocation(CloudZone.parse("us-central1-a"))
        .setName(TopicName.of("xyz"))
        .build();
  }

  abstract static class FakePublisher extends FakeApiService
      implements Publisher<PublishMetadata> {}

  @Spy private FakePublisher underlying;

  @Test
  public void testSettings() throws CheckedApiException {
    initMocks(this);
    SinglePartitionPublisherBuilder.Builder mockBuilder =
        mock(SinglePartitionPublisherBuilder.Builder.class, RETURNS_SELF);
    when(mockBuilder.build()).thenReturn(underlying);
    PublisherSettings.newBuilder()
        .setTopicPath(getPath())
        .setServiceClientSupplier(() -> mock(PublisherServiceClient.class))
        .setUnderlyingBuilder(mockBuilder)
        .setNumPartitions(77)
        .build()
        .instantiate();
  }
}
