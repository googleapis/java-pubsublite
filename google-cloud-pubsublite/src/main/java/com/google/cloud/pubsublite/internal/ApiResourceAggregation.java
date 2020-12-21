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

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.rpc.ApiException;
import com.google.common.collect.ImmutableList;
import java.util.concurrent.TimeUnit;

public class ApiResourceAggregation implements ApiBackgroundResource {
  private final BackgroundResourceAggregation resources;

  ApiResourceAggregation(BackgroundResource... resources) {
    this.resources = new BackgroundResourceAggregation(ImmutableList.copyOf(resources));
  }

  @Override
  public void close() throws ApiException {
    try {
      resources.close();
    } catch (Throwable t) {
      throw toCanonical(t).underlying;
    }
  }

  @Override
  public void shutdown() {
    close();
  }

  @Override
  public boolean isShutdown() {
    return resources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return resources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    resources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return resources.awaitTermination(duration, unit);
  }
}
