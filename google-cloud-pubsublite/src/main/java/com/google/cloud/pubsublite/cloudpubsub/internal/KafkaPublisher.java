/*
 * Copyright 2024 Google LLC
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

import com.google.api.core.AbstractApiService;
import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.pubsub.v1.PubsubMessage;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;

/**
 * A simple Kafka publisher that directly uses KafkaProducer without the complex partition watching
 * infrastructure needed for Pub/Sub Lite.
 */
public class KafkaPublisher extends AbstractApiService implements Publisher {

  private final String topicName;
  private final KafkaProducer<byte[], byte[]> kafkaProducer;

  public KafkaPublisher(PublisherSettings settings) {
    this.topicName = settings.topicPath().name().value();

    // Set up Kafka producer configuration
    Map<String, Object> kafkaProps =
        new HashMap<>(settings.kafkaProperties().orElse(new HashMap<>()));

    // Ensure key and value serializers are set
    kafkaProps.putIfAbsent("key.serializer", ByteArraySerializer.class.getName());
    kafkaProps.putIfAbsent("value.serializer", ByteArraySerializer.class.getName());

    this.kafkaProducer = new KafkaProducer<>(kafkaProps);
  }

  @Override
  public ApiFuture<String> publish(PubsubMessage message) {
    SettableApiFuture<String> future = SettableApiFuture.create();

    try {
      // Convert PubsubMessage to Kafka ProducerRecord
      ProducerRecord<byte[], byte[]> record = convertToKafkaRecord(message);

      // Send to Kafka
      kafkaProducer.send(
          record,
          (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
              future.setException(exception);
            } else {
              // Return partition:offset format like Pub/Sub Lite message IDs
              String messageId = metadata.partition() + ":" + metadata.offset();
              future.set(messageId);
            }
          });

    } catch (Exception e) {
      future.setException(e);
    }

    return future;
  }

  private ProducerRecord<byte[], byte[]> convertToKafkaRecord(PubsubMessage message) {
    // Use ordering key as Kafka key for partitioning
    byte[] key = null;
    if (!message.getOrderingKey().isEmpty()) {
      key = message.getOrderingKey().getBytes();
    }

    // Message data becomes Kafka value
    byte[] value = message.getData().toByteArray();

    // Convert attributes to Kafka headers
    ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topicName, key, value);

    // Add all attributes as headers
    for (Map.Entry<String, String> attr : message.getAttributesMap().entrySet()) {
      record.headers().add(attr.getKey(), attr.getValue().getBytes());
    }

    // Add event time as special header if present
    if (message.hasPublishTime()) {
      record
          .headers()
          .add(
              "pubsublite.publish_time",
              String.valueOf(message.getPublishTime().getSeconds()).getBytes());
    }

    return record;
  }

  @Override
  protected void doStart() {
    // KafkaProducer is ready immediately
    notifyStarted();
  }

  @Override
  protected void doStop() {
    try {
      kafkaProducer.close();
      notifyStopped();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }
}
