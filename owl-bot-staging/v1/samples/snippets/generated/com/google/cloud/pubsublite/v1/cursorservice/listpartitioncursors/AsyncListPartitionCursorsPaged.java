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

// [START pubsublite_v1_generated_CursorService_ListPartitionCursors_Paged_async]
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.common.base.Strings;

public class AsyncListPartitionCursorsPaged {

  public static void main(String[] args) throws Exception {
    asyncListPartitionCursorsPaged();
  }

  public static void asyncListPartitionCursorsPaged() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
      ListPartitionCursorsRequest request =
          ListPartitionCursorsRequest.newBuilder()
              .setParent(
                  SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
              .setPageSize(883849137)
              .setPageToken("pageToken873572522")
              .build();
      while (true) {
        ListPartitionCursorsResponse response =
            cursorServiceClient.listPartitionCursorsCallable().call(request);
        for (PartitionCursor element : response.getPartitionCursorsList()) {
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
// [END pubsublite_v1_generated_CursorService_ListPartitionCursors_Paged_async]
