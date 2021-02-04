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

package com.google.cloud.pubsublite.spark;

import java.util.concurrent.TimeUnit;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.CursorClientSettings;
import com.google.cloud.pubsublite.internal.TopicStatsClient;
import com.google.cloud.pubsublite.internal.TopicStatsClientSettings;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

public class CommitOffsetToZero {

  public static void main(String[] args) throws Exception {

    CursorClientSettings settings = CursorClientSettings.newBuilder()
            .setRegion(CloudRegion.of("us-central1"))
            .build();
    CursorClient cursorClient = CursorClient.create(settings);

    for (int i = 0; i < 1; i++) {
      cursorClient.commitCursor(SubscriptionPath.newBuilder()
              .setProject(ProjectNumber.of(129988248131L))
              .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
              .setName(SubscriptionName.of("menzella5")).build(),
              Partition.of(i), Offset.of(0L)).get();
    }
  }
}
