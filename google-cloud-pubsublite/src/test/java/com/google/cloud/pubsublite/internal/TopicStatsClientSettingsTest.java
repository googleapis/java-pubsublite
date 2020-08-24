package com.google.cloud.pubsublite.internal;

import static org.mockito.Mockito.mock;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.proto.TopicStatsServiceGrpc;
import io.grpc.Channel;
import io.grpc.StatusException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TopicStatsClientSettingsTest {
  @Test
  public void testSettings() throws StatusException {
    TopicStatsClientSettings.newBuilder()
        .setRegion(CloudRegion.of("us-central1"))
        .setStub(TopicStatsServiceGrpc.newBlockingStub(mock(Channel.class)))
        .build()
        .instantiate();
  }
}
