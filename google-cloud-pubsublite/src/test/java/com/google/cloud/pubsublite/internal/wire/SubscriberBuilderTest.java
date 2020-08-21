package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.common.collect.ImmutableList;
import io.grpc.Channel;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class SubscriberBuilderTest {
  @Mock
  public Consumer<ImmutableList<SequencedMessage>> mockConsumer;

  @Test
  public void testBuilder() throws Exception {
    initMocks(this);
    SubscriberBuilder.newBuilder()
        .setSubscriptionPath(
            SubscriptionPaths.newBuilder()
                .setZone(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                .setProjectNumber(ProjectNumber.of(3))
                .setSubscriptionName(SubscriptionName.of("abc"))
                .build())
        .setMessageConsumer(mockConsumer)
        .setPartition(Partition.of(3))
        .setSubscriberServiceStub(SubscriberServiceGrpc.newStub(mock(Channel.class)))
        .build();
  }
}
