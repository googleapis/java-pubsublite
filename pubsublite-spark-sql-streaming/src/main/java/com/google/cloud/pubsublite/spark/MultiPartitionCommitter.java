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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiPartitionCommitter implements Serializable {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  interface CommitterFactory extends Serializable {
    Committer newCommitter(Partition partition);
  }

  private final CommitterFactory committerFactory;
  private boolean initialized = false;
  private final Map<Partition, Committer> committerMap = new HashMap<>(); // lazily constructed

  @VisibleForTesting
  MultiPartitionCommitter(CommitterFactory committerFactory) {
    this.committerFactory = committerFactory;
  }

  private synchronized void init(Set<Partition> partitions) {
    partitions.forEach(
        p -> {
          Committer committer = committerFactory.newCommitter(p);
          committer.startAsync().awaitRunning();
          committerMap.put(p, committer);
        });
  }

  synchronized void close() {
    committerMap.values().forEach(c -> c.stopAsync().awaitTerminated());
  }

  synchronized void commit(PslSourceOffset offset) {
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
  }
}
