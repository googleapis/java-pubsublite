package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ProjectPaths extends ProjectPaths {

  private final ProjectNumber projectNumber;

  private AutoValue_ProjectPaths(
      ProjectNumber projectNumber) {
    this.projectNumber = projectNumber;
  }

  @Override
  ProjectNumber projectNumber() {
    return projectNumber;
  }

  @Override
  public String toString() {
    return "ProjectPaths{"
         + "projectNumber=" + projectNumber
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProjectPaths) {
      ProjectPaths that = (ProjectPaths) o;
      return this.projectNumber.equals(that.projectNumber());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= projectNumber.hashCode();
    return h$;
  }

  static final class Builder extends ProjectPaths.Builder {
    private ProjectNumber projectNumber;
    Builder() {
    }
    @Override
    public ProjectPaths.Builder setProjectNumber(ProjectNumber projectNumber) {
      if (projectNumber == null) {
        throw new NullPointerException("Null projectNumber");
      }
      this.projectNumber = projectNumber;
      return this;
    }
    @Override
    ProjectPaths autoBuild() {
      String missing = "";
      if (this.projectNumber == null) {
        missing += " projectNumber";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_ProjectPaths(
          this.projectNumber);
    }
  }

}
