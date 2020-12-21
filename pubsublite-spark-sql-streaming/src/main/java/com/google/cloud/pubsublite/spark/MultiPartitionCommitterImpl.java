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
import java.util.HashMap;
import java.util.Map;

public class MultiPartitionCommitterImpl implements MultiPartitionCommitter {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final Map<Partition, Committer> committerMap = new HashMap<>();

  @VisibleForTesting
  MultiPartitionCommitterImpl(long topicPartitionCount, CommitterFactory committerFactory) {
    for (int i = 0; i < topicPartitionCount; i++) {
      Partition p = Partition.of(i);
      Committer committer = committerFactory.newCommitter(p);
      committer.startAsync().awaitRunning();
      committerMap.put(p, committer);
    }
  }

  @Override
  public synchronized void close() {
    committerMap.values().forEach(c -> c.stopAsync().awaitTerminated());
  }

  @Override
  public synchronized void commit(PslSourceOffset offset) {
    offset
        .partitionOffsetMap()
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
