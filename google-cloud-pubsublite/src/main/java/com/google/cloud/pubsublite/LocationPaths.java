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

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;

@AutoValue
public abstract class LocationPaths {
  abstract ProjectNumber projectNumber();

  abstract CloudZone zone();

  public static Builder newBuilder() {
    return new AutoValue_LocationPaths.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setProjectNumber(ProjectNumber number);

    public abstract Builder setZone(CloudZone zone);

    abstract LocationPaths autoBuild();

    public LocationPath build() throws StatusException {
      LocationPaths built = autoBuild();
      return LocationPath.of(
          String.format("projects/%s/locations/%s", built.projectNumber().value(), built.zone()));
    }
  }

  private static void checkSplits(String[] splits) throws StatusException {
    checkArgument(splits.length == 4);
    checkArgument(splits[0].equals("projects"));
    checkArgument(splits[2].equals("locations"));
  }

  public static void check(LocationPath path) throws StatusException {
    ProjectNumber unusedProjectNumber = getProjectNumber(path);
    CloudZone unusedZone = getZone(path);
  }

  public static ProjectNumber getProjectNumber(LocationPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    try {
      return ProjectNumber.of(Long.parseLong(splits[1]));
    } catch (NumberFormatException e) {
      throw toCanonical(e);
    }
  }

  public static CloudZone getZone(LocationPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    return CloudZone.parse(splits[3]);
  }
}
