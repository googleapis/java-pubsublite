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

public class ProjectLookupUtils {
  private static final ResourceManager resourceManager =
      ResourceManagerOptions.getDefaultInstance().getService();

  private static ProjectNumber getProjectNumber(ProjectId id) throws StatusException {
    try {
      Project project = resourceManager.get(id.toString());
      return ProjectNumber.of(project.getProjectNumber());
    } catch (Throwable t) {
      throw ExtractStatus.toCanonical(t);
    }
  }

  public static ProjectPath toCannonical(ProjectPath project) throws StatusException {
    if (project.project().getKind() == Kind.NUMBER) return project;
    return ProjectPath.of(ProjectIdOrNumber.of(getProjectNumber(project.project().name())));
  }

  public static LocationPath toCannonical(LocationPath location) throws StatusException {
    ProjectPath canonicalProject = toCannonical(location.project());
    return LocationPath.of(canonicalProject, location.location());
  }

  public static SubscriptionPath toCannonical(SubscriptionPath subscription)
      throws StatusException {
    LocationPath canonicalLocation = toCannonical(subscription.location());
    return SubscriptionPath.of(canonicalLocation, subscription.name());
  }

  public static TopicPath toCannonical(TopicPath topic) throws StatusException {
    LocationPath canonicalLocation = toCannonical(topic.location());
    return TopicPath.of(canonicalLocation, topic.name());
  }
}
