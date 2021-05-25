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

import com.google.cloud.pubsublite.internal.CheckedApiException;

public interface ResetHandler {
  // Handle the stream reset signal. Handlers are notified between stream breakage and the next
  // stream reconnection, and should reset all necessary client state.
  void onReset() throws CheckedApiException;

  // No-op reset handler implementation.
  static void noop() {}
}
