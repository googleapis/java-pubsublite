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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.cloudpubsub.Publisher;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.TrivialProxyService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;

// A WrappingPublisher wraps the wire protocol client with a Cloud Pub/Sub api compliant
// publisher. It encodes a PublishMetadata object in the response string.
public class WrappingPublisher extends TrivialProxyService implements Publisher {
  private final com.google.cloud.pubsublite.internal.Publisher<PublishMetadata> wirePublisher;
  private final MessageTransformer<PubsubMessage, Message> transformer;

  public WrappingPublisher(
      com.google.cloud.pubsublite.internal.Publisher<PublishMetadata> wirePublisher,
      MessageTransformer<PubsubMessage, Message> transformer)
      throws ApiException {
    super(wirePublisher);
    this.wirePublisher = wirePublisher;
    this.transformer = transformer;
  }

  // Publisher implementation.
  @Override
  public ApiFuture<String> publish(PubsubMessage message) {
    Message wireMessage;
    try {
      wireMessage = transformer.transform(message);
    } catch (Throwable t) {
      CheckedApiException e = toCanonical(t);
      onPermanentError(e);
      return ApiFutures.immediateFailedFuture(e.underlying);
    }
    return ApiFutures.transform(
        wirePublisher.publish(wireMessage),
        PublishMetadata::encode,
        MoreExecutors.directExecutor());
  }
}
