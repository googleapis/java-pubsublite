package com.google.cloud.pubsublite.kafka;

import static com.google.cloud.pubsublite.kafka.KafkaExceptionUtils.toKafka;

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.PartitionInfo;

final class SharedBehavior {
  private SharedBehavior() {}

  static PartitionInfo toPartitionInfo(TopicPath topic, Partition partition) {
    return new PartitionInfo(
        topic.toString(),
        (int) partition.value(),
        PubsubLiteNode.NODE,
        PubsubLiteNode.NODES,
        PubsubLiteNode.NODES);
  }

  static List<PartitionInfo> partitionsFor(
      AdminClient adminClient, TopicPath topic, Duration timeout) {
    try {
      long count =
          adminClient.getTopicPartitionCount(topic).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      ImmutableList.Builder<PartitionInfo> result = ImmutableList.builder();
      for (int i = 0; i < count; ++i) {
        result.add(toPartitionInfo(topic, Partition.of(i)));
      }
      return result.build();
    } catch (Throwable t) {
      throw toKafka(t);
    }
  }
}
