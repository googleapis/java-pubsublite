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

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;

class RecordTransforms {
  private RecordTransforms() {}

  static Message toMessage(ProducerRecord<byte[], byte[]> record) {
    Message.Builder builder =
        Message.builder()
            .setKey(ByteString.copyFrom(record.key()))
            .setData(ByteString.copyFrom(record.value()));
    if (record.timestamp() != null) {
      builder.setEventTime(Timestamps.fromMillis(record.timestamp()));
    }
    ImmutableListMultimap.Builder<String, ByteString> attributes = ImmutableListMultimap.builder();
    record
        .headers()
        .forEach(header -> attributes.put(header.key(), ByteString.copyFrom(header.value())));
    return builder.setAttributes(attributes.build()).build();
  }

  static ConsumerRecord<byte[], byte[]> fromMessage(
      SequencedMessage message, TopicPath topic, Partition partition) {
    Headers headers = new LiteHeaders(message.message().attributes());
    TimestampType type;
    Timestamp timestamp;
    if (message.message().eventTime().isPresent()) {
      type = TimestampType.CREATE_TIME;
      timestamp = message.message().eventTime().get();
    } else {
      type = TimestampType.LOG_APPEND_TIME;
      timestamp = message.publishTime();
    }
    return new ConsumerRecord<>(
        topic.toString(),
        (int) partition.value(),
        message.offset().value(),
        Timestamps.toMillis(timestamp),
        type,
        0L,
        message.message().key().size(),
        message.message().data().size(),
        message.message().key().toByteArray(),
        message.message().data().toByteArray(),
        headers);
  }
}
