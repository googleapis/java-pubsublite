package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoOneOfProcessor")
final class AutoOneOf_ConnectedSubscriber_Response {
  private AutoOneOf_ConnectedSubscriber_Response() {} // There are no instances of this type.

  static ConnectedSubscriber.Response messages(ImmutableList<SequencedMessage> messages) {
    if (messages == null) {
      throw new NullPointerException();
    }
    return new Impl_messages(messages);
  }

  static ConnectedSubscriber.Response seekOffset(Offset seekOffset) {
    if (seekOffset == null) {
      throw new NullPointerException();
    }
    return new Impl_seekOffset(seekOffset);
  }

  // Parent class that each implementation will inherit from.
  private abstract static class Parent_ extends ConnectedSubscriber.Response {
    @Override
     ImmutableList<SequencedMessage> messages() {
      throw new UnsupportedOperationException(getKind().toString());
    }
    @Override
     Offset seekOffset() {
      throw new UnsupportedOperationException(getKind().toString());
    }
  }

  // Implementation when the contained property is "messages".
  private static final class Impl_messages extends Parent_ {
    private final ImmutableList<SequencedMessage> messages;
    Impl_messages(ImmutableList<SequencedMessage> messages) {
      this.messages = messages;
    }
    @Override
    public ImmutableList<SequencedMessage> messages() {
      return messages;
    }
    @Override
    public String toString() {
      return "Response{messages=" + this.messages + "}";
    }
    @Override
    public boolean equals(Object x) {
      if (x instanceof ConnectedSubscriber.Response) {
        ConnectedSubscriber.Response that = (ConnectedSubscriber.Response) x;
        return this.getKind() == that.getKind()
            && this.messages.equals(that.messages());
      } else {
        return false;
      }
    }
    @Override
    public int hashCode() {
      return messages.hashCode();
    }
    @Override
    public ConnectedSubscriber.Response.Kind getKind() {
      return ConnectedSubscriber.Response.Kind.MESSAGES;
    }
  }

  // Implementation when the contained property is "seekOffset".
  private static final class Impl_seekOffset extends Parent_ {
    private final Offset seekOffset;
    Impl_seekOffset(Offset seekOffset) {
      this.seekOffset = seekOffset;
    }
    @Override
    public Offset seekOffset() {
      return seekOffset;
    }
    @Override
    public String toString() {
      return "Response{seekOffset=" + this.seekOffset + "}";
    }
    @Override
    public boolean equals(Object x) {
      if (x instanceof ConnectedSubscriber.Response) {
        ConnectedSubscriber.Response that = (ConnectedSubscriber.Response) x;
        return this.getKind() == that.getKind()
            && this.seekOffset.equals(that.seekOffset());
      } else {
        return false;
      }
    }
    @Override
    public int hashCode() {
      return seekOffset.hashCode();
    }
    @Override
    public ConnectedSubscriber.Response.Kind getKind() {
      return ConnectedSubscriber.Response.Kind.SEEK_OFFSET;
    }
  }

}
