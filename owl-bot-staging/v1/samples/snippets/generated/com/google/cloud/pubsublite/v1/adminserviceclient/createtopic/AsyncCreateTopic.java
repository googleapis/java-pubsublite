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

// [START pubsublite_v1_generated_adminserviceclient_createtopic_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.LocationName;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.v1.AdminServiceClient;

public class AsyncCreateTopic {

  public static void main(String[] args) throws Exception {
    asyncCreateTopic();
  }

  public static void asyncCreateTopic() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      CreateTopicRequest request =
          CreateTopicRequest.newBuilder()
              .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
              .setTopic(Topic.newBuilder().build())
              .setTopicId("topicId-1139259734")
              .build();
      ApiFuture<Topic> future = adminServiceClient.createTopicCallable().futureCall(request);
      // Do something.
      Topic response = future.get();
    }
  }
}
// [END pubsublite_v1_generated_adminserviceclient_createtopic_async]
