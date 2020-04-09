package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_TopicName extends TopicName {

  private final String value;

  AutoValue_TopicName(
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
    return "TopicName{"
         + "value=" + value
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof TopicName) {
      TopicName that = (TopicName) o;
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

}
