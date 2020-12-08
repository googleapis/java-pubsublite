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
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import java.io.Serializable;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.ContinuousInputPartition;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;
import org.apache.spark.sql.sources.v2.reader.streaming.PartitionOffset;

public class PslContinuousInputPartition
    implements ContinuousInputPartition<InternalRow>, Serializable {

  private final SparkPartitionOffset startOffset;
  private final SubscriptionPath subscriptionPath;
  private final FlowControlSettings flowControlSettings;

  public PslContinuousInputPartition(
      SparkPartitionOffset startOffset,
      SubscriptionPath subscriptionPath,
      FlowControlSettings flowControlSettings) {
    this.startOffset = startOffset;
    this.subscriptionPath = subscriptionPath;
    this.flowControlSettings = flowControlSettings;
  }

  @Override
  public InputPartitionReader<InternalRow> createContinuousReader(PartitionOffset offset) {
    assert SparkPartitionOffset.class.isAssignableFrom(offset.getClass())
        : "offset is not assignable to SparkPartitionOffset";

    SparkPartitionOffset sparkPartitionOffset = (SparkPartitionOffset) offset;
    PslPartitionOffset pslPartitionOffset =
        PslSparkUtils.toPslPartitionOffset(sparkPartitionOffset);

    BlockingPullSubscriber subscriber;
    try {
      subscriber =
          new BlockingPullSubscriber(
              // TODO(jiangmichael): Pass credentials settings here.
              (consumer) ->
                  SubscriberBuilder.newBuilder()
                      .setSubscriptionPath(subscriptionPath)
                      .setPartition(pslPartitionOffset.partition())
                      .setContext(PubsubContext.of(Constants.FRAMEWORK))
                      .setMessageConsumer(consumer)
                      .build(),
              flowControlSettings,
              SeekRequest.newBuilder()
                  .setCursor(
                      Cursor.newBuilder().setOffset(pslPartitionOffset.offset().value()).build())
                  .build());
    } catch (CheckedApiException e) {
      throw new IllegalStateException(
          "Unable to create PSL subscriber for " + startOffset.toString(), e);
    }
    return new PslContinuousInputPartitionReader(
        subscriptionPath, sparkPartitionOffset, subscriber);
  }

  @Override
  public InputPartitionReader<InternalRow> createPartitionReader() {
    return createContinuousReader(startOffset);
  }
}
