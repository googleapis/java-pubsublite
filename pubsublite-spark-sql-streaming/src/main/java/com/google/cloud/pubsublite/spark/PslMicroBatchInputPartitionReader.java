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

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.common.flogger.GoogleLogger;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.ContinuousInputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

public class PslMicroBatchInputPartitionReader
        implements InputPartitionReader<InternalRow> {
    private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

    private final SubscriptionPath subscriptionPath;
    private final Partition partition;
    private final RangedBlockingPullSubscriber subscriber;
    private SequencedMessage currentMsg;

    PslMicroBatchInputPartitionReader(
            SubscriptionPath subscriptionPath,
            Partition partition,
            RangedBlockingPullSubscriber subscriber) {
        this.subscriptionPath = subscriptionPath;
        this.partition = partition;
        this.subscriber = subscriber;
        this.currentMsg = null;
    }

    @Override
    public boolean next() {
        try {
            currentMsg = subscriber.pull();
            return currentMsg != null;
        } catch (InterruptedException | CheckedApiException e) {
            throw new IllegalStateException("Failed to retrieve messages.", e);
        }
    }

    @Override
    public InternalRow get() {
        assert currentMsg != null;
        return PslSparkUtils.toInternalRow(currentMsg, subscriptionPath, partition);
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
