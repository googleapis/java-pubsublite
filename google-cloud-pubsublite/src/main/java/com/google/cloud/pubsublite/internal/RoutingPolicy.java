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

package com.google.cloud.pubsublite.internal;

import com.google.cloud.pubsublite.Partition;
import com.google.protobuf.ByteString;
import io.grpc.StatusException;

// Route the user message key to a given partition.
public interface RoutingPolicy {
  // Route a message without a key to a partition.
  Partition routeWithoutKey() throws StatusException;
  // Route a message with a key to a partition.
  Partition route(ByteString messageKey) throws StatusException;
}
