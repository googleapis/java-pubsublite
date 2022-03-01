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

// [START pubsublite_publish_batch]
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.threeten.bp.Duration;

public class PublishWithBatchSettingsExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing topic for the publish example to work.
    String topicId = "your-topic-id";
    long projectNumber = Long.parseLong("123456789");
    int messageCount = 100;
    boolean regional = false;

    publishWithBatchSettingsExample(
        cloudRegion, zoneId, projectNumber, topicId, messageCount, regional);
  }

  // Publish messages to a topic with batch settings.
  public static void publishWithBatchSettingsExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String topicId,
      int messageCount,
      boolean regional)
      throws ApiException, ExecutionException, InterruptedException {

    TopicPath topicPath = null;

    if (regional) {
      // A regional topic path.
      topicPath =
          TopicPath.newBuilder()
              .setProject(ProjectNumber.of(projectNumber))
              .setLocation(CloudRegion.of(cloudRegion))
              .setName(TopicName.of(topicId))
              .build();
    } else {
      // A zonal topic path.
      topicPath =
          TopicPath.newBuilder()
              .setProject(ProjectNumber.of(projectNumber))
              .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
              .setName(TopicName.of(topicId))
              .build();
    }

    Publisher publisher = null;
    List<ApiFuture<String>> futures = new ArrayList<>();

    try {
      // Batch settings control how the publisher batches messages
      long requestBytesThreshold = 5000L; // default : 3_500_000 bytes
      long messageCountBatchSize = 100L; // default : 1000L message
      Duration publishDelayThreshold = Duration.ofMillis(100); // default : 50 ms

      // Publish request get triggered based on request size, messages count & time since last
      // publish, whichever condition is met first.
      BatchingSettings batchingSettings =
          BatchingSettings.newBuilder()
              .setRequestByteThreshold(requestBytesThreshold)
              .setElementCountThreshold(messageCountBatchSize)
              .setDelayThreshold(publishDelayThreshold)
              .build();

      PublisherSettings publisherSettings =
          PublisherSettings.newBuilder()
              .setTopicPath(topicPath)
              .setBatchingSettings(batchingSettings)
              .build();

      publisher = Publisher.create(publisherSettings);

      // Start the publisher. Upon successful starting, its state will become RUNNING.
      publisher.startAsync().awaitRunning();

      for (int i = 0; i < messageCount; i++) {
        String message = "message-" + i;

        // Convert the message to a byte string.
        ByteString data = ByteString.copyFromUtf8(message);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

        // Publish a message.
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        futures.add(future);
      }
    } finally {
      ArrayList<MessageMetadata> metadata = new ArrayList<>();
      List<String> ackIds = ApiFutures.allAsList(futures).get();
      System.out.println("Published " + ackIds.size() + " messages with batch settings.");

      if (publisher != null) {
        // Shut down the publisher.
        publisher.stopAsync().awaitTerminated();
      }
    }
  }
}
// [END pubsublite_publish_batch]
