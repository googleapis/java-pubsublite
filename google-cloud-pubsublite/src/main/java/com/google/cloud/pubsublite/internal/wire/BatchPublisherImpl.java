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


import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.pubsublite.internal.PublishSequenceNumber;
import com.google.cloud.pubsublite.internal.wire.StreamFactories.PublishStreamFactory;
import com.google.cloud.pubsublite.proto.MessagePublishRequest;
import com.google.cloud.pubsublite.proto.MessagePublishResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import java.util.Collection;

class BatchPublisherImpl
    extends SingleConnection<PublishRequest, PublishResponse, MessagePublishResponse>
    implements BatchPublisher {
  static class Factory implements BatchPublisherFactory {
    @Override
    public BatchPublisherImpl New(
        StreamFactory<PublishRequest, PublishResponse> streamFactory,
        ResponseObserver<MessagePublishResponse> clientStream,
        PublishRequest initialRequest) {
      return new BatchPublisherImpl(streamFactory::New, clientStream, initialRequest);
    }
  }

  private final boolean sendSequenceNumbers;

  private BatchPublisherImpl(
      PublishStreamFactory streamFactory,
      ResponseObserver<MessagePublishResponse> publishCompleteStream,
      PublishRequest initialRequest) {
    super(streamFactory, publishCompleteStream);
    initialize(initialRequest);

    // Publish idempotency is enabled when a publisher client id is specified. Otherwise do not send
    // sequence numbers to the stream.
    this.sendSequenceNumbers = !initialRequest.getInitialRequest().getClientId().isEmpty();
  }

  @Override
  public void publish(
      Collection<PubSubMessage> messages, PublishSequenceNumber firstSequenceNumber) {
    PublishRequest.Builder builder = PublishRequest.newBuilder();
    MessagePublishRequest.Builder publishRequestBuilder = builder.getMessagePublishRequestBuilder();
    publishRequestBuilder.addAllMessages(messages);
    if (sendSequenceNumbers) {
      publishRequestBuilder.setFirstSequenceNumber(firstSequenceNumber.value());
    }
    sendToStream(builder.build());
  }

  @Override
  protected void handleStreamResponse(PublishResponse response) {
    if (!response.hasMessageResponse()) {
      return;
    }
    sendToClient(response.getMessageResponse());
  }
}
