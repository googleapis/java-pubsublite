/*
 * Copyright 2026 Google LLC
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
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.StatusCode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Utility for executing blocking Kafka admin calls with standard error handling. */
public final class KafkaFutureUtils {

  private KafkaFutureUtils() {}

  /**
   * Executes a blocking Kafka admin operation, catching {@link InterruptedException} (mapped to
   * {@link StatusCode.Code#ABORTED}) and {@link ExecutionException} (mapped to {@link
   * StatusCode.Code#INTERNAL}) with standard logging.
   *
   * @param operation The blocking operation to execute.
   * @param description A human-readable description for error messages (e.g., "committing offset").
   * @param logger The logger to use for warning messages on failure.
   * @param <T> The return type of the operation.
   * @return An {@link ApiFuture} containing the result or an appropriate error.
   */
  public static <T> ApiFuture<T> executeWithHandling(
      Callable<T> operation, String description, Logger logger) {
    try {
      return ApiFutures.immediateFuture(operation.call());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException("Interrupted while " + description, StatusCode.Code.ABORTED)
              .underlying);
    } catch (ExecutionException e) {
      logger.log(Level.WARNING, "Failed while " + description, e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed while " + description + ": " + e.getCause().getMessage(),
                  StatusCode.Code.INTERNAL)
              .underlying);
    } catch (Exception e) {
      logger.log(Level.WARNING, "Failed while " + description, e);
      return ApiFutures.immediateFailedFuture(
          new CheckedApiException(
                  "Failed while " + description + ": " + e.getMessage(), StatusCode.Code.INTERNAL)
              .underlying);
    }
  }
}
