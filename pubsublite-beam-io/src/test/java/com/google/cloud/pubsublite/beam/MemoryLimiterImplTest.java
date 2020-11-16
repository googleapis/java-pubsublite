/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.pubsublite.beam;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MemoryLimiterImplTest {
  @Test
  public void memoryDeducted() {
    long total = 1 << 20;
    MemoryLimiter limiter = new MemoryLimiterImpl(total);
    MemoryLease lease1 = limiter.acquireMemory(Long.MAX_VALUE);
    long bytes1 = total / 2;
    assertThat(lease1.byteCount()).isEqualTo(bytes1);
    MemoryLease lease2 = limiter.acquireMemory(Long.MAX_VALUE);
    long bytes2 = total / 3;
    assertThat(lease2.byteCount()).isEqualTo(bytes2);
    MemoryLease limitedByOutstanding = limiter.acquireMemory(Long.MAX_VALUE);
    long currentAvailable = total - bytes1 - bytes2;
    assertThat(limitedByOutstanding.byteCount()).isEqualTo(currentAvailable * 3 / 4);
    limitedByOutstanding.close();
    lease2.close();
    MemoryLease lease3 = limiter.acquireMemory(Long.MAX_VALUE);
    assertThat(lease3.byteCount()).isEqualTo(bytes2);
    MemoryLease lease4 = limiter.acquireMemory(1024);
    assertThat(lease4.byteCount()).isEqualTo(1024);
  }
}
