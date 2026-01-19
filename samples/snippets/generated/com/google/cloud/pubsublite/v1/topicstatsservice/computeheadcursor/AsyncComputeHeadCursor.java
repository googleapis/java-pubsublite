/*
 * Copyright 2026 Google LLC
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

// [START pubsublite_v1_generated_TopicStatsService_ComputeHeadCursor_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;

public class AsyncComputeHeadCursor {

  public static void main(String[] args) throws Exception {
    asyncComputeHeadCursor();
  }

  public static void asyncComputeHeadCursor() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
      ComputeHeadCursorRequest request =
          ComputeHeadCursorRequest.newBuilder()
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartition(-1799810326)
              .build();
      ApiFuture<ComputeHeadCursorResponse> future =
          topicStatsServiceClient.computeHeadCursorCallable().futureCall(request);
      // Do something.
      ComputeHeadCursorResponse response = future.get();
    }
  }
}
// [END pubsublite_v1_generated_TopicStatsService_ComputeHeadCursor_async]
