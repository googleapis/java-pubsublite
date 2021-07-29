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
import static com.google.cloud.pubsublite.internal.wire.ApiServiceUtils.blockingShutdown;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.RoutingPolicy;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.LongStream;

public class PartitionCountWatchingPublisher extends ProxyService
    implements Publisher<MessageMetadata> {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();
  private final PartitionPublisherFactory publisherFactory;
  private final RoutingPolicy.Factory policyFactory;

  private static class PartitionsWithRouting {
    public final ImmutableMap<Partition, Publisher<MessageMetadata>> publishers;
    private final RoutingPolicy routingPolicy;

    private PartitionsWithRouting(
        ImmutableMap<Partition, Publisher<MessageMetadata>> publishers,
        RoutingPolicy routingPolicy) {
      this.publishers = publishers;
      this.routingPolicy = routingPolicy;
    }

    public ApiFuture<MessageMetadata> publish(Message message) throws CheckedApiException {
      try {
        Partition routedPartition =
            message.key().isEmpty()
                ? routingPolicy.routeWithoutKey()
                : routingPolicy.route(message.key());
        checkState(
            publishers.containsKey(routedPartition),
            String.format(
                "Routed to partition %s for which there is no publisher available.",
                routedPartition));
        return publishers.get(routedPartition).publish(message);
      } catch (Throwable t) {
        throw toCanonical(t);
      }
    }

    public void cancelOutstandingPublishes() {
      for (Publisher<MessageMetadata> publisher : publishers.values()) {
        publisher.cancelOutstandingPublishes();
      }
    }

    public void flush() throws IOException {
      for (Publisher<MessageMetadata> publisher : publishers.values()) {
        publisher.flush();
      }
    }

    public void stop() {
      blockingShutdown(publishers.values());
    }
  }

  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  @GuardedBy("monitor.monitor")
  private Optional<PartitionsWithRouting> partitionsWithRouting = Optional.empty();

  PartitionCountWatchingPublisher(
      PartitionPublisherFactory publisherFactory,
      RoutingPolicy.Factory policyFactory,
      PartitionCountWatcher.Factory configWatcherFactory) {
    this.publisherFactory = publisherFactory;
    this.policyFactory = policyFactory;
    PartitionCountWatcher configWatcher = configWatcherFactory.newWatcher(this::handleConfig);
    addServices(configWatcher);
  }

  @Override
  public ApiFuture<MessageMetadata> publish(Message message) {
    Optional<PartitionsWithRouting> partitions;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      partitions = partitionsWithRouting;
    }
    if (!partitions.isPresent()) {
      throw new IllegalStateException("Publish called before start or after shutdown");
    }
    try {
      return partitions.get().publish(message);
    } catch (CheckedApiException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
  }

  @Override
  public void cancelOutstandingPublishes() {
    Optional<PartitionsWithRouting> partitions;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      partitions = partitionsWithRouting;
    }
    if (!partitions.isPresent()) {
      throw new IllegalStateException(
          "Cancel outstanding publishes called before start or after shutdown");
    }
    partitions.get().cancelOutstandingPublishes();
  }

  @Override
  public void flush() throws IOException {
    Optional<PartitionsWithRouting> partitions;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      partitions = partitionsWithRouting;
    }
    if (!partitions.isPresent()) {
      throw new IllegalStateException("Publish called before start or after shutdown");
    }
    partitions.get().flush();
  }

  private ImmutableMap<Partition, Publisher<MessageMetadata>> getNewPartitionPublishers(
      LongStream newPartitions) {
    ImmutableMap.Builder<Partition, Publisher<MessageMetadata>> mapBuilder = ImmutableMap.builder();
    newPartitions.forEach(
        i -> {
          Publisher<MessageMetadata> p = publisherFactory.newPublisher(Partition.of(i));
          p.addListener(
              new Listener() {
                @Override
                public void failed(State from, Throwable failure) {
                  onPermanentError(toCanonical(failure));
                }
              },
              SystemExecutors.getFuturesExecutor());
          mapBuilder.put(Partition.of(i), p);
          p.startAsync();
        });
    ImmutableMap<Partition, Publisher<MessageMetadata>> partitions = mapBuilder.build();
    partitions.values().forEach(ApiService::awaitRunning);
    return partitions;
  }

  private void handleConfig(long partitionCount) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (shutdown) {
        return;
      }
      Optional<PartitionsWithRouting> current = partitionsWithRouting;
      long currentSize = current.map(withRouting -> withRouting.publishers.size()).orElse(0);
      if (partitionCount == currentSize) {
        return;
      }
      if (partitionCount < currentSize) {
        log.atWarning().log(
            "Received an unexpected decrease in partition count. Previous partition count %s, new count %s",
            currentSize, partitionCount);
        return;
      }
      ImmutableMap.Builder<Partition, Publisher<MessageMetadata>> mapBuilder =
          ImmutableMap.builder();
      current.ifPresent(p -> p.publishers.forEach(mapBuilder::put));
      getNewPartitionPublishers(LongStream.range(currentSize, partitionCount))
          .forEach(mapBuilder::put);

      partitionsWithRouting =
          Optional.of(
              new PartitionsWithRouting(
                  mapBuilder.build(), policyFactory.newPolicy(partitionCount)));
    }
  }

  @Override
  protected void start() {}

  @Override
  protected void stop() {
    Optional<PartitionsWithRouting> current;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      current = partitionsWithRouting;
      partitionsWithRouting = Optional.empty();
    }
    current.ifPresent(PartitionsWithRouting::stop);
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    try {
      stop();
    } catch (Exception e) {
      log.atWarning().withCause(e).log("Encountered exception while trying to handle failure");
    }
  }
}
