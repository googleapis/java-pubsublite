package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_CloudRegion extends CloudRegion {

  private final String value;

  AutoValue_CloudRegion(
      String value) {
    if (value == null) {
      throw new NullPointerException("Null value");
    }
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return "CloudRegion{"
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof CloudRegion) {
      CloudRegion that = (CloudRegion) o;
      return this.value.equals(that.value());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= value.hashCode();
    return h$;
  }

  private static final long serialVersionUID = 6814654654L;

}
