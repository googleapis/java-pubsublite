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

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.SequencedPublisher;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.protobuf.ByteString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PublisherBuilderTest {
  @Test
  public void testBuilder() {
    PublisherBuilder.Builder builder =
        PublisherBuilder.builder()
            .setBatching(PublisherSettings.DEFAULT_BATCHING_SETTINGS)
            .setTopic(
                TopicPath.newBuilder()
                    .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                    .setProject(ProjectNumber.of(3))
                    .setName(TopicName.of("abc"))
                    .build())
            .setPartition(Partition.of(85))
            .setStreamFactory(mock(PublishStreamFactory.class));
    Publisher<Offset> unusedPublisher = builder.build();
    SequencedPublisher<Offset> unusedSequencedPublisher = builder.buildSequenced();

    // Optional parameters.
    builder.setClientId(ByteString.copyFromUtf8("publisher"));
    unusedPublisher = builder.build();
    unusedSequencedPublisher = builder.buildSequenced();
  }
}
