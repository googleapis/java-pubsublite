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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.gax.rpc.StatusCode.Code;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.mockito.ArgumentMatcher;

public class ApiExceptionMatcher implements ArgumentMatcher<Throwable> {
  private final Optional<Code> code;
  private final Optional<String> message;

  public ApiExceptionMatcher(Code code, String message) {
    this.code = Optional.of(code);
    this.message = Optional.of(message);
  }

  public ApiExceptionMatcher(Code code) {
    this.code = Optional.of(code);
    this.message = Optional.empty();
  }

  public ApiExceptionMatcher() {
    this.code = Optional.empty();
    this.message = Optional.empty();
  }

  private boolean matches(CheckedApiException e) {
    return (!code.isPresent() || code.get() == e.code())
        && (!message.isPresent() || message.get().equals(e.getMessage()));
  }

  @Override
  public boolean matches(Throwable argument) {
    Optional<CheckedApiException> statusOr = ExtractStatus.extract(argument);
    if (!statusOr.isPresent()) return false;
    return matches(statusOr.get());
  }

  public static void assertFutureThrowsCode(Future<?> f, Code code) {
    ExecutionException exception = assertThrows(ExecutionException.class, f::get);
    Optional<CheckedApiException> statusOr = ExtractStatus.extract(exception.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().code()).isEqualTo(code);
  }
}
