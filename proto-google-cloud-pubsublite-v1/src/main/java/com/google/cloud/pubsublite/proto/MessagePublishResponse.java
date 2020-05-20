// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/publisher.proto

package com.google.cloud.pubsublite.proto;

/**
 *
 *
 * <pre>
 * Response to a MessagePublishRequest.
 * </pre>
 *
 * Protobuf type {@code google.cloud.pubsublite.v1.MessagePublishResponse}
 */
public final class MessagePublishResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.pubsublite.v1.MessagePublishResponse)
    MessagePublishResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use MessagePublishResponse.newBuilder() to construct.
  private MessagePublishResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private MessagePublishResponse() {}

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new MessagePublishResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private MessagePublishResponse(
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
          case 10:
            {
              com.google.cloud.pubsublite.proto.Cursor.Builder subBuilder = null;
              if (startCursor_ != null) {
                subBuilder = startCursor_.toBuilder();
              }
              startCursor_ =
                  input.readMessage(
                      com.google.cloud.pubsublite.proto.Cursor.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(startCursor_);
                startCursor_ = subBuilder.buildPartial();
              }

              break;
            }
          default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.pubsublite.proto.PublisherProto
        .internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.pubsublite.proto.PublisherProto
        .internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.pubsublite.proto.MessagePublishResponse.class,
            com.google.cloud.pubsublite.proto.MessagePublishResponse.Builder.class);
  }

  public static final int START_CURSOR_FIELD_NUMBER = 1;
  private com.google.cloud.pubsublite.proto.Cursor startCursor_;
  /**
   *
   *
   * <pre>
   * The cursor of the first published message in the batch. The cursors for any
   * remaining messages in the batch are guaranteed to be sequential.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
   *
   * @return Whether the startCursor field is set.
   */
  public boolean hasStartCursor() {
    return startCursor_ != null;
  }
  /**
   *
   *
   * <pre>
   * The cursor of the first published message in the batch. The cursors for any
   * remaining messages in the batch are guaranteed to be sequential.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
   *
   * @return The startCursor.
   */
  public com.google.cloud.pubsublite.proto.Cursor getStartCursor() {
    return startCursor_ == null
        ? com.google.cloud.pubsublite.proto.Cursor.getDefaultInstance()
        : startCursor_;
  }
  /**
   *
   *
   * <pre>
   * The cursor of the first published message in the batch. The cursors for any
   * remaining messages in the batch are guaranteed to be sequential.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
   */
  public com.google.cloud.pubsublite.proto.CursorOrBuilder getStartCursorOrBuilder() {
    return getStartCursor();
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
    if (startCursor_ != null) {
      output.writeMessage(1, getStartCursor());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (startCursor_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getStartCursor());
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
    if (!(obj instanceof com.google.cloud.pubsublite.proto.MessagePublishResponse)) {
      return super.equals(obj);
    }
    com.google.cloud.pubsublite.proto.MessagePublishResponse other =
        (com.google.cloud.pubsublite.proto.MessagePublishResponse) obj;

    if (hasStartCursor() != other.hasStartCursor()) return false;
    if (hasStartCursor()) {
      if (!getStartCursor().equals(other.getStartCursor())) return false;
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
    if (hasStartCursor()) {
      hash = (37 * hash) + START_CURSOR_FIELD_NUMBER;
      hash = (53 * hash) + getStartCursor().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse parseFrom(
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
      com.google.cloud.pubsublite.proto.MessagePublishResponse prototype) {
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
   * Response to a MessagePublishRequest.
   * </pre>
   *
   * Protobuf type {@code google.cloud.pubsublite.v1.MessagePublishResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.pubsublite.v1.MessagePublishResponse)
      com.google.cloud.pubsublite.proto.MessagePublishResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.pubsublite.proto.MessagePublishResponse.class,
              com.google.cloud.pubsublite.proto.MessagePublishResponse.Builder.class);
    }

    // Construct using com.google.cloud.pubsublite.proto.MessagePublishResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {}
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (startCursorBuilder_ == null) {
        startCursor_ = null;
      } else {
        startCursor_ = null;
        startCursorBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishResponse getDefaultInstanceForType() {
      return com.google.cloud.pubsublite.proto.MessagePublishResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishResponse build() {
      com.google.cloud.pubsublite.proto.MessagePublishResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishResponse buildPartial() {
      com.google.cloud.pubsublite.proto.MessagePublishResponse result =
          new com.google.cloud.pubsublite.proto.MessagePublishResponse(this);
      if (startCursorBuilder_ == null) {
        result.startCursor_ = startCursor_;
      } else {
        result.startCursor_ = startCursorBuilder_.build();
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
      if (other instanceof com.google.cloud.pubsublite.proto.MessagePublishResponse) {
        return mergeFrom((com.google.cloud.pubsublite.proto.MessagePublishResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.pubsublite.proto.MessagePublishResponse other) {
      if (other == com.google.cloud.pubsublite.proto.MessagePublishResponse.getDefaultInstance())
        return this;
      if (other.hasStartCursor()) {
        mergeStartCursor(other.getStartCursor());
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
      com.google.cloud.pubsublite.proto.MessagePublishResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage =
            (com.google.cloud.pubsublite.proto.MessagePublishResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.cloud.pubsublite.proto.Cursor startCursor_;
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.pubsublite.proto.Cursor,
            com.google.cloud.pubsublite.proto.Cursor.Builder,
            com.google.cloud.pubsublite.proto.CursorOrBuilder>
        startCursorBuilder_;
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     *
     * @return Whether the startCursor field is set.
     */
    public boolean hasStartCursor() {
      return startCursorBuilder_ != null || startCursor_ != null;
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     *
     * @return The startCursor.
     */
    public com.google.cloud.pubsublite.proto.Cursor getStartCursor() {
      if (startCursorBuilder_ == null) {
        return startCursor_ == null
            ? com.google.cloud.pubsublite.proto.Cursor.getDefaultInstance()
            : startCursor_;
      } else {
        return startCursorBuilder_.getMessage();
      }
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public Builder setStartCursor(com.google.cloud.pubsublite.proto.Cursor value) {
      if (startCursorBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        startCursor_ = value;
        onChanged();
      } else {
        startCursorBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public Builder setStartCursor(
        com.google.cloud.pubsublite.proto.Cursor.Builder builderForValue) {
      if (startCursorBuilder_ == null) {
        startCursor_ = builderForValue.build();
        onChanged();
      } else {
        startCursorBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public Builder mergeStartCursor(com.google.cloud.pubsublite.proto.Cursor value) {
      if (startCursorBuilder_ == null) {
        if (startCursor_ != null) {
          startCursor_ =
              com.google.cloud.pubsublite.proto.Cursor.newBuilder(startCursor_)
                  .mergeFrom(value)
                  .buildPartial();
        } else {
          startCursor_ = value;
        }
        onChanged();
      } else {
        startCursorBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public Builder clearStartCursor() {
      if (startCursorBuilder_ == null) {
        startCursor_ = null;
        onChanged();
      } else {
        startCursor_ = null;
        startCursorBuilder_ = null;
      }

      return this;
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.Cursor.Builder getStartCursorBuilder() {

      onChanged();
      return getStartCursorFieldBuilder().getBuilder();
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.CursorOrBuilder getStartCursorOrBuilder() {
      if (startCursorBuilder_ != null) {
        return startCursorBuilder_.getMessageOrBuilder();
      } else {
        return startCursor_ == null
            ? com.google.cloud.pubsublite.proto.Cursor.getDefaultInstance()
            : startCursor_;
      }
    }
    /**
     *
     *
     * <pre>
     * The cursor of the first published message in the batch. The cursors for any
     * remaining messages in the batch are guaranteed to be sequential.
     * </pre>
     *
     * <code>.google.cloud.pubsublite.v1.Cursor start_cursor = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
            com.google.cloud.pubsublite.proto.Cursor,
            com.google.cloud.pubsublite.proto.Cursor.Builder,
            com.google.cloud.pubsublite.proto.CursorOrBuilder>
        getStartCursorFieldBuilder() {
      if (startCursorBuilder_ == null) {
        startCursorBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<
                com.google.cloud.pubsublite.proto.Cursor,
                com.google.cloud.pubsublite.proto.Cursor.Builder,
                com.google.cloud.pubsublite.proto.CursorOrBuilder>(
                getStartCursor(), getParentForChildren(), isClean());
        startCursor_ = null;
      }
      return startCursorBuilder_;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.pubsublite.v1.MessagePublishResponse)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.pubsublite.v1.MessagePublishResponse)
  private static final com.google.cloud.pubsublite.proto.MessagePublishResponse DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.pubsublite.proto.MessagePublishResponse();
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MessagePublishResponse> PARSER =
      new com.google.protobuf.AbstractParser<MessagePublishResponse>() {
        @java.lang.Override
        public MessagePublishResponse parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new MessagePublishResponse(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<MessagePublishResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MessagePublishResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.pubsublite.proto.MessagePublishResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
