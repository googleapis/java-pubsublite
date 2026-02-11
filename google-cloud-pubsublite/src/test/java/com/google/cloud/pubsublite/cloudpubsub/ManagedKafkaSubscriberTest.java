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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test for Google Managed Kafka subscriber.
 *
 * <p>This test publishes messages to a Kafka topic, then subscribes to receive them.
 *
 * <p>Run with: ./test-managed-kafka-subscriber.sh BOOTSTRAP_SERVER TOPIC_NAME [NUM_MESSAGES]
 *
 * <p>Example: ./test-managed-kafka-subscriber.sh
 * bootstrap.test-kafka.us-central1.managedkafka.myproject.cloud.goog:9092 test-topic 5
 */
public class ManagedKafkaSubscriberTest {

  public static void main(String[] args) throws Exception {
    // Get config from args
    String bootstrapServers = args.length > 0 ? args[0] : null;
    String topicName = args.length > 1 ? args[1] : null;
    int numMessages = args.length > 2 ? Integer.parseInt(args[2]) : 5;

    if (bootstrapServers == null || topicName == null) {
      System.out.println(
          "Usage: ManagedKafkaSubscriberTest <BOOTSTRAP_SERVER> <TOPIC_NAME> [NUM_MESSAGES]");
      System.out.println();
      System.out.println("Example:");
      System.out.println(
          "  ./test-managed-kafka-subscriber.sh"
              + " bootstrap.test-kafka.us-central1.managedkafka.myproject.cloud.goog:9092"
              + " test-topic 5");
      System.exit(1);
    }

    System.out.println("=== Google Managed Kafka Publisher + Subscriber Test ===");
    System.out.println();
    System.out.println("Configuration:");
    System.out.println("  Bootstrap:    " + bootstrapServers);
    System.out.println("  Topic:        " + topicName);
    System.out.println("  NumMessages:  " + numMessages);
    System.out.println();

    // Build paths (project/zone not used for Kafka, but required by API)
    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(1L))
            .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
            .setName(TopicName.of(topicName))
            .build();

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(1L))
            .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
            .setName(SubscriptionName.of(topicName))
            .build();

    // Kafka properties for Google Managed Kafka
    Map<String, Object> kafkaProps = new HashMap<>();
    kafkaProps.put("bootstrap.servers", bootstrapServers);
    kafkaProps.put("security.protocol", "SASL_SSL");
    kafkaProps.put("sasl.mechanism", "OAUTHBEARER");
    kafkaProps.put(
        "sasl.login.callback.handler.class",
        "com.google.cloud.hosted.kafka.auth.GcpLoginCallbackHandler");
    kafkaProps.put(
        "sasl.jaas.config",
        "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");

    // Publisher reliability settings
    kafkaProps.put("acks", "all");
    kafkaProps.put("retries", 3);
    kafkaProps.put("request.timeout.ms", 30000);

    // Test state
    String testId = "test-" + System.currentTimeMillis();
    Map<String, Boolean> sentMessages = new ConcurrentHashMap<>();
    Map<String, PubsubMessage> receivedMessages = new ConcurrentHashMap<>();
    AtomicInteger receivedCount = new AtomicInteger(0);
    CountDownLatch receiveLatch = new CountDownLatch(numMessages);

    // Create message receiver
    MessageReceiver receiver =
        new MessageReceiver() {
          @Override
          public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            String data = message.getData().toStringUtf8();
            String messageId = message.getMessageId();

            System.out.println("  Received: " + data);
            System.out.println("    Message ID:    " + messageId);
            System.out.println("    Ordering Key:  " + message.getOrderingKey());
            System.out.println("    Attributes:    " + message.getAttributesMap());
            System.out.println("    Publish Time:  " + message.getPublishTime());
            System.out.println();

            receivedMessages.put(data, message);
            receivedCount.incrementAndGet();
            receiveLatch.countDown();

            // Acknowledge the message
            consumer.ack();
          }
        };

    // ========== Phase 1: Publish messages ==========
    System.out.println("=== Phase 1: Publishing Messages ===");
    System.out.println();

    PublisherSettings publisherSettings =
        PublisherSettings.newBuilder()
            .setTopicPath(topicPath)
            .setMessagingBackend(MessagingBackend.MANAGED_KAFKA)
            .setKafkaProperties(kafkaProps)
            .build();

    Publisher publisher = Publisher.create(publisherSettings);

    try {
      System.out.println("Starting publisher...");
      publisher.startAsync().awaitRunning();
      System.out.println("Publisher started successfully!");
      System.out.println();

      // Send test messages
      for (int i = 1; i <= numMessages; i++) {
        String payload = testId + ":message-" + i;

        PubsubMessage message =
            PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(payload))
                .putAttributes("test_id", testId)
                .putAttributes("index", String.valueOf(i))
                .putAttributes("total", String.valueOf(numMessages))
                .setOrderingKey("test-ordering-key")
                .build();

        System.out.println("Publishing: " + payload);
        ApiFuture<String> future = publisher.publish(message);

        String messageId = future.get(30, TimeUnit.SECONDS);
        System.out.println("  -> Published with ID: " + messageId);
        sentMessages.put(payload, true);
      }

      System.out.println();
      System.out.println("All " + numMessages + " messages published successfully!");
      System.out.println();

    } finally {
      System.out.println("Stopping publisher...");
      publisher.stopAsync().awaitTerminated();
    }

    // ========== Phase 2: Subscribe and receive messages ==========
    System.out.println();
    System.out.println("=== Phase 2: Subscribing to Messages ===");
    System.out.println();

    // Add consumer-specific properties
    Map<String, Object> consumerProps = new HashMap<>(kafkaProps);
    consumerProps.put("group.id", "test-consumer-" + testId);
    consumerProps.put("auto.offset.reset", "earliest"); // Read from beginning

    SubscriberSettings subscriberSettings =
        SubscriberSettings.newBuilder()
            .setSubscriptionPath(subscriptionPath)
            .setReceiver(receiver)
            .setPerPartitionFlowControlSettings(
                FlowControlSettings.builder()
                    .setMessagesOutstanding(1000)
                    .setBytesOutstanding(100 * 1024 * 1024) // 100MB
                    .build())
            .setMessagingBackend(MessagingBackend.MANAGED_KAFKA)
            .setKafkaProperties(consumerProps)
            .build();

    Subscriber subscriber = Subscriber.create(subscriberSettings);

    try {
      System.out.println("Starting subscriber...");
      subscriber.startAsync().awaitRunning();
      System.out.println("Subscriber started successfully!");
      System.out.println();
      System.out.println("Waiting to receive " + numMessages + " messages (timeout: 60s)...");
      System.out.println();

      // Wait for messages
      boolean allReceived = receiveLatch.await(60, TimeUnit.SECONDS);

      System.out.println();
      if (allReceived) {
        System.out.println("=== TEST RESULTS ===");
        System.out.println();
        System.out.println("Messages sent:     " + numMessages);
        System.out.println("Messages received: " + receivedCount.get());
        System.out.println();

        // Verify received messages
        int matched = 0;
        for (String sentPayload : sentMessages.keySet()) {
          if (receivedMessages.containsKey(sentPayload)) {
            matched++;
            System.out.println("  [MATCHED] " + sentPayload);
          } else {
            System.out.println("  [MISSING] " + sentPayload);
          }
        }

        System.out.println();
        if (matched == numMessages) {
          System.out.println("=== TEST PASSED ===");
          System.out.println("All messages published and received successfully!");
        } else {
          System.out.println("=== TEST PARTIAL ===");
          System.out.println("Only " + matched + " of " + numMessages + " messages matched.");
        }
      } else {
        System.out.println("=== TEST TIMEOUT ===");
        System.out.println(
            "Only received "
                + receivedCount.get()
                + " of "
                + numMessages
                + " messages before timeout.");
        System.exit(1);
      }

    } catch (Exception e) {
      System.out.println();
      System.out.println("=== TEST FAILED ===");
      System.out.println("Error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    } finally {
      System.out.println();
      System.out.println("Stopping subscriber...");
      subscriber.stopAsync().awaitTerminated();
      System.out.println("Done.");
    }
  }
}
