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

package pubsublite.beam;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.beam.PubsubLiteIO;
import com.google.cloud.pubsublite.beam.SubscriberOptions;
import com.google.cloud.pubsublite.beam.UuidDeduplicationOptions;
import com.google.cloud.pubsublite.proto.SequencedMessage;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

public class ReadMessagesExample {
  public static PCollection<SequencedMessage> readMessagesExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String subscriptionId,
      Pipeline pipeline) {
    SubscriptionPath subscriptionPath =
        SubscriptionPath.newBuilder()
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setProject(ProjectNumber.of(projectNumber))
            .setName(SubscriptionName.of(subscriptionId))
            .build();
    PCollection<SequencedMessage> messages =
        pipeline.apply(
            "readMessages",
            PubsubLiteIO.read(
                SubscriberOptions.newBuilder()
                    .setSubscriptionPath(subscriptionPath)
                    // setMinBundleTimeout INTENDED FOR TESTING ONLY
                    // This sacrifices efficiency to make tests run faster. Do not use this in a
                    // real pipeline!
                    .setMinBundleTimeout(Duration.standardSeconds(5))
                    .build()));
    // Deduplicate messages based on the uuids added in PubsubLiteIO.addUuids() when writing.
    return messages.apply(
        "dedupeMessages", PubsubLiteIO.deduplicate(UuidDeduplicationOptions.newBuilder().build()));
  }
}
