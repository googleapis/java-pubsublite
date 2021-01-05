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

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;

public interface TopicStatsClient extends ApiBackgroundResource {
  static TopicStatsClient create(TopicStatsClientSettings settings) throws ApiException {
    return settings.instantiate();
  }

  /** The Google Cloud region this client operates on. */
  CloudRegion region();

  /**
   * Compute statistics about the messages between two cursors in a topic partition.
   *
   * @param path The topic to compute statistics on
   * @param partition The partition to compute statistics for
   * @param start The start cursor
   * @param end The end cursor
   * @return A future that will have either an error {@link ApiException} or the
   *     ComputeMessageStatistics on success.
   */
  ApiFuture<ComputeMessageStatsResponse> computeMessageStats(
      TopicPath path, Partition partition, Offset start, Offset end);

  /**
   * Compute the head cursor for the partition. The head cursor's offset is guaranteed to be before
   * or equal to all messages which have not yet been acknowledged to be published, and greater than
   * the offset of any message whose publish has already been acknowledged. It is 0 if there have
   * never been messages on the partition.
   *
   * @param path The topic to compute head cursor on
   * @param partition The partition to compute head cursor for
   * @return A future that will have either an error {@link ApiException} or the
   *     ComputeHeadCursorResponse on success.
   */
  ApiFuture<ComputeHeadCursorResponse> computeHeadCursor(TopicPath path, Partition partition);
}
