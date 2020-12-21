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

import com.google.api.core.ApiService;
import com.google.api.gax.rpc.ApiException;
import java.util.Arrays;
import java.util.Collection;

/** A ProxyService that just wraps all ApiService methods. */
public class TrivialProxyService extends ProxyService {
  protected <T extends ApiService> TrivialProxyService(Collection<T> services) throws ApiException {
    super();
    addServices(services);
  }

  protected TrivialProxyService(ApiService... services) throws ApiException {
    this(Arrays.asList(services));
  }

  @Override
  protected final void start() {}

  @Override
  protected final void stop() {}

  @Override
  protected final void handlePermanentError(CheckedApiException error) {}
}
