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

package com.google.cloud.pubsublite.internal;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import java.util.List;
import java.util.concurrent.Future;

public final class MoreApiFutures {
  private MoreApiFutures() {}

  public static <T> void connectFutures(
      ApiFuture<T> source, SettableApiFuture<? super T> toConnect) {
    ApiFutures.addCallback(
        source,
        new ApiFutureCallback<T>() {
          @Override
          public void onFailure(Throwable throwable) {
            toConnect.setException(throwable);
          }

          @Override
          public void onSuccess(T t) {
            toConnect.set(t);
          }
        },
        SystemExecutors.getFuturesExecutor());
  }

  public static ApiFuture<Void> whenFirstDone(List<ApiFuture<?>> futures) {
    SettableApiFuture<Void> anyDone = SettableApiFuture.create();
    futures.forEach(f -> f.addListener(() -> anyDone.set(null), directExecutor()));
    return anyDone;
  }

  public static <T> T get(Future<T> f) {
    try {
      return f.get();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }
}
