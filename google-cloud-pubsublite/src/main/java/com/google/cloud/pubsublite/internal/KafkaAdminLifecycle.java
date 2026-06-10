/*
 * Copyright 2026 Google LLC
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

package com.google.cloud.pubsublite.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.admin.AdminClient;

/**
 * Encapsulates the lifecycle management pattern for classes that wrap a Kafka {@link AdminClient}.
 *
 * <p>This handles the idempotent shutdown logic with {@link AtomicBoolean} flags that is otherwise
 * copy-pasted across {@code KafkaAdminClient}, {@code KafkaCursorClient}, and {@code
 * KafkaTopicStatsClient}.
 */
public final class KafkaAdminLifecycle {
  private final AdminClient kafkaAdmin;
  private final AtomicBoolean isShutdown = new AtomicBoolean(false);
  private final AtomicBoolean isTerminated = new AtomicBoolean(false);

  public KafkaAdminLifecycle(AdminClient kafkaAdmin) {
    this.kafkaAdmin = kafkaAdmin;
  }

  /** Returns the underlying Kafka AdminClient. */
  public AdminClient adminClient() {
    return kafkaAdmin;
  }

  public void close() {
    shutdown();
  }

  public void shutdown() {
    if (isShutdown.compareAndSet(false, true)) {
      kafkaAdmin.close();
      isTerminated.set(true);
    }
  }

  public boolean isShutdown() {
    return isShutdown.get();
  }

  public boolean isTerminated() {
    return isTerminated.get();
  }

  public void shutdownNow() {
    shutdown();
  }

  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return isTerminated.get();
  }
}
