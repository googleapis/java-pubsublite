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

// [START pubsublite_v1_generated_cursorserviceclient_listpartitioncursors_subscriptionname_sync]
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.v1.CursorServiceClient;

public class SyncListPartitionCursorsSubscriptionname {

  public static void main(String[] args) throws Exception {
    syncListPartitionCursorsSubscriptionname();
  }

  public static void syncListPartitionCursorsSubscriptionname() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
      SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
      for (PartitionCursor element :
          cursorServiceClient.listPartitionCursors(parent).iterateAll()) {
        // doThingsWith(element);
      }
    }
  }
}
// [END pubsublite_v1_generated_cursorserviceclient_listpartitioncursors_subscriptionname_sync]
