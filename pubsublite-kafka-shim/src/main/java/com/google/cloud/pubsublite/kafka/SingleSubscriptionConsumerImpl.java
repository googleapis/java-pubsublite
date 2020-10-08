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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

class SingleSubscriptionConsumerImpl implements SingleSubscriptionConsumer {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private final TopicPath topic;
  private final boolean autocommit;

  private final PullSubscriberFactory subscriberFactory;
  private final CommitterFactory committerFactory;

  private final CloseableMonitor monitor = new CloseableMonitor();

  static class SubscriberState {
    PullSubscriber<SequencedMessage> subscriber;
    Committer committer;
    Optional<Offset> lastUncommitted = Optional.empty();
  }

  @GuardedBy("monitor.monitor")
  private Map<Partition, SubscriberState> partitions = new HashMap<>();

  // Set to true when wakeup() has been called once.
  @GuardedBy("monitor.monitor")
  private boolean wakeupTriggered = false;

  SingleSubscriptionConsumerImpl(
      TopicPath topic,
      boolean autocommit,
      PullSubscriberFactory subscriberFactory,
      CommitterFactory committerFactory) {
    this.topic = topic;
    this.autocommit = autocommit;
    this.subscriberFactory = subscriberFactory;
    this.committerFactory = committerFactory;
  }

