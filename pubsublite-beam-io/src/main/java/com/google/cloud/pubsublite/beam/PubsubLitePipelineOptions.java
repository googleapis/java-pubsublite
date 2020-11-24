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

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface PubsubLitePipelineOptions extends PipelineOptions {
  @Description("Whether per-worker memory limiting is enabled.")
  @Default.Boolean(true)
  Boolean getPubsubLiteSubscriberWorkerMemoryLimiterEnabled();

  void setPubsubLiteSubscriberWorkerMemoryLimiterEnabled(Boolean val);

  @Description("A soft memory limit on messages outstanding to all subscribers on a given worker.")
  @Default.Long(1L << 30) // Default limit is 1GiB.
  Long getPubsubLiteSubscribeWorkerMemoryLimit();

  void setPubsubLiteSubscribeWorkerMemoryLimit(Long val);
}
