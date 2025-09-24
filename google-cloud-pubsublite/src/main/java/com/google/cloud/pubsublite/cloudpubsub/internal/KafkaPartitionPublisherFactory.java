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

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.PartitionPublisherFactory;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;

/**
 * Factory for creating Kafka-based partition publishers. Manages a single KafkaProducer instance
 * shared across all partitions.
 */
public class KafkaPartitionPublisherFactory implements PartitionPublisherFactory {
  private final KafkaProducer<byte[], byte[]> kafkaProducer;
  private final PublisherSettings settings;
  private final ConcurrentHashMap<Partition, Publisher<MessageMetadata>> publishers;
  private final String topicName;

  public KafkaPartitionPublisherFactory(PublisherSettings settings) throws ApiException {
    this.settings = settings;
    this.publishers = new ConcurrentHashMap<>();
    this.topicName = extractKafkaTopicName(settings.topicPath());

    Properties props = new Properties();

    // Configure Kafka connection
    configureKafkaConnection(props);

    // Configure producer settings
    configureProducerSettings(props);

    // Apply user-provided properties (override defaults)
    if (settings.kafkaProperties().isPresent()) {
      settings.kafkaProperties().get().forEach((key, value) -> props.put(key, value));
    }

    try {
      this.kafkaProducer = new KafkaProducer<>(props);
    } catch (Exception e) {
      throw new ApiException(e, null, false);
    }
  }

  private void configureKafkaConnection(Properties props) {
    // For now, require bootstrap servers in kafkaProperties
    // In production, we'd extract from TopicPath location encoding
    if (settings.kafkaProperties().isPresent()
        && settings.kafkaProperties().get().containsKey("bootstrap.servers")) {
      props.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
          settings.kafkaProperties().get().get("bootstrap.servers"));
    } else {
      throw new IllegalArgumentException(
          "Kafka bootstrap servers must be specified in kafkaProperties");
    }
  }

  private void configureProducerSettings(Properties props) {
    // Serialization
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

    // Performance settings aligned with Pub/Sub Lite defaults
    props.put(
        ProducerConfig.BATCH_SIZE_CONFIG,
        Math.toIntExact(settings.batchingSettings().getRequestByteThreshold()));
    props.put(
        ProducerConfig.LINGER_MS_CONFIG,
        settings.batchingSettings().getDelayThresholdDuration().toMillis());

    // Compression
    if (settings.enableCompression()) {
      props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
    }

    // Idempotence
    if (settings.enableIdempotence()) {
      props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
      props.put(ProducerConfig.ACKS_CONFIG, "all");
      props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    }

    // Reliability settings
    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
  }

  @Override
  public Publisher<MessageMetadata> newPublisher(Partition partition) throws ApiException {
    return publishers.computeIfAbsent(
        partition, p -> new KafkaPartitionPublisher(kafkaProducer, topicName, partition, settings));
  }

  @Override
  public void close() {
    publishers
        .values()
        .forEach(
            publisher -> {
              try {
                publisher.stopAsync().awaitTerminated();
              } catch (Exception e) {
                // Log but don't throw - best effort cleanup
              }
            });
    kafkaProducer.close();
  }

  private String extractKafkaTopicName(com.google.cloud.pubsublite.TopicPath topicPath) {
    // Extract the actual Kafka topic name from TopicPath
    return topicPath.name().value();
  }
}
