/*
 * Copyright 2026 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

/** Adapts a Kafka producer to the internal Publisher interface for a specific partition. */
public class KafkaPartitionPublisher extends ProxyService implements Publisher<MessageMetadata> {

  private final KafkaProducer<byte[], byte[]> producer;
  private final String topicName;
  private final Partition partition;
  private final PublisherSettings settings;
  private final ConcurrentLinkedQueue<SettableApiFuture<MessageMetadata>> pendingFutures;

  public KafkaPartitionPublisher(
      KafkaProducer<byte[], byte[]> producer,
      String topicName,
      Partition partition,
      PublisherSettings settings) {
    this.producer = producer;
    this.topicName = topicName;
    this.partition = partition;
    this.settings = settings;
    this.pendingFutures = new ConcurrentLinkedQueue<>();
  }

  @Override
  public ApiFuture<MessageMetadata> publish(PubSubMessage message) {
    if (state() == State.FAILED) {
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException("Publisher has failed", Code.FAILED_PRECONDITION).underlying);
    }

    try {
      // Convert to Kafka ProducerRecord
      ProducerRecord<byte[], byte[]> record = convertToKafkaRecord(message);

      // Create future for response
      SettableApiFuture<MessageMetadata> future = SettableApiFuture.create();
      pendingFutures.add(future);

      // Send to Kafka
      producer.send(
          record,
          (metadata, exception) -> {
            pendingFutures.remove(future);

            if (exception != null) {
              CheckedApiException apiException = new CheckedApiException(exception, Code.INTERNAL);
              future.setException(apiException.underlying);

              // If this is a permanent error, fail the publisher
              if (isPermanentError(exception)) {
                onPermanentError(apiException);
              }
            } else {
              // Convert Kafka metadata to MessageMetadata
              MessageMetadata messageMetadata =
                  MessageMetadata.of(
                      Partition.of(metadata.partition()), Offset.of(metadata.offset()));
              future.set(messageMetadata);
            }
          });

      return future;

    } catch (Exception e) {
      CheckedApiException apiException = new CheckedApiException(e, Code.INTERNAL);
      onPermanentError(apiException);
      return ApiFutures.immediateFailedFuture(apiException.underlying);
    }
  }

  @Override
  public void flush() {
    producer.flush();
  }

  @Override
  public void cancelOutstandingPublishes() {
    CheckedApiException exception =
        new CheckedApiException("Publisher is shutting down", Code.CANCELLED);

    pendingFutures.forEach(future -> future.setException(exception.underlying));
    pendingFutures.clear();
  }

  private ProducerRecord<byte[], byte[]> convertToKafkaRecord(PubSubMessage message) {
    // Extract key - use ordering key if available
    byte[] key = message.getKey().isEmpty() ? null : message.getKey().toByteArray();

    // Create record with explicit partition
    ProducerRecord<byte[], byte[]> record =
        new ProducerRecord<byte[], byte[]>(
            topicName,
            Integer.valueOf((int) partition.value()),
            key,
            message.getData().toByteArray());

    // Convert attributes to headers
    List<Header> headers = new ArrayList<>();
    message.getAttributesMap().forEach((k, v) -> headers.add(new RecordHeader(k, v.toByteArray())));

    // Add event time as header if present
    if (message.hasEventTime()) {
      headers.add(
          new RecordHeader(
              "pubsublite.event_time",
              String.valueOf(message.getEventTime().getSeconds()).getBytes()));
    }

    headers.forEach(record.headers()::add);

    return record;
  }

  private boolean isPermanentError(Exception e) {
    // Determine if error is permanent and should fail the publisher
    String message = e.getMessage();
    return message != null
        && (message.contains("InvalidTopicException")
            || message.contains("AuthorizationException")
            || message.contains("SecurityDisabledException"));
  }
}
