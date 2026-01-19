/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.v1.stub;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the TopicStatsService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class TopicStatsServiceStub implements BackgroundResource {

  public UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable() {
    throw new UnsupportedOperationException("Not implemented: computeMessageStatsCallable()");
  }

  public UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorCallable() {
    throw new UnsupportedOperationException("Not implemented: computeHeadCursorCallable()");
  }

  public UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorCallable() {
    throw new UnsupportedOperationException("Not implemented: computeTimeCursorCallable()");
  }

  @Override
  public abstract void close();
}
