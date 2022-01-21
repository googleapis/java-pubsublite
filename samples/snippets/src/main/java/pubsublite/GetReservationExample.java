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

// [START pubsublite_get_reservation]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import com.google.cloud.pubsublite.proto.Reservation;
import java.util.concurrent.ExecutionException;

public class GetReservationExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    long projectNumber = Long.parseLong("123456789");
    String cloudRegion = "your-cloud-region";
    String reservationId = "your-reservation-id";

    getReservationExample(projectNumber, cloudRegion, reservationId);
  }

  public static void getReservationExample(
      long projectNumber, String cloudRegion, String reservationId) throws Exception {
    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      Reservation response = adminClient.getReservation(reservationPath).get();
      long throughputCapacity = response.getThroughputCapacity();
      System.out.println(
          response.getAllFields()
              + "\nhas "
              + throughputCapacity
              + " units of throughput capacity.");
    } catch (ExecutionException e) {
      System.err.println(e.getCause());
    }
  }
}
// [END pubsublite_get_reservation]
