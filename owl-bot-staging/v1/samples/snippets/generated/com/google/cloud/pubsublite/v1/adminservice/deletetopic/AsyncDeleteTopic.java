/*
 * Copyright 2024 Google LLC
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

// [START pubsublite_v1_generated_AdminService_DeleteTopic_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.protobuf.Empty;

public class AsyncDeleteTopic {

  public static void main(String[] args) throws Exception {
    asyncDeleteTopic();
  }

  public static void asyncDeleteTopic() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      DeleteTopicRequest request =
          DeleteTopicRequest.newBuilder()
              .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .build();
      ApiFuture<Empty> future = adminServiceClient.deleteTopicCallable().futureCall(request);
      // Do something.
      future.get();
    }
  }
}
// [END pubsublite_v1_generated_AdminService_DeleteTopic_async]
