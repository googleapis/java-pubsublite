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

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriber;
import com.google.cloud.pubsublite.internal.BlockingPullSubscriberImpl;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.SubscriberFactory;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;

public class PslMicroBatchInputPartition implements InputPartition<InternalRow> {

  private final SubscriberFactory subscriberFactory;
  private final SparkPartitionOffset startOffset;
  private final SparkPartitionOffset endOffset;
  private final SubscriptionPath subscriptionPath;
  private final FlowControlSettings flowControlSettings;

  public PslMicroBatchInputPartition(
      SubscriptionPath subscriptionPath,
      FlowControlSettings flowControlSettings,
      SparkPartitionOffset startOffset,
      SparkPartitionOffset endOffset,
      SubscriberFactory subscriberFactory) {
    this.startOffset = startOffset;
    this.endOffset = endOffset;
    this.subscriptionPath = subscriptionPath;
    this.flowControlSettings = flowControlSettings;
    this.subscriberFactory = subscriberFactory;
  }

  @Override
  public InputPartitionReader<InternalRow> createPartitionReader() {
    BlockingPullSubscriber subscriber;
    try {
      subscriber =
          new BlockingPullSubscriberImpl(
              subscriberFactory,
              flowControlSettings,
              SeekRequest.newBuilder()
                  .setCursor(
                      Cursor.newBuilder()
                          .setOffset(
                              PslSparkUtils.toPslPartitionOffset(startOffset).offset().value())
                          .build())
                  .build());
    } catch (CheckedApiException e) {
      throw new IllegalStateException(
          "Unable to create PSL subscriber for " + endOffset.partition(), e);
    }
    return new PslMicroBatchInputPartitionReader(subscriptionPath, endOffset, subscriber);
  }
}
