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

package com.google.cloud.pubsublite.kafka;

import static com.google.cloud.pubsublite.kafka.KafkaExceptionUtils.toKafka;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.wire.Assigner;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.PartitionAssignmentReceiver;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.GoogleLogger;
import io.grpc.StatusException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.UnsupportedVersionException;

/**
 * A class that uses a SingleSubscriptionConsumer to remove the duplicate methods from the kafka
 * consumer.
 *
 * <p>This also filters methods that Pub/Sub Lite will not implement.
 */
class PubsubLiteConsumer implements Consumer<byte[], byte[]> {
  private static final Duration INFINITE_DURATION = Duration.ofMillis(Long.MAX_VALUE);
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();
  private final SubscriptionPath subscriptionPath;
  private final TopicPath topicPath;
  private final long partitionCount;
  private final ConsumerFactory consumerFactory;
  private final AssignerFactory assignerFactory;
  private final CursorClient cursorClient;
  private Optional<Assigner> assigner = Optional.empty();
  private Optional<SingleSubscriptionConsumer> consumer = Optional.empty();

  PubsubLiteConsumer(
      SubscriptionPath subscriptionPath,
      TopicPath topicPath,
      long partitionCount,
      ConsumerFactory consumerFactory,
      AssignerFactory assignerFactory,
      CursorClient cursorClient) {
    this.subscriptionPath = subscriptionPath;
    this.topicPath = topicPath;
    this.partitionCount = partitionCount;
    this.consumerFactory = consumerFactory;
    this.assignerFactory = assignerFactory;
    this.cursorClient = cursorClient;
  }

  private TopicPartition toTopicPartition(Partition partition) {
    return new TopicPartition(topicPath.toString(), (int) partition.value());
  }

  private SingleSubscriptionConsumer requireValidConsumer() {
    if (!consumer.isPresent()) {
      throw new IllegalStateException("Neither subscribe nor assign has been called.");
    }
    return consumer.get();
  }

