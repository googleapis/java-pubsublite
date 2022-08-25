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

// [START pubsublite_v1_generated_topicstatsserviceclient_computemessagestats_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.v1.TopicStatsServiceClient;

public class AsyncComputeMessageStats {

  public static void main(String[] args) throws Exception {
    asyncComputeMessageStats();
  }

  public static void asyncComputeMessageStats() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
      ComputeMessageStatsRequest request =
          ComputeMessageStatsRequest.newBuilder()
              .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
              .setPartition(-1799810326)
              .setStartCursor(Cursor.newBuilder().build())
              .setEndCursor(Cursor.newBuilder().build())
              .build();
      ApiFuture<ComputeMessageStatsResponse> future =
          topicStatsServiceClient.computeMessageStatsCallable().futureCall(request);
      // Do something.
      ComputeMessageStatsResponse response = future.get();
    }
  }
}
// [END pubsublite_v1_generated_topicstatsserviceclient_computemessagestats_async]