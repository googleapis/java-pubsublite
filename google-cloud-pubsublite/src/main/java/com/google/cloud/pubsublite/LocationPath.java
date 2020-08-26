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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;
import java.io.Serializable;
import java.util.Arrays;

/** A string wrapper representing a project and location. */
@AutoValue
public abstract class LocationPath implements Serializable {
  public abstract ProjectPath project();

  public abstract CloudZone location();

  @Override
  public String toString() {
    return project() + "/locations/" + location();
  }

  public static LocationPath of(ProjectPath project, CloudZone zone) {
    return new AutoValue_LocationPath(project, zone);
  }

  /**
   * Parse a location path. Should be structured like:
   *
   * <p>projects/&lt;project number&gt;/locations/&lt;cloud zone&gt;
   */
  public static LocationPath parse(String path) throws StatusException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 4);
    checkArgument(splits[2].equals("locations"));
    ProjectPath project = ProjectPath.parse(String.join("/", Arrays.copyOf(splits, 2)));
    return LocationPath.of(project, CloudZone.parse(splits[3]));
  }
}
