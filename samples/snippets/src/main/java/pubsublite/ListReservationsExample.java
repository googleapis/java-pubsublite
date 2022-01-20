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

// [START pubsublite_list_reservations]

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.LocationPath;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.proto.Reservation;
import java.util.List;

public class ListReservationsExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    long projectNumber = Long.parseLong("123456789");
    String cloudRegion = "your-cloud-region";

    listReservationsExample(projectNumber, cloudRegion);
  }

  public static void listReservationsExample(long projectNumber, String cloudRegion)
      throws Exception {

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    LocationPath locationPath =
        LocationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      List<Reservation> reservations = adminClient.listReservations(locationPath).get();
      for (Reservation reservation : reservations) {
        System.out.println(reservation.getAllFields());
      }
      System.out.println(reservations.size() + " reservation(s) listed in " + locationPath + ".");
    }
  }
}
// [END pubsublite_list_reservations]
