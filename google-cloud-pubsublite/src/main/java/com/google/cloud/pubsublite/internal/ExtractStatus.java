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
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public final class ExtractStatus {
  public static Optional<Status> extract(Throwable t) {
    if (t instanceof StatusException) {
      return Optional.of(((StatusException) t).getStatus());
    }
    if (t instanceof StatusRuntimeException) {
      return Optional.of(((StatusRuntimeException) t).getStatus());
    }
    return Optional.empty();
  }

  public static StatusException toCanonical(Throwable t) {
    Optional<Status> statusOr = extract(t);
    if (statusOr.isPresent()) return statusOr.get().asException();
    return Status.INTERNAL.withCause(t).asException();
  }

  public static void addFailureHandler(ApiFuture<?> future, Consumer<StatusException> consumer) {
    future.addListener(
        () -> {
          try {
            future.get();
          } catch (ExecutionException e) {
            consumer.accept(toCanonical(e.getCause()));
          } catch (InterruptedException e) {
            consumer.accept(toCanonical(e));
          }
        },
        MoreExecutors.directExecutor());
  }

  private ExtractStatus() {}
}
