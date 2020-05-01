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

// [START pubsublite_list_subscriptions_in_topic]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientBuilder;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ListSubscriptionsInTopicExample {

  public static void runListSubscriptionsInTopicExample() throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String CLOUD_REGION = "Your Cloud Region";
    char ZONE = 'b';
    long PROJECT_NUMBER = 123456789L;
    String TOPIC_NAME = "Your Lite Topic Name";

    ListSubscriptionsInTopicExample.listSubscriptionsInTopicExample(
        CLOUD_REGION, ZONE, PROJECT_NUMBER, TOPIC_NAME);
  }

  public static void listSubscriptionsInTopicExample(
      String CLOUD_REGION, char ZONE, long PROJECT_NUMBER, String TOPIC_NAME) throws Exception {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    try {
      CloudRegion cloudRegion = CloudRegion.create(CLOUD_REGION);
      CloudZone zone = CloudZone.create(cloudRegion, ZONE);
      ProjectNumber projectNum = ProjectNumber.of(PROJECT_NUMBER);
      TopicName topicName = TopicName.of(TOPIC_NAME);

      TopicPath topicPath =
          TopicPaths.newBuilder()
              .setProjectNumber(projectNum)
              .setZone(zone)
              .setTopicName(topicName)
              .build();

      // Create admin client
      AdminClient adminClient =
          AdminClientBuilder.builder().setRegion(cloudRegion).setExecutor(executor).build();

      List<SubscriptionPath> subscriptionPaths =
          adminClient.listTopicSubscriptions(topicPath).get();
      for (SubscriptionPath subscription : subscriptionPaths) {
        System.out.println(subscription.value());
      }
      System.out.println(subscriptionPaths.size() + " subscription(s) listed.");

    } catch (StatusRuntimeException e) {
      System.out.println("Failed to list subscriptions in the topic: " + e.toString());
    } finally {
      executor.shutdown();
      executor.awaitTermination(30, TimeUnit.SECONDS);
    }
  }
}
// [END pubsublite_list_subscriptions_in_topic]
