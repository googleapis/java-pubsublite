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

import com.google.api.core.AbstractApiService;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.common.flogger.GoogleLogger;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class PartitionCountWatcherImpl extends AbstractApiService implements PartitionCountWatcher {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();
  private final Duration period;
  private final TopicPath topicPath;
  private final AdminClient adminClient;
  private final ScheduledExecutorService executorService;
  private final Consumer<Long> partitionCountReceiver;

  private ScheduledFuture<?> partitionCountPoll;
  private Optional<Long> currentPartitionCount = Optional.empty();

  public static class Factory implements PartitionCountWatcher.Factory {
    private final TopicPath topicPath;
    private final AdminClient adminClient;
    private final Duration period;

    public Factory(TopicPath topicPath, AdminClient adminClient, Duration period) {
      this.topicPath = topicPath;
      this.adminClient = adminClient;
      this.period = period;
    }

    @Override
    public PartitionCountWatcher newWatcher(Consumer<Long> receiver) {
      return new PartitionCountWatcherImpl(topicPath, adminClient, receiver, period);
    }
  }

  public PartitionCountWatcherImpl(
      TopicPath topicPath, AdminClient adminClient, Consumer<Long> receiver, Duration period) {
    this.period = period;
    this.topicPath = topicPath;
    this.adminClient = adminClient;
    this.partitionCountReceiver = receiver;
    this.executorService = Executors.newSingleThreadScheduledExecutor();
  }

  private void pollTopicConfig() {
    Long partitionCount;
    try {
      partitionCount = adminClient.getTopicPartitionCount(topicPath).get();
    } catch (InterruptedException | ExecutionException e) {
      // If we encounter an exception on our first topic config poll, then fail. We need to fetch
      // the topic
      // config at least once to start up properly.
      if (!currentPartitionCount.isPresent()) {
        notifyFailed(ExtractStatus.toCanonical(e.getCause()));
        stop();
      }
      log.atWarning().withCause(e).log("Failed to refresh partition count");
      return;
    }
    if (currentPartitionCount.isPresent() && currentPartitionCount.get().equals(partitionCount)) {
      return;
    }
    partitionCountReceiver.accept(partitionCount);
    // Notify started after we successfully receive the config once.
    if (!currentPartitionCount.isPresent()) {
      notifyStarted();
    }
    currentPartitionCount = Optional.of(partitionCount);
  }

  private void stop() {
    partitionCountPoll.cancel(true);
    adminClient.close();
  }

  @Override
  protected void doStart() {
    partitionCountPoll =
        executorService.scheduleAtFixedRate(
            this::pollTopicConfig, 0, period.toMillis(), TimeUnit.MILLISECONDS);
  }

  @Override
  protected void doStop() {
    try {
      stop();
      notifyStopped();
    } catch (Exception e) {
      notifyFailed(e);
    }
  }
}
