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

package com.google.cloud.pubsublite.internal;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.Partition;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DefaultRoutingPolicyTest {
  private static final int NUM_PARTITIONS = 29;
  private static final RoutingPolicy policy = new DefaultRoutingPolicy(NUM_PARTITIONS);

  private static Map<ByteString, Partition> loadTestCases() throws Exception {
    Gson gson = new Gson();
    String json =
        new String(
            Files.readAllBytes(
                Paths.get(
                    DefaultRoutingPolicyTest.class.getResource("/routing_tests.json").toURI())));
    Map<String, Double> map = gson.fromJson(json, Map.class);
    ImmutableMap.Builder<ByteString, Partition> output = ImmutableMap.builder();
    for (String key : map.keySet()) {
      output.put(ByteString.copyFromUtf8(key), Partition.of((int) map.get(key).doubleValue()));
    }
    return output.build();
  }

  @Test
  public void routingPerformedCorrectly() throws Exception {
    Map<ByteString, Partition> map = loadTestCases();
    ImmutableMap.Builder<ByteString, Partition> results = ImmutableMap.builder();
    for (ByteString key : map.keySet()) {
      results.put(key, policy.route(key));
    }
    assertThat(results.build()).containsExactlyEntriesIn(map);
  }
}
