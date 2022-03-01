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

// [START pubsublite_publish_custom_attributes]
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudRegionOrZone;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.cloudpubsub.MessageTransforms;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.cloudpubsub.PublisherSettings;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import com.google.pubsub.v1.PubsubMessage;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

public class PublishWithCustomAttributesExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing topic for the publish example to work.
    String topicId = "your-topic-id";
    long projectNumber = Long.parseLong("123456789");
    boolean regional = false;

    publishWithCustomAttributesExample(cloudRegion, zoneId, projectNumber, topicId, regional);
  }

  // Publish messages to a topic with custom attributes.
  public static void publishWithCustomAttributesExample(
      String cloudRegion, char zoneId, long projectNumber, String topicId, boolean regional)
      throws ApiException, ExecutionException, InterruptedException {

    CloudRegionOrZone location = null;

    if (regional) {
      location = CloudRegionOrZone.of(CloudRegion.of(cloudRegion));
    } else {
      location = CloudRegionOrZone.of(CloudZone.of(CloudRegion.of(cloudRegion), zoneId));
    }

    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(location)
            .setName(TopicName.of(topicId))
            .build();

    PublisherSettings publisherSettings =
        PublisherSettings.newBuilder().setTopicPath(topicPath).build();

    Publisher publisher = Publisher.create(publisherSettings);

    // Start the publisher. Upon successful starting, its state will become RUNNING.
    publisher.startAsync().awaitRunning();

    // Prepare the message data as a byte string.
    String messageData = "message-with-custom-attributes";
    ByteString data = ByteString.copyFromUtf8(messageData);

    // Prepare a protobuf-encoded event timestamp for the message.
    Instant now = Instant.now();
    String eventTime =
        MessageTransforms.encodeAttributeEventTime(Timestamps.fromMillis(now.toEpochMilli()));

    PubsubMessage pubsubMessage =
        PubsubMessage.newBuilder()
            .setData(data)
            // Add two sets of custom attributes to the message.
            .putAllAttributes(ImmutableMap.of("year", "2020", "author", "unknown"))
            // Add an event timestamp as an attribute.
            .putAttributes(MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO, eventTime)
            .build();

    // Publish a message.
    ApiFuture<String> future = publisher.publish(pubsubMessage);

    // Shut down the publisher.
    publisher.stopAsync().awaitTerminated();

    String ackId = future.get();
    MessageMetadata metadata = MessageMetadata.decode(ackId);
    System.out.println("Published a message with custom attributes:\n" + metadata);
  }
}
// [END pubsublite_publish_custom_attributes]
