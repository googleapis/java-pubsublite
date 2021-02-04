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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.cloud.pubsublite.internal.wire.ServiceClients;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.cloud.pubsublite.v1.AdminServiceSettings;
import com.google.common.io.Resources;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Durations;
import com.google.pubsub.v1.PubsubMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublishForSample {

    public static void main(String[] args) throws Exception {

        // TODO(developer): Replace these variables before running the sample.
        String cloudRegion = "us-central1";
        char zoneId = 'c';
        String topicId = "test-sample-topic-1-partition-2";
        String subscriptionId = "test-sample-subscription-1-partition-2";
        long projectNumber = Long.parseLong("129988248131");
        int partitions = 1;

        String snippets = Resources.toString(Resources.getResource("text_snippets.txt"),
                Charset.defaultCharset());
        snippets = snippets.replaceAll("[:;,.!]", "").replaceAll("\n", " ").
                replaceAll("\\s+", " ").toLowerCase();
        List<String> words = Arrays.asList(snippets.split(" "));

//        createTopicExample(cloudRegion, zoneId, projectNumber, topicId, partitions);
//        createSubscriptionExample(cloudRegion, zoneId, projectNumber, topicId, subscriptionId);

        publisherExample(cloudRegion, zoneId, projectNumber, topicId, words);
    }

    // https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/CreateTopicExample.java
    public static void createTopicExample(
            String cloudRegion, char zoneId, long projectNumber, String topicId, int partitions)
            throws Exception {

        TopicPath topicPath =
                TopicPath.newBuilder()
                        .setProject(ProjectNumber.of(projectNumber))
                        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
                        .setName(TopicName.of(topicId))
                        .build();

        Topic topic =
                Topic.newBuilder()
                        .setPartitionConfig(
                                Topic.PartitionConfig.newBuilder()
                                        // Set throughput capacity per partition in MiB/s.
                                        .setCapacity(
                                                Topic.PartitionConfig.Capacity.newBuilder()
                                                        // Must be 4-16 MiB/s.
                                                        .setPublishMibPerSec(4)
                                                        // Must be 4-32 MiB/s.
                                                        .setSubscribeMibPerSec(8)
                                                        .build())
                                        .setCount(partitions))
                        .setRetentionConfig(
                                Topic.RetentionConfig.newBuilder()
                                        // How long messages are retained.
                                        .setPeriod(Durations.fromDays(1))
                                        // Set storage per partition to 30 GiB. This must be 30 GiB-10 TiB.
                                        // If the number of bytes stored in any of the topic's partitions grows
                                        // beyond this value, older messages will be dropped to make room for
                                        // newer ones, regardless of the value of `period`.
                                        .setPerPartitionBytes(30 * 1024 * 1024 * 1024L))
                        .setName(topicPath.toString())
                        .build();

        AdminServiceClient adminServiceClient = AdminServiceClient.create(
                ServiceClients.addDefaultSettings(CloudRegion.of(cloudRegion),
                        AdminServiceSettings.newBuilder())
        );

        AdminClientSettings adminClientSettings =
                AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion))
                        .setServiceClient(adminServiceClient)
                        .build();

        try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
            Topic response = adminClient.createTopic(topic).get();
            System.out.println(response.getAllFields() + "created successfully.");
        }
    }

    // https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/CreateSubscriptionExample.java
    public static void createSubscriptionExample(
            String cloudRegion, char zoneId, long projectNumber, String topicId, String subscriptionId)
            throws Exception {

        TopicPath topicPath =
                TopicPath.newBuilder()
                        .setProject(ProjectNumber.of(projectNumber))
                        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
                        .setName(TopicName.of(topicId))
                        .build();

        SubscriptionPath subscriptionPath =
                SubscriptionPath.newBuilder()
                        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
                        .setProject(ProjectNumber.of(projectNumber))
                        .setName(SubscriptionName.of(subscriptionId))
                        .build();

        Subscription subscription =
                Subscription.newBuilder()
                        .setDeliveryConfig(
                                // Possible values for DeliveryRequirement:
                                // - `DELIVER_IMMEDIATELY`
                                // - `DELIVER_AFTER_STORED`
                                // You may choose whether to wait for a published message to be successfully written
                                // to storage before the server delivers it to subscribers. `DELIVER_IMMEDIATELY` is
                                // suitable for applications that need higher throughput.
                                Subscription.DeliveryConfig.newBuilder()
                                        .setDeliveryRequirement(Subscription.DeliveryConfig.DeliveryRequirement.DELIVER_IMMEDIATELY))
                        .setName(subscriptionPath.toString())
                        .setTopic(topicPath.toString())
                        .build();

        AdminServiceClient adminServiceClient = AdminServiceClient.create(
                ServiceClients.addDefaultSettings(CloudRegion.of(cloudRegion),
                        AdminServiceSettings.newBuilder())
        );

        AdminClientSettings adminClientSettings =
                AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion))
                        .setServiceClient(adminServiceClient)
                        .build();

        try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
            Subscription response = adminClient.createSubscription(subscription).get();
            System.out.println(response.getAllFields() + "created successfully.");
        }
    }

    // Minor modification of https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/PublisherExample.java
    public static void publisherExample(
            String cloudRegion, char zoneId, long projectNumber, String topicId, List<String> words)
            throws Exception {

        TopicPath topicPath =
                TopicPath.newBuilder()
                        .setProject(ProjectNumber.of(projectNumber))
                        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
                        .setName(TopicName.of(topicId))
                        .build();
        Publisher publisher = null;
        List<ApiFuture<String>> futures = new ArrayList<>();

        try {
            PublisherSettings publisherSettings =
                    PublisherSettings.newBuilder().setTopicPath(topicPath)
                            .build();

            publisher = Publisher.create(publisherSettings);

            // Start the publisher. Upon successful starting, its state will become RUNNING.
            publisher.startAsync().awaitRunning();

            for (String word : words) {
                // Include the count in message, and convert the message to a byte string
                ByteString data = ByteString.copyFromUtf8(word + "_1");
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                // Publish a message. Messages are automatically batched.
                ApiFuture<String> future = publisher.publish(pubsubMessage);
                futures.add(future);
            }
        } finally {
            ArrayList<PublishMetadata> metadata = new ArrayList<>();
            List<String> ackIds = ApiFutures.allAsList(futures).get();
            for (String id : ackIds) {
                // Decoded metadata contains partition and offset.
                metadata.add(PublishMetadata.decode(id));
            }
            System.out.println(metadata + "\nPublished " + ackIds.size() + " messages.");

            if (publisher != null) {
                // Shut down the publisher.
                publisher.stopAsync().awaitTerminated();
                System.out.println("Publisher is shut down.");
            }
        }
    }

}
