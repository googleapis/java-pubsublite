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
import com.google.cloud.pubsublite.internal.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Durations;
import com.google.protobuf.util.Timestamps;
import io.grpc.Status;
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class PubsubLiteUnboundedReaderTest {
  @SuppressWarnings("unchecked")
  private final PullSubscriber<SequencedMessage> subscriber5 = mock(PullSubscriber.class);

  @SuppressWarnings("unchecked")
  private final PullSubscriber<SequencedMessage> subscriber8 = mock(PullSubscriber.class);

  abstract static class CommitterFakeService extends FakeApiService implements Committer {}

  private static class FakeTicker extends Ticker {
    private Timestamp time;

    FakeTicker(Timestamp start) {
      time = start;
    }

    @Override
    public long read() {
      return Timestamps.toNanos(time);
    }

    public void advance(Duration duration) {
      time = Timestamps.add(time, duration);
    }
  }

  @Spy private CommitterFakeService committer5;
  @Spy private CommitterFakeService committer8;

  @SuppressWarnings("unchecked")
  private final UnboundedSource<SequencedMessage, ?> source = mock(UnboundedSource.class);

  private final TopicBacklogReader backlogReader = mock(TopicBacklogReader.class);
  private final FakeTicker ticker = new FakeTicker(Timestamps.fromSeconds(450));

  private final PubsubLiteUnboundedReader reader;

  private static SequencedMessage exampleMessage(Offset offset, Timestamp publishTime) {
    return SequencedMessage.of(Message.builder().build(), publishTime, offset, 100);
  }

  private static Timestamp randomMilliAllignedTimestamp() {
    return Timestamps.fromMillis(new Random().nextInt(Integer.MAX_VALUE));
  }

  private static Instant toInstant(Timestamp timestamp) {
    return new Instant(Timestamps.toMillis(timestamp));
  }

  public PubsubLiteUnboundedReaderTest() throws StatusException {
    MockitoAnnotations.initMocks(this);
    SubscriberState state5 = new SubscriberState();
    state5.subscriber = subscriber5;
    state5.committer = committer5;
    SubscriberState state8 = new SubscriberState();
    state8.subscriber = subscriber8;
    state8.committer = committer8;
    reader =
        new PubsubLiteUnboundedReader(
            source,
            ImmutableMap.of(Partition.of(5), state5, Partition.of(8), state8),
            backlogReader,
            ticker);
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
    SequencedMessage message = exampleMessage(Offset.of(10), ts);
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
    SequencedMessage message1 = exampleMessage(Offset.of(10), ts1);
    SequencedMessage message2 = exampleMessage(Offset.of(888), ts2);
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
    SequencedMessage message1 = exampleMessage(Offset.of(10), randomMilliAllignedTimestamp());
    SequencedMessage message2 = exampleMessage(Offset.of(888), randomMilliAllignedTimestamp());
    SequencedMessage message3 = exampleMessage(Offset.of(999), randomMilliAllignedTimestamp());
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
    SequencedMessage message1 = exampleMessage(Offset.of(10), randomMilliAllignedTimestamp());
    SequencedMessage message2 = exampleMessage(Offset.of(888), randomMilliAllignedTimestamp());
    SequencedMessage message3 = exampleMessage(Offset.of(999), randomMilliAllignedTimestamp());
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
    SequencedMessage message = exampleMessage(Offset.of(10), ts);
    when(subscriber5.pull()).thenReturn(ImmutableList.of(message));
    when(subscriber8.pull()).thenReturn(ImmutableList.of());
    assertThat(reader.start()).isTrue();
    verify(subscriber5).pull();
    verify(subscriber8).pull();
    verifyNoMoreInteractions(subscriber5, subscriber8);

    CheckpointMark mark = reader.getCheckpointMark();

    when(committer5.commitOffset(Offset.of(10))).thenReturn(ApiFutures.immediateFuture(null));
    mark.finalizeCheckpoint();
    verify(committer5).commitOffset(Offset.of(10));
  }

  @Test
  public void splitBacklogBytes_returnsUnknownBacklogOnError() throws Exception {
    when(backlogReader.computeMessageStats(ImmutableMap.of()))
        .thenReturn(ApiFutures.immediateFailedFuture(new StatusException(Status.UNAVAILABLE)));
    Assert.assertEquals(PubsubLiteUnboundedReader.BACKLOG_UNKNOWN, reader.getSplitBacklogBytes());
  }

  @Test
  public void splitBacklogBytes_computesBacklog() throws Exception {
    ComputeMessageStatsResponse response =
        ComputeMessageStatsResponse.newBuilder().setMessageBytes(40).build();
    when(backlogReader.computeMessageStats(ImmutableMap.of()))
        .thenReturn(ApiFutures.immediateFuture(response));
    Assert.assertEquals(response.getMessageBytes(), reader.getSplitBacklogBytes());
  }

  @Test
  public void splitBacklogBytes_computesBacklogOncePerTenSeconds() throws Exception {
    ComputeMessageStatsResponse response1 =
        ComputeMessageStatsResponse.newBuilder().setMessageBytes(40).build();
    ComputeMessageStatsResponse response2 =
        ComputeMessageStatsResponse.newBuilder().setMessageBytes(50).build();

    when(backlogReader.computeMessageStats(ImmutableMap.of()))
        .thenReturn(ApiFutures.immediateFuture(response1), ApiFutures.immediateFuture(response2));

    Assert.assertEquals(response1.getMessageBytes(), reader.getSplitBacklogBytes());
    ticker.advance(Durations.fromSeconds(10));
    Assert.assertEquals(response1.getMessageBytes(), reader.getSplitBacklogBytes());
    ticker.advance(Durations.fromSeconds(1));
    Assert.assertEquals(response2.getMessageBytes(), reader.getSplitBacklogBytes());
  }

  @Test
  public void splitBacklogBytes_oldValueExpiresAfterOneMinute() throws Exception {
    ComputeMessageStatsResponse response =
        ComputeMessageStatsResponse.newBuilder().setMessageBytes(40).build();

    when(backlogReader.computeMessageStats(ImmutableMap.of()))
        .thenReturn(
            ApiFutures.immediateFuture(response),
            ApiFutures.immediateFailedFuture(new StatusException(Status.UNAVAILABLE)));

    Assert.assertEquals(response.getMessageBytes(), reader.getSplitBacklogBytes());
    ticker.advance(Durations.fromSeconds(30));
    Assert.assertEquals(response.getMessageBytes(), reader.getSplitBacklogBytes());
    ticker.advance(Durations.fromSeconds(31));
    Assert.assertEquals(PubsubLiteUnboundedReader.BACKLOG_UNKNOWN, reader.getSplitBacklogBytes());
  }

  @Test
  public void splitBacklogBytes_usesCorrectCursorValues() throws Exception {
    SequencedMessage message1 = exampleMessage(Offset.of(10), randomMilliAllignedTimestamp());
    SequencedMessage message2 = exampleMessage(Offset.of(888), randomMilliAllignedTimestamp());
    ComputeMessageStatsResponse response =
        ComputeMessageStatsResponse.newBuilder().setMessageBytes(40).build();

    when(subscriber5.pull()).thenReturn(ImmutableList.of(message1));
    when(subscriber8.pull()).thenReturn(ImmutableList.of(message2));
    when(backlogReader.computeMessageStats(
            ImmutableMap.of(Partition.of(5), Offset.of(10), Partition.of(8), Offset.of(888))))
        .thenReturn(ApiFutures.immediateFuture(response));

    assertThat(reader.start()).isTrue();
    assertThat(reader.advance()).isTrue();
    Assert.assertEquals(response.getMessageBytes(), reader.getSplitBacklogBytes());
  }
}
