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

// [START pubsublite_quickstart_subscriber]

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.MultiPartitionSubscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberBuilder;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;

public class SubscriberExample {
  // Load the project number from a commandline flag.
  private static final int PROJECT_NUMBER = 123;
  // Load the zone from a commandline flag.
  private static final String ZONE = "us-central9-z";
  // Load the subscription name from a commandline flag.
  private static final String SUBSCRIPTION_NAME = "my-sub";
  // Decide the list of partitions this subscriber is responsible for from a commandline flag.
  private static final List<Integer> PARTITIONS = List.of(3, 7);

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

  public static void run() throws Exception {
    SubscriberBuilder.Builder builder = SubscriberBuilder.newBuilder();
    builder.setSubscriptionPath(
        SubscriptionPaths.newBuilder()
            .setProjectNumber(ProjectNumber.of(PROJECT_NUMBER))
            .setZone(CloudZone.parse(ZONE))
            .setSubscriptionName(SubscriptionName.of(SUBSCRIPTION_NAME))
            .build());
    builder.setFlowControlSettings(
        FlowControlSettings.builder()
            .setBytesOutstanding(10_000_000) // 10 MB per partition.
            .setMessagesOutstanding(Long.MAX_VALUE)
            .build());
    ArrayList<SubscriberInterface> subscribers = new ArrayList<>();
    for (Integer num : PARTITIONS) {
      Partition partition = Partition.create(num);
      subscribers.add(
          builder
              .setPartition(partition)
              .setReceiver(new MessageReceiverExample(partition))
              .build());
    }
    SubscriberInterface wrapped = MultiPartitionSubscriber.of(subscribers);
    wrapped.startAsync().awaitRunning();
    wrapped.awaitTerminated();
  }
}
// [END pubsublite_quickstart_subscriber]
