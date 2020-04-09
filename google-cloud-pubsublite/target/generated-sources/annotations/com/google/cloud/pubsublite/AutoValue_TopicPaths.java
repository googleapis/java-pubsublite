package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_TopicPaths extends TopicPaths {

  private final ProjectNumber projectNumber;

  private final CloudZone zone;

  private final TopicName topicName;

  private AutoValue_TopicPaths(
      ProjectNumber projectNumber,
      CloudZone zone,
      TopicName topicName) {
    this.projectNumber = projectNumber;
    this.zone = zone;
    this.topicName = topicName;
  }

  @Override
  ProjectNumber projectNumber() {
    return projectNumber;
  }

  @Override
  CloudZone zone() {
    return zone;
  }

  @Override
  TopicName topicName() {
    return topicName;
  }

  @Override
  public String toString() {
    return "TopicPaths{"
         + "projectNumber=" + projectNumber + ", "
         + "zone=" + zone + ", "
         + "topicName=" + topicName
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof TopicPaths) {
      TopicPaths that = (TopicPaths) o;
      return this.projectNumber.equals(that.projectNumber())
          && this.zone.equals(that.zone())
          && this.topicName.equals(that.topicName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= projectNumber.hashCode();
    h$ *= 1000003;
    h$ ^= zone.hashCode();
    h$ *= 1000003;
    h$ ^= topicName.hashCode();
    return h$;
  }

  static final class Builder extends TopicPaths.Builder {
    private ProjectNumber projectNumber;
    private CloudZone zone;
    private TopicName topicName;
    Builder() {
    }
    @Override
    public TopicPaths.Builder setProjectNumber(ProjectNumber projectNumber) {
      if (projectNumber == null) {
        throw new NullPointerException("Null projectNumber");
      }
      this.projectNumber = projectNumber;
      return this;
    }
    @Override
    public TopicPaths.Builder setZone(CloudZone zone) {
      if (zone == null) {
        throw new NullPointerException("Null zone");
      }
      this.zone = zone;
      return this;
    }
    @Override
    public TopicPaths.Builder setTopicName(TopicName topicName) {
      if (topicName == null) {
        throw new NullPointerException("Null topicName");
      }
      this.topicName = topicName;
      return this;
    }
    @Override
    TopicPaths autoBuild() {
      String missing = "";
      if (this.projectNumber == null) {
        missing += " projectNumber";
      }
      if (this.zone == null) {
        missing += " zone";
      }
      if (this.topicName == null) {
        missing += " topicName";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_TopicPaths(
          this.projectNumber,
          this.zone,
          this.topicName);
    }
  }

}
