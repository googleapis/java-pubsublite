package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Partition extends Partition {

  private final long value;

  AutoValue_Partition(
      long value) {
    this.value = value;
  }

  @Override
  public long value() {
    return value;
  }

  @Override
  public String toString() {
    return "Partition{"
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Partition) {
      Partition that = (Partition) o;
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
