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

package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.BufferingPullSubscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import java.io.Serializable;
import java.util.concurrent.*;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.ContinuousInputPartition;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

public class PslContinuousInputPartition
    implements ContinuousInputPartition<InternalRow>, Serializable {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final PslPartitionOffset startOffset;
  private final PslDataSourceOptions options;

  public PslContinuousInputPartition(PslPartitionOffset startOffset, PslDataSourceOptions options) {
    this.startOffset = startOffset;
    this.options = options;
  }

  @VisibleForTesting
  public static ContinuousInputPartitionReader<InternalRow> createPartitionReader(
      SubscriptionPath subscriptionPath,
      PslPartitionOffset currentOffset,
      PullSubscriber<SequencedMessage> subscriber,
      ScheduledExecutorService pullExecutorService) {
    return new PslContinuousInputPartitionReader(
        subscriptionPath, currentOffset, subscriber, pullExecutorService);
  }

  @Override
  public InputPartitionReader<InternalRow> createContinuousReader(PartitionOffset offset) {
    assert PslPartitionOffset.class.isAssignableFrom(offset.getClass())
        : "offset is not assignable to PslPartitionOffset";

    PslPartitionOffset pslOffset = (PslPartitionOffset) offset;
    PslPartitionOffset currentOffset =
        PslPartitionOffset.builder()
            .partition(pslOffset.partition())
            // The first message to read is startOffset + 1
            .offset(Offset.of(pslOffset.offset().value() + 1))
            .build();

    BufferingPullSubscriber subscriber;
    try {
      subscriber =
          new BufferingPullSubscriber(
              // TODO(jiangmichael): Pass credentials settings here.
              (consumer) ->
                  SubscriberBuilder.newBuilder()
                      .setSubscriptionPath(options.subscriptionPath())
                      .setPartition(pslOffset.partition())
                      .setContext(PubsubContext.of(Constants.FRAMEWORK))
                      .setMessageConsumer(consumer)
                      .build(),
              options.flowControlSettings(),
              SeekRequest.newBuilder()
                  .setCursor(Cursor.newBuilder().setOffset(currentOffset.offset().value()).build())
                  .build());
    } catch (CheckedApiException e) {
      throw new IllegalStateException(
          "Unable to create PSL subscriber for " + startOffset.toString(), e);
    }
    return createPartitionReader(
        options.subscriptionPath(),
        currentOffset,
        subscriber,
        Executors.newSingleThreadScheduledExecutor());
  }

  @Override
  public InputPartitionReader<InternalRow> createPartitionReader() {
    return createContinuousReader(startOffset);
  }

  private static class PslContinuousInputPartitionReader
      implements ContinuousInputPartitionReader<InternalRow> {

    private final SubscriptionPath subscriptionPath;
    private final PullSubscriber<SequencedMessage> subscriber;
    private final BlockingDeque<SequencedMessage> messages = new LinkedBlockingDeque<>();
    private PslPartitionOffset currentOffset;
    private SequencedMessage currentMsg;

    private PslContinuousInputPartitionReader(
        SubscriptionPath subscriptionPath,
        PslPartitionOffset currentOffset,
        PullSubscriber<SequencedMessage> subscriber,
        ScheduledExecutorService pullExecutorService) {
      this.subscriptionPath = subscriptionPath;
      this.currentOffset = currentOffset;
      this.subscriber = subscriber;
      this.currentMsg = null;
      pullExecutorService.scheduleAtFixedRate(
          () -> {
            try {
              messages.addAll(subscriber.pull());
            } catch (CheckedApiException e) {
              log.atWarning().log("Unable to pull from subscriber.", e);
            }
          },
          0,
          50,
          TimeUnit.MILLISECONDS);
    }

    @Override
    public PartitionOffset getOffset() {
      return currentOffset;
    }

    @Override
    public boolean next() {
      try {
        currentMsg = messages.takeFirst();
        currentOffset =
            PslPartitionOffset.builder()
                .partition(currentOffset.partition())
                .offset(currentMsg.offset())
                .build();
        return true;
      } catch (InterruptedException e) {
        throw new IllegalStateException("Retrieving messages interrupted.", e);
      }
    }

    @Override
    public InternalRow get() {
      assert currentMsg != null;
      return PslSparkUtils.toInternalRow(currentMsg, subscriptionPath, currentOffset.partition());
    }

    @Override
    public void close() {
      try {
        subscriber.close();
      } catch (Exception e) {
        log.atWarning().log("Subscriber failed to close.");
      }
    }
  }
}
