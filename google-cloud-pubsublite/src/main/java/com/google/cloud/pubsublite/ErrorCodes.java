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

package com.google.cloud.pubsublite;

import com.google.api.gax.rpc.StatusCode.Code;
import com.google.common.collect.ImmutableSet;

/** Pub/Sub Lite retryable error codes. */
public final class ErrorCodes {
  public static final ImmutableSet<Code> STREAM_RETRYABLE_CODES =
      ImmutableSet.of(
          Code.DEADLINE_EXCEEDED,
          Code.ABORTED,
          Code.INTERNAL,
          Code.UNAVAILABLE,
          Code.UNKNOWN,
          Code.RESOURCE_EXHAUSTED);

  public static boolean IsRetryableForStreams(Code code) {
    return STREAM_RETRYABLE_CODES.contains(code);
  }

  private ErrorCodes() {}
}
