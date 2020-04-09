package com.google.cloud.pubsublite.internal.wire;

import java.util.Optional;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_RoutingPublisherBuilder extends RoutingPublisherBuilder {

  private final SinglePartitionPublisherBuilder.Builder publisherBuilder;

  private final Optional<Integer> numPartitions;

  private AutoValue_RoutingPublisherBuilder(
      SinglePartitionPublisherBuilder.Builder publisherBuilder,
      Optional<Integer> numPartitions) {
    this.publisherBuilder = publisherBuilder;
    this.numPartitions = numPartitions;
  }

  @Override
  SinglePartitionPublisherBuilder.Builder publisherBuilder() {
    return publisherBuilder;
  }

  @Override
  Optional<Integer> numPartitions() {
    return numPartitions;
  }

  @Override
  public String toString() {
    return "RoutingPublisherBuilder{"
         + "publisherBuilder=" + publisherBuilder + ", "
         + "numPartitions=" + numPartitions
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof RoutingPublisherBuilder) {
      RoutingPublisherBuilder that = (RoutingPublisherBuilder) o;
      return this.publisherBuilder.equals(that.publisherBuilder())
          && this.numPartitions.equals(that.numPartitions());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= publisherBuilder.hashCode();
    h$ *= 1000003;
    h$ ^= numPartitions.hashCode();
    return h$;
  }

  static final class Builder extends RoutingPublisherBuilder.Builder {
    private SinglePartitionPublisherBuilder.Builder publisherBuilder;
    private Optional<Integer> numPartitions = Optional.empty();
    Builder() {
    }
    @Override
    public RoutingPublisherBuilder.Builder setPublisherBuilder(SinglePartitionPublisherBuilder.Builder publisherBuilder) {
      if (publisherBuilder == null) {
        throw new NullPointerException("Null publisherBuilder");
      }
      this.publisherBuilder = publisherBuilder;
      return this;
    }
    @Override
    public RoutingPublisherBuilder.Builder setNumPartitions(Integer numPartitions) {
      this.numPartitions = Optional.of(numPartitions);
      return this;
    }
    @Override
    RoutingPublisherBuilder autoBuild() {
      String missing = "";
      if (this.publisherBuilder == null) {
        missing += " publisherBuilder";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_RoutingPublisherBuilder(
          this.publisherBuilder,
          this.numPartitions);
    }
  }

}
