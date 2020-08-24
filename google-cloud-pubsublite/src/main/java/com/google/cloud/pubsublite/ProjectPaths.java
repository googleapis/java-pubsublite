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

/** Helpers for constructing valid ProjectPaths. */
@AutoValue
public abstract class ProjectPaths {
  abstract ProjectNumber projectNumber();

  /** Create a new LocationPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_ProjectPaths.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    /** The project number. */
    public abstract Builder setProjectNumber(ProjectNumber number);

    abstract ProjectPaths autoBuild();

    /** Build a new ProjectPath. */
    public ProjectPath build() {
      ProjectPaths built = autoBuild();
      return ProjectPath.of(String.format("projects/%s", built.projectNumber().value()));
    }
  }

  private static void checkSplits(String[] splits) throws StatusException {
    checkArgument(splits.length == 2);
    checkArgument(splits[0].equals("projects"));
  }

  /** Check that the provided ProjectPath is valid. */
  public static void check(ProjectPath path) throws StatusException {
    ProjectNumber unusedProjectNumber = getProjectNumber(path);
  }

  /** Get the ProjectNumber from a ProjectPath. */
  public static ProjectNumber getProjectNumber(ProjectPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    try {
      return ProjectNumber.of(Long.parseLong(splits[1]));
    } catch (NumberFormatException e) {
      throw toCanonical(e);
    }
  }
}
