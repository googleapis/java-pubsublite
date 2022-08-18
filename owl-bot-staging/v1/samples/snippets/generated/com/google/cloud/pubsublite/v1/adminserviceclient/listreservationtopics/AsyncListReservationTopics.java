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

// [START pubsublite_v1_generated_adminserviceclient_listreservationtopics_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.ListReservationTopicsRequest;
import com.google.cloud.pubsublite.proto.ReservationName;
import com.google.cloud.pubsublite.v1.AdminServiceClient;

public class AsyncListReservationTopics {

  public static void main(String[] args) throws Exception {
    asyncListReservationTopics();
  }

  public static void asyncListReservationTopics() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      ListReservationTopicsRequest request =
          ListReservationTopicsRequest.newBuilder()
              .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
              .setPageSize(883849137)
              .setPageToken("pageToken873572522")
              .build();
      ApiFuture<String> future =
          adminServiceClient.listReservationTopicsPagedCallable().futureCall(request);
      // Do something.
      for (String element : future.get().iterateAll()) {
        // doThingsWith(element);
      }
    }
  }
}
// [END pubsublite_v1_generated_adminserviceclient_listreservationtopics_async]
