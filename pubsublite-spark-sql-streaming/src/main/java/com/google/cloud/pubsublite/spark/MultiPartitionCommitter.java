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

package com.google.cloud.pubsublite.spark;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.internal.wire.Committer;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class MultiPartitionCommitter implements Serializable {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  public interface CommitterFactory extends Serializable {
    Committer newCommitter(PslDataSourceOptions options, Partition partition);
  }

  private final PslDataSourceOptions options;
  private final CommitterFactory committerFactory;
  private final ReentrantLock lock = new ReentrantLock();
  private boolean initialized = false;
  private final Map<Partition, Committer> committerMap = new HashMap<>(); // lazily constructed

  public MultiPartitionCommitter(PslDataSourceOptions options) {
    this(
        options,
        (opt, partition) ->
            CommitterBuilder.newBuilder()
                .setSubscriptionPath(opt.subscriptionPath())
                .setPartition(partition)
                .setServiceClient(opt.newCursorClient())
                .build());
  }

  @VisibleForTesting
  public MultiPartitionCommitter(PslDataSourceOptions options, CommitterFactory committerFactory) {
    this.options = options;
    this.committerFactory = committerFactory;
  }

  private void init(Set<Partition> partitions) {
    assert lock.isLocked();
    partitions.forEach(
        p -> {
          Committer committer = committerFactory.newCommitter(options, p);
          committer.startAsync().awaitRunning();
          committerMap.put(p, committer);
        });
  }

  public void close() {
    lock.lock();
    try {
      committerMap.values().forEach(c -> c.stopAsync().awaitTerminated());
    } finally {
      lock.unlock();
    }
  }

  public void commit(PslSourceOffset offset) {
    lock.lock();
    try {
      if (!initialized) {
        init(offset.getPartitionOffsetMap().keySet());
        initialized = true;
      }

      offset
          .getPartitionOffsetMap()
          .forEach(
              (p, o) -> {
                // Note we don't need to worry about commit offset disorder here since Committer
                // guarantees the ordering. Once commitOffset() returns, it's either already
                // sent to stream, or waiting for next connection to open to be sent in order.
                ApiFuture<Void> future = committerMap.get(p).commitOffset(o);
                ApiFutures.addCallback(
                    future,
                    new ApiFutureCallback<Void>() {
                      @Override
                      public void onFailure(Throwable t) {
                        if (!future.isCancelled()) {
                          log.atWarning().log("Failed to commit %s,%s.", p.value(), o.value(), t);
                        }
                      }

                      @Override
                      public void onSuccess(Void result) {
                        log.atInfo().log("Committed %s,%s.", p.value(), o.value());
                      }
                    },
                    MoreExecutors.directExecutor());
              });
    } finally {
      lock.unlock();
    }
  }
}
