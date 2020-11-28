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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.ApiService.State;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.PartitionCountWatcher;
import com.google.common.flogger.GoogleLogger;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.UnboundedPerElement;
import org.apache.beam.sdk.transforms.SerializableBiFunction;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;

@UnboundedPerElement
class PubsubLiteTopicSdf extends DoFn<TopicPath, Partition> {
  private static GoogleLogger logger = GoogleLogger.forEnclosingClass();
  SerializableBiFunction<TopicPath, Consumer<Long>, PartitionCountWatcher>
      partitionCountWatcherFactory;

  PubsubLiteTopicSdf(
      SerializableBiFunction<TopicPath, Consumer<Long>, PartitionCountWatcher>
          partitionCountWatcherFactory) {
    this.partitionCountWatcherFactory = partitionCountWatcherFactory;
  }

  @ProcessElement
  public ProcessContinuation processElement(
      @Element TopicPath topic,
      RestrictionTracker<OffsetRange, Long> tracker,
      OutputReceiver<Partition> receiver)
      throws Exception {
    AtomicLong previousCount = new AtomicLong(tracker.currentRestriction().getFrom());
    SettableApiFuture<Optional<CheckedApiException>> error = SettableApiFuture.create();
    PartitionCountWatcher watcher =
        partitionCountWatcherFactory.apply(
            topic,
            newCount -> {
              long previous = previousCount.getAndSet(newCount);
              logger.atWarning().log("Updated partitions to: " + newCount);
              if (newCount < previous) {
                error.set(
                    Optional.of(
                        new CheckedApiException(
                            "PubsubLiteTopicSdf does not support decreasing topic partition counts.",
                            Code.INTERNAL)));
                return;
              }
              for (long i = previous; i < newCount; ++i) {
                if (!tracker.tryClaim(i)) {
                  error.set(
                      Optional.of(
                          new CheckedApiException(
                              "Unable to claim new partition in SDF.", Code.INTERNAL)));
                  return;
                }
                receiver.output(Partition.of(i));
              }
              if (newCount != previous) error.set(Optional.empty());
            });
    watcher.addListener(
        new Listener() {
          @Override
          public void failed(State from, Throwable failure) {
            error.set(Optional.of(toCanonical(failure)));
          }
        },
        MoreExecutors.directExecutor());
    watcher.startAsync().awaitRunning();
    try {
      Optional<CheckedApiException> maybeError = error.get();
      if (!maybeError.isPresent()) return ProcessContinuation.resume();
      throw maybeError.get();
    } finally {
      watcher.stopAsync().awaitTerminated();
    }
  }

  @GetInitialRestriction
  public OffsetRange getInitialRestriction() {
    return new OffsetRange(0, Long.MAX_VALUE /* open interval */);
  }

  private static class UnboundedOffsetRangeTracker extends OffsetRangeTracker {
    public UnboundedOffsetRangeTracker(OffsetRange range) {
      super(range);
    }

    @Override
    public IsBounded isBounded() {
      return IsBounded.UNBOUNDED;
    }
  }

  @NewTracker
  public OffsetRangeTracker newTracker(@Restriction OffsetRange range) {
    return new UnboundedOffsetRangeTracker(range);
  }
}
