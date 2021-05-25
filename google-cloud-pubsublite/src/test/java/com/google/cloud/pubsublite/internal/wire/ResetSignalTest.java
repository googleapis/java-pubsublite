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

import static com.google.common.truth.Truth.assertThat;

import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.TestResetSignal;
import com.google.protobuf.Any;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import io.grpc.protobuf.StatusProto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResetSignalTest {
  @Test
  public void isResetSignal_matches() {
    assertThat(ResetSignal.isResetSignal(TestResetSignal.newCheckedException())).isTrue();
  }

  @Test
  public void isResetSignal_notStatus() {
    assertThat(ResetSignal.isResetSignal(new CheckedApiException(new Exception(""), Code.ABORTED)))
        .isFalse();
  }

  @Test
  public void isResetSignal_missingDetails() {
    assertThat(ResetSignal.isResetSignal(new CheckedApiException(Code.ABORTED))).isFalse();
  }

  @Test
  public void isResetSignal_wrongReason() {
    ErrorInfo errorInfo =
        ErrorInfo.newBuilder().setReason("OTHER").setDomain("pubsublite.googleapis.com").build();
    Status status =
        Status.newBuilder().setCode(Code.ABORTED.ordinal()).addDetails(Any.pack(errorInfo)).build();
    CheckedApiException exception =
        new CheckedApiException(StatusProto.toStatusRuntimeException(status), Code.ABORTED);
    assertThat(ResetSignal.isResetSignal(exception)).isFalse();
  }

  @Test
  public void isResetSignal_wrongDomain() {
    ErrorInfo errorInfo =
        ErrorInfo.newBuilder().setReason("RESET").setDomain("other.googleapis.com").build();
    Status status =
        Status.newBuilder().setCode(Code.ABORTED.ordinal()).addDetails(Any.pack(errorInfo)).build();
    CheckedApiException exception =
        new CheckedApiException(StatusProto.toStatusRuntimeException(status), Code.ABORTED);
    assertThat(ResetSignal.isResetSignal(exception)).isFalse();
  }

  @Test
  public void isResetSignal_notRetryable() {
    ErrorInfo errorInfo =
        ErrorInfo.newBuilder().setReason("RESET").setDomain("pubsublite.googleapis.com").build();
    Status status =
        Status.newBuilder()
            .setCode(Code.FAILED_PRECONDITION.ordinal())
            .addDetails(Any.pack(errorInfo))
            .build();
    CheckedApiException exception =
        new CheckedApiException(
            StatusProto.toStatusRuntimeException(status), Code.FAILED_PRECONDITION);
    assertThat(ResetSignal.isResetSignal(exception)).isFalse();
  }
}
