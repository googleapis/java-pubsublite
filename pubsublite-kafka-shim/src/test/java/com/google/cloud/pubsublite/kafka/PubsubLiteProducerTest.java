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

package com.google.cloud.pubsublite.kafka;

import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static com.google.cloud.pubsublite.kafka.StatusTestHelpers.assertFutureThrowsCode;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class PubsubLiteProducerTest {
  abstract static class FakePublisher extends FakeApiService
      implements Publisher<PublishMetadata> {}

  private static final ProducerRecord<byte[], byte[]> RECORD =
      new ProducerRecord<>(
          example(TopicPath.class).toString(), "abc".getBytes(), "defg".getBytes());
  private static final Message MESSAGE = RecordTransforms.toMessage(RECORD);
  private static final TopicPartition TOPIC_PARTITION =
      new TopicPartition(
          example(TopicPath.class).toString(), (int) example(Partition.class).value());

  @Spy FakePublisher underlying;

  Producer<byte[], byte[]> producer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    producer = new PubsubLiteProducer(underlying, 3, example(TopicPath.class));
    verify(underlying).startAsync();
    verify(underlying).awaitRunning();
  }

  @Test
  public void noTransactions() {
    assertThrows(UnsupportedVersionException.class, () -> producer.initTransactions());
    assertThrows(UnsupportedVersionException.class, () -> producer.beginTransaction());
    assertThrows(
        UnsupportedVersionException.class,
        () -> producer.sendOffsetsToTransaction(ImmutableMap.of(), ""));
    assertThrows(
        UnsupportedVersionException.class,
        () -> producer.sendOffsetsToTransaction(ImmutableMap.of(), new ConsumerGroupMetadata("")));
    assertThrows(UnsupportedVersionException.class, () -> producer.commitTransaction());
    assertThrows(UnsupportedVersionException.class, () -> producer.abortTransaction());
  }

  @Test
  public void badRecordThrows() {
    TopicPath other =
        example(TopicPath.class).toBuilder().setName(TopicName.of("not-example")).build();
    ProducerRecord<byte[], byte[]> badTopicRecord =
        new ProducerRecord<>(other.toString(), "abc".getBytes());
    assertThrows(UnsupportedOperationException.class, () -> producer.send(badTopicRecord));
    assertThrows(
        UnsupportedOperationException.class,
        () -> producer.send(badTopicRecord, (metadata, ex) -> {}));

    ProducerRecord<byte[], byte[]> withPartitionRecord =
        new ProducerRecord<>(other.toString(), 999, "abc".getBytes(), "def".getBytes());
    assertThrows(UnsupportedOperationException.class, () -> producer.send(withPartitionRecord));
    assertThrows(
        UnsupportedOperationException.class,
        () -> producer.send(withPartitionRecord, (metadata, ex) -> {}));
  }

  @Test
  public void badTopicThrows() {
    TopicPath other =
        example(TopicPath.class).toBuilder().setName(TopicName.of("not-example")).build();
    ProducerRecord<byte[], byte[]> record =
        new ProducerRecord<>(other.toString(), "abc".getBytes());
    assertThrows(UnsupportedOperationException.class, () -> producer.send(record));
    assertThrows(
        UnsupportedOperationException.class, () -> producer.send(record, (metadata, ex) -> {}));
  }

  @Test
  public void sendSuccess() throws Exception {
    SettableApiFuture<PublishMetadata> response = SettableApiFuture.create();
    when(underlying.publish(MESSAGE)).thenReturn(response);
    Future<RecordMetadata> future = producer.send(RECORD);
    verify(underlying).publish(MESSAGE);
    response.set(PublishMetadata.of(example(Partition.class), example(Offset.class)));
    // RecordMetadata doesn't define a equals implementation.
    RecordMetadata metadata = future.get();
    assertThat(metadata.topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(metadata.partition()).isEqualTo(example(Partition.class).value());
    assertThat(metadata.offset()).isEqualTo(example(Offset.class).value());
    assertThat(metadata.serializedKeySize()).isEqualTo(3);
    assertThat(metadata.serializedValueSize()).isEqualTo(4);
  }

  @Test
  public void sendSuccessWithCallback() throws Exception {
    SettableApiFuture<PublishMetadata> response = SettableApiFuture.create();
    SettableApiFuture<RecordMetadata> leaked = SettableApiFuture.create();
    when(underlying.publish(MESSAGE)).thenReturn(response);
    Future<RecordMetadata> future =
        producer.send(
            RECORD,
            (metadata, ex) -> {
              if (metadata != null) {
                leaked.set(metadata);
              } else {
                leaked.setException(ex);
              }
            });
    verify(underlying).publish(MESSAGE);
    response.set(PublishMetadata.of(example(Partition.class), example(Offset.class)));
    // RecordMetadata doesn't define a equals implementation.
    RecordMetadata metadata = leaked.get();
    assertThat(metadata.topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(metadata.partition()).isEqualTo(example(Partition.class).value());
    assertThat(metadata.offset()).isEqualTo(example(Offset.class).value());
    assertThat(metadata.serializedKeySize()).isEqualTo(3);
    assertThat(metadata.serializedValueSize()).isEqualTo(4);
    RecordMetadata resultMetadata = future.get();
    assertThat(resultMetadata.topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(resultMetadata.partition()).isEqualTo(example(Partition.class).value());
    assertThat(resultMetadata.offset()).isEqualTo(example(Offset.class).value());
    assertThat(resultMetadata.serializedKeySize()).isEqualTo(3);
    assertThat(resultMetadata.serializedValueSize()).isEqualTo(4);
  }

  @Test
  public void sendError() {
    SettableApiFuture<PublishMetadata> response = SettableApiFuture.create();
    when(underlying.publish(MESSAGE)).thenReturn(response);
    Future<RecordMetadata> future = producer.send(RECORD);
    verify(underlying).publish(MESSAGE);
    response.setException(new CheckedApiException(Code.FAILED_PRECONDITION).underlying);
    assertFutureThrowsCode(future, Code.FAILED_PRECONDITION);
  }

  @Test
  public void sendErrorWithCallback() {
    SettableApiFuture<PublishMetadata> response = SettableApiFuture.create();
    SettableApiFuture<RecordMetadata> leaked = SettableApiFuture.create();
    when(underlying.publish(MESSAGE)).thenReturn(response);
    Future<RecordMetadata> future =
        producer.send(
            RECORD,
            (metadata, ex) -> {
              if (metadata != null) {
                leaked.set(metadata);
              } else {
                leaked.setException(ex);
              }
            });
    verify(underlying).publish(MESSAGE);
    response.setException(new CheckedApiException(Code.FAILED_PRECONDITION).underlying);
    assertFutureThrowsCode(future, Code.FAILED_PRECONDITION);
    assertFutureThrowsCode(leaked, Code.FAILED_PRECONDITION);
  }

  @Test
  public void flush() throws Exception {
    producer.flush();
    verify(underlying).flush();
  }

  @Test
  public void close() throws Exception {
    producer.close();
    verify(underlying).stopAsync();
    verify(underlying).awaitTerminated(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
  }
}
