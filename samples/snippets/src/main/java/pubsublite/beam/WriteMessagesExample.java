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
    TopicPath topicPath =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
            .setName(TopicName.of(topicId))
            .build();
    PCollection<Integer> indexes =
        pipeline.apply(
            "createIndexes",
            Create.of(IntStream.range(0, messageCount).boxed().collect(Collectors.toList())));
    PCollection<PubSubMessage> messages =
        indexes.apply(
            "createMessages",
            MapElements.via(
                new SimpleFunction<Integer, PubSubMessage>(
                    index ->
                        Message.builder()
                            .setData(ByteString.copyFromUtf8(index.toString()))
                            .build()
                            .toProto()) {}));
    // Add UUIDs to messages for later deduplication.
    messages = messages.apply("addUuids", PubsubLiteIO.addUuids());
    messages.apply(
        "writeMessages",
        PubsubLiteIO.write(PublisherOptions.newBuilder().setTopicPath(topicPath).build()));
  }
}
