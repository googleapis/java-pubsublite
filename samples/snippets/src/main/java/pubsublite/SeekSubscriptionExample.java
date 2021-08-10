/*
 * Copyright 2021 Google LLC
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

// [START pubsublite_seek_subscription]
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.BacklogLocation;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SeekTarget;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;

public class SeekSubscriptionExample {

  public static void main(String... args) throws Exception {
    // TODO(developer): Replace these variables before running the sample.
    String cloudRegion = "your-cloud-region";
    char zoneId = 'b';
    // Choose an existing subscription.
    String subscriptionId = "your-subscription-id";
    long projectNumber = Long.parseLong("123456789");

    // Choose a target location within the message backlog to seek a subscription to.
    // Possible values for SeekTarget:
    // - SeekTarget.of(BacklogLocation.BEGINNING): replays from the beginning of all retained
    //   messages.
    // - SeekTarget.of(BacklogLocation.END): skips past all current published messages.
    // - SeekTarget.ofPublishTime(<timestamp>): delivers messages with publish time greater than
    //   or equal to the specified timestamp.
    // - SeekTarget.ofEventTime(<timestamp>): seeks to the first message with event time greater
    //   than or equal to the specified timestamp.
    SeekTarget target = SeekTarget.of(BacklogLocation.BEGINNING);

    // Optional: Wait for the seek operation to complete, which indicates when subscribers for all
    // partitions are receiving messages from the seek target. If subscribers are offline, the
    // operation will complete once they are online.
    boolean waitForOperation = false;

    seekSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId, target, waitForOperation);
  }

  public static void seekSubscriptionExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String subscriptionId,
      SeekTarget target,
      boolean waitForOperation)
      throws Exception {

    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setProject(ProjectNumber.of(projectNumber))
            .setName(SubscriptionName.of(subscriptionId))
            .build();

    AdminClientSettings adminClientSettings =
        AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

    try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
      // Initiate an out-of-band seek for a subscription to the specified target. If an operation
      // is returned, the seek has been successfully registered and will eventually propagate to
      // subscribers.
      OperationFuture<SeekSubscriptionResponse, OperationMetadata> seekFuture =
          adminClient.seekSubscription(subscriptionPath, target);
      System.out.println("Seek operation " + seekFuture.getName() + " initiated successfully.");

      if (waitForOperation) {
        System.out.println("Waiting for operation to complete...");
        seekFuture.get();
        System.out.println("Operation completed. Metadata:\n" + seekFuture.getMetadata().get());
      }
    }
  }
}
// [END pubsublite_seek_subscription]
