package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Offset extends Offset {

  private final long value;

  AutoValue_Offset(
      long value) {
    this.value = value;
  }

  @Override
  public long value() {
    return value;
  }

  @Override
  public String toString() {
    return "Offset{"
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Offset) {
      Offset that = (Offset) o;
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
