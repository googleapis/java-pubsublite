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

import io.grpc.StatusException;

public interface RetryingConnectionObserver<ClientResponseT> {
  // Trigger reinitialization. This cannot be an upcall. It needs to be atomic so there is no
  // possibility for other client messages to be sent on the stream between the new stream being
  // created and the client initialization occurring. It cannot be called with connectionMonitor
  // held since all locks need to be acquired in concrete then abstract class order to avoid
  // deadlocks.
  void triggerReinitialize();

  void onClientResponse(ClientResponseT value) throws StatusException;
}
