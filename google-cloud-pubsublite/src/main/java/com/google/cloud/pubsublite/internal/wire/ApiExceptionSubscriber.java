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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toClientFuture;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;

public class ApiExceptionSubscriber extends TrivialProxyService implements Subscriber {
  private final Subscriber subscriber;

  ApiExceptionSubscriber(Subscriber subscriber) {
    super(subscriber);
    this.subscriber = subscriber;
  }

  @Override
  public ApiFuture<Offset> seek(SeekRequest request) {
    return toClientFuture(subscriber.seek(request));
  }

  @Override
  public boolean seekInFlight() {
    return subscriber.seekInFlight();
  }

  @Override
  public void allowFlow(FlowControlRequest request) throws CheckedApiException {
    subscriber.allowFlow(request);
  }
}
