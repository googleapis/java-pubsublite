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

package com.google.cloud.pubsublite.internal.wire;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.core.AbstractApiService;
import com.google.api.core.ApiService;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.common.collect.ImmutableList;
import com.google.common.flogger.GoogleLogger;

public class ApiServiceUtils {
  private static final GoogleLogger LOGGER = GoogleLogger.forEnclosingClass();

  private ApiServiceUtils() {}

  public static ApiService autoCloseableAsApiService(AutoCloseable resource) {
    return new AbstractApiService() {
      @Override
      protected void doStart() {
        notifyStarted();
      }

      @Override
      protected void doStop() {
        try {
          resource.close();
          notifyStopped();
        } catch (Exception e) {
          notifyFailed(toCanonical(e));
        }
      }
    };
  }

  public static void stopAsync(Iterable<? extends ApiService> services) throws ApiException {
    CheckedApiException lastException = null;
    for (ApiService service : services) {
      try {
        service.stopAsync();
      } catch (Throwable t) {
        LOGGER.atFine().withCause(t).log("Exception in service shutdown.");
        lastException = toCanonical(t);
      }
    }
    if (lastException != null) {
      throw lastException.underlying;
    }
  }

  public static void blockingShutdown(Iterable<? extends ApiService> services) throws ApiException {
    CheckedApiException lastException = null;
    for (ApiService service : services) {
      try {
        service.stopAsync();
        service.awaitTerminated();
      } catch (Throwable t) {
        LOGGER.atFine().withCause(t).log("Exception in service shutdown.");
        lastException = toCanonical(t);
      }
    }
    if (lastException != null) {
      throw lastException.underlying;
    }
  }

  public static void blockingShutdown(ApiService service) {
    blockingShutdown(ImmutableList.of(service));
  }
}
