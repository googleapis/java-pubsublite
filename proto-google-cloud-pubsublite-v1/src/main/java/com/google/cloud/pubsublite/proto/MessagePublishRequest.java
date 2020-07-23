/*
 * Copyright 2020 Google LLC
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
// source: google/cloud/pubsublite/v1/publisher.proto

package com.google.cloud.pubsublite.proto;

/**
 *
 *
 * <pre>
 * Request to publish messages to the topic.
 * </pre>
 *
 * Protobuf type {@code google.cloud.pubsublite.v1.MessagePublishRequest}
 */
public final class MessagePublishRequest extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.pubsublite.v1.MessagePublishRequest)
    MessagePublishRequestOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use MessagePublishRequest.newBuilder() to construct.
  private MessagePublishRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private MessagePublishRequest() {
    messages_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new MessagePublishRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }

  private MessagePublishRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                messages_ =
                    new java.util.ArrayList<com.google.cloud.pubsublite.proto.PubSubMessage>();
                mutable_bitField0_ |= 0x00000001;
              }
              messages_.add(
                  input.readMessage(
                      com.google.cloud.pubsublite.proto.PubSubMessage.parser(), extensionRegistry));
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
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        messages_ = java.util.Collections.unmodifiableList(messages_);
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.pubsublite.proto.PublisherProto
        .internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.pubsublite.proto.PublisherProto
        .internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.pubsublite.proto.MessagePublishRequest.class,
            com.google.cloud.pubsublite.proto.MessagePublishRequest.Builder.class);
  }

  public static final int MESSAGES_FIELD_NUMBER = 1;
  private java.util.List<com.google.cloud.pubsublite.proto.PubSubMessage> messages_;
  /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
  @java.lang.Override
  public java.util.List<com.google.cloud.pubsublite.proto.PubSubMessage> getMessagesList() {
    return messages_;
  }
  /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
  @java.lang.Override
  public java.util.List<? extends com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder>
      getMessagesOrBuilderList() {
    return messages_;
  }
  /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
  @java.lang.Override
  public int getMessagesCount() {
    return messages_.size();
  }
  /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.PubSubMessage getMessages(int index) {
    return messages_.get(index);
  }
  /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder getMessagesOrBuilder(int index) {
    return messages_.get(index);
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
    for (int i = 0; i < messages_.size(); i++) {
      output.writeMessage(1, messages_.get(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < messages_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, messages_.get(i));
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
    if (!(obj instanceof com.google.cloud.pubsublite.proto.MessagePublishRequest)) {
      return super.equals(obj);
    }
    com.google.cloud.pubsublite.proto.MessagePublishRequest other =
        (com.google.cloud.pubsublite.proto.MessagePublishRequest) obj;

    if (!getMessagesList().equals(other.getMessagesList())) return false;
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
    if (getMessagesCount() > 0) {
      hash = (37 * hash) + MESSAGES_FIELD_NUMBER;
      hash = (53 * hash) + getMessagesList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest parseFrom(
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
      com.google.cloud.pubsublite.proto.MessagePublishRequest prototype) {
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
   * Request to publish messages to the topic.
   * </pre>
   *
   * Protobuf type {@code google.cloud.pubsublite.v1.MessagePublishRequest}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.pubsublite.v1.MessagePublishRequest)
      com.google.cloud.pubsublite.proto.MessagePublishRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.pubsublite.proto.MessagePublishRequest.class,
              com.google.cloud.pubsublite.proto.MessagePublishRequest.Builder.class);
    }

    // Construct using com.google.cloud.pubsublite.proto.MessagePublishRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        getMessagesFieldBuilder();
      }
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (messagesBuilder_ == null) {
        messages_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        messagesBuilder_.clear();
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.pubsublite.proto.PublisherProto
          .internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishRequest getDefaultInstanceForType() {
      return com.google.cloud.pubsublite.proto.MessagePublishRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishRequest build() {
      com.google.cloud.pubsublite.proto.MessagePublishRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.MessagePublishRequest buildPartial() {
      com.google.cloud.pubsublite.proto.MessagePublishRequest result =
          new com.google.cloud.pubsublite.proto.MessagePublishRequest(this);
      int from_bitField0_ = bitField0_;
      if (messagesBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          messages_ = java.util.Collections.unmodifiableList(messages_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.messages_ = messages_;
      } else {
        result.messages_ = messagesBuilder_.build();
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
      if (other instanceof com.google.cloud.pubsublite.proto.MessagePublishRequest) {
        return mergeFrom((com.google.cloud.pubsublite.proto.MessagePublishRequest) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.pubsublite.proto.MessagePublishRequest other) {
      if (other == com.google.cloud.pubsublite.proto.MessagePublishRequest.getDefaultInstance())
        return this;
      if (messagesBuilder_ == null) {
        if (!other.messages_.isEmpty()) {
          if (messages_.isEmpty()) {
            messages_ = other.messages_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureMessagesIsMutable();
            messages_.addAll(other.messages_);
          }
          onChanged();
        }
      } else {
        if (!other.messages_.isEmpty()) {
          if (messagesBuilder_.isEmpty()) {
            messagesBuilder_.dispose();
            messagesBuilder_ = null;
            messages_ = other.messages_;
            bitField0_ = (bitField0_ & ~0x00000001);
            messagesBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders
                    ? getMessagesFieldBuilder()
                    : null;
          } else {
            messagesBuilder_.addAllMessages(other.messages_);
          }
        }
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
      com.google.cloud.pubsublite.proto.MessagePublishRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage =
            (com.google.cloud.pubsublite.proto.MessagePublishRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int bitField0_;

    private java.util.List<com.google.cloud.pubsublite.proto.PubSubMessage> messages_ =
        java.util.Collections.emptyList();

    private void ensureMessagesIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        messages_ =
            new java.util.ArrayList<com.google.cloud.pubsublite.proto.PubSubMessage>(messages_);
        bitField0_ |= 0x00000001;
      }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.cloud.pubsublite.proto.PubSubMessage,
            com.google.cloud.pubsublite.proto.PubSubMessage.Builder,
            com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder>
        messagesBuilder_;

    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public java.util.List<com.google.cloud.pubsublite.proto.PubSubMessage> getMessagesList() {
      if (messagesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(messages_);
      } else {
        return messagesBuilder_.getMessageList();
      }
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public int getMessagesCount() {
      if (messagesBuilder_ == null) {
        return messages_.size();
      } else {
        return messagesBuilder_.getCount();
      }
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public com.google.cloud.pubsublite.proto.PubSubMessage getMessages(int index) {
      if (messagesBuilder_ == null) {
        return messages_.get(index);
      } else {
        return messagesBuilder_.getMessage(index);
      }
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder setMessages(int index, com.google.cloud.pubsublite.proto.PubSubMessage value) {
      if (messagesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessagesIsMutable();
        messages_.set(index, value);
        onChanged();
      } else {
        messagesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder setMessages(
        int index, com.google.cloud.pubsublite.proto.PubSubMessage.Builder builderForValue) {
      if (messagesBuilder_ == null) {
        ensureMessagesIsMutable();
        messages_.set(index, builderForValue.build());
        onChanged();
      } else {
        messagesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder addMessages(com.google.cloud.pubsublite.proto.PubSubMessage value) {
      if (messagesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessagesIsMutable();
        messages_.add(value);
        onChanged();
      } else {
        messagesBuilder_.addMessage(value);
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder addMessages(int index, com.google.cloud.pubsublite.proto.PubSubMessage value) {
      if (messagesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureMessagesIsMutable();
        messages_.add(index, value);
        onChanged();
      } else {
        messagesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder addMessages(
        com.google.cloud.pubsublite.proto.PubSubMessage.Builder builderForValue) {
      if (messagesBuilder_ == null) {
        ensureMessagesIsMutable();
        messages_.add(builderForValue.build());
        onChanged();
      } else {
        messagesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder addMessages(
        int index, com.google.cloud.pubsublite.proto.PubSubMessage.Builder builderForValue) {
      if (messagesBuilder_ == null) {
        ensureMessagesIsMutable();
        messages_.add(index, builderForValue.build());
        onChanged();
      } else {
        messagesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder addAllMessages(
        java.lang.Iterable<? extends com.google.cloud.pubsublite.proto.PubSubMessage> values) {
      if (messagesBuilder_ == null) {
        ensureMessagesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, messages_);
        onChanged();
      } else {
        messagesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder clearMessages() {
      if (messagesBuilder_ == null) {
        messages_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        messagesBuilder_.clear();
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public Builder removeMessages(int index) {
      if (messagesBuilder_ == null) {
        ensureMessagesIsMutable();
        messages_.remove(index);
        onChanged();
      } else {
        messagesBuilder_.remove(index);
      }
      return this;
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public com.google.cloud.pubsublite.proto.PubSubMessage.Builder getMessagesBuilder(int index) {
      return getMessagesFieldBuilder().getBuilder(index);
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder getMessagesOrBuilder(
        int index) {
      if (messagesBuilder_ == null) {
        return messages_.get(index);
      } else {
        return messagesBuilder_.getMessageOrBuilder(index);
      }
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public java.util.List<? extends com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder>
        getMessagesOrBuilderList() {
      if (messagesBuilder_ != null) {
        return messagesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(messages_);
      }
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public com.google.cloud.pubsublite.proto.PubSubMessage.Builder addMessagesBuilder() {
      return getMessagesFieldBuilder()
          .addBuilder(com.google.cloud.pubsublite.proto.PubSubMessage.getDefaultInstance());
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public com.google.cloud.pubsublite.proto.PubSubMessage.Builder addMessagesBuilder(int index) {
      return getMessagesFieldBuilder()
          .addBuilder(index, com.google.cloud.pubsublite.proto.PubSubMessage.getDefaultInstance());
    }
    /** <code>repeated .google.cloud.pubsublite.v1.PubSubMessage messages = 1;</code> */
    public java.util.List<com.google.cloud.pubsublite.proto.PubSubMessage.Builder>
        getMessagesBuilderList() {
      return getMessagesFieldBuilder().getBuilderList();
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.cloud.pubsublite.proto.PubSubMessage,
            com.google.cloud.pubsublite.proto.PubSubMessage.Builder,
            com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder>
        getMessagesFieldBuilder() {
      if (messagesBuilder_ == null) {
        messagesBuilder_ =
            new com.google.protobuf.RepeatedFieldBuilderV3<
                com.google.cloud.pubsublite.proto.PubSubMessage,
                com.google.cloud.pubsublite.proto.PubSubMessage.Builder,
                com.google.cloud.pubsublite.proto.PubSubMessageOrBuilder>(
                messages_, ((bitField0_ & 0x00000001) != 0), getParentForChildren(), isClean());
        messages_ = null;
      }
      return messagesBuilder_;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.pubsublite.v1.MessagePublishRequest)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.pubsublite.v1.MessagePublishRequest)
  private static final com.google.cloud.pubsublite.proto.MessagePublishRequest DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.pubsublite.proto.MessagePublishRequest();
  }

  public static com.google.cloud.pubsublite.proto.MessagePublishRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<MessagePublishRequest> PARSER =
      new com.google.protobuf.AbstractParser<MessagePublishRequest>() {
        @java.lang.Override
        public MessagePublishRequest parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new MessagePublishRequest(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<MessagePublishRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<MessagePublishRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.pubsublite.proto.MessagePublishRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
