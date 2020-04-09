package com.google.cloud.pubsublite.cloudpubsub;

import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_FlowControlSettings extends FlowControlSettings {

  private final long bytesOutstanding;

  private final long messagesOutstanding;

  private AutoValue_FlowControlSettings(
      long bytesOutstanding,
      long messagesOutstanding) {
    this.bytesOutstanding = bytesOutstanding;
    this.messagesOutstanding = messagesOutstanding;
  }

  @Override
  public long bytesOutstanding() {
    return bytesOutstanding;
  }

  @Override
  public long messagesOutstanding() {
    return messagesOutstanding;
  }

  @Override
  public String toString() {
    return "FlowControlSettings{"
         + "bytesOutstanding=" + bytesOutstanding + ", "
         + "messagesOutstanding=" + messagesOutstanding
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof FlowControlSettings) {
      FlowControlSettings that = (FlowControlSettings) o;
      return this.bytesOutstanding == that.bytesOutstanding()
          && this.messagesOutstanding == that.messagesOutstanding();
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= (int) ((bytesOutstanding >>> 32) ^ bytesOutstanding);
    h$ *= 1000003;
    h$ ^= (int) ((messagesOutstanding >>> 32) ^ messagesOutstanding);
    return h$;
  }

  static final class Builder extends FlowControlSettings.Builder {
    private Long bytesOutstanding;
    private Long messagesOutstanding;
    Builder() {
    }
    @Override
    public FlowControlSettings.Builder setBytesOutstanding(long bytesOutstanding) {
      this.bytesOutstanding = bytesOutstanding;
      return this;
    }
    @Override
    public FlowControlSettings.Builder setMessagesOutstanding(long messagesOutstanding) {
      this.messagesOutstanding = messagesOutstanding;
      return this;
    }
    @Override
    FlowControlSettings autoBuild() {
      String missing = "";
      if (this.bytesOutstanding == null) {
        missing += " bytesOutstanding";
      }
      if (this.messagesOutstanding == null) {
        missing += " messagesOutstanding";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_FlowControlSettings(
          this.bytesOutstanding,
          this.messagesOutstanding);
    }
  }

}
