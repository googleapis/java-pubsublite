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

import com.google.cloud.pubsublite.Offset;
import java.util.List;
import java.util.Optional;

// A PullSubscriber exposes a "pull" mechanism for retrieving messages.
public interface PullSubscriber<T> extends AutoCloseable {
  // Pull currently available messages from this subscriber. Does not block.
  List<T> pull() throws CheckedApiException;

  // Pull one available message from this subscriber. Does not block.
  Optional<T> pullOne() throws CheckedApiException;

  // If there are available messages to pull.
  boolean hasNext() throws CheckedApiException;

  // The next offset expected to be returned by this PullSubscriber, or empty if unknown.
  // Subsequent messages are guaranteed to have offsets of at least this value.
  Optional<Offset> nextOffset();
}
