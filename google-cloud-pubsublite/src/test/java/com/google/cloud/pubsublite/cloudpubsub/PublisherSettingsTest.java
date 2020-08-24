package com.google.cloud.pubsublite.cloudpubsub;

import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.internal.FakeApiService;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.wire.SinglePartitionPublisherBuilder;
import com.google.cloud.pubsublite.proto.PublisherServiceGrpc;
import io.grpc.Channel;
import io.grpc.StatusException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public class PublisherSettingsTest {
  TopicPath getPath() throws StatusException {
    return TopicPaths.newBuilder()
        .setProjectNumber(ProjectNumber.of(56))
        .setZone(CloudZone.parse("us-central1-a"))
        .setTopicName(TopicName.of("xyz"))
        .build();
  }

  abstract static class FakePublisher extends FakeApiService
      implements Publisher<PublishMetadata> {}

  @Spy private FakePublisher underlying;

  @Test
  public void testSettings() throws StatusException {
    initMocks(this);
    Channel mockChannel = mock(Channel.class);
    SinglePartitionPublisherBuilder.Builder mockBuilder =
        mock(SinglePartitionPublisherBuilder.Builder.class, RETURNS_SELF);
    when(mockBuilder.build()).thenReturn(underlying);
    PublisherSettings.newBuilder()
        .setTopicPath(getPath())
        .setStub(PublisherServiceGrpc.newStub(mockChannel))
        .setUnderlyingBuilder(mockBuilder)
        .setNumPartitions(77)
        .build()
        .instantiate();
  }
}
