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

import static com.google.common.base.Preconditions.checkState;

import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriberImpl;
import com.google.common.flogger.GoogleLogger;
import java.util.Optional;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

public class PslContinuousInputPartitionReader
    implements ContinuousInputPartitionReader<InternalRow> {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final SubscriptionPath subscriptionPath;
  private final BlockingPullSubscriberImpl subscriber;
  private SparkPartitionOffset currentOffset;
  private SequencedMessage currentMsg;

  PslContinuousInputPartitionReader(
      SubscriptionPath subscriptionPath,
      SparkPartitionOffset startOffset,
      BlockingPullSubscriberImpl subscriber) {
    this.subscriptionPath = subscriptionPath;
    this.currentOffset = startOffset;
    this.subscriber = subscriber;
    this.currentMsg = null;
  }

  @Override
  public PartitionOffset getOffset() {
    return currentOffset;
  }

  @Override
  public boolean next() {
    try {
      subscriber.onData().get();
      // since next() will not be called concurrently, we are sure that the message
      // is available to this thread.
      Optional<SequencedMessage> msg = subscriber.messageIfAvailable();
      checkState(msg.isPresent());
      currentMsg = msg.get();
      currentOffset =
          SparkPartitionOffset.builder()
              .partition(currentOffset.partition())
              .offset(currentMsg.offset().value())
              .build();
      return true;
    } catch (Throwable t) {
      throw new IllegalStateException("Failed to retrieve messages.", t);
    }
  }

  @Override
  public InternalRow get() {
    checkState(currentMsg != null);
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
