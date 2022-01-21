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

// [START pubsublite_delete_reservation]
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.ReservationName;
import com.google.cloud.pubsublite.ReservationPath;
import java.util.concurrent.ExecutionException;

public class DeleteReservationExample {
  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    long projectNumber = Long.parseLong("123456789");
    String cloudRegion = "your-cloud-region";
    String reservationId = "your-reservation-id";

    deleteReservationExample(projectNumber, cloudRegion, reservationId);
  }

  public static void deleteReservationExample(
      long projectNumber, String cloudRegion, String reservationId) throws Exception {

    ReservationPath reservationPath =
        ReservationPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudRegion.of(cloudRegion))
            .setName(ReservationName.of(reservationId))
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    // If a reservation has topics attached, you must delete the topics before deleting
    // the reservation.
    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      adminClient.deleteReservation(reservationPath).get();
      System.out.println(reservationPath + " deleted successfully.");
    } catch (ExecutionException e) {
      try {
        throw e.getCause();
      } catch (NotFoundException notFound) {
        System.out.println("This reservation is not found.");
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
// [END pubsublite_delete_reservation]
