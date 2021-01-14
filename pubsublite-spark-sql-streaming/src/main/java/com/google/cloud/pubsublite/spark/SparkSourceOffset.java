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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.cloud.pubsublite.Partition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SparkSourceOffset
    extends org.apache.spark.sql.sources.v2.reader.streaming.Offset {
  private static final ObjectMapper objectMapper =
      new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

  // Using a map to ensure unique partitions.
  private final ImmutableMap<Partition, SparkPartitionOffset> partitionOffsetMap;

  public SparkSourceOffset(Map<Partition, SparkPartitionOffset> map) {
    validateMap(map);
    this.partitionOffsetMap = ImmutableMap.copyOf(map);
  }

  private static void validateMap(Map<Partition, SparkPartitionOffset> map) {
    map.forEach(
        (k, v) ->
            checkArgument(
                Objects.equals(k, v.partition()),
                "Key(Partition) and value(SparkPartitionOffset)'s partition don't match."));
  }

  public static SparkSourceOffset merge(SparkSourceOffset o1, SparkSourceOffset o2) {
    Map<Partition, SparkPartitionOffset> result = new HashMap<>(o1.partitionOffsetMap);
    o2.partitionOffsetMap.forEach(
        (k, v) ->
            result.merge(
                k,
                v,
                (v1, v2) ->
                    SparkPartitionOffset.builder()
                        .partition(Partition.of(k.value()))
                        .offset(Collections.max(ImmutableList.of(v1.offset(), v2.offset())))
                        .build()));
    return new SparkSourceOffset(result);
  }

  public static SparkSourceOffset merge(SparkPartitionOffset[] offsets) {
    Map<Partition, SparkPartitionOffset> map = new HashMap<>();
    for (SparkPartitionOffset po : offsets) {
      checkArgument(
          !map.containsKey(po.partition()), "Multiple PslPartitionOffset has same partition.");
      map.put(
          po.partition(),
          SparkPartitionOffset.builder().partition(po.partition()).offset(po.offset()).build());
    }
    return new SparkSourceOffset(map);
  }

  @SuppressWarnings("unchecked")
  public static SparkSourceOffset fromJson(String json) {
    Map<String, Number> map;
    try {
      // TODO: Use TypeReference instead of Map.class, currently TypeReference breaks spark with
      // java.lang.LinkageError: loader constraint violation: loader previously initiated loading
      // for a different type.
      map = objectMapper.readValue(json, Map.class);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to deserialize PslSourceOffset.", e);
    }
    Map<Partition, SparkPartitionOffset> partitionOffsetMap =
        map.entrySet().stream()
            .collect(
                Collectors.toMap(
                    e -> Partition.of(Long.parseLong(e.getKey())),
                    e ->
                        SparkPartitionOffset.builder()
                            .partition(Partition.of(Long.parseLong(e.getKey())))
                            .offset(e.getValue().longValue())
                            .build()));
    return new SparkSourceOffset(partitionOffsetMap);
  }

  public Map<Partition, SparkPartitionOffset> getPartitionOffsetMap() {
    return this.partitionOffsetMap;
  }

  @Override
  public String json() {
    try {
      Map<Long, Long> map =
          partitionOffsetMap.entrySet().stream()
              .collect(Collectors.toMap(e -> e.getKey().value(), e -> e.getValue().offset()));
      return objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Unable to serialize PslSourceOffset.", e);
    }
  }
}
