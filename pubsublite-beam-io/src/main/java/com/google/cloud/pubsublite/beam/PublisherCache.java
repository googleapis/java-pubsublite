// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.beam;

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.StatusException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class PublisherCache {
  private final CloseableMonitor monitor = new CloseableMonitor();

  private final Executor listenerExecutor = Executors.newSingleThreadExecutor();

  @GuardedBy("monitor.monitor")
  private final HashMap<PublisherOptions, Publisher<PublishMetadata>> livePublishers =
      new HashMap<>();

  Publisher<PublishMetadata> get(PublisherOptions options) throws StatusException {
    checkArgument(options.usesCache());
    try (CloseableMonitor.Hold h = monitor.enter()) {
      Publisher<PublishMetadata> publisher = livePublishers.get(options);
      if (publisher != null) return publisher;
      publisher = options.getPublisher();
      livePublishers.put(options, publisher);
      publisher.addListener(
          new Listener() {
            @Override
            public void failed(State s, Throwable t) {
              try (CloseableMonitor.Hold h = monitor.enter()) {
                livePublishers.remove(options);
              }
            }
          },
          listenerExecutor);
      publisher.startAsync();
      return publisher;
    }
  }

  @VisibleForTesting
  void set(PublisherOptions options, Publisher<PublishMetadata> toCache) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      livePublishers.put(options, toCache);
    }
  }
}
