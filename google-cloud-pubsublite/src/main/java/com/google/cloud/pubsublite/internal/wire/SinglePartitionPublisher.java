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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;

public class SinglePartitionPublisher extends TrivialProxyService
    implements Publisher<PublishMetadata> {
  private final Publisher<Offset> publisher;
  private final Partition partition;

  SinglePartitionPublisher(Publisher<Offset> publisher, Partition partition) throws ApiException {
    super(publisher);
    this.publisher = publisher;
    this.partition = partition;
  }

  // Publisher implementation.
  @Override
  public ApiFuture<PublishMetadata> publish(Message message) {
    return ApiFutures.transform(
        publisher.publish(message),
        offset -> PublishMetadata.of(partition, offset),
        MoreExecutors.directExecutor());
  }

  @Override
  public void flush() throws IOException {
    publisher.flush();
  }
}
