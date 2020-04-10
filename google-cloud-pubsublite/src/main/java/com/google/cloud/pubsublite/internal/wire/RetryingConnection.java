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

import com.google.api.core.ApiService;
import io.grpc.StatusException;
import java.util.Optional;

interface RetryingConnection<ConnectionT> extends ApiService {
  // Reinitialize the stream. Must be called in a downcall to prevent deadlock.
  void reinitialize();

  interface Modifier<ConnectionT> {
    void modify(Optional<ConnectionT> connectionOr) throws StatusException;
  }

  // Run modification on the current connection or empty if not connected.
  void modifyConnection(Modifier<ConnectionT> modifier) throws StatusException;
}
