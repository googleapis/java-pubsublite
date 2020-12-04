/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.proto;

import com.google.api.pathtemplate.PathTemplate;
import com.google.api.resourcenames.ResourceName;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
@Generated("by gapic-generator-java")
public class TopicName implements ResourceName {
  private static final PathTemplate PROJECT_LOCATION_TOPIC =
      PathTemplate.createWithoutUrlEncoding(
          "projects/{project}/locations/{location}/topics/{topic}");
  private volatile Map<String, String> fieldValuesMap;
  private final String project;
  private final String location;
  private final String topic;

  @Deprecated
  protected TopicName() {
    project = null;
    location = null;
    topic = null;
  }

  private TopicName(Builder builder) {
    project = Preconditions.checkNotNull(builder.getProject());
    location = Preconditions.checkNotNull(builder.getLocation());
    topic = Preconditions.checkNotNull(builder.getTopic());
  }

  public String getProject() {
    return project;
  }

  public String getLocation() {
    return location;
  }

  public String getTopic() {
    return topic;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static TopicName of(String project, String location, String topic) {
    return newBuilder().setProject(project).setLocation(location).setTopic(topic).build();
  }

  public static String format(String project, String location, String topic) {
    return newBuilder()
        .setProject(project)
        .setLocation(location)
        .setTopic(topic)
        .build()
        .toString();
  }

  public static TopicName parse(String formattedString) {
    if (formattedString.isEmpty()) {
      return null;
    }
    Map<String, String> matchMap =
        PROJECT_LOCATION_TOPIC.validatedMatch(
            formattedString, "TopicName.parse: formattedString not in valid format");
    return of(matchMap.get("project"), matchMap.get("location"), matchMap.get("topic"));
  }

  public static List<TopicName> parseList(List<String> formattedStrings) {
    List<TopicName> list = new ArrayList<>(formattedStrings.size());
    for (String formattedString : formattedStrings) {
      list.add(parse(formattedString));
    }
    return list;
  }

  public static List<String> toStringList(List<TopicName> values) {
    List<String> list = new ArrayList<>(values.size());
    for (TopicName value : values) {
      if (Objects.isNull(value)) {
        list.add("");
      } else {
        list.add(value.toString());
      }
    }
    return list;
  }

  public static boolean isParsableFrom(String formattedString) {
    return PROJECT_LOCATION_TOPIC.matches(formattedString);
  }

  @Override
  public Map<String, String> getFieldValuesMap() {
    if (Objects.isNull(fieldValuesMap)) {
      synchronized (this) {
        if (Objects.isNull(fieldValuesMap)) {
          ImmutableMap.Builder<String, String> fieldMapBuilder = ImmutableMap.builder();
          if (!Objects.isNull(project)) {
            fieldMapBuilder.put("project", project);
          }
          if (!Objects.isNull(location)) {
            fieldMapBuilder.put("location", location);
          }
          if (!Objects.isNull(topic)) {
            fieldMapBuilder.put("topic", topic);
          }
          fieldValuesMap = fieldMapBuilder.build();
        }
      }
    }
    return fieldValuesMap;
  }

  public String getFieldValue(String fieldName) {
    return getFieldValuesMap().get(fieldName);
  }

  @Override
  public String toString() {
    return PROJECT_LOCATION_TOPIC.instantiate(
        "project", project, "location", location, "topic", topic);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o != null || getClass() == o.getClass()) {
      TopicName that = ((TopicName) o);
      return Objects.equals(this.project, that.project)
          && Objects.equals(this.location, that.location)
          && Objects.equals(this.topic, that.topic);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= Objects.hashCode(project);
    h *= 1000003;
    h ^= Objects.hashCode(location);
    h *= 1000003;
    h ^= Objects.hashCode(topic);
    return h;
  }

  /** Builder for projects/{project}/locations/{location}/topics/{topic}. */
  public static class Builder {
    private String project;
    private String location;
    private String topic;

    protected Builder() {}

    public String getProject() {
      return project;
    }

    public String getLocation() {
      return location;
    }

    public String getTopic() {
      return topic;
    }

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setLocation(String location) {
      this.location = location;
      return this;
    }

    public Builder setTopic(String topic) {
      this.topic = topic;
      return this;
    }

    private Builder(TopicName topicName) {
      project = topicName.project;
      location = topicName.location;
      topic = topicName.topic;
    }

    public TopicName build() {
      return new TopicName(this);
    }
  }
}
