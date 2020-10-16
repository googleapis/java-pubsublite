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

import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekRequest.NamedTarget;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.protobuf.Timestamp;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class SingleSubscriptionConsumerImplTest {
  private static final SeekRequest DEFAULT_SEEK =
      SeekRequest.newBuilder().setNamedTarget(NamedTarget.COMMITTED_CURSOR).build();
  private static final SeekRequest OFFSET_SEEK =
      SeekRequest.newBuilder()
          .setCursor(Cursor.newBuilder().setOffset(example(Offset.class).value()))
          .build();

  @Mock PullSubscriberFactory subscriberFactory;
  @Mock CommitterFactory committerFactory;

  @Mock PullSubscriber<SequencedMessage> subscriber5;
  @Mock PullSubscriber<SequencedMessage> subscriber8;

  abstract static class FakeCommitter extends FakeApiService implements Committer {}

  @Spy FakeCommitter committer5;
  @Spy FakeCommitter committer8;

  private SingleSubscriptionConsumer consumer;

  @Before
  public void setUp() throws CheckedApiException {
    initMocks(this);
    consumer =
        new SingleSubscriptionConsumerImpl(
            example(TopicPath.class), false, subscriberFactory, committerFactory);
    verifyNoInteractions(subscriberFactory, committerFactory);
    when(subscriberFactory.newPullSubscriber(eq(Partition.of(5)), any())).thenReturn(subscriber5);
    when(subscriberFactory.newPullSubscriber(eq(Partition.of(8)), any())).thenReturn(subscriber8);
    when(committerFactory.newCommitter(Partition.of(5))).thenReturn(committer5);
    when(committerFactory.newCommitter(Partition.of(8))).thenReturn(committer8);
  }

  private static SequencedMessage message(long offset) {
    return SequencedMessage.of(
        Message.builder().build(), Timestamp.getDefaultInstance(), Offset.of(offset), 0L);
  }

  private static void assertConsumerRecordsEqual(
      ConsumerRecords<byte[], byte[]> records, ListMultimap<Partition, Offset> target) {
    ImmutableListMultimap.Builder<Partition, Offset> builder = ImmutableListMultimap.builder();
    for (ConsumerRecord<byte[], byte[]> record : records) {
      builder.put(Partition.of(record.partition()), Offset.of(record.offset()));
    }
    assertThat(builder.build()).isEqualTo(target);
  }

  @Test
  public void assignAndPoll() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5), Partition.of(8)));
    verify(subscriberFactory).newPullSubscriber(Partition.of(5), DEFAULT_SEEK);
    verify(subscriberFactory).newPullSubscriber(Partition.of(8), DEFAULT_SEEK);
    verify(committerFactory).newCommitter(Partition.of(5));
    verify(committerFactory).newCommitter(Partition.of(8));
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    // -----------------------------
    // Pulls ceil(15ms / 10ms) + 1 times (3) when no messages are returned
    assertConsumerRecordsEqual(consumer.poll(Duration.ofMillis(15)), ImmutableListMultimap.of());
    verify(subscriber5, times(3)).pull();
    verify(subscriber8, times(3)).pull();
    verify(committer5, times(0)).commitOffset(any());
    verify(committer8, times(0)).commitOffset(any());
    // -----------------------------
    // Pulls once when messages are available.
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message(1), message(2), message(3)));
    when(subscriber8.pull()).thenReturn(ImmutableList.of(message(1), message(2), message(4)));
    assertConsumerRecordsEqual(
        consumer.poll(Duration.ofMillis(15)),
        ImmutableListMultimap.<Partition, Offset>builder()
            .putAll(Partition.of(5), ImmutableList.of(Offset.of(1), Offset.of(2), Offset.of(3)))
            .putAll(Partition.of(8), ImmutableList.of(Offset.of(1), Offset.of(2), Offset.of(4)))
            .build());
    verify(subscriber5, times(4)).pull();
    verify(subscriber8, times(4)).pull();
    verify(committer5, times(0)).commitOffset(any());
    verify(committer8, times(0)).commitOffset(any());

    // --------------------------
    // Zero duration poll pulls once.
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertConsumerRecordsEqual(consumer.poll(Duration.ZERO), ImmutableListMultimap.of());
    verify(subscriber5, times(5)).pull();
    verify(subscriber8, times(5)).pull();
    verify(committer5, times(0)).commitOffset(any());
    verify(committer8, times(0)).commitOffset(any());
    // --------------------------
    // commitAll sends commits
    SettableApiFuture<Void> commit5 = SettableApiFuture.create();
    SettableApiFuture<Void> commit8 = SettableApiFuture.create();
    // Commits are last received + 1
    when(committer5.commitOffset(Offset.of(4))).thenReturn(commit5);
    when(committer8.commitOffset(Offset.of(5))).thenReturn(commit8);
    ApiFuture<Map<Partition, Offset>> committed = consumer.commitAll();
    assertThat(committed.isDone()).isFalse();
    commit5.set(null);
    assertThat(committed.isDone()).isFalse();
    commit8.set(null);
    assertThat(committed.get())
        .containsExactlyEntriesIn(
            ImmutableMap.of(Partition.of(5), Offset.of(4), Partition.of(8), Offset.of(5)));
    // Close closes.
    consumer.close(Duration.ZERO);
    verify(subscriber5).close();
    verify(subscriber8).close();
    verify(committer5).stopAsync();
    verify(committer5).awaitTerminated();
    verify(committer8).stopAsync();
    verify(committer8).awaitTerminated();
  }

  @Test
  public void assignAndPollAutocommit() throws Exception {
    consumer =
        new SingleSubscriptionConsumerImpl(
            example(TopicPath.class), true, subscriberFactory, committerFactory);
    consumer.setAssignment(ImmutableSet.of(Partition.of(5), Partition.of(8)));
    verify(subscriberFactory).newPullSubscriber(Partition.of(5), DEFAULT_SEEK);
    verify(subscriberFactory).newPullSubscriber(Partition.of(8), DEFAULT_SEEK);
    verify(committerFactory).newCommitter(Partition.of(5));
    verify(committerFactory).newCommitter(Partition.of(8));
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    // -----------------------------
    // Pulls ceil(15ms / 10ms) + 1 times (3) when no messages are returned
    assertConsumerRecordsEqual(consumer.poll(Duration.ofMillis(15)), ImmutableListMultimap.of());
    verify(subscriber5, times(3)).pull();
    verify(subscriber8, times(3)).pull();
    verify(committer5, times(0)).commitOffset(any());
    verify(committer8, times(0)).commitOffset(any());
    // -----------------------------
    // Pulls once when messages are available.
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message(1), message(2), message(3)));
    when(subscriber8.pull()).thenReturn(ImmutableList.of(message(1), message(2), message(4)));
    assertConsumerRecordsEqual(
        consumer.poll(Duration.ofMillis(15)),
        ImmutableListMultimap.<Partition, Offset>builder()
            .putAll(Partition.of(5), ImmutableList.of(Offset.of(1), Offset.of(2), Offset.of(3)))
            .putAll(Partition.of(8), ImmutableList.of(Offset.of(1), Offset.of(2), Offset.of(4)))
            .build());
    verify(subscriber5, times(4)).pull();
    verify(subscriber8, times(4)).pull();
    verify(committer5, times(0)).commitOffset(any());
    verify(committer8, times(0)).commitOffset(any());

    // --------------------------
    // Zero duration poll pulls once, commits previous offsets.
    SettableApiFuture<Void> commit5 = SettableApiFuture.create();
    SettableApiFuture<Void> commit8 = SettableApiFuture.create();
    // Commits are last received + 1
    when(committer5.commitOffset(Offset.of(4))).thenReturn(commit5);
    when(committer8.commitOffset(Offset.of(5))).thenReturn(commit8);

    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertConsumerRecordsEqual(consumer.poll(Duration.ZERO), ImmutableListMultimap.of());
    verify(subscriber5, times(5)).pull();
    verify(subscriber8, times(5)).pull();
    verify(committer5).commitOffset(Offset.of(4));
    verify(committer8).commitOffset(Offset.of(5));
    commit5.set(null);
    commit8.set(null);
    // Close closes.
    consumer.close(Duration.ZERO);
    verify(subscriber5).close();
    verify(subscriber8).close();
    verify(committer5).stopAsync();
    verify(committer5).awaitTerminated();
    verify(committer8).stopAsync();
    verify(committer8).awaitTerminated();
  }

  @Test
  public void wakeupBeforePoll() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    consumer.wakeup();
    assertThrows(WakeupException.class, () -> consumer.poll(Duration.ofMillis(15)));
  }

  @Test
  public void wakeupDuringPoll() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    when(subscriber5.pull())
        .thenAnswer(
            args -> {
              consumer.wakeup();
              return ImmutableList.of();
            });
    assertThrows(WakeupException.class, () -> consumer.poll(Duration.ofDays(1)));
  }

  @Test
  public void assignmentChange() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    assertThat(consumer.assignment()).isEqualTo(ImmutableSet.of(Partition.of(5)));
    verify(subscriberFactory).newPullSubscriber(Partition.of(5), DEFAULT_SEEK);
    verify(committerFactory).newCommitter(Partition.of(5));
    verify(committer5).startAsync();
    consumer.setAssignment(ImmutableSet.of(Partition.of(8)));
    assertThat(consumer.assignment()).isEqualTo(ImmutableSet.of(Partition.of(8)));
    verify(subscriberFactory).newPullSubscriber(Partition.of(8), DEFAULT_SEEK);
    verify(committerFactory).newCommitter(Partition.of(8));
    verify(committer8).startAsync();
    verify(subscriber5).close();
    verify(committer5).stopAsync();
  }

  @Test
  public void commitNotAssigned() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    assertThrows(
        CommitFailedException.class,
        () -> consumer.commit(ImmutableMap.of(Partition.of(8), Offset.of(1))));
  }

  @Test
  public void commitAssigned() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    SettableApiFuture<Void> commit5 = SettableApiFuture.create();
    when(committer5.commitOffset(Offset.of(1))).thenReturn(commit5);
    ApiFuture<Void> commitFuture = consumer.commit(ImmutableMap.of(Partition.of(5), Offset.of(1)));
    assertThat(commitFuture.isDone()).isFalse();
    commit5.set(null);
    assertThat(commitFuture.isDone()).isTrue();
  }

  @Test
  public void seekNotAssigned() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    assertThrows(IllegalStateException.class, () -> consumer.doSeek(Partition.of(8), OFFSET_SEEK));
  }

  @Test
  public void seekAssigned() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    verify(subscriberFactory).newPullSubscriber(Partition.of(5), DEFAULT_SEEK);
    verify(committerFactory).newCommitter(Partition.of(5));
    when(subscriberFactory.newPullSubscriber(Partition.of(5), OFFSET_SEEK)).thenReturn(subscriber8);
    consumer.doSeek(Partition.of(5), OFFSET_SEEK);
    verify(subscriber5).close();
    verify(subscriberFactory).newPullSubscriber(Partition.of(5), OFFSET_SEEK);
  }

  @Test
  public void position() throws Exception {
    consumer.setAssignment(ImmutableSet.of(Partition.of(5)));
    assertThat(consumer.position(Partition.of(8))).isEmpty();
    when(subscriber5.nextOffset()).thenReturn(Optional.empty());
    assertThat(consumer.position(Partition.of(5))).isEmpty();
    when(subscriber5.nextOffset()).thenReturn(Optional.of(example(Offset.class)));
    assertThat(consumer.position(Partition.of(5))).hasValue(example(Offset.class).value());
  }
}
