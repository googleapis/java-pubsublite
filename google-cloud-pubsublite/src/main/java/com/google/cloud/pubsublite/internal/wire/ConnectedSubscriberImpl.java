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

import static com.google.cloud.pubsublite.internal.CheckedApiPreconditions.checkState;

import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.MessageResponse;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.common.base.Preconditions;
import com.google.common.flogger.GoogleLogger;
import java.util.List;
import java.util.stream.Collectors;

class ConnectedSubscriberImpl
    extends SingleConnection<SubscribeRequest, SubscribeResponse, List<SequencedMessage>>
    implements ConnectedSubscriber {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final SubscribeRequest initialRequest;

  static class Factory implements ConnectedSubscriberFactory {

    @Override
    public ConnectedSubscriberImpl New(
        StreamFactory<SubscribeRequest, SubscribeResponse> streamFactory,
        ResponseObserver<List<SequencedMessage>> clientStream,
        SubscribeRequest initialRequest) {
      return new ConnectedSubscriberImpl(streamFactory, clientStream, initialRequest);
    }
  }

  private ConnectedSubscriberImpl(
      StreamFactory<SubscribeRequest, SubscribeResponse> streamFactory,
      ResponseObserver<List<SequencedMessage>> clientStream,
      SubscribeRequest initialRequest) {
    super(streamFactory, clientStream);
    this.initialRequest = initialRequest;
    initialize(initialRequest);
  }

  // ConnectedSubscriber implementation.
  @Override
  public void allowFlow(FlowControlRequest request) {
    Preconditions.checkArgument(request.getAllowedBytes() >= 0);
    Preconditions.checkArgument(request.getAllowedMessages() >= 0);
    sendToStream(SubscribeRequest.newBuilder().setFlowControl(request).build());
  }

  @Override
  protected void handleInitialResponse(SubscribeResponse response) throws CheckedApiException {
    checkState(
        response.hasInitial(),
        String.format(
            "Received non-initial first response %s on stream with initial request %s.",
            response, initialRequest));
  }

  @Override
  protected void handleStreamResponse(SubscribeResponse response) throws CheckedApiException {
    switch (response.getResponseCase()) {
      case INITIAL:
        throw new CheckedApiException(
            String.format(
                "Received duplicate initial response on stream with initial request %s.",
                initialRequest),
            Code.FAILED_PRECONDITION);
      case MESSAGES:
        onMessages(response.getMessages());
        return;
      case SEEK:
        throw new CheckedApiException(
            String.format(
                "Received seek response from client which never sends seek requests %s.",
                initialRequest),
            Code.FAILED_PRECONDITION);
      default:
        throw new CheckedApiException(
            "Received a message on the stream with no case set.", Code.FAILED_PRECONDITION);
    }
  }

  private void onMessages(MessageResponse response) throws CheckedApiException {
    checkState(
        response.getMessagesCount() > 0,
        String.format(
            "Received an empty MessageResponse on stream with initial request %s.",
            initialRequest));
    List<SequencedMessage> messages =
        response.getMessagesList().stream()
            .map(SequencedMessage::fromProto)
            .collect(Collectors.toList());
    checkState(
        Predicates.isOrdered(messages),
        String.format(
            "Received out of order messages on the stream with initial request %s.",
            initialRequest));
    sendToClient(messages);
  }
}
