package com.google.cloud.pubsublite;

import com.google.protobuf.Timestamp;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_SequencedMessage extends SequencedMessage {

  private final Message message;

  private final Timestamp publishTime;

  private final Offset offset;

  private final long byteSize;

  AutoValue_SequencedMessage(
      Message message,
      Timestamp publishTime,
      Offset offset,
      long byteSize) {
    if (message == null) {
      throw new NullPointerException("Null message");
    }
    this.message = message;
    if (publishTime == null) {
      throw new NullPointerException("Null publishTime");
    }
    this.publishTime = publishTime;
    if (offset == null) {
      throw new NullPointerException("Null offset");
    }
    this.offset = offset;
    this.byteSize = byteSize;
  }

  @Override
  public Message message() {
    return message;
  }

  @Override
  public Timestamp publishTime() {
    return publishTime;
  }

  @Override
  public Offset offset() {
    return offset;
  }

  @Override
  public long byteSize() {
    return byteSize;
  }

  @Override
  public String toString() {
    return "SequencedMessage{"
         + "message=" + message + ", "
         + "publishTime=" + publishTime + ", "
         + "offset=" + offset + ", "
         + "byteSize=" + byteSize
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SequencedMessage) {
      SequencedMessage that = (SequencedMessage) o;
      return this.message.equals(that.message())
          && this.publishTime.equals(that.publishTime())
          && this.offset.equals(that.offset())
          && this.byteSize == that.byteSize();
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= message.hashCode();
    h$ *= 1000003;
    h$ ^= publishTime.hashCode();
    h$ *= 1000003;
    h$ ^= offset.hashCode();
    h$ *= 1000003;
    h$ ^= (int) ((byteSize >>> 32) ^ byteSize);
    return h$;
  }

}
