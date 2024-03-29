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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import java.io.Serializable;
import java.util.Arrays;

/** A string wrapper representing a project and location. */
@AutoValue
public abstract class LocationPath implements Serializable {
  public abstract ProjectIdOrNumber project();

  public abstract CloudRegionOrZone location();

  public ProjectPath projectPath() {
    return ProjectPath.newBuilder().setProject(project()).build();
  }

  @Override
  public String toString() {
    return projectPath() + "/locations/" + location();
  }

  /** Create a new LocationPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_LocationPath.Builder();
  }

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder extends ProjectLocationBuilderHelper<Builder> {
    public abstract LocationPath build();
  }

  /**
   * Parse a location path. Should be structured like:
   *
   * <p>projects/&lt;project number&gt;/locations/&lt;cloud region or zone&gt;
   */
  public static LocationPath parse(String path) throws ApiException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 4);
    checkArgument(splits[2].equals("locations"));
    ProjectPath project = ProjectPath.parse(String.join("/", Arrays.copyOf(splits, 2)));
    return LocationPath.newBuilder()
        .setProject(project.project())
        .setLocation(CloudRegionOrZone.parse(splits[3]))
        .build();
  }
}
