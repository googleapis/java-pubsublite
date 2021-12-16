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
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ExtractStatus {
  public static Optional<CheckedApiException> extract(Throwable t) {
    try {
      throw t;
    } catch (ApiException e) {
      return Optional.of(new CheckedApiException(e));
    } catch (CheckedApiException e) {
      return Optional.of(e);
    } catch (ExecutionException e) {
      return extract(e.getCause());
    } catch (Throwable e) {
      return Optional.empty();
    }
  }

  public static CheckedApiException toCanonical(Throwable t) {
    Optional<CheckedApiException> statusOr = extract(t);
    if (statusOr.isPresent()) return statusOr.get();
    return new CheckedApiException(t, Code.INTERNAL);
  }

  public static <T> ApiFuture<T> toClientFuture(ApiFuture<T> source) {
    return ApiFutures.catchingAsync(
        source,
        Throwable.class,
        t -> ApiFutures.immediateFailedFuture(toCanonical(t).underlying),
        MoreExecutors.directExecutor());
  }

  public static void addFailureHandler(
      ApiFuture<?> future, Consumer<CheckedApiException> consumer) {
    future.addListener(
        () -> {
          try {
            future.get();
          } catch (InterruptedException | ExecutionException e) {
            consumer.accept(toCanonical(e));
          }
        },
        SystemExecutors.getFuturesExecutor());
  }

  public interface StatusFunction<I, O> {
    O apply(I input) throws CheckedApiException;
  }

  public interface StatusConsumer<I> {
    void apply(I input) throws CheckedApiException;
  }

  public interface StatusBiconsumer<K, V> {
    void apply(K key, V value) throws CheckedApiException;
  }

  public static <I, O> Function<I, O> rethrowAsRuntime(StatusFunction<I, O> function) {
    return i -> {
      try {
        return function.apply(i);
      } catch (CheckedApiException e) {
        throw e.underlying;
      }
    };
  }

  public static <I> Consumer<I> rethrowAsRuntime(StatusConsumer<I> consumer) {
    return i -> {
      try {
        consumer.apply(i);
      } catch (CheckedApiException e) {
        throw e.underlying;
      }
    };
  }

  public static <K, V> BiConsumer<K, V> rethrowAsRuntime(StatusBiconsumer<K, V> consumer) {
    return (k, v) -> {
      try {
        consumer.apply(k, v);
      } catch (CheckedApiException e) {
        throw e.underlying;
      }
    };
  }

  private ExtractStatus() {}
}
