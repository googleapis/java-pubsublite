package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_LocationPaths extends LocationPaths {

  private final ProjectNumber projectNumber;

  private final CloudZone zone;

  private AutoValue_LocationPaths(
      ProjectNumber projectNumber,
      CloudZone zone) {
    this.projectNumber = projectNumber;
    this.zone = zone;
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
  public String toString() {
    return "LocationPaths{"
         + "projectNumber=" + projectNumber + ", "
         + "zone=" + zone
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LocationPaths) {
      LocationPaths that = (LocationPaths) o;
      return this.projectNumber.equals(that.projectNumber())
          && this.zone.equals(that.zone());
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
    return h$;
  }

  static final class Builder extends LocationPaths.Builder {
    private ProjectNumber projectNumber;
    private CloudZone zone;
    Builder() {
    }
    @Override
    public LocationPaths.Builder setProjectNumber(ProjectNumber projectNumber) {
      if (projectNumber == null) {
        throw new NullPointerException("Null projectNumber");
      }
      this.projectNumber = projectNumber;
      return this;
    }
    @Override
    public LocationPaths.Builder setZone(CloudZone zone) {
      if (zone == null) {
        throw new NullPointerException("Null zone");
      }
      this.zone = zone;
      return this;
    }
    @Override
    LocationPaths autoBuild() {
      String missing = "";
      if (this.projectNumber == null) {
        missing += " projectNumber";
      }
      if (this.zone == null) {
        missing += " zone";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_LocationPaths(
          this.projectNumber,
          this.zone);
    }
  }

}
