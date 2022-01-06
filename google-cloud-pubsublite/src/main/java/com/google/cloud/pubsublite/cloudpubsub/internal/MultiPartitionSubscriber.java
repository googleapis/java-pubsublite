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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import com.google.api.core.ApiService;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.internal.ProxyService;
import java.util.List;

// A MultiPartitionSubscriber wraps multiple subscribers into a single ApiService that can be
// interacted with. If any single subscriber fails, all others are stopped.
public class MultiPartitionSubscriber extends ProxyService implements Subscriber {
  public static Subscriber of(List<ApiService> services) throws ApiException {
    return new MultiPartitionSubscriber(services);
  }

  private MultiPartitionSubscriber(List<ApiService> subscribers) throws ApiException {
    super(subscribers);
  }
}
