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

import com.google.auto.value.AutoValue;
import java.io.Serializable;

/** A wrapped string representing a Google Cloud region.*/
@AutoValue
public abstract class CloudRegion implements Serializable {
  private static final long serialVersionUID = 6814654654L;

  /** Construct a CloudRegion from a string. */
  public static CloudRegion of(String value) {
    return new AutoValue_CloudRegion(value);
  }

  /** The string representing this region. */
  public abstract String value();
}
