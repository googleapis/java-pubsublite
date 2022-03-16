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

import com.google.cloud.pubsublite.ErrorCodes;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;

// Pub/Sub Lite's stream RESET signal is sent by the server to instruct the client to reset the
// stream state.
public final class ResetSignal {
  private static final String REASON = "RESET";

  public static boolean isResetSignal(CheckedApiException checkedApiException) {
    if (!ErrorCodes.IsRetryableForStreams(checkedApiException.code())) {
      return false;
    }
    return ExtractStatus.getErrorInfoReason(checkedApiException).equals(REASON);
  }

  private ResetSignal() {}
}
