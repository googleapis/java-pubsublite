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

package com.google.cloud.pubsublite.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class ChannelCacheTest {
  @Mock Function<String, ManagedChannel> channelFactory;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void reusesChannels() {
    when(channelFactory.apply(any()))
        .thenAnswer(
            (target) -> {
              ManagedChannel channel = mock(ManagedChannel.class);
              when(channel.shutdownNow()).thenReturn(channel);
              return channel;
            });
    ChannelCache cache = new ChannelCache(channelFactory);

    // Only 10 Channels should be created.
    Set<Channel> channels = new HashSet<>();
    for (int i = 0; i < 20; i++) {
      channels.add(cache.get("abc"));
    }

    assertThat(channels.size()).isEqualTo(10);
    cache.onShutdown();
  }
}
