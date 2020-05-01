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
import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.MultiPartitionSubscriber;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.common.collect.ImmutableList;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SubscriberExample {

  public static void runSubscriberExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    String SUBSCRIPTION_NAME = "Your Subscription Name";
    long PROJECT_NUMBER = 123456789L;
    List<Integer> PARTITION_NOS = ImmutableList.of(0);

    subscriberExample(CLOUD_REGION, ZONE, PROJECT_NUMBER, SUBSCRIPTION_NAME, PARTITION_NOS);
  }

  static class MessageReceiverExample implements MessageReceiver {

    private final Partition partition;

    MessageReceiverExample(Partition partition) {
      this.partition = partition;
    }

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
      System.out.println(
          "Partition: "
              + partition
              + " Message Id: "
              + message.getMessageId()
              + " Data: "
              + message.getData().toStringUtf8());
      // Ack only after all work for the message is complete.
      consumer.ack();
    }
  }

  public static void subscriberExample(
      String CLOUD_REGION,
      char ZONE,
      long PROJECT_NUMBER,
      String SUBSCRIPTION_NAME,
      List<Integer> PARTITION_NOS) throws Exception {

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);
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

      Subscriber.Builder builder = Subscriber.newBuilder();
      builder.setSubscriptionPath(subscriptionPath);
      builder.setFlowControlSettings(flowControlSettings);

      ArrayList<SubscriberInterface> subscribers = new ArrayList<>();
      for (Integer num : PARTITION_NOS) {
        Partition partition = Partition.create(num);
        subscribers.add(
            builder
                // Each subscriber can only subscribe to one partition.
                .setPartition(partition)
                .setReceiver(new MessageReceiverExample(partition))
                .build());
      }
      SubscriberInterface multiPartitionSubscriber = MultiPartitionSubscriber.of(subscribers);

      multiPartitionSubscriber.startAsync().awaitRunning();
      System.out.println("Listening to messages on " + subscriptionPath.value() + " ...");

      multiPartitionSubscriber.awaitTerminated(30, TimeUnit.SECONDS);
      multiPartitionSubscriber.stopAsync();

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to subscribe to messages: " + e.toString());
    }
  }
}
// [END pubsublite_quickstart_subscriber]
