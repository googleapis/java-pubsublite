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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkState;

import com.google.api.core.AbstractApiService;
import com.google.api.core.ApiService;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.internal.wire.ApiServiceUtils;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import com.google.common.collect.ImmutableList;
import com.google.common.flogger.GoogleLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// A ProxyService is an AbstractApiService that wraps a number of other ApiServices.
//
// On any dependent service failure, fails all other services and calls handlePermanentError.
public abstract class ProxyService extends AbstractApiService {
  private static final GoogleLogger LOGGER = GoogleLogger.forEnclosingClass();
  private final List<ApiService> services = new ArrayList<>();
  private final AtomicBoolean failed = new AtomicBoolean(false);

  protected <T extends ApiService> ProxyService(Collection<T> services) {
    addServices(services);
  }

  protected ProxyService(ApiService... services) throws ApiException {
    this(Arrays.asList(services));
  }

  // Add a new ApiServices to this. Requires that all of them are in state NEW and this is in state
  // NEW.
  protected final <T extends ApiService> void addServices(Collection<T> services)
      throws ApiException {
    checkState(state() == State.NEW);
    Listener onServiceError =
        new Listener() {
          @Override
          public void failed(State state, Throwable throwable) {
            onPermanentError(toCanonical(throwable));
          }
        };
    for (ApiService service : services) {
      checkArgument(service.state() == State.NEW, "All services must not be started.");
      service.addListener(onServiceError, SystemExecutors.getFuturesExecutor());
      this.services.add(service);
    }
  }

  protected final void addServices(ApiService... services) throws ApiException {
    addServices(ImmutableList.copyOf(services));
  }

  // Method to be called on service start after dependent services start.
  protected void start() throws CheckedApiException {}

  // Method to be called on service stop before dependent services stop.
  protected void stop() throws CheckedApiException {}

  // Method to be called for class-specific permanent error handling after trying to stop all other
  // services. May not throw.
  protected void handlePermanentError(CheckedApiException error) {}

  // Tries to stop all dependent services and sets this service into the FAILED state.
  protected final void onPermanentError(CheckedApiException error) {
    if (failed.getAndSet(true)) return;
    try {
      ApiServiceUtils.stopAsync(services);
    } catch (Throwable t) {
      LOGGER.atFine().withCause(t).log("Exception in underlying service shutdown.");
    }
    try {
      handlePermanentError(error);
    } catch (Throwable t) {
      LOGGER.atFine().withCause(t).log("Exception in handlePermanentError.");
    }
    try {
      // Failures are sent to the client and should always be ApiExceptions.
      notifyFailed(error.underlying);
    } catch (IllegalStateException e) {
      LOGGER.atFine().withCause(e).log("Exception in notifyFailed.");
    }
  }

  // AbstractApiService implementation.
  @Override
  protected final void doStart() {
    SystemExecutors.getFuturesExecutor().execute(this::startImpl);
  }

  private void startImpl() {
    Listener listener =
        new Listener() {
          private final AtomicInteger leftToStart = new AtomicInteger(services.size());

          @Override
          public void running() {
            if (leftToStart.decrementAndGet() == 0) {
              try {
                start();
                notifyStarted();
              } catch (CheckedApiException e) {
                onPermanentError(e);
              }
            }
          }
        };
    try {
      for (ApiService service : services) {
        service.addListener(listener, SystemExecutors.getFuturesExecutor());
        service.startAsync();
      }
    } catch (Throwable t) {
      onPermanentError(toCanonical(t));
    }
  }

  @Override
  protected final void doStop() {
    SystemExecutors.getFuturesExecutor().execute(this::stopImpl);
  }

  private void stopImpl() {
    Listener listener =
        new Listener() {
          private final AtomicInteger leftToStop = new AtomicInteger(services.size());

          @Override
          public void terminated(State state) {
            if (leftToStop.decrementAndGet() == 0) {
              notifyStopped();
            }
          }
        };
    try {
      stop();
    } catch (CheckedApiException e) {
      onPermanentError(e);
      return;
    }
    try {
      for (ApiService service : services) {
        service.addListener(listener, SystemExecutors.getFuturesExecutor());
        service.stopAsync();
      }
    } catch (Throwable t) {
      onPermanentError(toCanonical(t));
    }
  }
}
