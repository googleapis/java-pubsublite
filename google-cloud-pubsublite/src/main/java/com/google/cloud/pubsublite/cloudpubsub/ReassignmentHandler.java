/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.cloud.pubsublite.Partition;
import java.util.Set;

/**
 * A ReassignmentHandler is called any time a new partition assignment is received from the server.
 * It will be called with both the previous and new assignments as decided by the backend.
 *
 * <p>The client library will not acknowledge the assignment until handleReassignment returns. The
 * assigning backend will not assign any of the partitions in `before` to another server unless the
 * assignment is acknowledged, or a client takes too long to acknowledged (currently 30 seconds from
 * the time the assignment is sent from server's point of view).
 *
 * <p>Because of the above, as long as reassignment handling is processed quickly, it can be used to
 * abort outstanding operations on partitions which are being assigned away from this client, or to
 * pre-warm state which will be used by the MessageReceiver.
 */
public interface ReassignmentHandler {
  /**
   * Called with the previous and new assignment delivered to this client on an assignment change.
   * The assignment will not be acknowledged until this method returns, so it should complete
   * quickly, or the backend will assume it is non-responsive and assign all partitions away without
   * waiting for acknowledgement.
   *
   * <p>handleReassignment will only be called after no new messages will be delivered for the
   * partition.
   *
   * <p>Acks or nacks on messages from partitions being assigned away will have no effect.
   *
   * @param before the previous assignment
   * @param after the new assignment
   */
  void handleReassignment(Set<Partition> before, Set<Partition> after);
}
