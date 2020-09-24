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
import com.google.api.gax.core.BackgroundResource;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import io.grpc.StatusException;
import java.util.Map;

public interface CursorClient extends BackgroundResource {

  static CursorClient create(CursorClientSettings settings) throws StatusException {
    return settings.instantiate();
  }

  /** The Google Cloud region this client operates on. */
  CloudRegion region();

  /**
   * List the cursors for a given subscription.
   *
   * @param path The subscription to list cursors for.
   * @return A future holding the map of Partition to Offset of the cursors.
   */
  ApiFuture<Map<Partition, Offset>> listPartitionCursors(SubscriptionPath path);

  /**
   * Commit a single cursor.
   *
   * @param path The subscription to commit a cursor for.
   * @param partition The partition to commit a cursor for.
   * @param offset The offset to commit.
   * @return A future for the operation's completion.
   */
  ApiFuture<Void> commitCursor(SubscriptionPath path, Partition partition, Offset offset);

  /**
   * Tear down this admin client.
   *
   * @throws StatusException on a failure to properly terminate.
   */
  @Override
  void close() throws StatusException;
}
