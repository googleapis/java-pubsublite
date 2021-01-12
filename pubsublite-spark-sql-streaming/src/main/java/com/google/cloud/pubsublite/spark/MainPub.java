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

import java.util.ArrayList;
import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;


public class MainPub {

    public static void main(String[] args) throws Exception {

        long projectNumber = 358307816737L;
        String cloudRegion = "us-central1";
        char zoneId = 'a';
        String topicId = "test-spark-jiangmichael";
        int messageCount = 1000;

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
                    PublisherSettings.newBuilder().setTopicPath(topicPath).build();

            publisher = Publisher.create(publisherSettings);

            // Start the publisher. Upon successful starting, its state will become RUNNING.
            publisher.startAsync().awaitRunning();

            while (true) {
                for (int i = 0; i < messageCount; i++) {
                    String message = "message-" + i;

                    // Convert the message to a byte string.
                    ByteString data = ByteString.copyFromUtf8(message);
                    PubsubMessage pubsubMessage =
                            PubsubMessage.newBuilder().setData(data).build();

                    // Publish a message. Messages are automatically batched.
                    ApiFuture<String> future = publisher.publish(pubsubMessage);
                    futures.add(future);
                }

                ArrayList<PublishMetadata> metadata = new ArrayList<>();
                List<String> ackIds = ApiFutures.allAsList(futures).get();
                for (String id : ackIds) {
                    // Decoded metadata contains partition and offset.
                    metadata.add(PublishMetadata.decode(id));
                }
                System.out.println("Published " + ackIds.size() + " messages.");
                Thread.sleep(500);
            }
        } catch (Throwable t) {
            throw t;
        }
    }
}
