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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.beam.PubsubLiteUnboundedReader.SubscriberState;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.io.UnboundedSource.CheckpointMark;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PubsubLiteUnboundedReaderTest {
  @SuppressWarnings("unchecked")
  private final PullSubscriber<SequencedMessage> subscriber5 = mock(PullSubscriber.class);

  @SuppressWarnings("unchecked")
  private final PullSubscriber<SequencedMessage> subscriber8 = mock(PullSubscriber.class);

  private final Committer committer5 = mock(Committer.class);
  private final Committer committer8 = mock(Committer.class);

  @SuppressWarnings("unchecked")
  private final UnboundedSource<SequencedMessage, ?> source = mock(UnboundedSource.class);

  private final PubsubLiteUnboundedReader reader;

  private static SequencedMessage exampleMessage(Offset offset, Timestamp publishTime) {
    return SequencedMessage.create(Message.builder().build(), publishTime, offset, 100);
  }

  private static Timestamp randomMilliAllignedTimestamp() {
    return Timestamps.fromMillis(new Random().nextInt(Integer.MAX_VALUE));
  }

  private static Instant toInstant(Timestamp timestamp) {
    return new Instant(Timestamps.toMillis(timestamp));
  }

  public PubsubLiteUnboundedReaderTest() throws StatusException {
    SubscriberState state5 = new SubscriberState();
    state5.subscriber = subscriber5;
    state5.committer = committer5;
    SubscriberState state8 = new SubscriberState();
    state8.subscriber = subscriber8;
    state8.committer = committer8;
    reader =
        new PubsubLiteUnboundedReader(
            source, ImmutableMap.of(Partition.create(5), state5, Partition.create(8), state8));
  }

  @Test
  public void sourceReturnsSource() {
    assertThat(reader.getCurrentSource()).isSameInstanceAs(source);
  }

  @Test
  public void startPullsFromAllSubscribers() throws Exception {
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertThat(reader.start()).isFalse();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    assertThat(reader.getWatermark()).isEqualTo(BoundedWindow.TIMESTAMP_MIN_VALUE);
    verifyNoMoreInteractions(subscriber5, subscriber8);
  }

  @Test
  public void startReturnsTrueIfMessagesExist() throws Exception {
    Timestamp ts = randomMilliAllignedTimestamp();
    SequencedMessage message = exampleMessage(Offset.create(10), ts);
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message));
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertThat(reader.start()).isTrue();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    assertThat(reader.getCurrent()).isEqualTo(message);
    assertThat(reader.getWatermark()).isEqualTo(BoundedWindow.TIMESTAMP_MIN_VALUE);
    assertThat(reader.getCurrentTimestamp()).isEqualTo(toInstant(ts));
    verifyNoMoreInteractions(subscriber5, subscriber8);
  }

  @Test
  public void advanceSetsWatermarkAfterAllSubscribersPopulated() throws Exception {
    Timestamp ts1 = randomMilliAllignedTimestamp();
    Timestamp ts2 = randomMilliAllignedTimestamp();
    SequencedMessage message1 = exampleMessage(Offset.create(10), ts1);
    SequencedMessage message2 = exampleMessage(Offset.create(888), ts2);
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message1));
    when(subscriber8.pull()).thenReturn(ImmutableList.of(message2));
    assertThat(reader.start()).isTrue();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);
    reset(subscriber5, subscriber8);
    List<SequencedMessage> messages = new ArrayList<>();
    messages.add(reader.getCurrent());
    assertThat(reader.getWatermark()).isEqualTo(BoundedWindow.TIMESTAMP_MIN_VALUE);
    // This could be either original message, but is the current message from the reader.
    assertThat(reader.getCurrentTimestamp()).isEqualTo(toInstant(messages.get(0).publishTime()));
    assertThat(reader.advance()).isTrue();
    messages.add(reader.getCurrent());
    assertThat(reader.getWatermark())
        .isEqualTo(Collections.min(Arrays.asList(toInstant(ts1), toInstant(ts2))));
    assertThat(reader.getCurrentTimestamp()).isEqualTo(toInstant(messages.get(1).publishTime()));
    // Second pull yields no more messages.
    when(subscriber5.pull()).thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertThat(reader.advance()).isFalse();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);
  }

  @Test
  public void multipleMessagesInPullReadsAllBeforeNextPull() throws Exception {
    SequencedMessage message1 = exampleMessage(Offset.create(10), randomMilliAllignedTimestamp());
    SequencedMessage message2 = exampleMessage(Offset.create(888), randomMilliAllignedTimestamp());
    SequencedMessage message3 = exampleMessage(Offset.create(999), randomMilliAllignedTimestamp());
    when(subscriber5.pull())
        .thenReturn(ImmutableList.of(message1, message2, message3))
        .thenReturn(ImmutableList.of());
    when(subscriber8.pull()).thenReturn(ImmutableList.of()).thenReturn(ImmutableList.of());
    assertThat(reader.start()).isTrue();
    assertThat(reader.advance()).isTrue();
    assertThat(reader.advance()).isTrue();
    assertThat(reader.advance()).isFalse();
    verify(subscriber5, times(2)).pull();
    verify(subscriber8, times(2)).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);
  }

  @Test
  public void messagesOnSubsequentPullsProcessed() throws Exception {
    SequencedMessage message1 = exampleMessage(Offset.create(10), randomMilliAllignedTimestamp());
    SequencedMessage message2 = exampleMessage(Offset.create(888), randomMilliAllignedTimestamp());
    SequencedMessage message3 = exampleMessage(Offset.create(999), randomMilliAllignedTimestamp());
    when(subscriber5.pull())
        .thenReturn(ImmutableList.of(message1))
        .thenReturn(ImmutableList.of(message2))
        .thenReturn(ImmutableList.of());
    when(subscriber8.pull())
        .thenReturn(ImmutableList.of())
        .thenReturn(ImmutableList.of(message3))
        .thenReturn(ImmutableList.of());
    assertThat(reader.start()).isTrue();
    assertThat(reader.advance()).isTrue();
    assertThat(reader.advance()).isTrue();
    assertThat(reader.advance()).isFalse();
    verify(subscriber5, times(3)).pull();
    verify(subscriber8, times(3)).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);
  }

  @Test
  public void checkpointMarkFinalizeCommits() throws Exception {
    Timestamp ts = randomMilliAllignedTimestamp();
    SequencedMessage message = exampleMessage(Offset.create(10), ts);
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message));
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertThat(reader.start()).isTrue();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);

    CheckpointMark mark = reader.getCheckpointMark();

    when(committer5.commitOffset(Offset.create(10))).thenReturn(ApiFutures.immediateFuture(null));
    mark.finalizeCheckpoint();
    verify(committer5).commitOffset(Offset.create(10));
    verifyNoMoreInteractions(committer5, committer8);
  }
}
