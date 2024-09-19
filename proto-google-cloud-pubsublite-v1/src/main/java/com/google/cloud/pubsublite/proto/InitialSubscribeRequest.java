/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/subscriber.proto

// Protobuf Java Version: 3.25.4
package com.google.cloud.pubsublite.proto;

/**
 *
 *
 * <pre>
 * The first request that must be sent on a newly-opened stream. The client must
 * wait for the response before sending subsequent requests on the stream.
 * </pre>
 *
 * Protobuf type {@code google.cloud.pubsublite.v1.InitialSubscribeRequest}
 */
public final class InitialSubscribeRequest extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.pubsublite.v1.InitialSubscribeRequest)
    InitialSubscribeRequestOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use InitialSubscribeRequest.newBuilder() to construct.
  private InitialSubscribeRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private InitialSubscribeRequest() {
    subscription_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new InitialSubscribeRequest();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.pubsublite.proto.SubscriberProto
        .internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.pubsublite.proto.SubscriberProto
        .internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.pubsublite.proto.InitialSubscribeRequest.class,
            com.google.cloud.pubsublite.proto.InitialSubscribeRequest.Builder.class);
  }

  private int bitField0_;
  public static final int SUBSCRIPTION_FIELD_NUMBER = 1;

  @SuppressWarnings("serial")
  private volatile java.lang.Object subscription_ = "";
  /**
   *
   *
   * <pre>
   * The subscription from which to receive messages.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   *
   * @return The subscription.
   */
  @java.lang.Override
  public java.lang.String getSubscription() {
    java.lang.Object ref = subscription_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      subscription_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * The subscription from which to receive messages.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   *
   * @return The bytes for subscription.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSubscriptionBytes() {
    java.lang.Object ref = subscription_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      subscription_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int PARTITION_FIELD_NUMBER = 2;
  private long partition_ = 0L;
  /**
   *
   *
   * <pre>
   * The partition from which to receive messages. Partitions are zero indexed,
   * so `partition` must be in the range [0, topic.num_partitions).
   * </pre>
   *
   * <code>int64 partition = 2;</code>
   *
   * @return The partition.
   */
  @java.lang.Override
  public long getPartition() {
    return partition_;
  }

  public static final int INITIAL_LOCATION_FIELD_NUMBER = 4;
  private com.google.cloud.pubsublite.proto.SeekRequest initialLocation_;
  /**
   *
   *
   * <pre>
   * Optional. Initial target location within the message backlog. If not set,
   * messages will be delivered from the commit cursor for the given
   * subscription and partition.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return Whether the initialLocation field is set.
   */
  @java.lang.Override
  public boolean hasInitialLocation() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   *
   *
   * <pre>
   * Optional. Initial target location within the message backlog. If not set,
   * messages will be delivered from the commit cursor for the given
   * subscription and partition.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   *
   * @return The initialLocation.
   */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.SeekRequest getInitialLocation() {
    return initialLocation_ == null
        ? com.google.cloud.pubsublite.proto.SeekRequest.getDefaultInstance()
        : initialLocation_;
  }
  /**
   *
   *
   * <pre>
   * Optional. Initial target location within the message backlog. If not set,
   * messages will be delivered from the commit cursor for the given
   * subscription and partition.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
   * </code>
   */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.SeekRequestOrBuilder getInitialLocationOrBuilder() {
    return initialLocation_ == null
        ? com.google.cloud.pubsublite.proto.SeekRequest.getDefaultInstance()
        : initialLocation_;
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
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(subscription_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, subscription_);
    }
    if (partition_ != 0L) {
      output.writeInt64(2, partition_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      output.writeMessage(4, getInitialLocation());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(subscription_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, subscription_);
    }
    if (partition_ != 0L) {
      size += com.google.protobuf.CodedOutputStream.computeInt64Size(2, partition_);
    }
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(4, getInitialLocation());
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof com.google.cloud.pubsublite.proto.InitialSubscribeRequest)) {
      return super.equals(obj);
    }
    com.google.cloud.pubsublite.proto.InitialSubscribeRequest other =
        (com.google.cloud.pubsublite.proto.InitialSubscribeRequest) obj;

    if (!getSubscription().equals(other.getSubscription())) return false;
    if (getPartition() != other.getPartition()) return false;
    if (hasInitialLocation() != other.hasInitialLocation()) return false;
    if (hasInitialLocation()) {
      if (!getInitialLocation().equals(other.getInitialLocation())) return false;
    }
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SUBSCRIPTION_FIELD_NUMBER;
    hash = (53 * hash) + getSubscription().hashCode();
    hash = (37 * hash) + PARTITION_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getPartition());
    if (hasInitialLocation()) {
      hash = (37 * hash) + INITIAL_LOCATION_FIELD_NUMBER;
      hash = (53 * hash) + getInitialLocation().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }

  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }

  public static Builder newBuilder(
      com.google.cloud.pubsublite.proto.InitialSubscribeRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }

  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   *
   *
   * <pre>
   * The first request that must be sent on a newly-opened stream. The client must
   * wait for the response before sending subsequent requests on the stream.
   * </pre>
   *
   * Protobuf type {@code google.cloud.pubsublite.v1.InitialSubscribeRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.pubsublite.v1.InitialSubscribeRequest)
      com.google.cloud.pubsublite.proto.InitialSubscribeRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.pubsublite.proto.SubscriberProto
          .internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.pubsublite.proto.SubscriberProto
          .internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.pubsublite.proto.InitialSubscribeRequest.class,
              com.google.cloud.pubsublite.proto.InitialSubscribeRequest.Builder.class);
    }

    // Construct using com.google.cloud.pubsublite.proto.InitialSubscribeRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        getInitialLocationFieldBuilder();
      }
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      subscription_ = "";
      partition_ = 0L;
      initialLocation_ = null;
      if (initialLocationBuilder_ != null) {
        initialLocationBuilder_.dispose();
        initialLocationBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.pubsublite.proto.SubscriberProto
          .internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.InitialSubscribeRequest getDefaultInstanceForType() {
      return com.google.cloud.pubsublite.proto.InitialSubscribeRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.InitialSubscribeRequest build() {
      com.google.cloud.pubsublite.proto.InitialSubscribeRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.InitialSubscribeRequest buildPartial() {
      com.google.cloud.pubsublite.proto.InitialSubscribeRequest result =
          new com.google.cloud.pubsublite.proto.InitialSubscribeRequest(this);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.google.cloud.pubsublite.proto.InitialSubscribeRequest result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.subscription_ = subscription_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.partition_ = partition_;
      }
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.initialLocation_ =
            initialLocationBuilder_ == null ? initialLocation_ : initialLocationBuilder_.build();
        to_bitField0_ |= 0x00000001;
      }
      result.bitField0_ |= to_bitField0_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }

    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.setField(field, value);
    }

    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }

    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }

    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }

    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.cloud.pubsublite.proto.InitialSubscribeRequest) {
        return mergeFrom((com.google.cloud.pubsublite.proto.InitialSubscribeRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.pubsublite.proto.InitialSubscribeRequest other) {
      if (other == com.google.cloud.pubsublite.proto.InitialSubscribeRequest.getDefaultInstance())
        return this;
      if (!other.getSubscription().isEmpty()) {
        subscription_ = other.subscription_;
        bitField0_ |= 0x00000001;
        onChanged();
      }
      if (other.getPartition() != 0L) {
        setPartition(other.getPartition());
      }
      if (other.hasInitialLocation()) {
        mergeInitialLocation(other.getInitialLocation());
      }
      this.mergeUnknownFields(other.getUnknownFields());
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
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10:
              {
                subscription_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000001;
                break;
              } // case 10
            case 16:
              {
                partition_ = input.readInt64();
                bitField0_ |= 0x00000002;
                break;
              } // case 16
            case 34:
              {
                input.readMessage(getInitialLocationFieldBuilder().getBuilder(), extensionRegistry);
                bitField0_ |= 0x00000004;
                break;
              } // case 34
            default:
              {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }

    private int bitField0_;

    private java.lang.Object subscription_ = "";
    /**
     *
     *
     * <pre>
     * The subscription from which to receive messages.
     * </pre>
     *
     * <code>string subscription = 1;</code>
     *
     * @return The subscription.
     */
    public java.lang.String getSubscription() {
      java.lang.Object ref = subscription_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        subscription_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * The subscription from which to receive messages.
     * </pre>
     *
     * <code>string subscription = 1;</code>
     *
     * @return The bytes for subscription.
     */
    public com.google.protobuf.ByteString getSubscriptionBytes() {
      java.lang.Object ref = subscription_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        subscription_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * The subscription from which to receive messages.
     * </pre>
     *
     * <code>string subscription = 1;</code>
     *
     * @param value The subscription to set.
     * @return This builder for chaining.
     */
    public Builder setSubscription(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      subscription_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The subscription from which to receive messages.
     * </pre>
     *
     * <code>string subscription = 1;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearSubscription() {
      subscription_ = getDefaultInstance().getSubscription();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The subscription from which to receive messages.
     * </pre>
     *
     * <code>string subscription = 1;</code>
     *
     * @param value The bytes for subscription to set.
     * @return This builder for chaining.
     */
    public Builder setSubscriptionBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);
      subscription_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }

    private long partition_;
    /**
     *
     *
     * <pre>
     * The partition from which to receive messages. Partitions are zero indexed,
     * so `partition` must be in the range [0, topic.num_partitions).
     * </pre>
     *
     * <code>int64 partition = 2;</code>
     *
     * @return The partition.
     */
    @java.lang.Override
    public long getPartition() {
      return partition_;
    }
    /**
     *
     *
     * <pre>
     * The partition from which to receive messages. Partitions are zero indexed,
     * so `partition` must be in the range [0, topic.num_partitions).
     * </pre>
     *
     * <code>int64 partition = 2;</code>
     *
     * @param value The partition to set.
     * @return This builder for chaining.
     */
    public Builder setPartition(long value) {

      partition_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * The partition from which to receive messages. Partitions are zero indexed,
     * so `partition` must be in the range [0, topic.num_partitions).
     * </pre>
     *
     * <code>int64 partition = 2;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearPartition() {
      bitField0_ = (bitField0_ & ~0x00000002);
      partition_ = 0L;
      onChanged();
      return this;
    }

    private com.google.cloud.pubsublite.proto.SeekRequest initialLocation_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.pubsublite.proto.SeekRequest,
            com.google.cloud.pubsublite.proto.SeekRequest.Builder,
            com.google.cloud.pubsublite.proto.SeekRequestOrBuilder>
        initialLocationBuilder_;
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     *
     * @return Whether the initialLocation field is set.
     */
    public boolean hasInitialLocation() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     *
     * @return The initialLocation.
     */
    public com.google.cloud.pubsublite.proto.SeekRequest getInitialLocation() {
      if (initialLocationBuilder_ == null) {
        return initialLocation_ == null
            ? com.google.cloud.pubsublite.proto.SeekRequest.getDefaultInstance()
            : initialLocation_;
      } else {
        return initialLocationBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public Builder setInitialLocation(com.google.cloud.pubsublite.proto.SeekRequest value) {
      if (initialLocationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        initialLocation_ = value;
      } else {
        initialLocationBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public Builder setInitialLocation(
        com.google.cloud.pubsublite.proto.SeekRequest.Builder builderForValue) {
      if (initialLocationBuilder_ == null) {
        initialLocation_ = builderForValue.build();
      } else {
        initialLocationBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public Builder mergeInitialLocation(com.google.cloud.pubsublite.proto.SeekRequest value) {
      if (initialLocationBuilder_ == null) {
        if (((bitField0_ & 0x00000004) != 0)
            && initialLocation_ != null
            && initialLocation_
                != com.google.cloud.pubsublite.proto.SeekRequest.getDefaultInstance()) {
          getInitialLocationBuilder().mergeFrom(value);
        } else {
          initialLocation_ = value;
        }
      } else {
        initialLocationBuilder_.mergeFrom(value);
      }
      if (initialLocation_ != null) {
        bitField0_ |= 0x00000004;
        onChanged();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public Builder clearInitialLocation() {
      bitField0_ = (bitField0_ & ~0x00000004);
      initialLocation_ = null;
      if (initialLocationBuilder_ != null) {
        initialLocationBuilder_.dispose();
        initialLocationBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public com.google.cloud.pubsublite.proto.SeekRequest.Builder getInitialLocationBuilder() {
      bitField0_ |= 0x00000004;
      onChanged();
      return getInitialLocationFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    public com.google.cloud.pubsublite.proto.SeekRequestOrBuilder getInitialLocationOrBuilder() {
      if (initialLocationBuilder_ != null) {
        return initialLocationBuilder_.getMessageOrBuilder();
      } else {
        return initialLocation_ == null
            ? com.google.cloud.pubsublite.proto.SeekRequest.getDefaultInstance()
            : initialLocation_;
      }
    }
    /**
     *
     *
     * <pre>
     * Optional. Initial target location within the message backlog. If not set,
     * messages will be delivered from the commit cursor for the given
     * subscription and partition.
     * </pre>
     *
     * <code>
     * .google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];
     * </code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.pubsublite.proto.SeekRequest,
            com.google.cloud.pubsublite.proto.SeekRequest.Builder,
            com.google.cloud.pubsublite.proto.SeekRequestOrBuilder>
        getInitialLocationFieldBuilder() {
      if (initialLocationBuilder_ == null) {
        initialLocationBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.cloud.pubsublite.proto.SeekRequest,
                com.google.cloud.pubsublite.proto.SeekRequest.Builder,
                com.google.cloud.pubsublite.proto.SeekRequestOrBuilder>(
                getInitialLocation(), getParentForChildren(), isClean());
        initialLocation_ = null;
      }
      return initialLocationBuilder_;
    }

    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:google.cloud.pubsublite.v1.InitialSubscribeRequest)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.pubsublite.v1.InitialSubscribeRequest)
  private static final com.google.cloud.pubsublite.proto.InitialSubscribeRequest DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.pubsublite.proto.InitialSubscribeRequest();
  }

  public static com.google.cloud.pubsublite.proto.InitialSubscribeRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<InitialSubscribeRequest> PARSER =
      new com.google.protobuf.AbstractParser<InitialSubscribeRequest>() {
        @java.lang.Override
        public InitialSubscribeRequest parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          Builder builder = newBuilder();
          try {
            builder.mergeFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(builder.buildPartial());
          } catch (com.google.protobuf.UninitializedMessageException e) {
            throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
          } catch (java.io.IOException e) {
            throw new com.google.protobuf.InvalidProtocolBufferException(e)
                .setUnfinishedMessage(builder.buildPartial());
          }
          return builder.buildPartial();
        }
      };

  public static com.google.protobuf.Parser<InitialSubscribeRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<InitialSubscribeRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.pubsublite.proto.InitialSubscribeRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
