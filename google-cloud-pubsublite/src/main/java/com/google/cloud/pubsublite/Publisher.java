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

package com.google.cloud.pubsublite;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService;
import java.io.Flushable;

/** A generic PubSub Lite publisher. Errors are handled out of band. Thread safe. */
public interface Publisher<ResponseT> extends ApiService, Flushable {
  // Publish a new message. Behavior is undefined if a call to flush() is outstanding or close() has
  // already been called. This method never blocks.
  //
  // Guarantees that if a single publish future has an exception set, all publish calls made after
  // that will also have an exception set.
  ApiFuture<ResponseT> publish(Message message);
}
