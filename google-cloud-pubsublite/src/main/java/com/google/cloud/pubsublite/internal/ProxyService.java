// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal;

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;
import static com.google.cloud.pubsublite.internal.Preconditions.checkState;

import com.google.api.core.AbstractApiService;
import com.google.api.core.ApiService;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// A ProxyService is an AbstractApiService that wraps a number of other ApiServices.
//
// On any dependent service failure, fails all other services and calls handlePermanentError.
public abstract class ProxyService extends AbstractApiService {
  private final List<ApiService> services = new ArrayList<>();
  private final AtomicBoolean stoppedOrFailed = new AtomicBoolean(false);

  protected ProxyService() {}

  // Add a new ApiServices to this. Requires that all of them are in state NEW and this is in state
  // NEW.
  protected final <T extends ApiService> void addServices(Collection<T> services) throws StatusException {
    checkState(state() == State.NEW);
    for (ApiService service: services) {
      checkArgument(service.state() == State.NEW, "All services must not be started.");
      this.services.add(service);
    }
  }
  protected final void addServices(ApiService... services) throws StatusException {
    addServices(ImmutableList.copyOf(services));
  }

  // Method to be called on service start after dependent services start.
  protected abstract void start() throws StatusException;
  // Method to be called on service stop before dependent services stop.
  protected abstract void stop() throws StatusException;

  // Method to be called for class-specific permanent error handling after trying to stop all other
  // services. May not throw.
  protected abstract void handlePermanentError(StatusException error);

  // Tries to stop all dependent services and sets this service into the FAILED state.
  protected final void onPermanentError(StatusException error) {
    if (stoppedOrFailed.getAndSet(true)) return;
    for (ApiService service : services) {
      service.stopAsync();
    }
    handlePermanentError(error);
    notifyFailed(error);
  }

  // AbstractApiService implementation.
  @Override
  protected final void doStart() {
    Listener listener = new Listener() {
      private final AtomicInteger leftToStart = new AtomicInteger(services.size());
      @Override
      public void running() {
        if (leftToStart.decrementAndGet() == 0) {
          try {
            start();
          } catch (StatusException e) {
            onPermanentError(e);
            return;
          }
          notifyStarted();
        }
      }

      @Override
      public void failed(State state, Throwable throwable) {
        Optional<Status> statusOr = ExtractStatus.extract(throwable);
        onPermanentError(statusOr.orElse(Status.INTERNAL.withCause(throwable)).asException());
      }
    };
    for (ApiService service : services) {
      service.addListener(listener, MoreExecutors.directExecutor());
      service.startAsync();
    }
  }

  @Override
  protected final void doStop() {
    Listener listener = new Listener() {
      private final AtomicInteger leftToStart = new AtomicInteger(services.size());
      @Override
      public void terminated(State state) {
        if (leftToStart.decrementAndGet() == 0) {
          if (!stoppedOrFailed.getAndSet(true)) {
            notifyStopped();
          }
        }
      }
    };
    try {
      stop();
    } catch (StatusException e) {
      onPermanentError(e);
      return;
    }
    for (ApiService service : services) {
      service.addListener(listener, MoreExecutors.directExecutor());
      service.stopAsync();
    }
  }
}
