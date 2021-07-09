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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;
import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.wire.ApiServiceUtils.blockingShutdown;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.Assigner;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssigningSubscriber extends ProxyService implements Subscriber {
  private static final GoogleLogger LOG = GoogleLogger.forEnclosingClass();
  private final PartitionSubscriberFactory subscriberFactory;

  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private final Map<Partition, Subscriber> liveSubscriberMap = new HashMap<>();

  @GuardedBy("monitor.monitor")
  private final List<Subscriber> stoppingSubscribers = new ArrayList<>();

  @GuardedBy("monitor.monitor")
  private boolean shutdown = false;

  public AssigningSubscriber(
      PartitionSubscriberFactory subscriberFactory, AssignerFactory assignerFactory)
      throws ApiException {
    this.subscriberFactory = subscriberFactory;
    Assigner assigner = assignerFactory.New(this::handleAssignment);
    addServices(assigner);
  }

  @Override
  protected void start() {}

  @Override
  protected void stop() {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      shutdown = true;
      blockingShutdown(liveSubscriberMap.values());
      liveSubscriberMap.clear();
      blockingShutdown(stoppingSubscribers);
    }
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    stop();
  }

  private void handleAssignment(Set<Partition> assignment) {
    try {
      try (CloseableMonitor.Hold h = monitor.enter()) {
        if (shutdown) return;
        Set<Partition> livePartitions = ImmutableSet.copyOf(liveSubscriberMap.keySet());
        for (Partition partition : livePartitions) {
          if (!assignment.contains(partition)) {
            stopSubscriber(liveSubscriberMap.remove(partition));
          }
        }
        for (Partition partition : assignment) {
          if (!liveSubscriberMap.containsKey(partition)) startSubscriber(partition);
        }
      }
    } catch (CheckedApiException e) {
      onPermanentError(e);
    }
  }

  @GuardedBy("monitor.monitor")
  private void startSubscriber(Partition partition) throws CheckedApiException {
    checkState(!liveSubscriberMap.containsKey(partition));
    Subscriber subscriber = subscriberFactory.newSubscriber(partition);
    subscriber.addListener(
        new Listener() {
          @Override
          public void failed(State from, Throwable failure) {
            onPermanentError(toCanonical(failure));
          }

          @Override
          public void terminated(State from) {
            try (CloseableMonitor.Hold h = monitor.enter()) {
              stoppingSubscribers.remove(subscriber);
            }
          }
        },
        SystemExecutors.getFuturesExecutor());
    liveSubscriberMap.put(partition, subscriber);
    subscriber.startAsync();
  }

  @GuardedBy("monitor.monitor")
  private void stopSubscriber(Subscriber subscriber) {
    stoppingSubscribers.add(subscriber);
    subscriber.stopAsync();
  }
}
