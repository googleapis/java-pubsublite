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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class PslSourceOffset extends org.apache.spark.sql.sources.v2.reader.streaming.Offset {
  private static final ObjectMapper objectMapper =
      new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

  private final ImmutableMap<Partition, Offset> partitionOffsetMap;

  public PslSourceOffset(Map<Partition, Offset> map) {
    this.partitionOffsetMap = ImmutableMap.copyOf(map);
  }

  public static PslSourceOffset merge(PslSourceOffset o1, PslSourceOffset o2) {
    Map<Partition, Offset> result = new HashMap<>(o1.partitionOffsetMap);
    o2.partitionOffsetMap.forEach(
        (k, v) -> result.merge(k, v, (v1, v2) -> Collections.max(ImmutableList.of(v1, v2))));
    return new PslSourceOffset(result);
  }

  public static PslSourceOffset merge(PslPartitionOffset[] offsets) {
    Map<Partition, Offset> map = new HashMap<>();
    for (PslPartitionOffset po : offsets) {
      assert !map.containsKey(po.partition()) : "Multiple PslPartitionOffset has same partition.";
      map.put(po.partition(), po.offset());
    }
    return new PslSourceOffset(map);
  }

  public static PslSourceOffset fromJson(String json) {
    Map<Long, Long> map;
    try {
      map = objectMapper.readValue(json, new TypeReference<Map<Long, Long>>() {});
    } catch (IOException e) {
      throw new IllegalStateException("Unable to deserialize PslSourceOffset.", e);
    }
    Map<Partition, Offset> partitionOffsetMap =
        map.entrySet().stream()
            .collect(Collectors.toMap(e -> Partition.of(e.getKey()), e -> Offset.of(e.getValue())));
    return new PslSourceOffset(partitionOffsetMap);
  }

  public Map<Partition, Offset> getPartitionOffsetMap() {
    return this.partitionOffsetMap;
  }

  @Override
  public String json() {
    try {
      Map<Long, Long> map =
          partitionOffsetMap.entrySet().stream()
              .collect(Collectors.toMap(e -> e.getKey().value(), e -> e.getValue().value()));
      return objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Unable to serialize PslSourceOffset.", e);
    }
  }
}
