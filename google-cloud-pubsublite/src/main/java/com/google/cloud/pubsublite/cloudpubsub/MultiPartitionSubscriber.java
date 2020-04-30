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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.cloud.pubsub.v1.SubscriberInterface;
import com.google.cloud.pubsublite.internal.ProxyService;
import io.grpc.StatusException;
import java.util.List;

// A MultiPartitionSubscriber wraps multiple subscribers into a single ApiService that can be
// interacted with. If any single subscriber fails, all others are stopped.
public class MultiPartitionSubscriber extends ProxyService implements SubscriberInterface {
  public static SubscriberInterface of(List<SubscriberInterface> subscribers)
      throws StatusException {
    return new MultiPartitionSubscriber(subscribers);
  }

  private MultiPartitionSubscriber(List<SubscriberInterface> subscribers) throws StatusException {
    addServices(subscribers);
  }

  @Override
  protected void start() {}

  @Override
  protected void stop() {}

  @Override
  protected void handlePermanentError(StatusException error) {}
}
