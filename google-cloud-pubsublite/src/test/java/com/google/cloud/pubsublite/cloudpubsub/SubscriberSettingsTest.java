package com.google.cloud.pubsublite.cloudpubsub;

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.CursorServiceGrpc;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import com.google.cloud.pubsublite.proto.SubscriberServiceGrpc;
import com.google.common.collect.ImmutableList;
import io.grpc.Channel;
import io.grpc.StatusException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SubscriberSettingsTest {
  SubscriptionPath getPath() throws StatusException {
    return SubscriptionPaths.newBuilder()
        .setProjectNumber(ProjectNumber.of(56))
        .setZone(CloudZone.parse("us-central1-a"))
        .setSubscriptionName(SubscriptionName.of("xyz"))
        .build();
  }

  @Test
  public void testSettingsWithPartitons() throws StatusException {
    Channel mockChannel = mock(Channel.class);
    SubscriberSettings.newBuilder()
        .setReceiver(mock(MessageReceiver.class))
        .setSubscriptionPath(getPath())
        .setPerPartitionFlowControlSettings(
            FlowControlSettings.builder().setBytesOutstanding(1).setMessagesOutstanding(1).build())
        .setCursorServiceStub(CursorServiceGrpc.newStub(mockChannel))
        .setSubscriberServiceStub(SubscriberServiceGrpc.newStub(mockChannel))
        .setPartitions(ImmutableList.of(Partition.of(3), Partition.of(1)))
        .build()
        .instantiate();
  }

  @Test
  public void testSettingsWithoutPartitons() throws StatusException {
    Channel mockChannel = mock(Channel.class);
    SubscriberSettings.newBuilder()
        .setReceiver(mock(MessageReceiver.class))
        .setSubscriptionPath(getPath())
        .setPerPartitionFlowControlSettings(
            FlowControlSettings.builder().setBytesOutstanding(1).setMessagesOutstanding(1).build())
        .setAssignmentServiceStub(PartitionAssignmentServiceGrpc.newStub(mockChannel))
        .setCursorServiceStub(CursorServiceGrpc.newStub(mockChannel))
        .setSubscriberServiceStub(SubscriberServiceGrpc.newStub(mockChannel))
        .build()
        .instantiate();
  }
}
