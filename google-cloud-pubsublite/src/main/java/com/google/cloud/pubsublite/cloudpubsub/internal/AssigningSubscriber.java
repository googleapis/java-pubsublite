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
import com.google.cloud.pubsublite.cloudpubsub.ReassignmentHandler;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.wire.ApiServiceUtils;
import com.google.cloud.pubsublite.internal.wire.Assigner;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.common.collect.ImmutableSet;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AssigningSubscriber extends ProxyService implements Subscriber {
  private static final GoogleLogger LOG = GoogleLogger.forEnclosingClass();
  private final PartitionSubscriberFactory subscriberFactory;
  private final ReassignmentHandler reassignmentHandler;

  @GuardedBy("this")
  private final Map<Partition, Subscriber> liveSubscriberMap = new HashMap<>();

  @GuardedBy("this")
  private boolean shutdown = false;

  public AssigningSubscriber(
      PartitionSubscriberFactory subscriberFactory,
      ReassignmentHandler reassignmentHandler,
      AssignerFactory assignerFactory)
      throws ApiException {
    this.subscriberFactory = subscriberFactory;
    this.reassignmentHandler = reassignmentHandler;
    Assigner assigner = assignerFactory.New(this::handleAssignment);
    addServices(assigner);
  }

  @Override
  protected synchronized void stop() {
    shutdown = true;
    blockingShutdown(liveSubscriberMap.values());
    liveSubscriberMap.clear();
  }

  @Override
  protected void handlePermanentError(CheckedApiException error) {
    stop();
  }

  private void handleAssignment(Set<Partition> assignment) {
    try {
      Set<Partition> livePartitions;
      List<Subscriber> removed = new ArrayList<>();
      synchronized (this) {
        if (shutdown) return;
        livePartitions = ImmutableSet.copyOf(liveSubscriberMap.keySet());
        for (Partition partition : livePartitions) {
          if (!assignment.contains(partition)) {
            removed.add(Objects.requireNonNull(liveSubscriberMap.remove(partition)));
          }
        }
        for (Partition partition : assignment) {
          if (!liveSubscriberMap.containsKey(partition)) startSubscriber(partition);
        }
      }
      blockingShutdown(removed);
      // Call reassignment handler outside lock so it won't deadlock if it is asynchronously
      // reentrant such as by calling sub.stopAsync().awaitTerminated().
      reassignmentHandler.handleReassignment(livePartitions, assignment);
    } catch (Throwable t) {
      onPermanentError(toCanonical(t));
    }
  }

  private synchronized void startSubscriber(Partition partition) throws CheckedApiException {
    checkState(!liveSubscriberMap.containsKey(partition));
    Subscriber subscriber = subscriberFactory.newSubscriber(partition);
    subscriber.addListener(
        new Listener() {
          @Override
          public void failed(State from, Throwable failure) {
            if (State.STOPPING.equals(from)) return;
            onPermanentError(toCanonical(failure));
          }
        },
        SystemExecutors.getFuturesExecutor());
    liveSubscriberMap.put(partition, subscriber);
    subscriber.startAsync();
  }
}
