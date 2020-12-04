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

import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.common.flogger.GoogleLogger;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

public class PslContinuousInputPartitionReader
    implements ContinuousInputPartitionReader<InternalRow> {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final SubscriptionPath subscriptionPath;
  private final PullSubscriber<SequencedMessage> subscriber;
  private final BlockingDeque<SequencedMessage> messages = new LinkedBlockingDeque<>();
  private SparkPartitionOffset currentOffset;
  private SequencedMessage currentMsg;

  PslContinuousInputPartitionReader(
      SubscriptionPath subscriptionPath,
      SparkPartitionOffset startOffset,
      PullSubscriber<SequencedMessage> subscriber,
      ScheduledExecutorService pullExecutorService) {
    this.subscriptionPath = subscriptionPath;
    this.currentOffset = startOffset;
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
          SparkPartitionOffset.builder()
              .partition(currentOffset.partition())
              .offset(currentMsg.offset().value())
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
