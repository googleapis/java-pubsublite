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

import io.grpc.Status;
import io.grpc.StatusException;

// Reduce the boilerplate of precondition checking by throwing a StatusException.
public class Preconditions {
  private Preconditions() {}

  public static void checkArgument(boolean test) throws StatusException {
    if (!test) throw Status.INVALID_ARGUMENT.asException();
  }

  public static void checkArgument(boolean test, String description) throws StatusException {
    if (!test) throw Status.INVALID_ARGUMENT.withDescription(description).asException();
  }

  public static void checkState(boolean test) throws StatusException {
    if (!test) throw Status.FAILED_PRECONDITION.asException();
  }

  public static void checkState(boolean test, String description) throws StatusException {
    if (!test) throw Status.FAILED_PRECONDITION.withDescription(description).asException();
  }
}
