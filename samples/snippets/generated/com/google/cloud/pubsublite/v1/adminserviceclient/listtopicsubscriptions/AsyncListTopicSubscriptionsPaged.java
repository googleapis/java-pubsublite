/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.v1.samples;

// [START pubsublite_v1_generated_adminserviceclient_listtopicsubscriptions_paged_async]
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.common.base.Strings;

public class AsyncListTopicSubscriptionsPaged {

  public static void main(String[] args) throws Exception {
    asyncListTopicSubscriptionsPaged();
  }

  public static void asyncListTopicSubscriptionsPaged() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      ListTopicSubscriptionsRequest request =
          ListTopicSubscriptionsRequest.newBuilder()
              .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPageSize(883849137)
              .setPageToken("pageToken873572522")
              .build();
      while (true) {
        ListTopicSubscriptionsResponse response =
            adminServiceClient.listTopicSubscriptionsCallable().call(request);
        for (String element : response.getSubscriptionsList()) {
          // doThingsWith(element);
        }
        String nextPageToken = response.getNextPageToken();
        if (!Strings.isNullOrEmpty(nextPageToken)) {
          request = request.toBuilder().setPageToken(nextPageToken).build();
        } else {
          break;
        }
      }
    }
  }
}
// [END pubsublite_v1_generated_adminserviceclient_listtopicsubscriptions_paged_async]
