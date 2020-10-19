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

import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import io.grpc.StatusException;
import java.io.IOException;
import java.util.Map;

public class RoutingPublisher extends TrivialProxyService implements Publisher<PublishMetadata> {
  private final Map<Partition, Publisher<PublishMetadata>> partitionPublishers;
  private final RoutingPolicy policy;

  RoutingPublisher(
      Map<Partition, Publisher<PublishMetadata>> partitionPublishers, RoutingPolicy policy)
      throws StatusException {
    super(partitionPublishers.values());
    this.partitionPublishers = partitionPublishers;
    this.policy = policy;
  }

  // Publisher implementation.
  @Override
  public ApiFuture<PublishMetadata> publish(Message message) {
    try {
      Partition routedPartition =
          message.key().isEmpty() ? policy.routeWithoutKey() : policy.route(message.key());
      checkState(
          partitionPublishers.containsKey(routedPartition),
          String.format(
              "Routed to partition %s for which there is no publisher available.",
              routedPartition));
      return partitionPublishers.get(routedPartition).publish(message);
    } catch (StatusException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void flush() throws IOException {
    for (Publisher<PublishMetadata> publisher : partitionPublishers.values()) {
      publisher.flush();
    }
  }
}
