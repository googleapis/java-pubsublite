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

import com.google.api.gax.rpc.StatusCode.Code;

// Reduce the boilerplate of precondition checking by throwing a CheckedApiException.
public class CheckedApiPreconditions {
  private CheckedApiPreconditions() {}

  public static void checkArgument(boolean test) throws CheckedApiException {
    checkArgument(test, "");
  }

  public static void checkArgument(boolean test, String description) throws CheckedApiException {
    if (!test) throw new CheckedApiException(description, Code.INVALID_ARGUMENT);
  }

  public static void checkArgument(boolean test, String descriptionFormat, Object... args)
      throws CheckedApiException {
    if (!test)
      throw new CheckedApiException(String.format(descriptionFormat, args), Code.INVALID_ARGUMENT);
  }

  public static void checkState(boolean test) throws CheckedApiException {
    checkState(test, "");
  }

  public static void checkState(boolean test, String description) throws CheckedApiException {
    if (!test) throw new CheckedApiException(description, Code.FAILED_PRECONDITION);
  }

  public static void checkState(boolean test, String descriptionFormat, Object... args)
      throws CheckedApiException {
    if (!test)
      throw new CheckedApiException(
          String.format(descriptionFormat, args), Code.FAILED_PRECONDITION);
  }
}
