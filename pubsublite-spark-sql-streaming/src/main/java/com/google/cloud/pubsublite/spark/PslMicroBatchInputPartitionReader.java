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
import com.google.cloud.pubsublite.internal.BlockingPullSubscriber;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;

public class PslMicroBatchInputPartitionReader implements InputPartitionReader<InternalRow> {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private static final Duration SUBSCRIBER_PULL_TIMEOUT = Duration.ofSeconds(10);

  private final SubscriptionPath subscriptionPath;
  private final SparkPartitionOffset endOffset;
  private final BlockingPullSubscriber subscriber;
  @Nullable private SequencedMessage currentMsg = null;
  private boolean batchFulfilled = false;

  @VisibleForTesting
  PslMicroBatchInputPartitionReader(
      SubscriptionPath subscriptionPath,
      SparkPartitionOffset endOffset,
      BlockingPullSubscriber subscriber) {
    this.subscriptionPath = subscriptionPath;
    this.subscriber = subscriber;
    this.endOffset = endOffset;
  }

  @Override
  public boolean next() {
    if (batchFulfilled) {
      return false;
    }
    Optional<SequencedMessage> msg;
    while (true) {
      try {
        subscriber.onData().get(SUBSCRIBER_PULL_TIMEOUT.getSeconds(), TimeUnit.SECONDS);
        msg = subscriber.messageIfAvailable();
        break;
      } catch (TimeoutException e) {
        log.atWarning().log("Unable to get any messages in last " + SUBSCRIBER_PULL_TIMEOUT);
      } catch (Throwable t) {
        throw new IllegalStateException("Failed to retrieve messages.", t);
      }
    }
    // since next() is only called on one thread at a time, we are sure that the message is
    // available to this thread.
    checkState(msg.isPresent());
    currentMsg = msg.get();
    if (currentMsg.offset().value() == endOffset.offset()) {
      // this is the last msg for the batch.
      batchFulfilled = true;
    } else if (currentMsg.offset().value() > endOffset.offset()) {
      batchFulfilled = true;
      return false;
    }
    return true;
  }

  @Override
  public InternalRow get() {
    checkState(currentMsg != null);
    return PslSparkUtils.toInternalRow(currentMsg, subscriptionPath, endOffset.partition());
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
