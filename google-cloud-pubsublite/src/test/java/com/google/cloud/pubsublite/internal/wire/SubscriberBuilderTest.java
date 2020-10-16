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
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.common.collect.ImmutableList;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class SubscriberBuilderTest {
  @Mock public Consumer<ImmutableList<SequencedMessage>> mockConsumer;

  @Test
  public void testBuilder() {
    initMocks(this);
    Subscriber unusedSubscriber =
        SubscriberBuilder.newBuilder()
            .setSubscriptionPath(
                SubscriptionPath.newBuilder()
                    .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                    .setProject(ProjectNumber.of(3))
                    .setName(SubscriptionName.of("abc"))
                    .build())
            .setMessageConsumer(mockConsumer)
            .setPartition(Partition.of(3))
            .setServiceClient(mock(SubscriberServiceClient.class))
            .build();
  }
}
