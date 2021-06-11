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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.cloudpubsub.NackHandler;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.ProxyService;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import java.util.List;

public class SinglePartitionSubscriber extends ProxyService implements Subscriber {
  private final MessageReceiver receiver;
  private final MessageTransformer<SequencedMessage, PubsubMessage> transformer;
  private final AckSetTracker ackSetTracker;
  private final NackHandler nackHandler;
  private final FlowControlSettings flowControlSettings;
  private final com.google.cloud.pubsublite.internal.wire.Subscriber wireSubscriber;

  public SinglePartitionSubscriber(
      MessageReceiver receiver,
      MessageTransformer<SequencedMessage, PubsubMessage> transformer,
      AckSetTracker ackSetTracker,
      NackHandler nackHandler,
      ResettableSubscriberFactory wireSubscriberFactory,
      FlowControlSettings flowControlSettings)
      throws ApiException {
    this.receiver = receiver;
    this.transformer = transformer;
    this.ackSetTracker = ackSetTracker;
    this.nackHandler = nackHandler;
    this.flowControlSettings = flowControlSettings;
    this.wireSubscriber =
        wireSubscriberFactory.newSubscriber(this::onMessages, this::onSubscriberReset);
    addServices(ackSetTracker, wireSubscriber);
  }

  // ProxyService implementation.
  @Override
  protected void handlePermanentError(CheckedApiException error) {}

  @Override
  protected void start() throws CheckedApiException {
    wireSubscriber.allowFlow(
        FlowControlRequest.newBuilder()
            .setAllowedMessages(flowControlSettings.messagesOutstanding())
            .setAllowedBytes(flowControlSettings.bytesOutstanding())
            .build());
  }

  @Override
  protected void stop() {}

  @VisibleForTesting
  void onMessages(List<SequencedMessage> sequencedMessages) {
    try {
      for (SequencedMessage message : sequencedMessages) {
        PubsubMessage userMessage = transformer.transform(message);
        long bytes = message.byteSize();
        Runnable trackerConsumer = ackSetTracker.track(message);
        AckReplyConsumer clientConsumer =
            new AckReplyConsumer() {
              @Override
              public void ack() {
                trackerConsumer.run();
                try {
                  wireSubscriber.allowFlow(
                      FlowControlRequest.newBuilder()
                          .setAllowedMessages(1)
                          .setAllowedBytes(bytes)
                          .build());
                } catch (CheckedApiException e) {
                  onPermanentError(e);
                }
              }

              @Override
              public void nack() {
                ApiFuture<Void> nackDone = nackHandler.nack(userMessage);
                ApiFutures.addCallback(
                    nackDone,
                    new ApiFutureCallback<Void>() {
                      @Override
                      public void onFailure(Throwable t) {
                        onPermanentError(ExtractStatus.toCanonical(t));
                      }

                      @Override
                      public void onSuccess(Void result) {
                        ack();
                      }
                    },
                    MoreExecutors.directExecutor());
              }
            };
        receiver.receiveMessage(userMessage, clientConsumer);
      }
    } catch (Throwable t) {
      onPermanentError(ExtractStatus.toCanonical(t));
    }
  }

  @VisibleForTesting
  boolean onSubscriberReset() throws CheckedApiException {
    ackSetTracker.waitUntilCommitted();
    return true;
  }
}