  @Override
  public void setAssignment(Set<Partition> assignment) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      List<SubscriberState> unassigned =
          ImmutableSet.copyOf(partitions.keySet()).stream()
              .filter(p -> !assignment.contains(p))
              .map(p -> partitions.remove(p))
              .collect(Collectors.toList());
      for (SubscriberState state : unassigned) {
        state.subscriber.close();
        state.committer.stopAsync().awaitTerminated();
      }
      assignment.stream()
          .filter(p -> !partitions.containsKey(p))
          .forEach(
              ExtractStatus.rethrowAsRuntime(
                  partition -> {
                    SubscriberState s = new SubscriberState();
                    s.subscriber =
                        subscriberFactory.newPullSubscriber(
                            partition,
                            SeekRequest.newBuilder()
                                .setNamedTarget(NamedTarget.COMMITTED_CURSOR)
                                .build());
                    s.committer = committerFactory.newCommitter(partition);
                    s.committer.startAsync().awaitRunning();
                    partitions.put(partition, s);
                  }));
    } catch (Throwable t) {
      throw ExtractStatus.toCanonical(t).getStatus().asRuntimeException();
    }
  }

  @Override
  public Set<Partition> assignment() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      return partitions.keySet();
    }
  }

  @GuardedBy("monitor.monitor")
  private Map<Partition, Queue<SequencedMessage>> fetchAll() {
    Map<Partition, Queue<SequencedMessage>> partitionQueues = new HashMap<>();
    partitions.forEach(
        ExtractStatus.rethrowAsRuntime(
            (partition, state) -> {
              List<SequencedMessage> messages = state.subscriber.pull();
              if (messages.isEmpty()) return;
              partitionQueues.computeIfAbsent(partition, x -> new ArrayDeque<>()).addAll(messages);
            }));
    return partitionQueues;
  }

  private Map<Partition, Queue<SequencedMessage>> doPoll(Duration duration) {
    try {
      while (!duration.isZero()) {
        try (CloseableMonitor.Hold h = monitor.enter()) {
          if (wakeupTriggered) throw new WakeupException();
          Map<Partition, Queue<SequencedMessage>> partitionQueues = fetchAll();
          if (!partitionQueues.isEmpty()) return partitionQueues;
        }
        Duration sleepFor = Collections.min(ImmutableList.of(duration, Duration.ofMillis(10)));
        Thread.sleep(sleepFor.toMillis());
        duration = duration.minus(sleepFor);
      }
      // Last fetch to handle duration originally being 0 and last time window sleep.
      try (CloseableMonitor.Hold h = monitor.enter()) {
        return fetchAll();
      }
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public ConsumerRecords<byte[], byte[]> poll(Duration duration) {
    Map<Partition, Queue<SequencedMessage>> partitionQueues = doPoll(duration);
    Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> records = new HashMap<>();
    if (autocommit) {
      ApiFuture<?> future = commitAll();
      ApiFutures.addCallback(
          future,
          new ApiFutureCallback<Object>() {
            @Override
            public void onFailure(Throwable throwable) {
              logger.atWarning().withCause(throwable).log("Failed to commit offsets.");
            }

            @Override
            public void onSuccess(Object result) {}
          },
          MoreExecutors.directExecutor());
    }
    partitionQueues.forEach(
        (partition, queue) -> {
          if (queue.isEmpty()) return;
          try (CloseableMonitor.Hold h = monitor.enter()) {
            SubscriberState state = partitions.getOrDefault(partition, null);
            if (state != null) {
              state.lastUncommitted = Optional.of(Iterables.getLast(queue).offset());
            }
          }
          List<ConsumerRecord<byte[], byte[]>> partitionRecords =
              queue.stream()
                  .map(message -> RecordTransforms.fromMessage(message, topic, partition))
                  .collect(Collectors.toList());
          records.put(
              new TopicPartition(topic.toString(), (int) partition.value()), partitionRecords);
        });
    return new ConsumerRecords<>(records);
  }

  @Override
  public ApiFuture<Map<Partition, Offset>> commitAll() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      ImmutableMap.Builder<Partition, Offset> builder = ImmutableMap.builder();
      ImmutableList.Builder<ApiFuture<?>> commitFutures = ImmutableList.builder();
      partitions.entrySet().stream()
          .filter(entry -> entry.getValue().lastUncommitted.isPresent())
          .forEach(
              entry -> {
                // The Pub/Sub Lite commit offset is the next to be received.
                Offset lastUncommitted = entry.getValue().lastUncommitted.get();
                entry.getValue().lastUncommitted = Optional.empty();
                Offset toCommit = Offset.of(lastUncommitted.value() + 1);
                builder.put(entry.getKey(), toCommit);
                commitFutures.add(entry.getValue().committer.commitOffset(toCommit));
              });
      Map<Partition, Offset> map = builder.build();
      return ApiFutures.transform(
          ApiFutures.allAsList(commitFutures.build()),
          ignored -> map,
          MoreExecutors.directExecutor());
    }
  }

  @Override
  public ApiFuture<Void> commit(Map<Partition, Offset> commitOffsets) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      ImmutableList.Builder<ApiFuture<?>> commitFutures = ImmutableList.builder();
      commitOffsets.forEach(
          (partition, offset) -> {
            if (!partitions.containsKey(partition)) {
              throw new CommitFailedException(
                  "Tried to commit to partition "
                      + partition.value()
                      + " which is not assigned to this consumer.");
            }
            commitFutures.add(partitions.get(partition).committer.commitOffset(offset));
          });
      return ApiFutures.transform(
          ApiFutures.allAsList(commitFutures.build()),
          ignored -> null,
          MoreExecutors.directExecutor());
    }
  }

  @Override
  public void doSeek(Partition partition, SeekRequest request) throws KafkaException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (!partitions.containsKey(partition)) {
        throw new IllegalStateException(
            "Received seek for partition "
                + partition.value()
                + " which is not assigned to this consumer.");
      }
      SubscriberState state = partitions.get(partition);
      state.subscriber.close();
      state.subscriber = subscriberFactory.newPullSubscriber(partition, request);
    } catch (IllegalStateException e) {
      throw e;
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public Optional<Long> position(Partition partition) {
    if (!partitions.containsKey(partition)) return Optional.empty();
    return partitions.get(partition).subscriber.nextOffset().map(Offset::value);
  }

  @Override
  public void close(Duration duration) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      for (SubscriberState state : partitions.values()) {
        state.subscriber.close();
        state.committer.stopAsync().awaitTerminated();
      }
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }

  @Override
  public void wakeup() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      wakeupTriggered = true;
    }
  }
}
