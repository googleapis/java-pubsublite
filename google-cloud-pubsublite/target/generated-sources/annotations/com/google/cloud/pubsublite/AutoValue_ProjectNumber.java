package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ProjectNumber extends ProjectNumber {

  private final long value;

  AutoValue_ProjectNumber(
      long value) {
    this.value = value;
  }

  @Override
  public long value() {
    return value;
  }

  @Override
  public String toString() {
    return "ProjectNumber{"
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ProjectNumber) {
      ProjectNumber that = (ProjectNumber) o;
      return this.value == that.value();
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= (int) ((value >>> 32) ^ value);
    return h$;
  }

}
