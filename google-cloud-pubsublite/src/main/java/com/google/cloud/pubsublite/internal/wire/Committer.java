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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;

public interface Committer extends ApiService {
  // Commit a given offset. Clean shutdown waits for all outstanding commits to complete.
  ApiFuture<Void> commitOffset(Offset offset);

  // Waits until all commits have been sent and acknowledged by the server. Throws an exception if
  // the committer shut down due to a permanent error.
  void waitUntilEmpty() throws CheckedApiException;
}
