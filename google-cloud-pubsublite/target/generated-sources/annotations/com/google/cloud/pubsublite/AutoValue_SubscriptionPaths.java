package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SubscriptionPaths extends SubscriptionPaths {

  private final ProjectNumber projectNumber;

  private final CloudZone zone;

  private final SubscriptionName subscriptionName;

  private AutoValue_SubscriptionPaths(
      ProjectNumber projectNumber,
      CloudZone zone,
      SubscriptionName subscriptionName) {
    this.projectNumber = projectNumber;
    this.zone = zone;
    this.subscriptionName = subscriptionName;
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
  SubscriptionName subscriptionName() {
    return subscriptionName;
  }

  @Override
  public String toString() {
    return "SubscriptionPaths{"
         + "projectNumber=" + projectNumber + ", "
         + "zone=" + zone + ", "
         + "subscriptionName=" + subscriptionName
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SubscriptionPaths) {
      SubscriptionPaths that = (SubscriptionPaths) o;
      return this.projectNumber.equals(that.projectNumber())
          && this.zone.equals(that.zone())
          && this.subscriptionName.equals(that.subscriptionName());
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
    h$ ^= subscriptionName.hashCode();
    return h$;
  }

  static final class Builder extends SubscriptionPaths.Builder {
    private ProjectNumber projectNumber;
    private CloudZone zone;
    private SubscriptionName subscriptionName;
    Builder() {
    }
    @Override
    public SubscriptionPaths.Builder setProjectNumber(ProjectNumber projectNumber) {
      if (projectNumber == null) {
        throw new NullPointerException("Null projectNumber");
      }
      this.projectNumber = projectNumber;
      return this;
    }
    @Override
    public SubscriptionPaths.Builder setZone(CloudZone zone) {
      if (zone == null) {
        throw new NullPointerException("Null zone");
      }
      this.zone = zone;
      return this;
    }
    @Override
    public SubscriptionPaths.Builder setSubscriptionName(SubscriptionName subscriptionName) {
      if (subscriptionName == null) {
        throw new NullPointerException("Null subscriptionName");
      }
      this.subscriptionName = subscriptionName;
      return this;
    }
    @Override
    SubscriptionPaths autoBuild() {
      String missing = "";
      if (this.projectNumber == null) {
        missing += " projectNumber";
      }
      if (this.zone == null) {
        missing += " zone";
      }
      if (this.subscriptionName == null) {
        missing += " subscriptionName";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_SubscriptionPaths(
          this.projectNumber,
          this.zone,
          this.subscriptionName);
    }
  }

}
