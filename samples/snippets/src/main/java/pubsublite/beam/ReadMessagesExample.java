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
    return messages.apply(
        "dedupeMessages", PubsubLiteIO.deduplicate(UuidDeduplicationOptions.newBuilder().build()));
  }
}
