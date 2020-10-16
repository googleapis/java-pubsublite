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

import com.google.api.gax.rpc.ApiException;

// Reduce the boilerplate of precondition checking by throwing an ApiException.
public class UncheckedApiPreconditions {
  private UncheckedApiPreconditions() {}

  public static void checkArgument(boolean test) throws ApiException {
    checkArgument(test, "");
  }

  public static void checkArgument(boolean test, String description) throws ApiException {
    try {
      CheckedApiPreconditions.checkArgument(test, description);
    } catch (CheckedApiException e) {
      throw e.underlying;
    }
  }

  public static void checkState(boolean test) throws ApiException {
    checkState(test, "");
  }

  public static void checkState(boolean test, String description) throws ApiException {
    try {
      CheckedApiPreconditions.checkState(test, description);
    } catch (CheckedApiException e) {
      throw e.underlying;
    }
  }
}
