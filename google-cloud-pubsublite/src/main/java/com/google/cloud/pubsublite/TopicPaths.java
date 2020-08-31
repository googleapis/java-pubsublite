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

import com.google.auto.value.AutoValue;

/** Helpers for constructing valid TopicPaths. */
@AutoValue
public abstract class TopicPaths {
  abstract ProjectIdOrNumber project();

  abstract CloudZone location();

  abstract TopicName topicName();

  /** Create a new TopicPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_TopicPaths.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder extends ProjectBuilderHelper<Builder> {
    public abstract Builder setLocation(CloudZone zone);

    public abstract Builder setTopicName(TopicName name);

    abstract TopicPaths autoBuild();

    /** Build a new TopicPath. */
    public TopicPath build() {
      TopicPaths built = autoBuild();
      return TopicPath.of(
          LocationPath.of(ProjectPath.of(built.project()), built.location()), built.topicName());
    }
  }
}
