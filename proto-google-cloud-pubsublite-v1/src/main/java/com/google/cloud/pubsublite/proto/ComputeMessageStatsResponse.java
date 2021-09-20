// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/topic_stats.proto

package com.google.cloud.pubsublite.proto;

/**
 * <pre>
 * Response containing stats for messages in the requested topic and partition.
 * </pre>
 *
 * Protobuf type {@code google.cloud.pubsublite.v1.ComputeMessageStatsResponse}
 */
public final class ComputeMessageStatsResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:google.cloud.pubsublite.v1.ComputeMessageStatsResponse)
    ComputeMessageStatsResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ComputeMessageStatsResponse.newBuilder() to construct.
  private ComputeMessageStatsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ComputeMessageStatsResponse() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ComputeMessageStatsResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ComputeMessageStatsResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            messageCount_ = input.readInt64();
            break;
          }
          case 16: {

            messageBytes_ = input.readInt64();
            break;
          }
          case 26: {
            com.google.protobuf.Timestamp.Builder subBuilder = null;
            if (minimumPublishTime_ != null) {
              subBuilder = minimumPublishTime_.toBuilder();
            }
            minimumPublishTime_ = input.readMessage(com.google.protobuf.Timestamp.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(minimumPublishTime_);
              minimumPublishTime_ = subBuilder.buildPartial();
            }

            break;
          }
          case 34: {
            com.google.protobuf.Timestamp.Builder subBuilder = null;
            if (minimumEventTime_ != null) {
              subBuilder = minimumEventTime_.toBuilder();
            }
            minimumEventTime_ = input.readMessage(com.google.protobuf.Timestamp.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(minimumEventTime_);
              minimumEventTime_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.cloud.pubsublite.proto.TopicStatsProto.internal_static_google_cloud_pubsublite_v1_ComputeMessageStatsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.pubsublite.proto.TopicStatsProto.internal_static_google_cloud_pubsublite_v1_ComputeMessageStatsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.class, com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.Builder.class);
  }

  public static final int MESSAGE_COUNT_FIELD_NUMBER = 1;
  private long messageCount_;
  /**
   * <pre>
   * The count of messages.
   * </pre>
   *
   * <code>int64 message_count = 1;</code>
   * @return The messageCount.
   */
  @java.lang.Override
  public long getMessageCount() {
    return messageCount_;
  }

  public static final int MESSAGE_BYTES_FIELD_NUMBER = 2;
  private long messageBytes_;
  /**
   * <pre>
   * The number of quota bytes accounted to these messages.
   * </pre>
   *
   * <code>int64 message_bytes = 2;</code>
   * @return The messageBytes.
   */
  @java.lang.Override
  public long getMessageBytes() {
    return messageBytes_;
  }

  public static final int MINIMUM_PUBLISH_TIME_FIELD_NUMBER = 3;
  private com.google.protobuf.Timestamp minimumPublishTime_;
  /**
   * <pre>
   * The minimum publish timestamp across these messages. Note that publish
   * timestamps within a partition are not guaranteed to be non-decreasing. The
   * timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
   * @return Whether the minimumPublishTime field is set.
   */
  @java.lang.Override
  public boolean hasMinimumPublishTime() {
    return minimumPublishTime_ != null;
  }
  /**
   * <pre>
   * The minimum publish timestamp across these messages. Note that publish
   * timestamps within a partition are not guaranteed to be non-decreasing. The
   * timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
   * @return The minimumPublishTime.
   */
  @java.lang.Override
  public com.google.protobuf.Timestamp getMinimumPublishTime() {
    return minimumPublishTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : minimumPublishTime_;
  }
  /**
   * <pre>
   * The minimum publish timestamp across these messages. Note that publish
   * timestamps within a partition are not guaranteed to be non-decreasing. The
   * timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
   */
  @java.lang.Override
  public com.google.protobuf.TimestampOrBuilder getMinimumPublishTimeOrBuilder() {
    return getMinimumPublishTime();
  }

  public static final int MINIMUM_EVENT_TIME_FIELD_NUMBER = 4;
  private com.google.protobuf.Timestamp minimumEventTime_;
  /**
   * <pre>
   * The minimum event timestamp across these messages. For the purposes of this
   * computation, if a message does not have an event time, we use the publish
   * time. The timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
   * @return Whether the minimumEventTime field is set.
   */
  @java.lang.Override
  public boolean hasMinimumEventTime() {
    return minimumEventTime_ != null;
  }
  /**
   * <pre>
   * The minimum event timestamp across these messages. For the purposes of this
   * computation, if a message does not have an event time, we use the publish
   * time. The timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
   * @return The minimumEventTime.
   */
  @java.lang.Override
  public com.google.protobuf.Timestamp getMinimumEventTime() {
    return minimumEventTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : minimumEventTime_;
  }
  /**
   * <pre>
   * The minimum event timestamp across these messages. For the purposes of this
   * computation, if a message does not have an event time, we use the publish
   * time. The timestamp will be unset if there are no messages.
   * </pre>
   *
   * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
   */
  @java.lang.Override
  public com.google.protobuf.TimestampOrBuilder getMinimumEventTimeOrBuilder() {
    return getMinimumEventTime();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (messageCount_ != 0L) {
      output.writeInt64(1, messageCount_);
    }
    if (messageBytes_ != 0L) {
      output.writeInt64(2, messageBytes_);
    }
    if (minimumPublishTime_ != null) {
      output.writeMessage(3, getMinimumPublishTime());
    }
    if (minimumEventTime_ != null) {
      output.writeMessage(4, getMinimumEventTime());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (messageCount_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, messageCount_);
    }
    if (messageBytes_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, messageBytes_);
    }
    if (minimumPublishTime_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, getMinimumPublishTime());
    }
    if (minimumEventTime_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(4, getMinimumEventTime());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse)) {
      return super.equals(obj);
    }
    com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse other = (com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse) obj;

    if (getMessageCount()
        != other.getMessageCount()) return false;
    if (getMessageBytes()
        != other.getMessageBytes()) return false;
    if (hasMinimumPublishTime() != other.hasMinimumPublishTime()) return false;
    if (hasMinimumPublishTime()) {
      if (!getMinimumPublishTime()
          .equals(other.getMinimumPublishTime())) return false;
    }
    if (hasMinimumEventTime() != other.hasMinimumEventTime()) return false;
    if (hasMinimumEventTime()) {
      if (!getMinimumEventTime()
          .equals(other.getMinimumEventTime())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + MESSAGE_COUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getMessageCount());
    hash = (37 * hash) + MESSAGE_BYTES_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getMessageBytes());
    if (hasMinimumPublishTime()) {
      hash = (37 * hash) + MINIMUM_PUBLISH_TIME_FIELD_NUMBER;
      hash = (53 * hash) + getMinimumPublishTime().hashCode();
    }
    if (hasMinimumEventTime()) {
      hash = (37 * hash) + MINIMUM_EVENT_TIME_FIELD_NUMBER;
      hash = (53 * hash) + getMinimumEventTime().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * Response containing stats for messages in the requested topic and partition.
   * </pre>
   *
   * Protobuf type {@code google.cloud.pubsublite.v1.ComputeMessageStatsResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.cloud.pubsublite.v1.ComputeMessageStatsResponse)
      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.cloud.pubsublite.proto.TopicStatsProto.internal_static_google_cloud_pubsublite_v1_ComputeMessageStatsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.pubsublite.proto.TopicStatsProto.internal_static_google_cloud_pubsublite_v1_ComputeMessageStatsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.class, com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.Builder.class);
    }

    // Construct using com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      messageCount_ = 0L;

      messageBytes_ = 0L;

      if (minimumPublishTimeBuilder_ == null) {
        minimumPublishTime_ = null;
      } else {
        minimumPublishTime_ = null;
        minimumPublishTimeBuilder_ = null;
      }
      if (minimumEventTimeBuilder_ == null) {
        minimumEventTime_ = null;
      } else {
        minimumEventTime_ = null;
        minimumEventTimeBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.cloud.pubsublite.proto.TopicStatsProto.internal_static_google_cloud_pubsublite_v1_ComputeMessageStatsResponse_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse getDefaultInstanceForType() {
      return com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse build() {
      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse buildPartial() {
      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse result = new com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse(this);
      result.messageCount_ = messageCount_;
      result.messageBytes_ = messageBytes_;
      if (minimumPublishTimeBuilder_ == null) {
        result.minimumPublishTime_ = minimumPublishTime_;
      } else {
        result.minimumPublishTime_ = minimumPublishTimeBuilder_.build();
      }
      if (minimumEventTimeBuilder_ == null) {
        result.minimumEventTime_ = minimumEventTime_;
      } else {
        result.minimumEventTime_ = minimumEventTimeBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse) {
        return mergeFrom((com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse other) {
      if (other == com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse.getDefaultInstance()) return this;
      if (other.getMessageCount() != 0L) {
        setMessageCount(other.getMessageCount());
      }
      if (other.getMessageBytes() != 0L) {
        setMessageBytes(other.getMessageBytes());
      }
      if (other.hasMinimumPublishTime()) {
        mergeMinimumPublishTime(other.getMinimumPublishTime());
      }
      if (other.hasMinimumEventTime()) {
        mergeMinimumEventTime(other.getMinimumEventTime());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long messageCount_ ;
    /**
     * <pre>
     * The count of messages.
     * </pre>
     *
     * <code>int64 message_count = 1;</code>
     * @return The messageCount.
     */
    @java.lang.Override
    public long getMessageCount() {
      return messageCount_;
    }
    /**
     * <pre>
     * The count of messages.
     * </pre>
     *
     * <code>int64 message_count = 1;</code>
     * @param value The messageCount to set.
     * @return This builder for chaining.
     */
    public Builder setMessageCount(long value) {
      
      messageCount_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * The count of messages.
     * </pre>
     *
     * <code>int64 message_count = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessageCount() {
      
      messageCount_ = 0L;
      onChanged();
      return this;
    }

    private long messageBytes_ ;
    /**
     * <pre>
     * The number of quota bytes accounted to these messages.
     * </pre>
     *
     * <code>int64 message_bytes = 2;</code>
     * @return The messageBytes.
     */
    @java.lang.Override
    public long getMessageBytes() {
      return messageBytes_;
    }
    /**
     * <pre>
     * The number of quota bytes accounted to these messages.
     * </pre>
     *
     * <code>int64 message_bytes = 2;</code>
     * @param value The messageBytes to set.
     * @return This builder for chaining.
     */
    public Builder setMessageBytes(long value) {
      
      messageBytes_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * The number of quota bytes accounted to these messages.
     * </pre>
     *
     * <code>int64 message_bytes = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessageBytes() {
      
      messageBytes_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.Timestamp minimumPublishTime_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> minimumPublishTimeBuilder_;
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     * @return Whether the minimumPublishTime field is set.
     */
    public boolean hasMinimumPublishTime() {
      return minimumPublishTimeBuilder_ != null || minimumPublishTime_ != null;
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     * @return The minimumPublishTime.
     */
    public com.google.protobuf.Timestamp getMinimumPublishTime() {
      if (minimumPublishTimeBuilder_ == null) {
        return minimumPublishTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : minimumPublishTime_;
      } else {
        return minimumPublishTimeBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public Builder setMinimumPublishTime(com.google.protobuf.Timestamp value) {
      if (minimumPublishTimeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        minimumPublishTime_ = value;
        onChanged();
      } else {
        minimumPublishTimeBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public Builder setMinimumPublishTime(
        com.google.protobuf.Timestamp.Builder builderForValue) {
      if (minimumPublishTimeBuilder_ == null) {
        minimumPublishTime_ = builderForValue.build();
        onChanged();
      } else {
        minimumPublishTimeBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public Builder mergeMinimumPublishTime(com.google.protobuf.Timestamp value) {
      if (minimumPublishTimeBuilder_ == null) {
        if (minimumPublishTime_ != null) {
          minimumPublishTime_ =
            com.google.protobuf.Timestamp.newBuilder(minimumPublishTime_).mergeFrom(value).buildPartial();
        } else {
          minimumPublishTime_ = value;
        }
        onChanged();
      } else {
        minimumPublishTimeBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public Builder clearMinimumPublishTime() {
      if (minimumPublishTimeBuilder_ == null) {
        minimumPublishTime_ = null;
        onChanged();
      } else {
        minimumPublishTime_ = null;
        minimumPublishTimeBuilder_ = null;
      }

      return this;
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public com.google.protobuf.Timestamp.Builder getMinimumPublishTimeBuilder() {
      
      onChanged();
      return getMinimumPublishTimeFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    public com.google.protobuf.TimestampOrBuilder getMinimumPublishTimeOrBuilder() {
      if (minimumPublishTimeBuilder_ != null) {
        return minimumPublishTimeBuilder_.getMessageOrBuilder();
      } else {
        return minimumPublishTime_ == null ?
            com.google.protobuf.Timestamp.getDefaultInstance() : minimumPublishTime_;
      }
    }
    /**
     * <pre>
     * The minimum publish timestamp across these messages. Note that publish
     * timestamps within a partition are not guaranteed to be non-decreasing. The
     * timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_publish_time = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
        getMinimumPublishTimeFieldBuilder() {
      if (minimumPublishTimeBuilder_ == null) {
        minimumPublishTimeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                getMinimumPublishTime(),
                getParentForChildren(),
                isClean());
        minimumPublishTime_ = null;
      }
      return minimumPublishTimeBuilder_;
    }

    private com.google.protobuf.Timestamp minimumEventTime_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> minimumEventTimeBuilder_;
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     * @return Whether the minimumEventTime field is set.
     */
    public boolean hasMinimumEventTime() {
      return minimumEventTimeBuilder_ != null || minimumEventTime_ != null;
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     * @return The minimumEventTime.
     */
    public com.google.protobuf.Timestamp getMinimumEventTime() {
      if (minimumEventTimeBuilder_ == null) {
        return minimumEventTime_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : minimumEventTime_;
      } else {
        return minimumEventTimeBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public Builder setMinimumEventTime(com.google.protobuf.Timestamp value) {
      if (minimumEventTimeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        minimumEventTime_ = value;
        onChanged();
      } else {
        minimumEventTimeBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public Builder setMinimumEventTime(
        com.google.protobuf.Timestamp.Builder builderForValue) {
      if (minimumEventTimeBuilder_ == null) {
        minimumEventTime_ = builderForValue.build();
        onChanged();
      } else {
        minimumEventTimeBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public Builder mergeMinimumEventTime(com.google.protobuf.Timestamp value) {
      if (minimumEventTimeBuilder_ == null) {
        if (minimumEventTime_ != null) {
          minimumEventTime_ =
            com.google.protobuf.Timestamp.newBuilder(minimumEventTime_).mergeFrom(value).buildPartial();
        } else {
          minimumEventTime_ = value;
        }
        onChanged();
      } else {
        minimumEventTimeBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public Builder clearMinimumEventTime() {
      if (minimumEventTimeBuilder_ == null) {
        minimumEventTime_ = null;
        onChanged();
      } else {
        minimumEventTime_ = null;
        minimumEventTimeBuilder_ = null;
      }

      return this;
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public com.google.protobuf.Timestamp.Builder getMinimumEventTimeBuilder() {
      
      onChanged();
      return getMinimumEventTimeFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    public com.google.protobuf.TimestampOrBuilder getMinimumEventTimeOrBuilder() {
      if (minimumEventTimeBuilder_ != null) {
        return minimumEventTimeBuilder_.getMessageOrBuilder();
      } else {
        return minimumEventTime_ == null ?
            com.google.protobuf.Timestamp.getDefaultInstance() : minimumEventTime_;
      }
    }
    /**
     * <pre>
     * The minimum event timestamp across these messages. For the purposes of this
     * computation, if a message does not have an event time, we use the publish
     * time. The timestamp will be unset if there are no messages.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp minimum_event_time = 4;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
        getMinimumEventTimeFieldBuilder() {
      if (minimumEventTimeBuilder_ == null) {
        minimumEventTimeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                getMinimumEventTime(),
                getParentForChildren(),
                isClean());
        minimumEventTime_ = null;
      }
      return minimumEventTimeBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:google.cloud.pubsublite.v1.ComputeMessageStatsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.pubsublite.v1.ComputeMessageStatsResponse)
  private static final com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse();
  }

  public static com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ComputeMessageStatsResponse>
      PARSER = new com.google.protobuf.AbstractParser<ComputeMessageStatsResponse>() {
    @java.lang.Override
    public ComputeMessageStatsResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ComputeMessageStatsResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ComputeMessageStatsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ComputeMessageStatsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

