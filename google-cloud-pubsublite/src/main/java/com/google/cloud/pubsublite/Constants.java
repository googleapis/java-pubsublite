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

public class Constants {
  public static final long MAX_PUBLISH_BATCH_COUNT = 1_000;
  public static final long MAX_PUBLISH_MESSAGE_BYTES = 1_000_000;
  public static final long MAX_PUBLISH_BATCH_BYTES = 3_500_000;

  private Constants() {}
}
