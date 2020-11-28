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

package com.google.cloud.pubsublite.beam;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.PullSubscriber;
import com.google.cloud.pubsublite.proto.SeekRequest;
import java.util.List;
import java.util.Optional;

public class MemoryLimitingPullSubscriber implements PullSubscriber<SequencedMessage> {
  private final MemoryLease lease;
  private final PullSubscriber<SequencedMessage> subscriber;

  MemoryLimitingPullSubscriber(
      PullSubscriberFactory factory,
      MemoryLimiter limiter,
      FlowControlSettings baseSettings,
      SeekRequest initialSeek)
      throws ApiException {
    lease = limiter.acquireMemory(baseSettings.bytesOutstanding());
    try {
      subscriber = factory.newPullSubscriber(initialSeek, getSettings(baseSettings));
    } catch (CheckedApiException e) {
      throw e.underlying;
    }
  }

  private FlowControlSettings getSettings(FlowControlSettings baseSettings) {
    // Must allow at least 1 MiB to allow all potential messages.
    long bytesAllowed =
        Math.max(1 << 20, Math.min(this.lease.byteCount(), baseSettings.bytesOutstanding()));
    return FlowControlSettings.builder()
        .setMessagesOutstanding(baseSettings.messagesOutstanding())
        .setBytesOutstanding(bytesAllowed)
        .build();
  }

  @Override
  public synchronized List<SequencedMessage> pull() throws CheckedApiException {
    return subscriber.pull();
  }

  @Override
  public synchronized Optional<Offset> nextOffset() {
    return subscriber.nextOffset();
  }

  @Override
  public void close() throws Exception {
    try (MemoryLease l = lease;
        PullSubscriber<SequencedMessage> s = subscriber) {}
  }
}
