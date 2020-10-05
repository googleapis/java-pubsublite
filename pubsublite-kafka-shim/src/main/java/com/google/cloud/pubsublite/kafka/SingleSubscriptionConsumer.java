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

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.proto.SeekRequest;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;

/** A stripped down KafkaConsumer interface that operates on a single subscription. */
interface SingleSubscriptionConsumer {
  void setAssignment(Set<Partition> partitions);

  Set<Partition> assignment();

  ConsumerRecords<byte[], byte[]> poll(Duration duration);

  ApiFuture<Map<Partition, Offset>> commitAll();

  ApiFuture<Void> commit(Map<Partition, Offset> commitOffsets);

  void doSeek(Partition partition, SeekRequest request) throws KafkaException;

  Optional<Long> position(Partition partition);

  void close(Duration duration);

  /**
   * Cause the outstanding or next call to poll to throw a WakeupException. The consumer is left in
   * an unspecified state.
   */
  void wakeup();
}