  @Override
  public Set<TopicPartition> assignment() {
    return requireValidConsumer().assignment().stream()
        .map(this::toTopicPartition)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<String> subscription() {
    if (consumer.isPresent()) {
      return ImmutableSet.of(topicPath.toString());
    }
    return ImmutableSet.of();
  }

  private static class NoOpRebalanceListener implements ConsumerRebalanceListener {
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {}

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {}
  }

  @Override
  public void subscribe(Pattern pattern) {
    subscribe(pattern, new NoOpRebalanceListener());
  }

  @Override
  public void subscribe(Pattern pattern, ConsumerRebalanceListener consumerRebalanceListener) {
    throw new UnsupportedOperationException(
        "Pattern assignment is not available for Pub/Sub Lite.");
  }

  private void checkTopic(String topic) {
    try {
      TopicPath path = TopicPath.parse(topic);
      if (!path.equals(topicPath)) {
        throw new UnsupportedOperationException(
            "Pub/Sub Lite consumers may only interact with the one topic they are configured for.");
      }
    } catch (StatusException e) {
      throw toKafka(e);
    }
  }

  private Partition checkTopicGetPartition(TopicPartition topicPartition) {
    checkTopic(topicPartition.topic());
    try {
      return Partition.of(topicPartition.partition());
    } catch (StatusException e) {
      throw toKafka(e);
    }
  }

  private PartitionAssignmentReceiver newAssignmentReceiver(ConsumerRebalanceListener listener) {
    AtomicReference<Set<Partition>> lastPartitions = new AtomicReference<>(ImmutableSet.of());
    return newAssignment -> {
      Set<Partition> previousAssignment = lastPartitions.get();
      Set<Partition> removed = new HashSet<>(previousAssignment);
      removed.removeAll(newAssignment);
      Set<Partition> added = new HashSet<>(newAssignment);
      added.removeAll(previousAssignment);
      if (!removed.isEmpty()) {
        listener.onPartitionsLost(
            removed.stream().map(this::toTopicPartition).collect(Collectors.toSet()));
      }
      if (!added.isEmpty()) {
        listener.onPartitionsAssigned(
            added.stream().map(this::toTopicPartition).collect(Collectors.toSet()));
      }
      consumer.get().setAssignment(newAssignment);
      lastPartitions.set(newAssignment);
    };
  }

  @Override
  public void subscribe(Collection<String> collection) {
    subscribe(collection, new NoOpRebalanceListener());
  }

  @Override
  public void subscribe(
      Collection<String> collection, ConsumerRebalanceListener consumerRebalanceListener) {
    if (collection.size() != 1) {
      throw new UnsupportedOperationException(
          "Subscribing to multiple topics is not available for Pub/Sub Lite.");
    }
    checkTopic(collection.iterator().next());
    if (consumer.isPresent()) {
      if (assigner.isPresent()) {
        // No-op: we can only subscribe to one topic and we already are.
        return;
      }
      throw new IllegalStateException("Called subscribe after calling assign.");
    }
    consumer = Optional.of(consumerFactory.newConsumer());
    try {
      assigner = Optional.of(assignerFactory.New(newAssignmentReceiver(consumerRebalanceListener)));
    } catch (StatusException e) {
      throw toKafka(e);
    }
  }

  @Override
  public void assign(Collection<TopicPartition> collection) {
    if (collection.isEmpty()) {
      unsubscribe();
      return;
    }
    if (assigner.isPresent()) {
      throw new IllegalStateException("Called assign after calling subscribe.");
    }
    Set<Partition> partitions =
        collection.stream().map(this::checkTopicGetPartition).collect(Collectors.toSet());
    if (!consumer.isPresent()) {
      consumer = Optional.of(consumerFactory.newConsumer());
    }
    consumer.get().setAssignment(partitions);
  }

  @Override
  public void unsubscribe() {
    assigner.ifPresent(instance -> instance.stopAsync().awaitTerminated());
    assigner = Optional.empty();
    consumer.ifPresent(instance -> instance.close(INFINITE_DURATION));
    consumer = Optional.empty();
  }

  private static Duration toDuration(long l, TimeUnit timeUnit) {
    return Duration.ofMillis(TimeUnit.MILLISECONDS.convert(l, timeUnit));
  }

  @Override
  public ConsumerRecords<byte[], byte[]> poll(long l) {
    return poll(Duration.ofMillis(l));
  }

  @Override
  public ConsumerRecords<byte[], byte[]> poll(Duration timeout) {
    return requireValidConsumer().poll(timeout);
  }

  Map<Partition, Offset> checkAndTransformOffsets(Map<TopicPartition, OffsetAndMetadata> map) {
    ImmutableMap.Builder<Partition, Offset> output = ImmutableMap.builder();
    try {
      map.forEach(
          ExtractStatus.rethrowAsRuntime(
              (topicPartition, offsetAndMetadata) -> {
                output.put(
                    checkTopicGetPartition(topicPartition), Offset.of(offsetAndMetadata.offset()));
              }));
    } catch (Throwable t) {
      throw toKafka(t);
    }
    return output.build();
  }

  @Override
  public void commitSync(Map<TopicPartition, OffsetAndMetadata> map) {
    commitSync(map, INFINITE_DURATION);
  }

  @Override
  public void commitSync(Map<TopicPartition, OffsetAndMetadata> map, Duration duration) {
    try {
      requireValidConsumer()
          .commit(checkAndTransformOffsets(map))
          .get(duration.toMillis(), TimeUnit.MILLISECONDS);
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public void commitAsync(
      Map<TopicPartition, OffsetAndMetadata> map, OffsetCommitCallback offsetCommitCallback) {
    ApiFutures.addCallback(
        requireValidConsumer().commit(checkAndTransformOffsets(map)),
        new ApiFutureCallback<Void>() {
          @Override
          public void onFailure(Throwable throwable) {
            offsetCommitCallback.onComplete(null, toKafka(throwable));
          }

          @Override
          public void onSuccess(Void result) {
            offsetCommitCallback.onComplete(map, null);
          }
        });
  }

  @Override
  public void commitSync() {
    commitSync(INFINITE_DURATION);
  }

  @Override
  public void commitSync(Duration duration) {
    try {
      requireValidConsumer().commitAll().get(duration.toMillis(), TimeUnit.MILLISECONDS);
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public void commitAsync(OffsetCommitCallback offsetCommitCallback) {
    ApiFutures.addCallback(
        requireValidConsumer().commitAll(),
        new ApiFutureCallback<Map<Partition, Offset>>() {
          @Override
          public void onFailure(Throwable throwable) {
            offsetCommitCallback.onComplete(null, toKafka(throwable));
          }

          @Override
          public void onSuccess(Map<Partition, Offset> map) {
            ImmutableMap.Builder<TopicPartition, OffsetAndMetadata> result = ImmutableMap.builder();
            map.forEach(
                (partition, offset) ->
                    result.put(toTopicPartition(partition), new OffsetAndMetadata(offset.value())));
            offsetCommitCallback.onComplete(result.build(), null);
          }
        });
  }

  @Override
  public void commitAsync() {
    commitAsync(
        (map, e) -> {
          if (e != null) {
            logger.atWarning().withCause(e).log("Failed to commit offsets.");
          }
        });
  }

  @Override
  public void seek(TopicPartition topicPartition, long l) {
    Partition partition = checkTopicGetPartition(topicPartition);
    requireValidConsumer()
        .doSeek(
            partition,
            SeekRequest.newBuilder().setCursor(Cursor.newBuilder().setOffset(l)).build());
  }

  @Override
  public void seek(TopicPartition topicPartition, OffsetAndMetadata offsetAndMetadata) {
    seek(topicPartition, offsetAndMetadata.offset());
  }

  @Override
  public void seekToBeginning(Collection<TopicPartition> collection) {
    collection.forEach(
        topicPartition ->
            requireValidConsumer()
                .doSeek(
                    checkTopicGetPartition(topicPartition),
                    SeekRequest.newBuilder()
                        .setCursor(Cursor.newBuilder().setOffset(0).build())
                        .build()));
  }

  @Override
  public void seekToEnd(Collection<TopicPartition> collection) {
    collection.forEach(
        topicPartition ->
            requireValidConsumer()
                .doSeek(
                    checkTopicGetPartition(topicPartition),
                    SeekRequest.newBuilder().setNamedTarget(NamedTarget.HEAD).build()));
  }

  @Override
  public long position(TopicPartition topicPartition) {
    return position(topicPartition, INFINITE_DURATION);
  }

  @Override
  public long position(TopicPartition partition, Duration timeout) {
    Partition litePartition = checkTopicGetPartition(partition);
    Optional<Long> consumerPosition = requireValidConsumer().position(litePartition);
    if (consumerPosition.isPresent()) return consumerPosition.get();
    return committed(partition, timeout).offset();
  }

  @Override
  public OffsetAndMetadata committed(TopicPartition topicPartition) {
    return committed(topicPartition, INFINITE_DURATION);
  }

  @Override
  public OffsetAndMetadata committed(TopicPartition topicPartition, Duration duration) {
    return committed(ImmutableSet.of(topicPartition), duration).get(topicPartition);
  }

  @Override
  public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> set) {
    return committed(set, INFINITE_DURATION);
  }

  @Override
  public Map<TopicPartition, OffsetAndMetadata> committed(
      Set<TopicPartition> partitions, Duration timeout) {
    Set<Partition> targets =
        partitions.stream().map(this::checkTopicGetPartition).collect(Collectors.toSet());
    try {
      Map<Partition, Offset> full_map =
          cursorClient
              .listPartitionCursors(subscriptionPath)
              .get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      ImmutableMap.Builder<TopicPartition, OffsetAndMetadata> output = ImmutableMap.builder();
      targets.forEach(
          ExtractStatus.rethrowAsRuntime(
              partition -> {
                output.put(
                    toTopicPartition(partition),
                    new OffsetAndMetadata(full_map.getOrDefault(partition, Offset.of(0)).value()));
              }));
      return output.build();
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public Map<MetricName, ? extends Metric> metrics() {
    return ImmutableMap.of();
  }

  @Override
  public List<PartitionInfo> partitionsFor(String s) {
    return partitionsFor(s, INFINITE_DURATION);
  }

  @Override
  public List<PartitionInfo> partitionsFor(String topic, Duration timeout) {
    checkTopic(topic);
    return SharedBehavior.partitionsFor(partitionCount, topicPath);
  }

  @Override
  public Map<String, List<PartitionInfo>> listTopics() {
    return listTopics(INFINITE_DURATION);
  }

  @Override
  public Map<String, List<PartitionInfo>> listTopics(Duration timeout) {
    return ImmutableMap.of(topicPath.toString(), partitionsFor(topicPath.toString(), timeout));
  }

  @Override
  public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> map) {
    return offsetsForTimes(map, INFINITE_DURATION);
  }

  @Override
  public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(
      Map<TopicPartition, Long> map, Duration duration) {
    throw new UnsupportedVersionException(
        "Pub/Sub Lite does not support Consumer backlog introspection.");
  }

  @Override
  public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> collection) {
    return beginningOffsets(collection, INFINITE_DURATION);
  }

  @Override
  public Map<TopicPartition, Long> beginningOffsets(
      Collection<TopicPartition> collection, Duration duration) {
    ImmutableMap.Builder<TopicPartition, Long> results = ImmutableMap.builder();
    collection.forEach(
        topicPartition -> {
          checkTopic(topicPartition.topic());
          results.put(topicPartition, 0L);
        });
    return results.build();
  }

  @Override
  public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> collection) {
    return endOffsets(collection, INFINITE_DURATION);
  }

  @Override
  public Map<TopicPartition, Long> endOffsets(
      Collection<TopicPartition> collection, Duration duration) {
    throw new UnsupportedVersionException(
        "Pub/Sub Lite does not support Consumer backlog introspection.");
  }

  @Override
  public void close() {
    close(INFINITE_DURATION);
  }

  @Override
  public void close(long l, TimeUnit timeUnit) {
    close(toDuration(l, timeUnit));
  }

  @Override
  public void close(Duration timeout) {
    try {
      cursorClient.close();
    } catch (Exception e) {
      logger.atSevere().withCause(e).log("Error closing cursor client during Consumer shutdown.");
    }
    unsubscribe();
  }

  @Override
  public ConsumerGroupMetadata groupMetadata() {
    return new ConsumerGroupMetadata(subscriptionPath.toString());
  }

  @Override
  public Set<TopicPartition> paused() {
    return ImmutableSet.of();
  }

  @Override
  public void pause(Collection<TopicPartition> collection) {
    logger.atWarning().log(
        "Calling pause on a Pub/Sub Lite Consumer is a no-op. Configure the amount of outstanding bytes and messages instead.");
  }

  @Override
  public void resume(Collection<TopicPartition> collection) {
    logger.atWarning().log(
        "Calling resume on a Pub/Sub Lite Consumer is a no-op. Configure the amount of outstanding bytes and messages instead.");
  }

  @Override
  public void enforceRebalance() {
    logger.atWarning().log("Calling enforceRebalance on a Pub/Sub Lite Consumer is a no-op.");
  }

  @Override
  public void wakeup() {
    requireValidConsumer().wakeup();
  }
}
