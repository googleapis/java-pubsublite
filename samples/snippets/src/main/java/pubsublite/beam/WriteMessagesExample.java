package pubsublite.beam;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.beam.PublisherOptions;
import com.google.cloud.pubsublite.beam.PubsubLiteIO;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.protobuf.ByteString;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;

public class WriteMessagesExample {
  public static void writeMessagesExample(
      String cloudRegion,
      char zoneId,
      long projectNumber,
      String topicId,
      int messageCount,
      Pipeline pipeline) {
    PCollection<Integer> indexes =
        pipeline.apply(
            "createIndexes",
            Create.of(IntStream.range(0, messageCount).boxed().collect(Collectors.toList())));
    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setName(TopicName.of(topicId))
            .build();
    PCollection<PubSubMessage> messages =
        indexes.apply(
            "createMessages",
            MapElements.via(
                new SimpleFunction<Integer, PubSubMessage>() {
                  @Override
                  public PubSubMessage apply(Integer input) {
                    return Message.builder()
                        .setData(ByteString.copyFromUtf8(input.toString()))
                        .build()
                        .toProto();
                  }
                }));
    messages = messages.apply("addUuids", PubsubLiteIO.addUuids());
    messages.apply(
        "writeMessages",
        PubsubLiteIO.write(PublisherOptions.newBuilder().setTopicPath(topicPath).build()));
  }
}
