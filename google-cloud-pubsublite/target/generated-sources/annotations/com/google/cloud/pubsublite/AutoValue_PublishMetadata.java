package com.google.cloud.pubsublite;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_PublishMetadata extends PublishMetadata {

  private final Partition partition;

  private final Offset offset;

  AutoValue_PublishMetadata(
      Partition partition,
      Offset offset) {
    if (partition == null) {
      throw new NullPointerException("Null partition");
    }
    this.partition = partition;
    if (offset == null) {
      throw new NullPointerException("Null offset");
    }
    this.offset = offset;
  }

  @Override
  public Partition partition() {
    return partition;
  }

  @Override
  public Offset offset() {
    return offset;
  }

  @Override
  public String toString() {
    return "PublishMetadata{"
         + "partition=" + partition + ", "
         + "offset=" + offset
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PublishMetadata) {
      PublishMetadata that = (PublishMetadata) o;
      return this.partition.equals(that.partition())
          && this.offset.equals(that.offset());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= partition.hashCode();
    h$ *= 1000003;
    h$ ^= offset.hashCode();
    return h$;
  }

}
