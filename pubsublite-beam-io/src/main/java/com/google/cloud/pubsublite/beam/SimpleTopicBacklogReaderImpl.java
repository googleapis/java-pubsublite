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

package com.google.cloud.pubsublite.beam;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.common.collect.ImmutableMap;

class SimpleTopicBacklogReaderImpl implements SimpleTopicBacklogReader {
  private final TopicBacklogReader underlying;
  private final Partition partition;

  SimpleTopicBacklogReaderImpl(TopicBacklogReader underlying, Partition partition) {
    this.underlying = underlying;
    this.partition = partition;
  }

  @Override
  public ComputeMessageStatsResponse computeStats(Offset offset) throws ApiException {
    try {
      return underlying.computeMessageStats(ImmutableMap.of(partition, offset)).get();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @Override
  public void close() {
    try {
      underlying.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
