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

// [START pubsublite_v1_generated_adminserviceclient_deletereservation_sync]
import com.google.cloud.pubsublite.proto.DeleteReservationRequest;
import com.google.cloud.pubsublite.proto.ReservationName;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.protobuf.Empty;

public class SyncDeleteReservation {

  public static void main(String[] args) throws Exception {
    syncDeleteReservation();
  }

  public static void syncDeleteReservation() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      DeleteReservationRequest request =
          DeleteReservationRequest.newBuilder()
              .setName(ReservationName.of("[PROJECT]", "[LOCATION]", "[RESERVATION]").toString())
              .build();
      adminServiceClient.deleteReservation(request);
    }
  }
}
// [END pubsublite_v1_generated_adminserviceclient_deletereservation_sync]
