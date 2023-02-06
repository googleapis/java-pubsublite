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
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.Publisher;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import javax.annotation.concurrent.GuardedBy;

/** A publisher which tears down connections when inactive. */
class UnloadingPublisher extends AbstractApiService implements Publisher<MessageMetadata> {
  private final Supplier<Publisher<MessageMetadata>> supplier;
  private final Future<?> alarmFuture;

  @GuardedBy("this")
  private Optional<Publisher<MessageMetadata>> publisher = Optional.empty();

  @GuardedBy("this")
  private boolean sawPublish = false;

  UnloadingPublisher(Supplier<Publisher<MessageMetadata>> supplier, AlarmFactory unloadAlarm) {
    this.supplier = supplier;
    this.alarmFuture = unloadAlarm.newAlarm(this::onUnloadAlarm);
  }

  @Override
  protected void doStart() {
    notifyStarted();
  }

  @Override
  protected synchronized void doStop() {
    alarmFuture.cancel(false);
    if (!publisher.isPresent()) {
      notifyStopped();
      return;
    }
    publisher
        .get()
        .addListener(
            new Listener() {
              @Override
              public void terminated(State from) {
                notifyStopped();
              }
            },
            SystemExecutors.getFuturesExecutor());
    publisher.get().stopAsync();
  }

  @Override
  public synchronized ApiFuture<MessageMetadata> publish(Message message) {
    sawPublish = true;
    return getPublisher().publish(message);
  }

  private synchronized Publisher<MessageMetadata> getPublisher() {
    if (!publisher.isPresent()) {
      publisher = Optional.of(supplier.get());
      publisher
          .get()
          .addListener(
              new Listener() {
                @Override
                public void failed(State from, Throwable failure) {
                  notifyFailed(failure);
                }
              },
              SystemExecutors.getFuturesExecutor());
      publisher.get().startAsync().awaitRunning();
    }
    return publisher.get();
  }

  @Override
  public synchronized void cancelOutstandingPublishes() {
    if (publisher.isPresent()) {
      publisher.get().cancelOutstandingPublishes();
    }
  }

  @Override
  public synchronized void flush() throws IOException {
    if (publisher.isPresent()) {
      publisher.get().flush();
    }
  }

  private synchronized void onUnloadAlarm() {
    if (publisher.isPresent() && !sawPublish) {
      publisher.get().stopAsync().awaitTerminated();
      publisher = Optional.empty();
    }
    sawPublish = false;
  }
}
