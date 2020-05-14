/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pubsublite;

// [START pubsublite_quickstart_publisher]

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.List;

public class PublisherExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    String TOPIC_NAME = "Your Topic Name";
    long PROJECT_NUMBER = Long.parseLong("123456789");
    int MESSAGE_COUNT = 100;

    PublisherExample.publisherExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, MESSAGE_COUNT);
  }

  // Publish messages to a topic.
  public static void publisherExample(
      String CLOUD_REGION, char ZONE_ID, long PROJECT_NUMBER, String TOPIC_NAME, int MESSAGE_COUNT)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setProjectNumber(projectNum)
              .setZone(zone)
              .setTopicName(topicName)
              .build();

      PublisherSettings publisherSettings =
          PublisherSettings.newBuilder().setTopicPath(topicPath).build();

      Publisher publisher = Publisher.create(publisherSettings);

      // You may choose to handle publish errors by adding a listener.
      // publisher.addListener(new Publisher.Listener() {
      //   public void failed(Publisher.State from, Throwable failure){
      //     // Handle error.
      //   }
      // }, MoreExecutors.directExecutor());

      publisher.startAsync().awaitRunning();

      List<ApiFuture<String>> futures = new ArrayList<>();

      for (int i = 0; i < MESSAGE_COUNT; i++) {
        String message = "message-" + i;

        // Convert the message to a byte string.
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage =
            PubsubMessage.newBuilder()
                .setData(data)
                // Messages of the same ordering key will always get published to the
                // same partition. When OrderingKey is unset, messages can get published
                // to different partitions if more than one partition exist for the topic.
                // .setOrderingKey("testing")
                .build();

        // Schedule a message to be published. Messages are automatically batched.
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        futures.add(future);
      }

      // Wait for the publisher to complete its execution. An error will be thrown if
      // the service fails.
      publisher.stopAsync().awaitTerminated();

      ArrayList<PublishMetadata> metadata = new ArrayList<>();
      List<String> ackIds = ApiFutures.allAsList(futures).get();

      for (String id : ackIds) {
        // Decoded metadata contains partition and offset.
        metadata.add(PublishMetadata.decode(id));
      }
      System.out.println(metadata);
      System.out.println("Published " + metadata.size() + "  messages to " + topicPath.value());

    } catch (StatusException statusException) {
      System.out.println("Failed to publish messages: " + statusException);
      System.out.println(statusException.getStatus().getCode());
      System.out.println(statusException.getStatus());
      throw statusException;
    }
  }
}
// [END pubsublite_quickstart_publisher]
