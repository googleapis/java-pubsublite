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

// [START pubsublite_v1_generated_adminserviceclient_updatereservation_reservationfieldmask_sync]
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.v1.AdminServiceClient;
import com.google.protobuf.FieldMask;

public class SyncUpdateReservationReservationFieldmask {

  public static void main(String[] args) throws Exception {
    syncUpdateReservationReservationFieldmask();
  }

  public static void syncUpdateReservationReservationFieldmask() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      Reservation reservation = Reservation.newBuilder().build();
      FieldMask updateMask = FieldMask.newBuilder().build();
      Reservation response = adminServiceClient.updateReservation(reservation, updateMask);
    }
  }
}
// [END pubsublite_v1_generated_adminserviceclient_updatereservation_reservationfieldmask_sync]
