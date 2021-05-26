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

package com.google.cloud.pubsublite.internal.testing;

import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.protobuf.Any;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;

public final class TestResetSignal {
  public static CheckedApiException newCheckedException() {
    ErrorInfo errorInfo =
        ErrorInfo.newBuilder().setReason("RESET").setDomain("pubsublite.googleapis.com").build();
    Status status =
        Status.newBuilder().setCode(Code.ABORTED.ordinal()).addDetails(Any.pack(errorInfo)).build();
    return new CheckedApiException(StatusProto.toStatusRuntimeException(status), Code.ABORTED);
  }

  private TestResetSignal() {}
}
