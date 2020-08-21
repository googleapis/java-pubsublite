package com.google.cloud.pubsublite.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class ChannelCacheTest {
  @Mock ManagedChannel mockChannel;
  @Mock Function<String, ManagedChannel> channelFactory;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void reusesChannels() {
    when(channelFactory.apply(any())).thenReturn(mockChannel);
    ChannelCache cache = new ChannelCache(channelFactory);
    Channel chan1 = cache.get("abc");
    Channel chan2 = cache.get("abc");
    assertThat(chan1).isEqualTo(chan2);
    verify(channelFactory, times(1)).apply("abc");
    when(mockChannel.shutdownNow()).thenReturn(mockChannel);
    cache.onShutdown();
    verify(mockChannel, times(1)).shutdownNow();
  }
}
