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

import com.google.cloud.pubsublite.ProjectIdOrNumber.Kind;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.resourcemanager.Project;
import com.google.cloud.resourcemanager.ResourceManager;
import com.google.cloud.resourcemanager.ResourceManagerOptions;
import io.grpc.StatusException;

public final class ProjectLookupUtils {
  private ProjectLookupUtils() {}

  private static ResourceManager resourceManager = null;

  private static synchronized ResourceManager getResourceManager() {
    if (resourceManager == null) {
      resourceManager = ResourceManagerOptions.getDefaultInstance().getService();
    }
    return resourceManager;
  }

  private static ProjectNumber getProjectNumber(ProjectId id) throws StatusException {
    try {
      Project project = getResourceManager().get(id.toString());
      return ProjectNumber.of(project.getProjectNumber());
    } catch (Throwable t) {
      throw ExtractStatus.toCanonical(t);
    }
  }

  static ProjectNumber toCanonical(ProjectIdOrNumber project) throws StatusException {
    if (project.getKind() == Kind.NUMBER) return project.number();
    return getProjectNumber(project.name());
  }

  public static ProjectPath toCanonical(ProjectPath path) throws StatusException {
    return path.toBuilder().setProject(toCanonical(path.project())).build();
  }

  public static LocationPath toCanonical(LocationPath path) throws StatusException {
    return path.toBuilder().setProject(toCanonical(path.project())).build();
  }

  public static SubscriptionPath toCanonical(SubscriptionPath path) throws StatusException {
    return path.toBuilder().setProject(toCanonical(path.project())).build();
  }

  public static TopicPath toCanonical(TopicPath path) throws StatusException {
    return path.toBuilder().setProject(toCanonical(path.project())).build();
  }
}
