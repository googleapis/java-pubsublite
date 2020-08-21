package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import io.grpc.Channel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PublisherBuilderTest {
  @Test
  public void testBuilder() throws Exception {
    PublisherBuilder.builder()
        .setBatching(PublisherBuilder.DEFAULT_BATCHING_SETTINGS)
        .setTopic(
            TopicPaths.newBuilder()
                .setZone(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                .setProjectNumber(ProjectNumber.of(3))
                .setTopicName(TopicName.of("abc"))
                .build())
        .setPartition(Partition.of(85))
        .setStub(PublisherServiceGrpc.newStub(mock(Channel.class)))
        .build();
  }
}
