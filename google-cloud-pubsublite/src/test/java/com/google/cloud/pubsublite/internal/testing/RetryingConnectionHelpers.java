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

package com.google.cloud.pubsublite.internal.testing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.api.core.ApiService;
import com.google.api.core.ApiService.Listener;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.internal.wire.SystemExecutors;
import java.util.concurrent.Future;

public class RetryingConnectionHelpers {
  public static Future<Void> whenFailed(ApiService service) {
    Listener listener = mock(Listener.class);
    SettableApiFuture<Void> future = SettableApiFuture.create();
    doAnswer(
            args -> {
              future.set(null);
              return null;
            })
        .when(listener)
        .failed(any(), any());
    service.addListener(listener, SystemExecutors.getFuturesExecutor());
    return future;
  }

  public static Future<Void> whenTerminated(ApiService service) {
    Listener listener = mock(Listener.class);
    SettableApiFuture<Void> future = SettableApiFuture.create();
    doAnswer(
            args -> {
              future.set(null);
              return null;
            })
        .when(listener)
        .failed(any(), any());
    doAnswer(
            args -> {
              future.set(null);
              return null;
            })
        .when(listener)
        .terminated(any());
    service.addListener(listener, SystemExecutors.getFuturesExecutor());
    return future;
  }
}
