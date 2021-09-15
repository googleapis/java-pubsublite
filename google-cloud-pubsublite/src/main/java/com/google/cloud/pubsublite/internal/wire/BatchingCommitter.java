/*
 * Copyright 2021 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.MoreApiFutures;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.Optional;
import java.util.concurrent.Future;

public class BatchingCommitter extends ProxyService implements Committer {
  private final Committer underlying;

  @GuardedBy("this")
  private SettableApiFuture<Void> currentFuture = SettableApiFuture.create();

  @GuardedBy("this")
  private Optional<Offset> currentOffset = Optional.empty();

  BatchingCommitter(Committer underlying, AlarmFactory alarmFactory) {
    super(underlying);
    this.underlying = underlying;
    Future<?> alarm = alarmFactory.newAlarm(this::flush);
    addServices(ApiServiceUtils.autoCloseableAsApiService(() -> alarm.cancel(false)));
  }

  @Override
  public synchronized ApiFuture<Void> commitOffset(Offset offset) {
    currentOffset = Optional.of(offset);
    return currentFuture;
  }

  @Override
  public void waitUntilEmpty() throws CheckedApiException {
    flush();
    underlying.waitUntilEmpty();
  }

  @Override
  protected void stop() {
    flush();
  }

  private synchronized void flush() {
    if (!currentOffset.isPresent()) {
      return;
    }
    ApiFuture<Void> underlyingFuture = underlying.commitOffset(currentOffset.get());
    MoreApiFutures.connectFutures(underlyingFuture, currentFuture);
    currentOffset = Optional.empty();
    currentFuture = SettableApiFuture.create();
  }
}
