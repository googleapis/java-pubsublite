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

package com.google.cloud.pubsublite.kafka;

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.proto.SeekRequest;
import io.grpc.StatusException;

/** A factory for making new PullSubscribers for a given partition of a subscription. */
interface PullSubscriberFactory {
  PullSubscriber<SequencedMessage> newPullSubscriber(Partition partition, SeekRequest initial)
      throws StatusException;
}
