/*
 * Copyright 2023 Google LLC
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

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.PublishSequenceNumber;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.SequencedPublisher;
import java.io.IOException;
import javax.annotation.concurrent.GuardedBy;

/**
 * A publisher that assigns sequence numbers to messages and delegates to an underlying
 * SequencedPublisher.
 */
public class SequenceAssigningPublisher extends ProxyService implements Publisher<Offset> {
  private final SequencedPublisher<Offset> publisher;

  @GuardedBy("this")
  private PublishSequenceNumber nextSequence = PublishSequenceNumber.FIRST;

  SequenceAssigningPublisher(SequencedPublisher<Offset> publisher) throws ApiException {
    super(publisher);
    this.publisher = publisher;
  }

  // Publisher implementation.
  @Override
  public synchronized ApiFuture<Offset> publish(Message message) {
    ApiFuture<Offset> future = publisher.publish(message, nextSequence);
    nextSequence = nextSequence.next();
    return future;
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
