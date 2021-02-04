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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;
import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import java.io.IOException;
import java.util.Map;

public class RoutingPublisher extends TrivialProxyService implements Publisher<MessageMetadata> {
  private final Map<Partition, Publisher<MessageMetadata>> partitionPublishers;
  private final RoutingPolicy policy;

  RoutingPublisher(
      Map<Partition, Publisher<MessageMetadata>> partitionPublishers, RoutingPolicy policy)
      throws ApiException {
    super(partitionPublishers.values());
    this.partitionPublishers = partitionPublishers;
    this.policy = policy;
  }

  // Publisher implementation.
  @Override
  public ApiFuture<MessageMetadata> publish(Message message) {
    try {
      Partition routedPartition =
          message.key().isEmpty() ? policy.routeWithoutKey() : policy.route(message.key());
      checkState(
          partitionPublishers.containsKey(routedPartition),
          String.format(
              "Routed to partition %s for which there is no publisher available.",
              routedPartition));
      return partitionPublishers.get(routedPartition).publish(message);
    } catch (Throwable t) {
      CheckedApiException e = toCanonical(t);
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void cancelOutstandingPublishes() {
    for (Publisher<MessageMetadata> publisher : partitionPublishers.values()) {
      publisher.cancelOutstandingPublishes();
    }
  }

  @Override
  public void flush() throws IOException {
    for (Publisher<MessageMetadata> publisher : partitionPublishers.values()) {
      publisher.flush();
    }
  }
}
