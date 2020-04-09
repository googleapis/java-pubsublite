// Copyright 2019 Google LLC
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

package com.google.cloud.pubsublite.internal.wire;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;

/**
 * A generic PubSub Lite subscriber. Errors are handled out of band. Messages are sent out of band.
 * Thread compatible, as synchronization is required between seek calls.
 */
public interface Subscriber extends ApiService {
  // Seek the subscriber using the given SeekRequest. Requires that no seeks are outstanding.
  // Returns the seeked-to offset.
  ApiFuture<Offset> seek(SeekRequest request);
  // Whether or not a seek is in flight for this subscriber. If a seek is in flight, any further
  // seek requests will result in a permanent error.
  boolean seekInFlight();

  // Allow the provided amount of messages and bytes to be sent by the server.
  void allowFlow(FlowControlRequest request);
}
