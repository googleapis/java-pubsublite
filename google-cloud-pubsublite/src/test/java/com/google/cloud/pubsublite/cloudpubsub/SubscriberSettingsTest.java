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

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SubscriberSettingsTest {
  SubscriptionPath getPath() {
    return SubscriptionPath.newBuilder()
        .setProject(ProjectNumber.of(56))
        .setLocation(CloudZone.parse("us-central1-a"))
        .setName(SubscriptionName.of("xyz"))
        .build();
  }

  @Test
  public void testSettingsWithPartitons() {
    SubscriberSettings.newBuilder()
        .setReceiver(mock(MessageReceiver.class))
        .setSubscriptionPath(getPath())
        .setPerPartitionFlowControlSettings(
            FlowControlSettings.builder().setBytesOutstanding(1).setMessagesOutstanding(1).build())
        .setCursorServiceClientSupplier(() -> mock(CursorServiceClient.class))
        .setSubscriberServiceClient(mock(SubscriberServiceClient.class))
        .setPartitions(ImmutableList.of(Partition.of(3), Partition.of(1)))
        .build()
        .instantiate();
  }

  @Test
  public void testSettingsWithoutPartitons() {
    SubscriberSettings.newBuilder()
        .setReceiver(mock(MessageReceiver.class))
        .setSubscriptionPath(getPath())
        .setPerPartitionFlowControlSettings(
            FlowControlSettings.builder().setBytesOutstanding(1).setMessagesOutstanding(1).build())
        .setAssignmentServiceClient(mock(PartitionAssignmentServiceClient.class))
        .setCursorServiceClientSupplier(() -> mock(CursorServiceClient.class))
        .setSubscriberServiceClient(mock(SubscriberServiceClient.class))
        .build()
        .instantiate();
  }
}
