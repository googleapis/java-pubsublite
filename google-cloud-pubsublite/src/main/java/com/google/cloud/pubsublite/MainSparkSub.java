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

package com.google.cloud.pubsublite;

import static com.google.cloud.pubsublite.internal.wire.ServiceClients.addDefaultSettings;

import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriberImpl;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.RoutingMetadata;
import com.google.cloud.pubsublite.internal.wire.ServiceClients;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;
import com.google.cloud.pubsublite.v1.SubscriberServiceSettings;
import com.google.common.flogger.GoogleLogger;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class MainSparkSub {

  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  public static void main(String[] args) throws Exception {

    long projectNumber = 129988248131L;
    String cloudRegion = "us-central1";
    char zoneId = 'a';
    //        char zoneId = 'b';
    //        char zoneId = 'c';
    //        String subscriptionId = "test-sample-subscription-1-partition-2";
    //        String subscriptionId = "test-spark-subscription-1-partition-recent";
    //        String subscriptionId = "test-spark-one-kb-sub";
    String subscriptionId = "menzella5";

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setName(SubscriptionName.of(subscriptionId))
            .build();
    //        Subscriber subscriber = null;

    AtomicLong counter = new AtomicLong();

    SubscriberFactory subscriberFactory =
        (consumer) -> {
          PubsubContext context = PubsubContext.of(PubsubContext.Framework.of(""));
          SubscriberServiceSettings.Builder settingsBuilder =
              SubscriberServiceSettings.newBuilder();
          ServiceClients.addDefaultMetadata(
              context, RoutingMetadata.of(subscriptionPath, Partition.of(0)), settingsBuilder);
          try {
            SubscriberServiceClient serviceClient =
                SubscriberServiceClient.create(
                    addDefaultSettings(subscriptionPath.location().region(), settingsBuilder));
            return SubscriberBuilder.newBuilder()
                .setSubscriptionPath(subscriptionPath)
                .setPartition(Partition.of(0))
                .setContext(context)
                .setServiceClient(serviceClient)
                .setMessageConsumer(consumer)
                .build();
          } catch (IOException e) {
            throw new IllegalStateException("Failed to create subscriber service.", e);
          }
        };

    BlockingPullSubscriberImpl subscriber =
        new BlockingPullSubscriberImpl(
            subscriberFactory,
            FlowControlSettings.builder()
                .setMessagesOutstanding(Long.MAX_VALUE)
                .setBytesOutstanding(Long.MAX_VALUE)
                .build(),
            SeekRequest.newBuilder()
                .setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR)
                .build());

    while (true) {
      long start = System.currentTimeMillis();
      subscriber.onData().get();
      subscriber
          .messageIfAvailable()
          .ifPresent(
              (msg) -> {
                long sum = counter.addAndGet(1);
                if (sum % 10000 == 0) {
                  System.out.println("Received " + sum + " messages.");
                }
              });
      long finish = System.currentTimeMillis();
      long latencyInsideNextThis = finish - start;
      if (latencyInsideNextThis > 100) {
        log.atWarning().log(
            "[MJ] latency inside next: " + latencyInsideNextThis + " larger than 100");
      }
    }
  }
}
