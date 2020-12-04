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
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.sql.catalyst.InternalRow;

public class PslSparkUtils {
  public static InternalRow toInternalRow(
      SequencedMessage msg, SubscriptionPath subscription, Partition partition) {
    return InternalRow.apply(
        scala.collection.JavaConverters.asScalaBuffer(
            ImmutableList.of(
                subscription.toString(),
                partition.value(),
                msg.offset().value(),
                msg.message().key(),
                msg.message().data(),
                msg.publishTime(),
                msg.message().eventTime(),
                msg.message().attributes())));
  }

  public static SparkSourceOffset toSparkSourceOffset(PslSourceOffset pslSourceOffset) {
    return new SparkSourceOffset(
        pslSourceOffset.getPartitionOffsetMap().entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e ->
                        SparkPartitionOffset.builder()
                            .partition(Partition.of(e.getKey().value()))
                            .offset(e.getValue().value() - 1)
                            .build())));
  }

  public static PslSourceOffset toPslSourceOffset(SparkSourceOffset sparkSourceOffset) {
    long partitionCount = sparkSourceOffset.getPartitionOffsetMap().size();
    PslSourceOffset.Builder pslSourceOffsetBuilder = PslSourceOffset.newBuilder(partitionCount);
    for (long i = 0; i < partitionCount; i++) {
      Partition p = Partition.of(i);
      assert sparkSourceOffset.getPartitionOffsetMap().containsKey(p);
      pslSourceOffsetBuilder.set(
          p, Offset.of(sparkSourceOffset.getPartitionOffsetMap().get(p).offset() + 1));
    }
    return pslSourceOffsetBuilder.build();
  }

  public static PslPartitionOffset toPslPartitionOffset(SparkPartitionOffset sparkPartitionOffset) {
    return PslPartitionOffset.builder()
        .partition(sparkPartitionOffset.partition())
        .offset(Offset.of(sparkPartitionOffset.offset() + 1))
        .build();
  }
}
