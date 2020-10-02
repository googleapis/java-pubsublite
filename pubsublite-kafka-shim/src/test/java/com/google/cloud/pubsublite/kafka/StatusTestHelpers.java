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

package com.google.cloud.pubsublite.kafka;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.cloud.pubsublite.internal.ExtractStatus;
import io.grpc.Status;
import io.grpc.Status.Code;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StatusTestHelpers {
  private StatusTestHelpers() {}

  public static void assertFutureThrowsCode(Future<?> f, Code code) {
    ExecutionException exception = assertThrows(ExecutionException.class, f::get);
    Optional<Status> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(code);
  }
}
