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

package com.google.cloud.pubsublite.internal;

import com.google.cloud.pubsublite.SequencedMessage;
import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface BlockingPullSubscriber extends Closeable {

  /**
   * Returns a {@link Future} that will be completed when there are messages available.
   *
   * <p>{@link java.util.concurrent.ExecutionException} will be set to the Future if there is
   * underlying non-retryable error.
   */
  Future<Void> onData();

  /**
   * Returns a {@link Future} that will be completed when there are messages available within the
   * timeout.
   *
   * <p>{@link java.util.concurrent.TimeoutException} will be set to the Future if no messages
   * available when timeout expires. {@link java.util.concurrent.ExecutionException} will be set to
   * the Future if there is underlying non-retryable error.
   */
  Future<Void> onData(long time, TimeUnit unit);

  /**
   * Pull messages if there is any. Any message will only be delivered to one call if there are
   * multiple concurrent calls.
   *
   * @throws CheckedApiException if there is underlying non-retryable error. Note for multiple
   *     consecutive calls, it prioritizes delivering all existing available messages over throwing
   *     underlying non-retryable error.
   */
  Optional<SequencedMessage> messageIfAvailable() throws CheckedApiException;

  void close();
}
