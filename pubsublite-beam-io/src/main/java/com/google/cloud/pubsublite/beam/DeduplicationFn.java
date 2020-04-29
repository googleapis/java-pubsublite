// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.beam;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.DelegateCoder;
import org.apache.beam.sdk.coders.InstantCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.MapCoder;
import org.apache.beam.sdk.state.SetState;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.joda.time.Instant;

// A DeduplicationFn takes a KV from a hashed version of KeyT to a KV of KeyT and ValueT and
// deduplicates based on KeyT. KeyT must provide a non-identity implementation of equals.
class DeduplicationFn<KeyT, ValueT> extends DoFn<KV<Integer, KV<KeyT, ValueT>>, KV<KeyT, ValueT>> {
  private final DeduplicationFnOptions<KeyT> options;

  // A state cell holding the set of recently received keys.
  @SuppressWarnings("unused")
  @StateId("keySet")
  private final StateSpec<SetState<KeyT>> keySetSpec;
  // A state cell holding a mapping from event times to the keys with that event time for
  // garbage collection.
  @SuppressWarnings("unused")
  @StateId("eventTimeGcMap")
  private final StateSpec<ValueState<TreeMap<Instant, List<KeyT>>>> eventTimeGcMapSpec;

  private Map<Instant, List<KeyT>> toDelegateFn(TreeMap<Instant, List<KeyT>> source) {
    return source;
  }

  private TreeMap<Instant, List<KeyT>> fromDelegateFn(Map<Instant, List<KeyT>> delegate) {
    TreeMap<Instant, List<KeyT>> output = new TreeMap<>();
    delegate.forEach(
        (instant, keyList) -> {
          output.put(instant, Lists.newArrayList(keyList));
        });
    return output;
  }

  private Coder<TreeMap<Instant, List<KeyT>>> treeMapCoder() {
    return DelegateCoder.of(
        MapCoder.of(InstantCoder.of(), ListCoder.of(options.keyCoder())),
        this::toDelegateFn,
        this::fromDelegateFn);
  }

  DeduplicationFn(DeduplicationFnOptions<KeyT> options) {
    this.options = options;
    this.keySetSpec = StateSpecs.set(options.keyCoder());
    this.eventTimeGcMapSpec = StateSpecs.value(treeMapCoder());
  }

  @ProcessElement
  public void processElement(
      ProcessContext context,
      OutputReceiver<KV<KeyT, ValueT>> messageReceiver,
      @StateId("keySet") SetState<KeyT> keySet,
      @StateId("eventTimeGcMap") ValueState<TreeMap<Instant, List<KeyT>>> eventTimeGcMap,
      @Element KV<Integer, KV<KeyT, ValueT>> element) {
    Instant eventTime = context.timestamp();
    TreeMap<Instant, List<KeyT>> eventTimeMap = eventTimeGcMap.read();
    if (eventTimeMap == null) {
      eventTimeMap = new TreeMap<>();
    }
    evict(eventTime, keySet, eventTimeMap);

    KeyT key = element.getValue().getKey();
    if (!keySet.addIfAbsent(key).read()) {
      return;
    }
    List<KeyT> gcList = eventTimeMap.computeIfAbsent(eventTime, k -> new ArrayList<>());
    gcList.add(key);
    evict(eventTime, keySet, eventTimeMap);
    eventTimeGcMap.write(eventTimeMap);
    messageReceiver.output(element.getValue());
  }

  private void evict(
      Instant eventTime, SetState<KeyT> keySet, TreeMap<Instant, List<KeyT>> eventTimeMap) {
    Instant latestToGc = eventTime.minus(options.gcDelay());
    while (!eventTimeMap.isEmpty() && eventTimeMap.firstKey().isBefore(latestToGc)) {
      eventTimeMap.pollFirstEntry().getValue().forEach(keySet::remove);
    }
  }
}
