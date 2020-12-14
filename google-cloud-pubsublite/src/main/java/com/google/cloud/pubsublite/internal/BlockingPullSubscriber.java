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
import java.util.concurrent.TimeUnit;

public interface BlockingPullSubscriber extends Closeable {

  // Pull one message, blocking. Only one call may be outstanding at any time.
  SequencedMessage pull() throws InterruptedException, CheckedApiException;

  // Try to pull one message with timeout. Returns Optional.empty() if no
  // messages available within timeout.
  Optional<SequencedMessage> pull(long time, TimeUnit unit)
      throws InterruptedException, CheckedApiException;

  void close();
}
