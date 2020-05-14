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

// [START pubsublite_quickstart_subscriber]

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberSettings;
import com.google.common.collect.ImmutableList;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SubscriberExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE_ID = 'b';
    String SUBSCRIPTION_NAME = "Your Subscription Name";
    long PROJECT_NUMBER = Long.parseLong("123456789");
    // List of partitions to subscribe to. It can be all the partitions in a topic or
    // a subset of them. A topic of N partitions has partition numbers [0~N-1].
    List<Integer> PARTITION_NOS = ImmutableList.of(0);

    subscriberExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, SUBSCRIPTION_NAME, PARTITION_NOS);
  }

  public static void subscriberExample(
      String CLOUD_REGION,
      char ZONE_ID,
      long PROJECT_NUMBER,
      String SUBSCRIPTION_NAME,
      List<Integer> PARTITION_NOS)
      throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.of(CLOUD_REGION);
      CloudZone zone = CloudZone.of(cloudRegion, ZONE_ID);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      SubscriptionName subscriptionName = SubscriptionName.of(SUBSCRIPTION_NAME);

      SubscriptionPath subscriptionPath =
          SubscriptionPaths.newBuilder()
              .setZone(zone)
              .setProjectNumber(projectNum)
              .setSubscriptionName(subscriptionName)
              .build();

      FlowControlSettings flowControlSettings =
          FlowControlSettings.builder()
              // Set outstanding bytes to 10 MiB per partition.
              .setBytesOutstanding(10 * 1024 * 1024L)
              .setMessagesOutstanding(Long.MAX_VALUE)
              .build();

      List<Partition> partitions = new ArrayList<>();
      for (Integer num : PARTITION_NOS) {
        partitions.add(Partition.of(num));
      }

      MessageReceiver receiver =
          new MessageReceiver() {
            @Override
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
              System.out.println("Id : " + message.getMessageId());
              System.out.println("Data : " + message.getData().toStringUtf8());
              consumer.ack();
            }
          };

      SubscriberSettings subscriberSettings =
          SubscriberSettings.newBuilder()
              .setSubscriptionPath(subscriptionPath)
              .setPerPartitionFlowControlSettings(flowControlSettings)
              .setPartitions(partitions)
              .setReceiver(receiver)
              .build();

      Subscriber subscriber = Subscriber.create(subscriberSettings);

      subscriber.startAsync().awaitRunning();

      System.out.println("Listening to messages on " + subscriptionPath.value() + " ...");

      // If the service fails within 30s, awaitTerminated will throw an exception.
      // When unspecified, the subscriber is expected to run indefinitely.
      subscriber.awaitTerminated(30, TimeUnit.SECONDS);
      subscriber.stopAsync();

    } catch (Throwable t) {
      System.out.println("Error in subscribing to messages: " + t);
    }
  }
}
// [END pubsublite_quickstart_subscriber]
