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
import com.google.cloud.pubsublite.cloudpubsub.PublisherApiService;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PublisherExample {

  public static void runPublisherExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    String TOPIC_NAME = "Your Topic Name";
    long PROJECT_NUMBER = 123456789L;
    int MESSAGE_COUNT = 100;

    PublisherExample.publisherExample(
        CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME, MESSAGE_COUNT);
  }

  // Publish messages to a topic.
  public static void publisherExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String TOPIC_NAME, int MESSAGE_COUNT)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setProjectNumber(ProjectNumber.of(PROJECT_NUMBER))
              .setZone(zone)
              .setTopicName(TopicName.of(TOPIC_NAME))
              .build();

      PublisherApiService publisherService =
          Publisher.newBuilder().setTopicPath(topicPath).build();

      publisherService.startAsync().awaitRunning();

      List<ApiFuture<String>> futures = new ArrayList<>();

      for (int i = 0; i < MESSAGE_COUNT; i++) {
        String message = "message-" + i;

        // Convert the message to a byte string.
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data)
            // Messages of the same ordering key will always get published to the
            // same partition. When OrderingKey is unset, messages can get published
            // to different partitions if more than one partition exist for the topic.
            // .setOrderingKey("testing")
            .build();

        // Schedule a message to be published. Messages are automatically batched.
        ApiFuture<String> future = publisherService.publish(pubsubMessage);
        futures.add(future);
      }

      publisherService.stopAsync().awaitTerminated(30, TimeUnit.SECONDS);

      ArrayList<PublishMetadata> metadata = new ArrayList<>();
      List<String> ackIds = ApiFutures.allAsList(futures).get();

      for (String id : ackIds) {
        // Decoded metadata contains partition and offset.
        metadata.add(PublishMetadata.decode(id));
      }
      System.out.println(metadata);
      System.out.println("Published " + metadata.size() + "  messages to " + topicPath.value());

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to publish messages: " + e.toString());
    }
  }
}
// [END pubsublite_quickstart_publisher]
