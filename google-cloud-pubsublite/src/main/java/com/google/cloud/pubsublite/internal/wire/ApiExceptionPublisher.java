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
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import java.io.IOException;

public class ApiExceptionPublisher<T> extends ProxyService implements Publisher<T> {
  private final Publisher<T> publisher;

  ApiExceptionPublisher(Publisher<T> publisher) throws ApiException {
    super(publisher);
    this.publisher = publisher;
  }

  @Override
  public ApiFuture<T> publish(PubSubMessage message) {
    return toClientFuture(publisher.publish(message));
  }

  @Override
  public void cancelOutstandingPublishes() {
    publisher.cancelOutstandingPublishes();
  }

  @Override
  public void flush() throws IOException {
    publisher.flush();
  }
}
