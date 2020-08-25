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

/**
 * A string wrapper representing a project.
 */
@AutoValue
public abstract class ProjectPath implements Serializable {
  public abstract ProjectIdOrNumber project();

  @Override
  public String toString() {
    return "projects/" + project();
  }

  public static ProjectPath of(ProjectIdOrNumber value) {
    return new AutoValue_ProjectPath(value);
  }

  /**
   * Parse a project path.  Should be structured like:
   *
   * <p>projects/&lt;project number&gt;
   */
  public static ProjectPath parse(String path) throws StatusException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 2);
    checkArgument(splits[0].equals("projects"));
    checkArgument(!splits[1].isEmpty());
    try {
      return ProjectPath.of(ProjectIdOrNumber.of(ProjectNumber.of(Long.parseLong(splits[1]))));
    } catch (NumberFormatException e) {
      // Pass, treat as a name.
    }
    return ProjectPath.of(ProjectIdOrNumber.of(ProjectId.of(splits[1])));
  }
}
