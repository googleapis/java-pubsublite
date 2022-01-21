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

// [START pubsublite_create_reservation]

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.proto.Reservation;

public class CreateReservationExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    long projectNumber = Long.parseLong("123456789");
    String cloudRegion = "your-cloud-region";
    String reservationId = "your-topic-id";
    // Each unit of throughput capacity supports up to 1 MiB/s of published messages or
    // 2 MiB/s of subscribed messages.
    int throughputCapacity = 4;

    createReservationExample(projectNumber, cloudRegion, reservationId, throughputCapacity);
  }

  public static void createReservationExample(
      long projectNumber, String cloudRegion, String reservationId, int throughputCapacity)
      throws Exception {

    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    Reservation reservation =
        Reservation.newBuilder()
            .setName(reservationPath.toString())
            .setThroughputCapacity(throughputCapacity)
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Reservation response = adminClient.createReservation(reservation).get();
      System.out.println(response.getAllFields() + " created successfully.");
    }
  }
}
// [END pubsublite_create_reservation]
