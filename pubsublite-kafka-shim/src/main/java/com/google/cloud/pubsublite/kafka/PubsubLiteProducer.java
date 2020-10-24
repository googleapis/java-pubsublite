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

import static com.google.cloud.pubsublite.kafka.KafkaExceptionUtils.toKafka;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.common.collect.ImmutableMap;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.UnsupportedVersionException;

class PubsubLiteProducer implements Producer<byte[], byte[]> {
  private static final UnsupportedVersionException NO_TRANSACTIONS_EXCEPTION =
      new UnsupportedVersionException(
          "Pub/Sub Lite is a non-transactional system and does not support producer transactions.");
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  private final Publisher<PublishMetadata> publisher;
  private final TopicPath topicPath;
  private final long partitionCount;

  PubsubLiteProducer(
      Publisher<PublishMetadata> publisher, long partitionCount, TopicPath topicPath) {
    this.publisher = publisher;
    this.topicPath = topicPath;
    this.partitionCount = partitionCount;
    this.publisher.addListener(
        new Listener() {
          @Override
          public void failed(State from, Throwable failure) {
            logger.atWarning().withCause(failure).log("Pub/Sub Lite Publisher failed.");
          }
        },
        MoreExecutors.directExecutor());
    this.publisher.startAsync().awaitRunning();
  }

  @Override
  public void initTransactions() {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  @Override
  public void beginTransaction() {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  @Override
  public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> map, String s) {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  @Override
  public void sendOffsetsToTransaction(
      Map<TopicPartition, OffsetAndMetadata> map, ConsumerGroupMetadata consumerGroupMetadata) {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  @Override
  public void commitTransaction() {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  @Override
  public void abortTransaction() {
    throw NO_TRANSACTIONS_EXCEPTION;
  }

  private void checkTopic(String topic) {
    try {
      TopicPath path = TopicPath.parse(topic);
      if (!path.equals(topicPath)) {
        throw new UnsupportedOperationException(
            "Pub/Sub Lite producers may only interact with the one topic they are configured for.");
      }
    } catch (ApiException e) {
      throw toKafka(e);
    }
  }

  @Override
  public ApiFuture<RecordMetadata> send(ProducerRecord<byte[], byte[]> producerRecord) {
    checkTopic(producerRecord.topic());
    if (producerRecord.partition() != null) {
      throw new UnsupportedOperationException(
          "Pub/Sub Lite producers may not specify a partition in their records.");
    }
    ApiFuture<PublishMetadata> future =
        publisher.publish(RecordTransforms.toMessage(producerRecord));
    return ApiFutures.transform(
        future,
        meta ->
            new RecordMetadata(
                new TopicPartition(topicPath.toString(), (int) meta.partition().value()),
                meta.offset().value(),
                0,
                -1,
                0L,
                producerRecord.key().length,
                producerRecord.value().length),
        MoreExecutors.directExecutor());
  }

  @Override
  public Future<RecordMetadata> send(
      ProducerRecord<byte[], byte[]> producerRecord, Callback callback) {
    ApiFuture<RecordMetadata> future = send(producerRecord);
    ApiFutures.addCallback(
        future,
        new ApiFutureCallback<RecordMetadata>() {
          @Override
          public void onFailure(Throwable throwable) {
            callback.onCompletion(null, ExtractStatus.toCanonical(throwable));
          }

          @Override
          public void onSuccess(RecordMetadata recordMetadata) {
            callback.onCompletion(recordMetadata, null);
          }
        },
        MoreExecutors.directExecutor());
    return future;
  }

  @Override
  public void flush() {
    try {
      publisher.flush();
    } catch (IOException e) {
      throw toKafka(e);
    }
  }

  @Override
  public List<PartitionInfo> partitionsFor(String s) {
    checkTopic(s);
    return SharedBehavior.partitionsFor(partitionCount, topicPath);
  }

  @Override
  public Map<MetricName, ? extends Metric> metrics() {
    return ImmutableMap.of();
  }

  @Override
  public void close() {
    close(Duration.ofMillis(Long.MAX_VALUE));
  }

  @Override
  public void close(Duration duration) {
    try {
      publisher.stopAsync().awaitTerminated(duration.toMillis(), MILLISECONDS);
    } catch (TimeoutException e) {
      logger.atWarning().withCause(e).log("Failed to close publisher.");
    }
  }
}
