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
import com.google.api.core.ApiService;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberSettings;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.pubsub.v1.PubsubMessage;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

/**
 * A Kafka-based subscriber that uses KafkaConsumer to consume messages from Kafka topics. This
 * implementation is designed to work with Google Managed Kafka (GMK) clusters.
 */
public class KafkaSubscriber extends AbstractApiService implements Subscriber {
  private static final Logger log = Logger.getLogger(KafkaSubscriber.class.getName());

  private final String topicName;
  private final String groupId;
  private final MessageReceiver receiver;
  private final KafkaConsumer<byte[], byte[]> kafkaConsumer;
  private final ExecutorService pollExecutor;
  private final AtomicBoolean isPolling = new AtomicBoolean(false);
  private final Map<String, OffsetInfo> pendingAcks = new ConcurrentHashMap<>();

  // Track offset info for each message
  private static class OffsetInfo {
    final TopicPartition partition;
    final long offset;
    final long timestamp;

    OffsetInfo(TopicPartition partition, long offset, long timestamp) {
      this.partition = partition;
      this.offset = offset;
      this.timestamp = timestamp;
    }
  }

  public KafkaSubscriber(SubscriberSettings settings) {
    this.topicName = settings.subscriptionPath().name().value();
    this.groupId = settings.subscriptionPath().toString().replace('/', '-');
    this.receiver = settings.receiver();
    this.pollExecutor =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r, "kafka-subscriber-poll-" + topicName);
              t.setDaemon(true);
              return t;
            });

    // Set up Kafka consumer configuration
    Map<String, Object> kafkaProps =
        new HashMap<>(settings.kafkaProperties().orElse(new HashMap<>()));

    // Set required properties
    kafkaProps.putIfAbsent("key.deserializer", ByteArrayDeserializer.class.getName());
    kafkaProps.putIfAbsent("value.deserializer", ByteArrayDeserializer.class.getName());
    kafkaProps.putIfAbsent("group.id", groupId);
    kafkaProps.putIfAbsent("enable.auto.commit", "false"); // Manual offset management
    kafkaProps.putIfAbsent("auto.offset.reset", "earliest");
    kafkaProps.putIfAbsent("max.poll.records", "500");
    kafkaProps.putIfAbsent("session.timeout.ms", "30000");

    try {
      this.kafkaConsumer = new KafkaConsumer<>(kafkaProps);
    } catch (Exception e) {
      String bootstrapServers = (String) kafkaProps.get("bootstrap.servers");
      if (e.getCause() instanceof org.apache.kafka.common.config.ConfigException
          && e.getMessage().contains("No resolvable bootstrap urls")) {
        throw new RuntimeException(
            "Failed to resolve Kafka bootstrap servers: "
                + bootstrapServers
                + ". This could indicate:\n"
                + "1. The Google Managed Kafka cluster doesn't exist or isn't accessible\n"
                + "2. Network/DNS resolution issues\n"
                + "3. Incorrect bootstrap server URL format\n"
                + "Please verify the cluster exists with: gcloud managed-kafka clusters describe"
                + " <cluster-name> --location=<region> --project=<project>",
            e);
      }
      throw new RuntimeException("Failed to initialize Kafka consumer: " + e.getMessage(), e);
    }
  }

  private void startPolling() {
    if (!isPolling.compareAndSet(false, true)) {
      return;
    }

    // Subscribe to the topic
    kafkaConsumer.subscribe(Collections.singletonList(topicName));

    // Start the polling loop
    pollExecutor.submit(
        () -> {
          try {
            while (isPolling.get() && !Thread.currentThread().isInterrupted()) {
              try {
                ConsumerRecords<byte[], byte[]> records =
                    kafkaConsumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<byte[], byte[]> record : records) {
                  if (!isPolling.get()) break;

                  try {
                    // Convert Kafka record to PubsubMessage
                    PubsubMessage message = convertToPubsubMessage(record);

                    // Generate a unique message ID
                    String messageId =
                        String.format(
                            "%s:%d:%d", record.topic(), record.partition(), record.offset());

                    // Store offset info for later acknowledgment
                    pendingAcks.put(
                        messageId,
                        new OffsetInfo(
                            new TopicPartition(record.topic(), record.partition()),
                            record.offset(),
                            record.timestamp()));

                    // Create AckReplyConsumer for this message
                    AckReplyConsumer ackReplyConsumer =
                        new AckReplyConsumer() {
                          private final AtomicBoolean acked = new AtomicBoolean(false);

                          @Override
                          public void ack() {
                            if (acked.compareAndSet(false, true)) {
                              commitOffset(messageId);
                            }
                          }

                          @Override
                          public void nack() {
                            if (acked.compareAndSet(false, true)) {
                              // In Kafka, nack typically means we don't commit the offset
                              // The message will be redelivered after session timeout
                              log.info("Message nacked, will be redelivered: " + messageId);
                              pendingAcks.remove(messageId);
                            }
                          }
                        };

                    // Deliver message to receiver
                    receiver.receiveMessage(message, ackReplyConsumer);

                  } catch (Exception e) {
                    log.log(Level.WARNING, "Error processing message from Kafka", e);
                  }
                }

              } catch (WakeupException e) {
                // This is expected when consumer.wakeup() is called
                break;
              } catch (Exception e) {
                log.log(Level.SEVERE, "Error in Kafka poll loop", e);
                if (!isPolling.get()) break;

                // Sleep briefly before retrying
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException ie) {
                  Thread.currentThread().interrupt();
                  break;
                }
              }
            }
          } finally {
            isPolling.set(false);
          }
        });
  }

  /**
   * Converts a Kafka ConsumerRecord to a PubsubMessage.
   *
   * <p>Translation rules: - Data: Bytes -> Bytes (direct pass-through) - Key: Kafka key -> PubSub
   * orderingKey (preserves ordering logic) - Headers: Kafka headers -> PubSub attributes
   * (multi-value headers are flattened) - Timestamp: Kafka timestamp (Unix epoch millis) ->
   * Protobuf Timestamp
   */
  private PubsubMessage convertToPubsubMessage(ConsumerRecord<byte[], byte[]> record) {
    PubsubMessage.Builder builder = PubsubMessage.newBuilder();

    // Data: direct bytes pass-through
    if (record.value() != null) {
      builder.setData(ByteString.copyFrom(record.value()));
    }

    // Key: Kafka key becomes ordering key (preserves partitioning/ordering)
    if (record.key() != null) {
      builder.setOrderingKey(new String(record.key(), java.nio.charset.StandardCharsets.UTF_8));
    }

    // Convert Kafka timestamp (Unix epoch milliseconds) to Protobuf Timestamp
    long timestampMs = record.timestamp();
    if (timestampMs > 0) {
      long seconds = timestampMs / 1000;
      int nanos = (int) ((timestampMs % 1000) * 1_000_000);
      builder.setPublishTime(Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build());
    }

    // Headers: Convert Kafka headers to PubSub attributes
    // For multi-value headers with the same key, we use indexed suffixes
    Map<String, String> attributes = new HashMap<>();
    Map<String, Integer> headerCounts = new HashMap<>();

    for (Header header : record.headers()) {
      if (header.value() != null) {
        String key = header.key();
        String value = new String(header.value(), java.nio.charset.StandardCharsets.UTF_8);

        // Handle special headers that map to PubsubMessage fields
        if (key.equals("pubsublite.publish_time")) {
          // Already handled via record.timestamp()
          continue;
        }

        // Handle multi-value attributes by appending index suffix
        int count = headerCounts.getOrDefault(key, 0);
        if (count == 0) {
          attributes.put(key, value);
        } else {
          attributes.put(key + "." + count, value);
        }
        headerCounts.put(key, count + 1);
      }
    }

    // Add Kafka-specific metadata as special attributes
    attributes.put("x-kafka-topic", record.topic());
    attributes.put("x-kafka-partition", String.valueOf(record.partition()));
    attributes.put("x-kafka-offset", String.valueOf(record.offset()));
    attributes.put("x-kafka-timestamp-ms", String.valueOf(record.timestamp()));
    attributes.put("x-kafka-timestamp-type", record.timestampType().name());

    builder.putAllAttributes(attributes);

    // Set message ID in format: topic:partition:offset
    builder.setMessageId(
        String.format("%s:%d:%d", record.topic(), record.partition(), record.offset()));

    return builder.build();
  }

  private void commitOffset(String messageId) {
    OffsetInfo info = pendingAcks.remove(messageId);
    if (info == null) {
      return;
    }

    // Skip commit if we're shutting down
    if (!isPolling.get()) {
      log.fine("Skipping offset commit during shutdown for message: " + messageId);
      return;
    }

    try {
      // Commit the offset for this message
      Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
      offsets.put(info.partition, new OffsetAndMetadata(info.offset + 1));
      kafkaConsumer.commitSync(offsets);

      log.fine("Committed offset for message: " + messageId);
    } catch (WakeupException e) {
      // This is expected during shutdown - consumer.wakeup() was called
      log.fine("Offset commit interrupted by shutdown for message: " + messageId);
    } catch (Exception e) {
      log.log(Level.WARNING, "Failed to commit offset for message: " + messageId, e);
    }
  }

  public String getSubscriptionNameString() {
    return topicName + "/" + groupId;
  }

  @Override
  public ApiService startAsync() {
    // Start parent service first
    super.startAsync();
    return this;
  }

  @Override
  protected void doStart() {
    try {
      startPolling();
      notifyStarted();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }

  @Override
  protected void doStop() {
    try {
      // Stop polling
      isPolling.set(false);

      // Wake up the consumer if it's blocked in poll()
      kafkaConsumer.wakeup();

      // Shutdown executor
      pollExecutor.shutdown();
      try {
        if (!pollExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
          pollExecutor.shutdownNow();
        }
      } catch (InterruptedException e) {
        pollExecutor.shutdownNow();
        Thread.currentThread().interrupt();
      }

      kafkaConsumer.close();

      notifyStopped();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }
}
