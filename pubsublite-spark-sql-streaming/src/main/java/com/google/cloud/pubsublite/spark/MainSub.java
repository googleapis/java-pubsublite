/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.cloudpubsub.SubscriberSettings;

import java.util.concurrent.atomic.AtomicLong;

public class MainSub {

    public static void main(String[] args) throws Exception {

        long projectNumber = 129988248131L;
        String cloudRegion = "us-central1";

        char zoneId = 'b';
        String subscriptionId = "test-spark-one-kb-sub";

//        char zoneId = 'a';
//        String subscriptionId = "menzella5";

//        char zoneId = 'a';
//        String subscriptionId = "menzella-large-messages";

        System.out.println("Subscribing from " + subscriptionId);

        SubscriptionPath subscriptionPath = SubscriptionPath.newBuilder()
                .setProject(ProjectNumber.of(projectNumber))
                .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
                .setName(SubscriptionName.of(subscriptionId))
                .build();

        AtomicLong counter = new AtomicLong();
        SubscriberSettings subscriberSettings =
                SubscriberSettings.newBuilder().setSubscriptionPath(subscriptionPath)
                        .setPerPartitionFlowControlSettings(FlowControlSettings.builder()
                                .setBytesOutstanding(Long.MAX_VALUE)
                                .setMessagesOutstanding(Long.MAX_VALUE)
                                .build())
                        .setReceiver((msg, ackConsumer) -> {
                            long sum = counter.addAndGet(1);
                            if (sum % 1024 == 0) {
                                System.out.println("Received " + sum + " messages.");
                            }
                            ackConsumer.ack();
                        }).build();

        Subscriber subscriber = Subscriber.create(subscriberSettings);
        subscriber.startAsync().awaitRunning();
    }
}
