// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.pubsublite;
// [START pubsub_quickstart_publisher]

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.PublisherInterface;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.cloudpubsub.PublisherApiService;
import com.google.cloud.pubsublite.cloudpubsub.PublisherBuilder;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;

public class PublisherExample {
  private static final int MESSAGE_COUNT = 10;

  // Load the project number from a commandline flag.
  private static final int PROJECT_NUMBER = 123;
  // Load the zone from a commandline flag.
  private static final String ZONE = "us-central9-z";
  // Load the topic name from a commandline flag.
  private static final String TOPIC_NAME = "my-topic";

  public static List<String> runPublisher(PublisherInterface publisher) throws Exception {
    List<ApiFuture<String>> futures = new ArrayList<>();
    for (int i = 0; i < MESSAGE_COUNT; i++) {
      String message = "message-" + i;

      // convert message to bytes
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      // Schedule a message to be published. Messages are automatically batched.
      ApiFuture<String> future = publisher.publish(pubsubMessage);
      futures.add(future);
    }
    return ApiFutures.allAsList(futures).get();
  }

  // Publish messages to a topic.
  public static void run() throws Exception {
    PublisherApiService publisherService =
        PublisherBuilder.newBuilder()
            .setTopicPath(
                TopicPaths.newBuilder()
                    .setProjectNumber(ProjectNumber.of(PROJECT_NUMBER))
                    .setZone(CloudZone.parse(ZONE))
                    .setTopicName(TopicName.of(TOPIC_NAME))
                    .build())
            .build();
    publisherService.startAsync().awaitRunning();
    List<String> ackIds = runPublisher(publisherService);
    publisherService.stopAsync().awaitTerminated();
    ArrayList<PublishMetadata> metadata = new ArrayList<>();
    for (String id : ackIds) {
      metadata.add(PublishMetadata.decode(id));
    }
  }
}
// [END pubsublite_quickstart_publisher]
