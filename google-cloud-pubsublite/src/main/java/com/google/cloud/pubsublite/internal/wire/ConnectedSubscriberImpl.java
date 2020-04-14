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

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.internal.CloseableMonitor;
import com.google.cloud.pubsublite.internal.wire.ConnectedSubscriber.Response;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.MessageResponse;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.cloud.pubsublite.proto.SeekResponse;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.common.base.Preconditions;
import com.google.common.flogger.GoogleLogger;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.stream.Collectors;

class ConnectedSubscriberImpl
    extends SingleConnection<SubscribeRequest, SubscribeResponse, Response>
    implements ConnectedSubscriber {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final SubscribeRequest initialRequest;
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private boolean seekInFlight = false;

  static class Factory implements ConnectedSubscriberFactory {

    @Override
    public ConnectedSubscriberImpl New(
        StreamFactory<SubscribeRequest, SubscribeResponse> streamFactory,
        StreamObserver<Response> clientStream,
        SubscribeRequest initialRequest) {
      return new ConnectedSubscriberImpl(streamFactory, clientStream, initialRequest);
    }
  }

  private ConnectedSubscriberImpl(
      StreamFactory<SubscribeRequest, SubscribeResponse> streamFactory,
      StreamObserver<Response> clientStream,
      SubscribeRequest initialRequest) {
    super(streamFactory, clientStream);
    this.initialRequest = initialRequest;
    initialize(initialRequest);
  }

  // ConnectedSubscriber implementation.
  @Override
  public void seek(SeekRequest request) {
    Preconditions.checkArgument(Predicates.isValidSeekRequest(request));
    Status seekStatus;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      seekStatus = seekLockHeld(request);
    }
    if (!seekStatus.isOk()) {
      setError(seekStatus);
    }
  }

  @Override
  public void allowFlow(FlowControlRequest request) {
    Preconditions.checkArgument(request.getAllowedBytes() >= 0);
    Preconditions.checkArgument(request.getAllowedMessages() >= 0);
    try (CloseableMonitor.Hold h = monitor.enter()) {
      sendToStream(SubscribeRequest.newBuilder().setFlowControl(request).build());
    }
  }

  @GuardedBy("monitor.monitor")
  private Status seekLockHeld(SeekRequest request) {
    if (seekInFlight) {
      return Status.FAILED_PRECONDITION.withDescription(
          String.format(
              "Sent second seek while seek in flight on stream with initial request %s.",
              initialRequest));
    }
    seekInFlight = true;
    sendToStream(SubscribeRequest.newBuilder().setSeek(request).build());
    return Status.OK;
  }

  @Override
  protected Status handleInitialResponse(SubscribeResponse response) {
    if (!response.hasInitial()) {
      return Status.FAILED_PRECONDITION.withDescription(
          String.format(
              "Received non-initial first response %s on stream with initial request %s.",
              response, initialRequest));
    }
    return Status.OK;
  }

  @Override
  protected Status handleStreamResponse(SubscribeResponse response) {
    switch (response.getResponseCase()) {
      case INITIAL:
        return Status.FAILED_PRECONDITION.withDescription(
            String.format(
                "Received duplicate initial response on stream with initial request %s.",
                initialRequest));
      case MESSAGES:
        return onMessages(response.getMessages());
      case SEEK:
        return onSeekResponse(response.getSeek());
      default:
        return Status.FAILED_PRECONDITION.withDescription(
            "Received a message on the stream with no case set.");
    }
  }

  private Status onMessages(MessageResponse response) {
    List<SequencedMessage> messages;
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (seekInFlight) {
        log.atInfo().log(
            "Dropping %s messages due to outstanding seek on stream with initial request %s.",
            response.getMessagesCount(), initialRequest);
        return Status.OK;
      }
      if (response.getMessagesCount() == 0) {
        return Status.FAILED_PRECONDITION.withDescription(
            String.format(
                "Received an empty PullResponse on stream with initial request %s.",
                initialRequest));
      }
      messages =
          response.getMessagesList().stream()
              .map(SequencedMessage::fromProto)
              .collect(Collectors.toList());
      if (!Predicates.isOrdered(messages)) {
        return Status.FAILED_PRECONDITION.withDescription(
            String.format(
                "Received out of order messages on the stream with initial request %s.",
                initialRequest));
      }
    }
    sendToClient(Response.ofMessages(messages));
    return Status.OK;
  }

  private Status onSeekResponse(SeekResponse response) {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      if (!seekInFlight) {
        return Status.FAILED_PRECONDITION.withDescription(
            String.format(
                "Received a SeekResponse when no seeks were in flight on stream with initial"
                    + " request %s.",
                initialRequest));
      }
      seekInFlight = false;
    }
    sendToClient(Response.ofSeekOffset(Offset.create(response.getCursor().getOffset())));
    return Status.OK;
  }
}
