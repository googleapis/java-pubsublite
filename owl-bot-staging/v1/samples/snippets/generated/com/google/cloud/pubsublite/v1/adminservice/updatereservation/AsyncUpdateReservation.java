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

// [START pubsublite_v1_generated_AdminService_UpdateReservation_async]
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.protobuf.FieldMask;

public class AsyncUpdateReservation {

  public static void main(String[] args) throws Exception {
    asyncUpdateReservation();
  }

  public static void asyncUpdateReservation() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      UpdateReservationRequest request =
          UpdateReservationRequest.newBuilder()
              .setReservation(Reservation.newBuilder().build())
              .setUpdateMask(FieldMask.newBuilder().build())
              .build();
      ApiFuture<Reservation> future =
          adminServiceClient.updateReservationCallable().futureCall(request);
      // Do something.
      Reservation response = future.get();
    }
  }
}
// [END pubsublite_v1_generated_AdminService_UpdateReservation_async]
