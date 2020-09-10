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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.flogger.GoogleLogger;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.LinkedList;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** A ChannelCache creates and stores default channels for use with api methods. */
public class ChannelCache {
  private static final GoogleLogger log = GoogleLogger.forEnclosingClass();

  private final Function<String, ManagedChannel> channelFactory;
  private final ConcurrentHashMap<String, Deque<ManagedChannel>> channels =
      new ConcurrentHashMap<>();

  private static final int NUMBER_OF_CHANNELS_PER_TARGET = 10;
  private static final String NUMBER_OF_CHANNELS_PER_TARGET_VM_OVERRIDE =
          "google.cloud.pubsublite.channelCacheSize";

  public ChannelCache() {
    this(ChannelCache::newChannel);
    Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
  }

  @VisibleForTesting
  ChannelCache(Function<String, ManagedChannel> channelFactory) {
    this.channelFactory = channelFactory;
  }

  @VisibleForTesting
  void onShutdown() {
    channels.forEachValue(
        channels.size(),
        channels -> {
          try {
            for (ManagedChannel channel : channels) {
              channel.shutdownNow().awaitTermination(60, TimeUnit.SECONDS);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
  }

  public Channel get(String target) {
    ManagedChannel channel = null;
    synchronized (this) {
      Deque<ManagedChannel> channelQueue = channels.computeIfAbsent(target, this::newChannels);
      channel = channelQueue.removeFirst();
      channelQueue.addLast(channel);
    }
    return channel;
  }

  private Deque<ManagedChannel> newChannels(String target) {
    int numberOfChannels = NUMBER_OF_CHANNELS_PER_TARGET;
    String numberOfChannelsOverride = System.getProperty(NUMBER_OF_CHANNELS_PER_TARGET_VM_OVERRIDE);
    if (numberOfChannelsOverride != null && !numberOfChannelsOverride.isEmpty()) {
      try {
        numberOfChannels = Integer.parseInt((numberOfChannelsOverride));
      } catch (NumberFormatException e) {
        log.atSevere().log("Unable to parse override for number of channels per target: %s", numberOfChannelsOverride);
      }
    }

    Deque<ManagedChannel> channels = new LinkedList<>();
    for (int i = 0; i < numberOfChannels; i++) {
      channels.add(channelFactory.apply(target));
    }
    return channels;
  }

  private static ManagedChannel newChannel(String target) {
    return ManagedChannelBuilder.forTarget(target).build();
  }
}
