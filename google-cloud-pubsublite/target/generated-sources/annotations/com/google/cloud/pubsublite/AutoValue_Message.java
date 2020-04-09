package com.google.cloud.pubsublite;

import com.google.common.collect.ImmutableListMultimap;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import java.util.Optional;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Message extends Message {

  private final ByteString key;

  private final ByteString data;

  private final ImmutableListMultimap<String, ByteString> attributes;

  private final Optional<Timestamp> eventTime;

  private AutoValue_Message(
      ByteString key,
      ByteString data,
      ImmutableListMultimap<String, ByteString> attributes,
      Optional<Timestamp> eventTime) {
    this.key = key;
    this.data = data;
    this.attributes = attributes;
    this.eventTime = eventTime;
  }

  @Override
  public ByteString key() {
    return key;
  }

  @Override
  public ByteString data() {
    return data;
  }

  @Override
  public ImmutableListMultimap<String, ByteString> attributes() {
    return attributes;
  }

  @Override
  public Optional<Timestamp> eventTime() {
    return eventTime;
  }

  @Override
  public String toString() {
    return "Message{"
         + "key=" + key + ", "
         + "data=" + data + ", "
         + "attributes=" + attributes + ", "
         + "eventTime=" + eventTime
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Message) {
      Message that = (Message) o;
      return this.key.equals(that.key())
          && this.data.equals(that.data())
          && this.attributes.equals(that.attributes())
          && this.eventTime.equals(that.eventTime());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= key.hashCode();
    h$ *= 1000003;
    h$ ^= data.hashCode();
    h$ *= 1000003;
    h$ ^= attributes.hashCode();
    h$ *= 1000003;
    h$ ^= eventTime.hashCode();
    return h$;
  }

  @Override
  public Message.Builder toBuilder() {
    return new Builder(this);
  }

  static final class Builder extends Message.Builder {
    private ByteString key;
    private ByteString data;
    private ImmutableListMultimap<String, ByteString> attributes;
    private Optional<Timestamp> eventTime = Optional.empty();
    Builder() {
    }
    private Builder(Message source) {
      this.key = source.key();
      this.data = source.data();
      this.attributes = source.attributes();
      this.eventTime = source.eventTime();
    }
    @Override
    public Message.Builder setKey(ByteString key) {
      if (key == null) {
        throw new NullPointerException("Null key");
      }
      this.key = key;
      return this;
    }
    @Override
    public Message.Builder setData(ByteString data) {
      if (data == null) {
        throw new NullPointerException("Null data");
      }
      this.data = data;
      return this;
    }
    @Override
    public Message.Builder setAttributes(ImmutableListMultimap<String, ByteString> attributes) {
      if (attributes == null) {
        throw new NullPointerException("Null attributes");
      }
      this.attributes = attributes;
      return this;
    }
    @Override
    public Message.Builder setEventTime(Timestamp eventTime) {
      this.eventTime = Optional.of(eventTime);
      return this;
    }
    @Override
    public Message build() {
      String missing = "";
      if (this.key == null) {
        missing += " key";
      }
      if (this.data == null) {
        missing += " data";
      }
      if (this.attributes == null) {
        missing += " attributes";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_Message(
          this.key,
          this.data,
          this.attributes,
          this.eventTime);
    }
  }

}
