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

package com.google.cloud.pubsublite.kafka;

import static com.google.cloud.pubsublite.kafka.KafkaExceptionUtils.toKafka;

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.kafka.common.PartitionInfo;

/** Shared behavior for producer and consumer. */
final class SharedBehavior {
  private SharedBehavior() {}

  static PartitionInfo toPartitionInfo(TopicPath topic, Partition partition) {
    return new PartitionInfo(
        topic.toString(),
        (int) partition.value(),
        PubsubLiteNode.NODE,
        PubsubLiteNode.NODES,
        PubsubLiteNode.NODES);
  }

  static List<PartitionInfo> partitionsFor(long partitionCount, TopicPath topic) {
    try {
      ImmutableList.Builder<PartitionInfo> result = ImmutableList.builder();
      for (int i = 0; i < partitionCount; ++i) {
        result.add(toPartitionInfo(topic, Partition.of(i)));
      }
      return result.build();
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }
}
