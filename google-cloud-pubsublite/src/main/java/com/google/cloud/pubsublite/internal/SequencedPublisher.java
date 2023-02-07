/*
 * Copyright 2023 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService;
import com.google.cloud.pubsublite.Message;
import java.io.Flushable;

/**
 * A PubSub Lite publisher that requires a sequence number assigned to every message, for publish
 * idempotency. Errors are handled out of band. Thread safe.
 */
public interface SequencedPublisher<ResponseT> extends ApiService, Flushable {
  /**
   * Publish a new message with an assigned sequence number.
   *
   * <p>Behavior is undefined if a call to flush() is outstanding or close() has already been
   * called. This method never blocks.
   *
   * <p>Guarantees that if a single publish future has an exception set, all publish calls made
   * after that will also have an exception set.
   */
  ApiFuture<ResponseT> publish(Message message, PublishSequenceNumber sequenceNumber);

  /** Attempts to cancel all outstanding publishes. */
  void cancelOutstandingPublishes();
}
