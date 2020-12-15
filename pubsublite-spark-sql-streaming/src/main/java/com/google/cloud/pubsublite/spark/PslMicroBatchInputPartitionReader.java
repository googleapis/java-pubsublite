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
import com.google.cloud.pubsublite.internal.BlockingPullSubscriber;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;

public class PslMicroBatchInputPartitionReader implements InputPartitionReader<InternalRow> {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private static final Duration SUBSCRIBER_PULL_TIMEOUT = Duration.ofSeconds(10);

  private final SubscriptionPath subscriptionPath;
  private final SparkPartitionOffset endOffset;
  private final BlockingPullSubscriber subscriber;
  private SequencedMessage currentMsg = null;
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
        // NOTE this would only happen when a broker abnormally crashes and fails to
        // write the unused reserved offsets back to storage. In this case, the
        // spark pipeline could get stuck forever until a new message is published at
        // head.
        log.atWarning().log(
            "Unable to get any messages in last " + SUBSCRIBER_PULL_TIMEOUT.toString());
      } catch (Throwable t) {
        throw new IllegalStateException("Failed to retrieve messages.", t);
      }
    }
    // since next() is only called one at a time, we are sure that the message is
    // available to this thread.
    assert msg.isPresent();
    currentMsg = msg.get();
    if (currentMsg.offset().value() >= endOffset.offset()) {
      batchFulfilled = true;
    }
    return true;
  }

  @Override
  public InternalRow get() {
    assert currentMsg != null;
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
