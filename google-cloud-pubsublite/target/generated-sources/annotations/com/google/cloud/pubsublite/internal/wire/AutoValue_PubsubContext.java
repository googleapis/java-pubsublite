package com.google.cloud.pubsublite.internal.wire;

import java.util.Optional;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_PubsubContext extends PubsubContext {

  private final Optional<PubsubContext.Framework> framework;

  AutoValue_PubsubContext(
      Optional<PubsubContext.Framework> framework) {
    if (framework == null) {
      throw new NullPointerException("Null framework");
    }
    this.framework = framework;
  }

  @Override
  public Optional<PubsubContext.Framework> framework() {
    return framework;
  }

  @Override
  public String toString() {
    return "PubsubContext{"
         + "framework=" + framework
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PubsubContext) {
      PubsubContext that = (PubsubContext) o;
      return this.framework.equals(that.framework());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= framework.hashCode();
    return h$;
  }

}
