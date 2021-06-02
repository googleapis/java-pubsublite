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
import com.google.protobuf.Any;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;

// Pub/Sub Lite's stream RESET signal is sent by the server to instruct the client to reset the
// stream state.
public final class ResetSignal {
  private static final String REASON = "RESET";
  private static final String DOMAIN = "pubsublite.googleapis.com";

  public static boolean isResetSignal(CheckedApiException checkedApiException) {
    if (!ErrorCodes.IsRetryableForStreams(checkedApiException.code())) {
      return false;
    }
    Status status = StatusProto.fromThrowable(checkedApiException.underlying);
    if (status == null) {
      return false;
    }
    for (Any any : status.getDetailsList()) {
      if (any.is(ErrorInfo.class)) {
        try {
          ErrorInfo errorInfo = any.unpack(ErrorInfo.class);
          if (REASON.equals(errorInfo.getReason()) && DOMAIN.equals(errorInfo.getDomain())) {
            return true;
          }
        } catch (Throwable t) {
        }
      }
    }
    return false;
  }

  private ResetSignal() {}
}
