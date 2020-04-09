// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite;

import com.google.auto.value.AutoValue;
import io.grpc.Status;
import io.grpc.StatusException;
import java.io.Serializable;

@AutoValue
public abstract class CloudZone implements Serializable {
  private static final long serialVersionUID = 867184651465L;
  /** A google cloud zone. */
  public static CloudZone create(CloudRegion region, char zoneId) {
    return new AutoValue_CloudZone(region, zoneId);
  }

  // Construct a CloudZone from a valid zone string. `zone` must be formatted as:
  // <location>-<direction><number>-<letter>
  public static CloudZone parse(String zone) throws StatusException {
    String[] splits = zone.split("-", -1);
    if (splits.length != 3) {
      throw Status.INVALID_ARGUMENT.withDescription("Invalid zone name: " + zone).asException();
    }
    if (splits[2].length() != 1) {
      throw Status.INVALID_ARGUMENT.withDescription("Invalid zone name: " + zone).asException();
    }
    CloudRegion region = CloudRegion.create(splits[0] + "-" + splits[1]);
    return create(region, splits[2].charAt(0));
  }

  public abstract CloudRegion region();

  public abstract char zoneId();

  @Override
  public final String toString() {
    return String.format("%s-%c", region().value(), zoneId());
  }
}
