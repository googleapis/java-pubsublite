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

package com.google.cloud.pubsublite.cloudpubsub;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageTransformer;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.Timestamps;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import java.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MessageTransformsTest {
  private static final byte[] notUtf8Array = {(byte) 0xFF, (byte) 0xFF};
  private static final MessageTransformer<SequencedMessage, PubsubMessage> subscribeTransformer =
      MessageTransforms.toCpsSubscribeTransformer();
  private static final MessageTransformer<PubsubMessage, Message> publishTransformer =
      MessageTransforms.fromCpsPublishTransformer(KeyExtractor.DEFAULT);

  @Test
  public void subscribeTransformInvalidKey() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                subscribeTransformer.transform(
                    SequencedMessage.of(
                        Message.builder().setKey(ByteString.copyFrom(notUtf8Array)).build(),
                        Timestamps.fromNanos(0),
                        Offset.of(10),
                        10)));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void subscribeTransformContainsMagicAttribute() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                subscribeTransformer.transform(
                    SequencedMessage.of(
                        Message.builder()
                            .setAttributes(
                                ImmutableListMultimap.<String, ByteString>builder()
                                    .put(
                                        MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                                        ByteString.EMPTY)
                                    .build())
                            .build(),
                        Timestamps.fromNanos(0),
                        Offset.of(10),
                        10)));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void subscribeTransformContainsMultipleAttributes() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                subscribeTransformer.transform(
                    SequencedMessage.of(
                        Message.builder()
                            .setAttributes(
                                ImmutableListMultimap.<String, ByteString>builder()
                                    .put("abc", ByteString.EMPTY)
                                    .put("abc", ByteString.copyFromUtf8("def"))
                                    .build())
                            .build(),
                        Timestamps.fromNanos(0),
                        Offset.of(10),
                        10)));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void subscribeTransformContainsNonUtf8Attributes() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                subscribeTransformer.transform(
                    SequencedMessage.of(
                        Message.builder()
                            .setAttributes(
                                ImmutableListMultimap.<String, ByteString>builder()
                                    .put("abc", ByteString.copyFrom(notUtf8Array))
                                    .build())
                            .build(),
                        Timestamps.fromNanos(0),
                        Offset.of(10),
                        10)));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void subscribeTransformCorrect() throws StatusException {
    SequencedMessage message =
        SequencedMessage.of(
            Message.builder()
                .setAttributes(
                    ImmutableListMultimap.<String, ByteString>builder()
                        .put("abc", ByteString.EMPTY)
                        .put("def", ByteString.copyFromUtf8("hij"))
                        .build())
                .setData(ByteString.copyFrom(notUtf8Array))
                .setEventTime(Timestamps.fromNanos(10))
                .setKey(ByteString.copyFromUtf8("some_key"))
                .build(),
            Timestamps.fromSeconds(5),
            Offset.of(7),
            2);
    PubsubMessage result =
        PubsubMessage.newBuilder()
            .setData(message.message().data())
            .setMessageId("7") // The offset as a string
            .setOrderingKey("some_key") // The key field
            .setPublishTime(message.publishTime())
            .putAttributes("abc", "")
            .putAttributes("def", "hij")
            .putAttributes(
                MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                MessageTransforms.encodeAttributeEventTime(message.message().eventTime().get()))
            .build();
    assertThat(subscribeTransformer.transform(message)).isEqualTo(result);
  }

  @Test
  public void publishTransformExtractorFailure() {
    MessageTransformer<PubsubMessage, Message> transformer =
        MessageTransforms.fromCpsPublishTransformer(
            message -> {
              throw Status.INTERNAL.asException();
            });
    StatusException e =
        assertThrows(
            StatusException.class, () -> transformer.transform(PubsubMessage.getDefaultInstance()));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INTERNAL);
  }

  @Test
  public void publishTransformInvalidEventTime() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                publishTransformer.transform(
                    PubsubMessage.newBuilder()
                        .putAttributes(
                            MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                            "Unlikely to be an encoded timestamp proto.")
                        .build()));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void publishTransformValidBase64InvalidEventTime() {
    StatusException e =
        assertThrows(
            StatusException.class,
            () ->
                publishTransformer.transform(
                    PubsubMessage.newBuilder()
                        .putAttributes(
                            MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                            Base64.getEncoder()
                                .encodeToString(
                                    ("Unlikely to be an encoded timestamp proto.".getBytes())))
                        .build()));
    assertThat(e.getStatus().getCode()).isEqualTo(Code.INVALID_ARGUMENT);
  }

  @Test
  public void publishTransformCorrect() throws StatusException {
    PubsubMessage message =
        PubsubMessage.newBuilder()
            .setData(ByteString.copyFrom(notUtf8Array))
            .setOrderingKey("some_key") // The key field
            .putAttributes("abc", "")
            .putAttributes("def", "hij")
            .putAttributes(
                MessageTransforms.PUBSUB_LITE_EVENT_TIME_TIMESTAMP_PROTO,
                MessageTransforms.encodeAttributeEventTime(Timestamps.fromNanos(100)))
            .build();
    Message result =
        Message.builder()
            .setData(ByteString.copyFrom(notUtf8Array))
            .setKey(ByteString.copyFromUtf8("some_key"))
            .setAttributes(
                ImmutableListMultimap.<String, ByteString>builder()
                    .put("abc", ByteString.EMPTY)
                    .put("def", ByteString.copyFromUtf8("hij"))
                    .build())
            .setEventTime(Timestamps.fromNanos(100))
            .build();

    assertThat(publishTransformer.transform(message)).isEqualTo(result);
  }
}
