package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_CloudZone extends CloudZone {

  private final CloudRegion region;

  private final char zoneId;

  AutoValue_CloudZone(
      CloudRegion region,
      char zoneId) {
    if (region == null) {
      throw new NullPointerException("Null region");
    }
    this.region = region;
    this.zoneId = zoneId;
  }

  @Override
  public CloudRegion region() {
    return region;
  }

  @Override
  public char zoneId() {
    return zoneId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CloudZone) {
      CloudZone that = (CloudZone) o;
      return this.region.equals(that.region())
          && this.zoneId == that.zoneId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= region.hashCode();
    h$ *= 1000003;
    h$ ^= zoneId;
    return h$;
  }

  private static final long serialVersionUID = 867184651465L;

}
