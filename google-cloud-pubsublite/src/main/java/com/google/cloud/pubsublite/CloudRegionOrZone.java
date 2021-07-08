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
import com.google.auto.value.AutoOneOf;
import java.io.Serializable;

@AutoOneOf(CloudRegionOrZone.Kind.class)
public abstract class CloudRegionOrZone implements Serializable {
  public enum Kind {
    REGION,
    ZONE
  }

  public abstract Kind getKind();

  public abstract CloudRegion region();

  public abstract CloudZone zone();

  /** Extract the region from this regardless of which case is set. */
  public CloudRegion extractRegion() {
    switch (getKind()) {
      case REGION:
        return region();
      case ZONE:
        return zone().region();
      default:
        throw new RuntimeException("Unknown case for CloudRegionOrZone.");
    }
  }

  public static CloudRegionOrZone of(CloudRegion region) {
    return AutoOneOf_CloudRegionOrZone.region(region);
  }

  public static CloudRegionOrZone of(CloudZone zone) {
    return AutoOneOf_CloudRegionOrZone.zone(zone);
  }

  public static CloudRegionOrZone parse(String value) throws ApiException {
    try {
      return of(CloudZone.parse(value));
    } catch (ApiException e) {
      // pass
    }
    return of(CloudRegion.of(value));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    switch (getKind()) {
      case REGION:
        return region().toString();
      case ZONE:
        return zone().toString();
      default:
        throw new RuntimeException("Unknown case for CloudRegionOrZone.");
    }
  }
}
