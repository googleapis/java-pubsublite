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
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AssignerSettingsTest {
  @Test
  public void testBuilder() {
    AssignerSettings unusedAssigner = AssignerSettings.newBuilder()
        .setSubscriptionPath(
            SubscriptionPath.newBuilder()
                .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                .setProject(ProjectNumber.of(3))
                .setName(SubscriptionName.of("abc"))
                .build())
        .setReceiver(mock(PartitionAssignmentReceiver.class))
        .setServiceClient(mock(PartitionAssignmentServiceClient.class))
        .build();
  }
}
