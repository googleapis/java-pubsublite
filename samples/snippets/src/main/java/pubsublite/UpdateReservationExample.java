/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pubsublite;

// [START pubsublite_update_reservation]

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.proto.Reservation;

public class UpdateReservationExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    long projectNumber = Long.parseLong("123456789");
    String cloudRegion = "your-cloud-region";
    String reservationId = "your-reservation-id";
    // Must be a positive integer.
    int throughputCapacity = 8;

    updateReservationExample(projectNumber, cloudRegion, reservationId, throughputCapacity);
  }

  public static void updateReservationExample(
      long projectNumber, String cloudRegion, String reservationId, int throughputCapacity)
      throws Exception {

    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    com.google.protobuf.FieldMask fieldMask =
        com.google.protobuf.FieldMask.newBuilder().addPaths("throughput_capacity").build();

    Reservation reservation =
        Reservation.newBuilder()
            .setName(reservationPath.toString())
            .setThroughputCapacity(throughputCapacity)
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Reservation reservationBeforeUpdate = adminClient.getReservation(reservationPath).get();
      System.out.println("Before update: " + reservationBeforeUpdate.getAllFields());

      Reservation reservationAfterUpdate =
          adminClient.updateReservation(reservation, fieldMask).get();
      System.out.println("After update: " + reservationAfterUpdate.getAllFields());
    }
  }
}
// [END pubsublite_update_reservation]
