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

package com.google.cloud.pubsublite.beam;

import static com.google.cloud.pubsublite.cloudpubsub.MessageTransforms.fromCpsPublishTransformer;
import static com.google.cloud.pubsublite.cloudpubsub.MessageTransforms.toCpsPublishTransformer;
import static com.google.cloud.pubsublite.cloudpubsub.MessageTransforms.toCpsSubscribeTransformer;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.KeyExtractor;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.pubsub.v1.PubsubMessage;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

// A class providing transforms between Cloud Pub/Sub and Pub/Sub Lite message types.
public final class CloudPubsubTransforms {
  private CloudPubsubTransforms() {}

  // Transform a collection of SequencedMessages to Cloud Pub/Sub received PubsubMessages.
  public static PTransform<PCollection<? extends SequencedMessage>, PCollection<PubsubMessage>>
      toCpsSubscribeTransform() {
    return ParDo.of(
        new DoFn<SequencedMessage, PubsubMessage>() {
          @ProcessElement
          public void processElement(
              @Element SequencedMessage sequencedMessage, OutputReceiver<PubsubMessage> output)
              throws CheckedApiException {
            output.output(toCpsSubscribeTransformer().transform(sequencedMessage));
          }
        });
  }

  // Transform a collection of Cloud Pub/Sub publishable PubsubMessages (ignoring message_id and
  // publish_time) to Pub/Sub Lite Messages.
  public static PTransform<PCollection<? extends PubsubMessage>, PCollection<Message>>
      fromCpsPublishTransform() {
    return ParDo.of(
        new DoFn<PubsubMessage, Message>() {
          @ProcessElement
          public void processElement(@Element PubsubMessage message, OutputReceiver<Message> output)
              throws CheckedApiException {
            output.output(fromCpsPublishTransformer(KeyExtractor.DEFAULT).transform(message));
          }
        });
  }

  // Transform a collection of Pub/Sub Lite Messages to publishab Cloud Pub/Sub incomplete,
  // publishable
  // PubsubMessages.
  public static PTransform<PCollection<? extends Message>, PCollection<PubsubMessage>>
      toCpsPublishTransform() {
    return ParDo.of(
        new DoFn<Message, PubsubMessage>() {
          @ProcessElement
          public void processElement(@Element Message message, OutputReceiver<PubsubMessage> output)
              throws CheckedApiException {
            output.output(toCpsPublishTransformer().transform(message));
          }
        });
  }

  // Ensure that all messages that pass through can be converted to Cloud Pub/Sub messages using the
  // standard transformation methods.
  public static PTransform<PCollection<? extends Message>, PCollection<Message>>
      ensureUsableAsCloudPubsub() {
    return ParDo.of(
        new DoFn<Message, Message>() {
          @ProcessElement
          public void processElement(@Element Message message, OutputReceiver<Message> output)
              throws CheckedApiException {
            Object unused = toCpsPublishTransformer().transform(message);
            output.output(message);
          }
        });
  }
}
