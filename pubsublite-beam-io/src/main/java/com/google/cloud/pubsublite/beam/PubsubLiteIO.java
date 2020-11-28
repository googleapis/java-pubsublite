/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.beam;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.AdminClientSettings;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.BufferingPullSubscriber;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.CursorClientSettings;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.internal.wire.PartitionCountWatcher;
import com.google.cloud.pubsublite.internal.wire.PartitionCountWatcherImpl;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.base.Ticker;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Reshuffle;
import org.apache.beam.sdk.transforms.SerializableBiFunction;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.util.Sleeper;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.PInput;
import org.apache.beam.sdk.values.POutput;

public final class PubsubLiteIO {
  private PubsubLiteIO() {}

  private static <InT extends PInput, OutT extends POutput> PTransform<InT, OutT> toTransform(
      SerializableFunction<InT, OutT> fn, String name) {
    return new PTransform<InT, OutT>(name) {
      @Override
      public OutT expand(InT input) {
        return fn.apply(input);
      }
    };
  }

  private static @Nullable Long getMaxMem(PipelineOptions options) {
    if (!(options instanceof PubsubLitePipelineOptions)) return null;
    PubsubLitePipelineOptions psOptions = (PubsubLitePipelineOptions) options;
    if (!psOptions.getPubsubLiteSubscriberWorkerMemoryLimiterEnabled()) return null;
    return psOptions.getPubsubLiteSubscribeWorkerMemoryLimit();
  }

  // Read messages from Pub/Sub Lite. These messages may contain duplicates if the publisher
  // retried, which the PubsubLiteIO write method will do. Use the dedupe transform to remove these
  // duplicates.
  public static PTransform<PBegin, PCollection<SequencedMessage>> read(SubscriberOptions options) {
    TopicPath topic;
    try (AdminClient client =
        AdminClient.create(
            AdminClientSettings.newBuilder()
                .setRegion(options.subscriptionPath().location().region())
                .build())) {
      topic = TopicPath.parse(client.getSubscription(options.subscriptionPath()).get().getTopic());
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
    PTransform<PBegin, PCollection<TopicPath>> fixedTopic =
        toTransform(begin -> begin.apply(Create.of(topic)), "Topic");
    SerializableBiFunction<TopicPath, Consumer<Long>, PartitionCountWatcher>
        partitionCountWatcherFactory =
            (path, consumer) ->
                new PartitionCountWatcherImpl(
                    path,
                    AdminClient.create(
                        AdminClientSettings.newBuilder()
                            .setRegion(path.location().region())
                            .build()),
                    consumer,
                    Duration.ofMinutes(1));
    PTransform<PCollection<TopicPath>, PCollection<Partition>> loadPartitions =
        toTransform(
            topics -> topics.apply(ParDo.of(new PubsubLiteTopicSdf(partitionCountWatcherFactory))),
            "LoadPartitions");
    SerializableFunction<
            Long, SerializableBiFunction<Partition, Offset, PullSubscriber<SequencedMessage>>>
        subscriberFactoryFactory =
            (@Nullable Long maxMem) ->
                (partition, offset) ->
                    new MemoryLimitingPullSubscriber(
                        (newSeek, flow) -> {
                          System.err.println("New subscriber for: " + partition);
                          return new BufferingPullSubscriber(
                              options.getPartitionSubscriberFactory(partition), flow, newSeek);
                        },
                        PerServerMemoryLimiter.getLimiter(Optional.ofNullable(maxMem)),
                        options.flowControlSettings(),
                        SeekRequest.newBuilder()
                            .setCursor(Cursor.newBuilder().setOffset(offset.value()))
                            .build());
    PTransform<PCollection<Partition>, PCollection<SequencedMessage>> loadMessages =
        toTransform(
            partitions ->
                partitions.apply(
                    ParDo.of(
                        new PubsubLitePartitionSdf(
                            org.joda.time.Duration.standardMinutes(2),
                            subscriberFactoryFactory.apply(
                                getMaxMem(partitions.getPipeline().getOptions())),
                            options::getPartitionCommitter,
                            () -> Sleeper.DEFAULT,
                            partition ->
                                new InitialOffsetReaderImpl(
                                    CursorClient.create(
                                        CursorClientSettings.newBuilder()
                                            .setRegion(topic.location().region())
                                            .build()),
                                    options.subscriptionPath(),
                                    partition),
                            (partition, range) ->
                                new PubsubLiteOffsetRangeTracker(
                                    range,
                                    new RateLimitedSimpleTopicBacklogReader(
                                        new SimpleTopicBacklogReaderImpl(
                                            options.topicBacklogReaderSettings().instantiate(),
                                            partition),
                                        Ticker.systemTicker()),
                                    partition)))),
            "LoadMessages");
    return toTransform(
        begin ->
            begin
                .apply(fixedTopic)
                .apply(loadPartitions)
                // Prevent fusion of partitions, ensuring they can be spread to different machines.
                .apply(Reshuffle.viaRandomKey())
                .apply(loadMessages),
        "read");
  }

  // Remove duplicates from the PTransform from a read. Assumes by default that the uuids were
  // added by a call to PubsubLiteIO.addUuids().
  public static PTransform<PCollection<SequencedMessage>, PCollection<SequencedMessage>>
      deduplicate(UuidDeduplicationOptions options) {
    return new UuidDeduplicationTransform(options);
  }

  // Add Uuids to to-be-published messages that ensures that uniqueness is maintained.
  public static PTransform<PCollection<Message>, PCollection<Message>> addUuids() {
    return new AddUuidsTransform();
  }

  // Write messages to Pub/Sub Lite.
  public static PTransform<PCollection<Message>, PDone> write(PublisherOptions options) {
    return toTransform(
        input -> {
          PubsubLiteSink sink = new PubsubLiteSink(options);
          input.apply("Write", ParDo.of(sink));
          return PDone.in(input.getPipeline());
        },
        "PubsubLiteIO");
  }
}
