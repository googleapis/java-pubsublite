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

package com.google.cloud.pubsublite.cloudpubsub;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.internal.KafkaPartitionPublisherFactory;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class KafkaBackendTest {

  private static final TopicPath TOPIC_PATH =
      TopicPath.newBuilder()
          .setProject(ProjectNumber.of(123456789L))
          .setLocation(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
          .setName(TopicName.of("test-topic"))
          .build();

  @Test
  public void testDefaultBackendIsPubSubLite() {
    PublisherSettings settings = PublisherSettings.newBuilder().setTopicPath(TOPIC_PATH).build();

    assertThat(settings.messagingBackend()).isEqualTo(MessagingBackend.PUBSUB_LITE);
  }

  @Test
  public void testKafkaBackendSelection() {
    Map<String, Object> kafkaProps = new HashMap<>();
    kafkaProps.put("bootstrap.servers", "localhost:9092");

    PublisherSettings settings =
        PublisherSettings.newBuilder()
            .setTopicPath(TOPIC_PATH)
            .setMessagingBackend(MessagingBackend.MANAGED_KAFKA)
            .setKafkaProperties(kafkaProps)
            .build();

    assertThat(settings.messagingBackend()).isEqualTo(MessagingBackend.MANAGED_KAFKA);
    assertThat(settings.kafkaProperties()).isPresent();
    assertThat(settings.kafkaProperties().get())
        .containsEntry("bootstrap.servers", "localhost:9092");
  }

  @Test
  public void testKafkaFactoryCreation() throws Exception {
    Map<String, Object> kafkaProps = new HashMap<>();
    kafkaProps.put("bootstrap.servers", "localhost:9092");

    PublisherSettings settings =
        PublisherSettings.newBuilder()
            .setTopicPath(TOPIC_PATH)
            .setMessagingBackend(MessagingBackend.MANAGED_KAFKA)
            .setKafkaProperties(kafkaProps)
            .build();

    // This should create a Kafka factory successfully
    // (connection is only attempted when actually publishing)
    KafkaPartitionPublisherFactory factory = new KafkaPartitionPublisherFactory(settings);
    assertThat(factory).isNotNull();
    factory.close(); // Clean up
  }

  @Test
  public void testKafkaPropertiesOptional() {
    PublisherSettings settings =
        PublisherSettings.newBuilder()
            .setTopicPath(TOPIC_PATH)
            .setMessagingBackend(MessagingBackend.PUBSUB_LITE)
            .build();

    assertThat(settings.kafkaProperties()).isEmpty();
  }

  @Test
  public void testBackwardCompatibility() {
    // Test that existing code without backend specification still works
    PublisherSettings settings =
        PublisherSettings.newBuilder()
            .setTopicPath(TOPIC_PATH)
            .setBatchingSettings(PublisherSettings.DEFAULT_BATCHING_SETTINGS)
            .setEnableIdempotence(true)
            .build();

    // Should default to Pub/Sub Lite
    assertThat(settings.messagingBackend()).isEqualTo(MessagingBackend.PUBSUB_LITE);
    assertThat(settings.kafkaProperties()).isEmpty();
  }
}
