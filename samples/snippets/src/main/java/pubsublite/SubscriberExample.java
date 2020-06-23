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
import java.util.concurrent.TimeoutException;

public class SubscriberExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing subscription for the subscribe example to work.
    String subscriptionId = "your-subscription-id";
    long projectNumber = Long.parseLong("123456789");
    // List of partitions to subscribe to. It can be all the partitions in a topic or
    // a subset of them. A topic of N partitions has partition numbers [0~N-1].
    List<Integer> partitionNumbers = ImmutableList.of(0);

    subscriberExample(cloudRegion, zoneId, projectNumber, subscriptionId, partitionNumbers);
  }

  public static void subscriberExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String subscriptionId,
      List<Integer> partitionNumbers)
      throws Exception {

    SubscriptionPath subscriptionPath =
        SubscriptionPaths.newBuilder()
            .setZone(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setProjectNumber(ProjectNumber.of(projectNumber))
            .setSubscriptionName(SubscriptionName.of(subscriptionId))
            .build();

    // The message stream is paused based on the maximum size or number of messages that the
    // subscriber has already received, whichever condition is met first.
    FlowControlSettings flowControlSettings =
        FlowControlSettings.builder()
            // 10 MiB. Must be greater than the size of the largest message.
            .setBytesOutstanding(10 * 1024 * 1024L)
            // 1,000 outstanding messages. Must be >0.
            .setMessagesOutstanding(1000L)
            .build();

    List<Partition> partitions = new ArrayList<>();
    for (Integer num : partitionNumbers) {
      partitions.add(Partition.of(num));
    }

    MessageReceiver receiver =
        (PubsubMessage message, AckReplyConsumer consumer) -> {
          System.out.println("Id : " + message.getMessageId());
          System.out.println("Data : " + message.getData().toStringUtf8());
          consumer.ack();
        };

    SubscriberSettings subscriberSettings =
        SubscriberSettings.newBuilder()
            .setSubscriptionPath(subscriptionPath)
            .setPartitions(partitions)
            .setReceiver(receiver)
            // Flow control settings are set at the partition level.
            .setPerPartitionFlowControlSettings(flowControlSettings)
            .build();

    Subscriber subscriber = Subscriber.create(subscriberSettings);

    // Start the subscriber. Upon successful starting, its state will become RUNNING.
    subscriber.startAsync().awaitRunning();

    System.out.println("Listening to messages on " + subscriptionPath.value() + "...");

    try {
      // Wait 30 seconds for the subscriber to reach TERMINATED state. If it encounters
      // unrecoverable errors before then, its state will change to FAILED and an
      // IllegalStateException will be thrown.
      subscriber.awaitTerminated(30, TimeUnit.SECONDS);
    } catch (TimeoutException t) {
      // Shut down the subscriber. This will change the state of the subscriber to TERMINATED.
      subscriber.stopAsync().awaitTerminated();
      System.out.println("Subscriber is shut down: " + subscriber.state());
    }
  }
}
// [END pubsublite_quickstart_subscriber]
