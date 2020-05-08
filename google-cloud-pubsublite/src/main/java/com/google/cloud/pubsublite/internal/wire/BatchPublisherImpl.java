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

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.proto.MessagePublishResponse;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Collection;
import java.util.Optional;

class BatchPublisherImpl extends SingleConnection<PublishRequest, PublishResponse, Offset>
    implements BatchPublisher {
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private Optional<Offset> lastOffset = Optional.empty();

  static class Factory implements BatchPublisherFactory {
    @Override
    public BatchPublisherImpl New(
        StreamFactory<PublishRequest, PublishResponse> streamFactory,
        StreamObserver<Offset> clientStream,
        PublishRequest initialRequest) {
      return new BatchPublisherImpl(streamFactory, clientStream, initialRequest);
    }
  }

  private BatchPublisherImpl(
      StreamFactory<PublishRequest, PublishResponse> streamFactory,
      StreamObserver<Offset> publishCompleteStream,
      PublishRequest initialRequest) {
    super(streamFactory, publishCompleteStream);
    initialize(initialRequest);
  }

  @Override
  public void publish(Collection<PubSubMessage> messages) {
    PublishRequest.Builder builder = PublishRequest.newBuilder();
    builder.getMessagePublishRequestBuilder().addAllMessages(messages);
    sendToStream(builder.build());
  }

  @Override
  protected Status handleInitialResponse(PublishResponse response) {
    if (!response.hasInitialResponse()) {
      return Status.FAILED_PRECONDITION.withDescription(
          "First stream response is not an initial response: " + response);
    }
    return Status.OK;
  }

  @Override
  protected Status handleStreamResponse(PublishResponse response) {
    if (response.hasInitialResponse()) {
      return Status.FAILED_PRECONDITION.withDescription("Received duplicate initial response.");
    } else if (response.hasMessageResponse()) {
      return onMessageResponse(response.getMessageResponse());
    } else {
      return Status.FAILED_PRECONDITION.withDescription(
          "Received response on stream which was neither a message or initial response.");
    }
  }

  private Status onMessageResponse(MessagePublishResponse response) {
    Offset offset = Offset.of(response.getStartCursor().getOffset());
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (lastOffset.isPresent() && offset.value() <= lastOffset.get().value()) {
        return Status.FAILED_PRECONDITION.withDescription(
            "Received out of order offsets on stream.");
      }
      lastOffset = Optional.of(offset);
    }
    sendToClient(offset);
    return Status.OK;
  }
}
