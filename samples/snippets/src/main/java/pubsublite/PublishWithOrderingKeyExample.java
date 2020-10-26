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

// [START pubsublite_publish_ordering_key]
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
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
import java.util.concurrent.ExecutionException;

public class PublishWithOrderingKeyExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing topic for the publish example to work.
    String topicId = "your-topic-id";
    long projectNumber = Long.parseLong("123456789");

    publishWithOrderingKeyExample(cloudRegion, zoneId, projectNumber, topicId);
  }

  // Publish a message to a topic with an ordering key.
  public static void publishWithOrderingKeyExample(
      String cloudRegion, char zoneId, long projectNumber, String topicId)
      throws ApiException, ExecutionException, InterruptedException {

    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setName(TopicName.of(topicId))
            .build();

    PublisherSettings publisherSettings =
        PublisherSettings.newBuilder().setTopicPath(topicPath).build();

    Publisher publisher = Publisher.create(publisherSettings);

    // Start the publisher. Upon successful starting, its state will become RUNNING.
    publisher.startAsync().awaitRunning();

    String message = "message-with-ordering-key";

    // Convert the message to a byte string.
    ByteString data = ByteString.copyFromUtf8(message);
    PubsubMessage pubsubMessage =
        PubsubMessage.newBuilder()
            .setData(data)
            // Messages of the same ordering key will always get published to the
            // same partition. When OrderingKey is unset, messages can get published
            // to different partitions if more than one partition exists for the topic.
            .setOrderingKey("testing")
            .build();

    // Publish a message.
    ApiFuture<String> future = publisher.publish(pubsubMessage);

    // Shut down the publisher.
    publisher.stopAsync().awaitTerminated();

    String ackId = future.get();
    PublishMetadata metadata = PublishMetadata.decode(ackId);
    System.out.println("Published a message with ordering key:\n" + metadata);
  }
}
// [END pubsublite_publish_ordering_key]
