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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.BufferingPullSubscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.options.PipelineOptions;

class PubsubLiteUnboundedSource extends UnboundedSource<SequencedMessage, OffsetCheckpointMark> {
  private final SubscriberOptions subscriberOptions;

  PubsubLiteUnboundedSource(SubscriberOptions options) {
    this.subscriberOptions = options;
  }

  /**
   * Splits the source into its constituent partitions. It is fine for split to return more splits
   * than desired.
   *
   * <p>This splitting is performed so that all readers are created and destroyed independently,
   * which enables the memory limiter logic to approach an even distribution when the number of
   * workers is stable.
   */
  @Override
  public List<? extends UnboundedSource<SequencedMessage, OffsetCheckpointMark>> split(
      int desiredNumSplits, PipelineOptions options) {
    ImmutableList.Builder<PubsubLiteUnboundedSource> builder = ImmutableList.builder();
    for (Partition partition : subscriberOptions.partitions()) {
      builder.add(
          new PubsubLiteUnboundedSource(
              subscriberOptions.toBuilder().setPartitions(ImmutableSet.of(partition)).build()));
    }
    return builder.build();
  }

  private MemoryLimiter getMemoryLimiter(PipelineOptions options) {
    Optional<Long> limit = Optional.empty();
    if (options instanceof PubsubLitePipelineOptions) {
      PubsubLitePipelineOptions psOptions = (PubsubLitePipelineOptions) options;
      if (psOptions.getPubsubLiteSubscriberWorkerMemoryLimiterEnabled()) {
        limit = Optional.of(psOptions.getPubsubLiteSubscribeWorkerMemoryLimit());
      }
    }
    return PerServerMemoryLimiter.getLimiter(limit);
  }

  @Override
  public UnboundedReader<SequencedMessage> createReader(
      PipelineOptions options, @Nullable OffsetCheckpointMark checkpointMark) throws IOException {
    MemoryLimiter limiter = getMemoryLimiter(options);
    try {
      ImmutableMap<Partition, SubscriberFactory> subscriberFactories =
          subscriberOptions.getSubscriberFactories();
      ImmutableMap<Partition, Committer> committers = subscriberOptions.getCommitters();
      ImmutableMap.Builder<Partition, PubsubLiteUnboundedReader.SubscriberState> statesBuilder =
          ImmutableMap.builder();
      for (Partition partition : subscriberFactories.keySet()) {
        checkState(committers.containsKey(partition));
        PubsubLiteUnboundedReader.SubscriberState state =
            new PubsubLiteUnboundedReader.SubscriberState();
        state.committer = committers.get(partition);
        SeekRequest initialSeek;
        if (checkpointMark != null && checkpointMark.partitionOffsetMap.containsKey(partition)) {
          Offset checkpointed = checkpointMark.partitionOffsetMap.get(partition);
          state.lastDelivered = Optional.of(checkpointed);
          initialSeek =
              SeekRequest.newBuilder()
                  .setCursor(Cursor.newBuilder().setOffset(checkpointed.value()))
                  .build();
        } else {
          initialSeek =
              SeekRequest.newBuilder()
                  .setNamedTarget(SeekRequest.NamedTarget.COMMITTED_CURSOR)
                  .build();
        }
        state.subscriber =
            new MemoryLimitingPullSubscriber(
                (seek, settings) ->
                    new BufferingPullSubscriber(subscriberFactories.get(partition), settings, seek),
                limiter,
                subscriberOptions.flowControlSettings(),
                initialSeek,
                PerServerAlarmFactory.getInstance());
        statesBuilder.put(partition, state);
      }
      return new PubsubLiteUnboundedReader(
          this,
          statesBuilder.build(),
          TopicBacklogReader.create(subscriberOptions.topicBacklogReaderSettings()),
          Ticker.systemTicker());
    } catch (CheckedApiException e) {
      throw new IOException(e);
    }
  }

  @Override
  public Coder<OffsetCheckpointMark> getCheckpointMarkCoder() {
    return OffsetCheckpointMark.getCoder();
  }

  @Override
  public Coder<SequencedMessage> getOutputCoder() {
    return new SequencedMessageCoder();
  }
}
