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

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.internal.PublishSequenceNumber;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import java.util.Collection;

interface BatchPublisher extends AutoCloseable {
  /**
   * Publish the batch of messages, with the given sequence number of the first message in the
   * batch. Failures are communicated out of band.
   */
  void publish(Collection<PubSubMessage> messages, PublishSequenceNumber firstSequenceNumber);
}
