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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.cloudpubsub.PublisherApiService;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;

// A WrappingPublisher wraps the wire protocol client with a Cloud Pub/Sub api compliant
// publisher. It encodes a PublishMetadata object in the response string.
public class WrappingPublisher extends ProxyService implements PublisherApiService {
  private final Publisher<PublishMetadata> wirePublisher;
  private final MessageTransformer<PubsubMessage, Message> transformer;

  public WrappingPublisher(
      Publisher<PublishMetadata> wirePublisher,
      MessageTransformer<PubsubMessage, Message> transformer)
      throws StatusException {
    this.wirePublisher = wirePublisher;
    this.transformer = transformer;
    addServices(wirePublisher);
  }

  // ProxyService implementation. SinglePartitionPublisher is a thin proxy around a wire publisher.
  @Override
  protected void start() {}

  @Override
  protected void stop() {}

  @Override
  protected void handlePermanentError(StatusException error) {}

  // Publisher implementation.
  @Override
  public ApiFuture<String> publish(PubsubMessage message) {
    Message wireMessage;
    try {
      wireMessage = transformer.transform(message);
    } catch (StatusException e) {
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e);
    }
    return ApiFutures.transform(
        wirePublisher.publish(wireMessage),
        PublishMetadata::encode,
        MoreExecutors.directExecutor());
  }
}
