package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.SubscriptionPaths;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import io.grpc.Channel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AssignerBuilderTest {
  @Test
  public void testBuilder() throws Exception {
    AssignerBuilder.newBuilder()
        .setSubscriptionPath(
            SubscriptionPaths.newBuilder()
                .setZone(CloudZone.of(CloudRegion.of("us-central1"), 'a'))
                .setProjectNumber(ProjectNumber.of(3))
                .setSubscriptionName(SubscriptionName.of("abc"))
                .build())
        .setReceiver(mock(PartitionAssignmentReceiver.class))
        .setAssignmentStub(PartitionAssignmentServiceGrpc.newStub(mock(Channel.class)))
        .build();
  }
}
