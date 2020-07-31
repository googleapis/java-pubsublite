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

import com.google.api.core.ApiFuture;
import com.google.api.core.NanoClock;
import com.google.api.gax.retrying.ExponentialRetryAlgorithm;
import com.google.api.gax.retrying.ResultRetryAlgorithm;
import com.google.api.gax.retrying.RetryAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetryingExecutor;
import com.google.api.gax.retrying.RetryingFuture;
import com.google.api.gax.retrying.ScheduledRetryingExecutor;
import com.google.api.gax.retrying.TimedAttemptSettings;
import com.google.cloud.pubsublite.ErrorCodes;
import io.grpc.Status;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

public final class RetryingExecutorUtil {

  private RetryingExecutorUtil() {}

  public static <T> RetryingExecutor<T> retryingExecutor(
      RetrySettings settings, ScheduledExecutorService executor) {
    return new ScheduledRetryingExecutor<>(retryAlgorithm(settings), executor);
  }

  public static <T> ApiFuture<T> runWithRetries(
      Callable<T> callable, RetryingExecutor<T> executor) {
    RetryingFuture<T> retryingFuture = executor.createFuture(callable);
    retryingFuture.setAttemptFuture(executor.submit(retryingFuture));
    return retryingFuture;
  }

  private static <T> RetryAlgorithm<T> retryAlgorithm(RetrySettings retrySettings) {
    return new RetryAlgorithm<>(
        resultRetryAlgorithm(),
        new ExponentialRetryAlgorithm(retrySettings, NanoClock.getDefaultClock()));
  }

  private static <T> ResultRetryAlgorithm<T> resultRetryAlgorithm() {
    return new ResultRetryAlgorithm<T>() {
      @Override
      public TimedAttemptSettings createNextAttempt(
          Throwable prevThrowable, T prevResponse, TimedAttemptSettings prevSettings) {
        return null; // Null means no specific settings.
      }

      @Override
      public boolean shouldRetry(Throwable prevThrowable, T prevResponse) {
        if (null != prevResponse) {
          return false;
        }
        Optional<Status> statusOr = ExtractStatus.extract(prevThrowable);
        if (!statusOr.isPresent()) {
          return false; // Received a non-grpc error.
        }
        return ErrorCodes.IsRetryable(statusOr.get().getCode());
      }
    };
  }
}
