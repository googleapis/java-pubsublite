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

package com.google.cloud.pubsublite;

import com.google.api.gax.retrying.RetrySettings;
import org.threeten.bp.Duration;

/** Useful general constants for Pub/Sub Lite. */
public class Constants {
  public static final RetrySettings DEFAULT_RETRY_SETTINGS =
      RetrySettings.newBuilder()
          .setInitialRetryDelay(Duration.ofMillis(100))
          .setRetryDelayMultiplier(1.3)
          .setMaxRetryDelay(Duration.ofSeconds(60))
          .setJittered(true)
          .setTotalTimeout(Duration.ofMinutes(10))
          .build();

  public static final long MAX_PUBLISH_BATCH_COUNT = 1_000;
  public static final long MAX_PUBLISH_BATCH_BYTES = 1024 * 1024 * 7 / 2; // 3.5 MiB

  private Constants() {}
}
