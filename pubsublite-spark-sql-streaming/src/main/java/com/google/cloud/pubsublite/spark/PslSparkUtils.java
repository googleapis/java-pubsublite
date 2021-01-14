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

import static com.google.common.base.Preconditions.checkArgument;
import static scala.collection.JavaConverters.asScalaBufferConverter;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.common.collect.ListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.util.ArrayBasedMapData;
import org.apache.spark.sql.catalyst.util.GenericArrayData;
import org.apache.spark.unsafe.types.ByteArray;
import org.apache.spark.unsafe.types.UTF8String;

public class PslSparkUtils {
  private static ArrayBasedMapData convertAttributesToSparkMap(
      ListMultimap<String, ByteString> attributeMap) {

    List<UTF8String> keyList = new ArrayList<>();
    List<GenericArrayData> valueList = new ArrayList<>();

    attributeMap
        .asMap()
        .forEach(
            (key, value) -> {
              keyList.add(UTF8String.fromString(key));
              List<byte[]> attributeVals =
                  value.stream()
                      .map(v -> ByteArray.concat(v.toByteArray()))
                      .collect(Collectors.toList());
              valueList.add(new GenericArrayData(asScalaBufferConverter(attributeVals).asScala()));
            });

    return new ArrayBasedMapData(
        new GenericArrayData(asScalaBufferConverter(keyList).asScala()),
        new GenericArrayData(asScalaBufferConverter(valueList).asScala()));
  }

  public static InternalRow toInternalRow(
      SequencedMessage msg, SubscriptionPath subscription, Partition partition) {
    List<Object> list =
        new ArrayList<>(
            Arrays.asList(
                UTF8String.fromString(subscription.toString()),
                partition.value(),
                msg.offset().value(),
                ByteArray.concat(msg.message().key().toByteArray()),
                ByteArray.concat(msg.message().data().toByteArray()),
                Timestamps.toMillis(msg.publishTime()),
                msg.message().eventTime().isPresent()
                    ? Timestamps.toMillis(msg.message().eventTime().get())
                    : null,
                convertAttributesToSparkMap(msg.message().attributes())));
    return InternalRow.apply(asScalaBufferConverter(list).asScala());
  }

  public static SparkSourceOffset toSparkSourceOffset(PslSourceOffset pslSourceOffset) {
    return new SparkSourceOffset(
        pslSourceOffset.partitionOffsetMap().entrySet().stream()
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
    Map<Partition, Offset> pslSourceOffsetMap = new HashMap<>();
    for (long i = 0; i < partitionCount; i++) {
      Partition p = Partition.of(i);
      checkArgument(sparkSourceOffset.getPartitionOffsetMap().containsKey(p));
      pslSourceOffsetMap.put(
          p, Offset.of(sparkSourceOffset.getPartitionOffsetMap().get(p).offset() + 1));
    }
    return PslSourceOffset.builder().partitionOffsetMap(pslSourceOffsetMap).build();
  }

  public static PslPartitionOffset toPslPartitionOffset(SparkPartitionOffset sparkPartitionOffset) {
    return PslPartitionOffset.builder()
        .partition(sparkPartitionOffset.partition())
        .offset(Offset.of(sparkPartitionOffset.offset() + 1))
        .build();
  }

  public static SparkSourceOffset getSparkStartOffset(
      CursorClient cursorClient, SubscriptionPath subscriptionPath, long topicPartitionCount) {
    try {
      Map<Partition, com.google.cloud.pubsublite.Offset> pslSourceOffsetMap = new HashMap<>();
      for (int i = 0; i < topicPartitionCount; i++) {
        pslSourceOffsetMap.put(Partition.of(i), com.google.cloud.pubsublite.Offset.of(0));
      }
      cursorClient
          .listPartitionCursors(subscriptionPath)
          .get()
          .forEach(pslSourceOffsetMap::replace);
      return PslSparkUtils.toSparkSourceOffset(
          PslSourceOffset.builder().partitionOffsetMap(pslSourceOffsetMap).build());
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(
          "Failed to get information from PSL and construct startOffset", e);
    }
  }
}
