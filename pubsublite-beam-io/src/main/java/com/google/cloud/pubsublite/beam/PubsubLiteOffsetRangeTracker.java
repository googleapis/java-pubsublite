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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.sdk.transforms.splittabledofn.SplitResult;

class PubsubLiteOffsetRangeTracker extends OffsetRangeTracker {
  private final SimpleTopicBacklogReader backlogReader;

  public PubsubLiteOffsetRangeTracker(
      OffsetRange range, SimpleTopicBacklogReader backlogReader, Partition partition)
      throws ApiException {
    super(range);
    checkArgument(range.getTo() == Long.MAX_VALUE);
    this.backlogReader = backlogReader;
  }

  @Override
  protected void finalize() throws Throwable {
    backlogReader.close();
  }

  @Override
  public IsBounded isBounded() {
    return IsBounded.UNBOUNDED;
  }

  @Override
  public SplitResult<OffsetRange> trySplit(double fractionOfRemainder) {
    return null;
  }

  @Override
  public void checkDone() {
    if (lastAttemptedOffset == null) {
      return;
    }
    super.checkDone();
  }

  @Override
  public Progress getProgress() {
    ComputeMessageStatsResponse stats =
        this.backlogReader.computeStats(Offset.of(currentRestriction().getTo()));
    return new Progress() {
      @Override
      public double getWorkCompleted() {
        // We do not track completed work. It may be impossible to know since files may have been
        // garbage collected.
        return 0;
      }

      @Override
      public double getWorkRemaining() {
        return stats.getMessageBytes();
      }
    };
  }
}
