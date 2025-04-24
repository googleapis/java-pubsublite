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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.google.api.core.AbstractApiService;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.common.flogger.GoogleLogger;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class PartitionCountWatcherImpl extends AbstractApiService implements PartitionCountWatcher {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();
  private final TopicPath topicPath;
  private final AdminClient adminClient;
  private final Consumer<Long> partitionCountReceiver;
  private final AlarmFactory alarmFactory;

  private Optional<Future<?>> partitionCountPoll = Optional.empty();
  private long currentPartitionCount = 0;

  public static class Factory implements PartitionCountWatcher.Factory {
    private final TopicPath topicPath;
    private final AdminClient adminClient;
    private final AlarmFactory alarmFactory;

    public Factory(TopicPath topicPath, AdminClient adminClient, AlarmFactory alarmFactory) {
      this.topicPath = topicPath;
      this.adminClient = adminClient;
      this.alarmFactory = alarmFactory;
    }

    @Override
    public PartitionCountWatcher newWatcher(Consumer<Long> receiver) {
      return new PartitionCountWatcherImpl(topicPath, adminClient, alarmFactory, receiver);
    }
  }

  private PartitionCountWatcherImpl(
      TopicPath topicPath,
      AdminClient adminClient,
      AlarmFactory alarmFactory,
      Consumer<Long> receiver) {
    this.topicPath = topicPath;
    this.adminClient = adminClient;
    this.alarmFactory = alarmFactory;
    this.partitionCountReceiver = receiver;
  }

  private void onAlarm() {
    try {
      pollTopicConfig();
    } catch (Throwable t) {
      // Failures to update the partition count are not actual failures since we would have already
      // fetched the first config.
      log.atWarning().withCause(t).log("Failed to refresh partition count");
    }
  }

  private void pollTopicConfig() {
    try {
      long partitionCount = adminClient.getTopicPartitionCount(topicPath).get(5, MINUTES);
      if (currentPartitionCount == partitionCount) {
        return;
      }
      currentPartitionCount = partitionCount;
      partitionCountReceiver.accept(partitionCount);
    } catch (TimeoutException e) {
      log.atWarning().withCause(e).log(
          "Timed out polling for partition count- see"
              + " https://github.com/googleapis/gax-java/issues/1577");
      throw toCanonical(e).underlying;
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @Override
  protected void doStart() {
    pollTopicConfig();
    partitionCountPoll = Optional.of(alarmFactory.newAlarm(this::onAlarm));
    notifyStarted();
  }

  @Override
  protected void doStop() {
    partitionCountPoll.ifPresent(future -> future.cancel(true));
    adminClient.close();
    notifyStopped();
  }
}
