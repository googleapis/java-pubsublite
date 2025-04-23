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

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import java.io.Serializable;

/** A representation of a Google Cloud zone. */
@AutoValue
public abstract class CloudZone implements Serializable {
  private static final long serialVersionUID = 867184651465L;

  /** A google cloud zone. */
  public static CloudZone of(CloudRegion region, char zoneId) {
    return new AutoValue_CloudZone(region, zoneId);
  }

  /**
   * Construct a CloudZone from a valid zone string. `zone` must be formatted as:
   * &lt;location&gt;-&lt;direction&gt;&lt;number&gt;-&lt;letter&gt;
   */
  public static CloudZone parse(String zone) throws ApiException {
    String[] splits = zone.split("-", -1);
    if (splits.length != 3) {
      throw new CheckedApiException("Invalid zone name: " + zone, Code.INVALID_ARGUMENT).underlying;
    }
    if (splits[2].length() != 1) {
      throw new CheckedApiException("Invalid zone name: " + zone, Code.INVALID_ARGUMENT).underlying;
    }
    CloudRegion region = CloudRegion.of(splits[0] + "-" + splits[1]);
    return of(region, splits[2].charAt(0));
  }

  /** The region this zone is in. */
  public abstract CloudRegion region();

  /** The character identifier for this zone in this region. */
  public abstract char zoneId();

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return String.format("%s-%c", region().value(), zoneId());
  }
}
