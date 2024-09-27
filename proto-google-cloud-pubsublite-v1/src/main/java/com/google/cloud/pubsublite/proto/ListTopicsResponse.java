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
// source: google/cloud/pubsublite/v1/admin.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.pubsublite.proto;

/**
 *
 *
 * <pre>
 * Response for ListTopics.
 * </pre>
 *
 * Protobuf type {@code google.cloud.pubsublite.v1.ListTopicsResponse}
 */
public final class ListTopicsResponse extends com.google.protobuf.GeneratedMessageV3
    implements
    // @@protoc_insertion_point(message_implements:google.cloud.pubsublite.v1.ListTopicsResponse)
    ListTopicsResponseOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use ListTopicsResponse.newBuilder() to construct.
  private ListTopicsResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }

  private ListTopicsResponse() {
    topics_ = java.util.Collections.emptyList();
    nextPageToken_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
    return new ListTopicsResponse();
  }

  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return com.google.cloud.pubsublite.proto.AdminProto
        .internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.cloud.pubsublite.proto.AdminProto
        .internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.cloud.pubsublite.proto.ListTopicsResponse.class,
            com.google.cloud.pubsublite.proto.ListTopicsResponse.Builder.class);
  }

  public static final int TOPICS_FIELD_NUMBER = 1;

  @SuppressWarnings("serial")
  private java.util.List<com.google.cloud.pubsublite.proto.Topic> topics_;
  /**
   *
   *
   * <pre>
   * The list of topic in the requested parent. The order of the topics is
   * unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
   */
  @java.lang.Override
  public java.util.List<com.google.cloud.pubsublite.proto.Topic> getTopicsList() {
    return topics_;
  }
  /**
   *
   *
   * <pre>
   * The list of topic in the requested parent. The order of the topics is
   * unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.google.cloud.pubsublite.proto.TopicOrBuilder>
      getTopicsOrBuilderList() {
    return topics_;
  }
  /**
   *
   *
   * <pre>
   * The list of topic in the requested parent. The order of the topics is
   * unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
   */
  @java.lang.Override
  public int getTopicsCount() {
    return topics_.size();
  }
  /**
   *
   *
   * <pre>
   * The list of topic in the requested parent. The order of the topics is
   * unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
   */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.Topic getTopics(int index) {
    return topics_.get(index);
  }
  /**
   *
   *
   * <pre>
   * The list of topic in the requested parent. The order of the topics is
   * unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
   */
  @java.lang.Override
  public com.google.cloud.pubsublite.proto.TopicOrBuilder getTopicsOrBuilder(int index) {
    return topics_.get(index);
  }

  public static final int NEXT_PAGE_TOKEN_FIELD_NUMBER = 2;

  @SuppressWarnings("serial")
  private volatile java.lang.Object nextPageToken_ = "";
  /**
   *
   *
   * <pre>
   * A token that can be sent as `page_token` to retrieve the next page of
   * results. If this field is omitted, there are no more results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The nextPageToken.
   */
  @java.lang.Override
  public java.lang.String getNextPageToken() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      nextPageToken_ = s;
      return s;
    }
  }
  /**
   *
   *
   * <pre>
   * A token that can be sent as `page_token` to retrieve the next page of
   * results. If this field is omitted, there are no more results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The bytes for nextPageToken.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getNextPageTokenBytes() {
    java.lang.Object ref = nextPageToken_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      nextPageToken_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    for (int i = 0; i < topics_.size(); i++) {
      output.writeMessage(1, topics_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, nextPageToken_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < topics_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, topics_.get(i));
    }
    if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(nextPageToken_)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, nextPageToken_);
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
    if (!(obj instanceof com.google.cloud.pubsublite.proto.ListTopicsResponse)) {
      return super.equals(obj);
    }
    com.google.cloud.pubsublite.proto.ListTopicsResponse other =
        (com.google.cloud.pubsublite.proto.ListTopicsResponse) obj;

    if (!getTopicsList().equals(other.getTopicsList())) return false;
    if (!getNextPageToken().equals(other.getNextPageToken())) return false;
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
    if (getTopicsCount() > 0) {
      hash = (37 * hash) + TOPICS_FIELD_NUMBER;
      hash = (53 * hash) + getTopicsList().hashCode();
    }
    hash = (37 * hash) + NEXT_PAGE_TOKEN_FIELD_NUMBER;
    hash = (53 * hash) + getNextPageToken().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseDelimitedFrom(
      java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseDelimitedFrom(
      java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(
        PARSER, input, extensionRegistry);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse parseFrom(
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

  public static Builder newBuilder(com.google.cloud.pubsublite.proto.ListTopicsResponse prototype) {
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
   * Response for ListTopics.
   * </pre>
   *
   * Protobuf type {@code google.cloud.pubsublite.v1.ListTopicsResponse}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder>
      implements
      // @@protoc_insertion_point(builder_implements:google.cloud.pubsublite.v1.ListTopicsResponse)
      com.google.cloud.pubsublite.proto.ListTopicsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.cloud.pubsublite.proto.AdminProto
          .internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.cloud.pubsublite.proto.AdminProto
          .internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.cloud.pubsublite.proto.ListTopicsResponse.class,
              com.google.cloud.pubsublite.proto.ListTopicsResponse.Builder.class);
    }

    // Construct using com.google.cloud.pubsublite.proto.ListTopicsResponse.newBuilder()
    private Builder() {}

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }

    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (topicsBuilder_ == null) {
        topics_ = java.util.Collections.emptyList();
      } else {
        topics_ = null;
        topicsBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      nextPageToken_ = "";
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return com.google.cloud.pubsublite.proto.AdminProto
          .internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ListTopicsResponse getDefaultInstanceForType() {
      return com.google.cloud.pubsublite.proto.ListTopicsResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ListTopicsResponse build() {
      com.google.cloud.pubsublite.proto.ListTopicsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.cloud.pubsublite.proto.ListTopicsResponse buildPartial() {
      com.google.cloud.pubsublite.proto.ListTopicsResponse result =
          new com.google.cloud.pubsublite.proto.ListTopicsResponse(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) {
        buildPartial0(result);
      }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(
        com.google.cloud.pubsublite.proto.ListTopicsResponse result) {
      if (topicsBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          topics_ = java.util.Collections.unmodifiableList(topics_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.topics_ = topics_;
      } else {
        result.topics_ = topicsBuilder_.build();
      }
    }

    private void buildPartial0(com.google.cloud.pubsublite.proto.ListTopicsResponse result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.nextPageToken_ = nextPageToken_;
      }
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
      if (other instanceof com.google.cloud.pubsublite.proto.ListTopicsResponse) {
        return mergeFrom((com.google.cloud.pubsublite.proto.ListTopicsResponse) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.cloud.pubsublite.proto.ListTopicsResponse other) {
      if (other == com.google.cloud.pubsublite.proto.ListTopicsResponse.getDefaultInstance())
        return this;
      if (topicsBuilder_ == null) {
        if (!other.topics_.isEmpty()) {
          if (topics_.isEmpty()) {
            topics_ = other.topics_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTopicsIsMutable();
            topics_.addAll(other.topics_);
          }
          onChanged();
        }
      } else {
        if (!other.topics_.isEmpty()) {
          if (topicsBuilder_.isEmpty()) {
            topicsBuilder_.dispose();
            topicsBuilder_ = null;
            topics_ = other.topics_;
            bitField0_ = (bitField0_ & ~0x00000001);
            topicsBuilder_ =
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders
                    ? getTopicsFieldBuilder()
                    : null;
          } else {
            topicsBuilder_.addAllMessages(other.topics_);
          }
        }
      }
      if (!other.getNextPageToken().isEmpty()) {
        nextPageToken_ = other.nextPageToken_;
        bitField0_ |= 0x00000002;
        onChanged();
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
                com.google.cloud.pubsublite.proto.Topic m =
                    input.readMessage(
                        com.google.cloud.pubsublite.proto.Topic.parser(), extensionRegistry);
                if (topicsBuilder_ == null) {
                  ensureTopicsIsMutable();
                  topics_.add(m);
                } else {
                  topicsBuilder_.addMessage(m);
                }
                break;
              } // case 10
            case 18:
              {
                nextPageToken_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
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

    private java.util.List<com.google.cloud.pubsublite.proto.Topic> topics_ =
        java.util.Collections.emptyList();

    private void ensureTopicsIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        topics_ = new java.util.ArrayList<com.google.cloud.pubsublite.proto.Topic>(topics_);
        bitField0_ |= 0x00000001;
      }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.cloud.pubsublite.proto.Topic,
            com.google.cloud.pubsublite.proto.Topic.Builder,
            com.google.cloud.pubsublite.proto.TopicOrBuilder>
        topicsBuilder_;

    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public java.util.List<com.google.cloud.pubsublite.proto.Topic> getTopicsList() {
      if (topicsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(topics_);
      } else {
        return topicsBuilder_.getMessageList();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public int getTopicsCount() {
      if (topicsBuilder_ == null) {
        return topics_.size();
      } else {
        return topicsBuilder_.getCount();
      }
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.Topic getTopics(int index) {
      if (topicsBuilder_ == null) {
        return topics_.get(index);
      } else {
        return topicsBuilder_.getMessage(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder setTopics(int index, com.google.cloud.pubsublite.proto.Topic value) {
      if (topicsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTopicsIsMutable();
        topics_.set(index, value);
        onChanged();
      } else {
        topicsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder setTopics(
        int index, com.google.cloud.pubsublite.proto.Topic.Builder builderForValue) {
      if (topicsBuilder_ == null) {
        ensureTopicsIsMutable();
        topics_.set(index, builderForValue.build());
        onChanged();
      } else {
        topicsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder addTopics(com.google.cloud.pubsublite.proto.Topic value) {
      if (topicsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTopicsIsMutable();
        topics_.add(value);
        onChanged();
      } else {
        topicsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder addTopics(int index, com.google.cloud.pubsublite.proto.Topic value) {
      if (topicsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureTopicsIsMutable();
        topics_.add(index, value);
        onChanged();
      } else {
        topicsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder addTopics(com.google.cloud.pubsublite.proto.Topic.Builder builderForValue) {
      if (topicsBuilder_ == null) {
        ensureTopicsIsMutable();
        topics_.add(builderForValue.build());
        onChanged();
      } else {
        topicsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder addTopics(
        int index, com.google.cloud.pubsublite.proto.Topic.Builder builderForValue) {
      if (topicsBuilder_ == null) {
        ensureTopicsIsMutable();
        topics_.add(index, builderForValue.build());
        onChanged();
      } else {
        topicsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder addAllTopics(
        java.lang.Iterable<? extends com.google.cloud.pubsublite.proto.Topic> values) {
      if (topicsBuilder_ == null) {
        ensureTopicsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, topics_);
        onChanged();
      } else {
        topicsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder clearTopics() {
      if (topicsBuilder_ == null) {
        topics_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        topicsBuilder_.clear();
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public Builder removeTopics(int index) {
      if (topicsBuilder_ == null) {
        ensureTopicsIsMutable();
        topics_.remove(index);
        onChanged();
      } else {
        topicsBuilder_.remove(index);
      }
      return this;
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.Topic.Builder getTopicsBuilder(int index) {
      return getTopicsFieldBuilder().getBuilder(index);
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.TopicOrBuilder getTopicsOrBuilder(int index) {
      if (topicsBuilder_ == null) {
        return topics_.get(index);
      } else {
        return topicsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public java.util.List<? extends com.google.cloud.pubsublite.proto.TopicOrBuilder>
        getTopicsOrBuilderList() {
      if (topicsBuilder_ != null) {
        return topicsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(topics_);
      }
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.Topic.Builder addTopicsBuilder() {
      return getTopicsFieldBuilder()
          .addBuilder(com.google.cloud.pubsublite.proto.Topic.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public com.google.cloud.pubsublite.proto.Topic.Builder addTopicsBuilder(int index) {
      return getTopicsFieldBuilder()
          .addBuilder(index, com.google.cloud.pubsublite.proto.Topic.getDefaultInstance());
    }
    /**
     *
     *
     * <pre>
     * The list of topic in the requested parent. The order of the topics is
     * unspecified.
     * </pre>
     *
     * <code>repeated .google.cloud.pubsublite.v1.Topic topics = 1;</code>
     */
    public java.util.List<com.google.cloud.pubsublite.proto.Topic.Builder> getTopicsBuilderList() {
      return getTopicsFieldBuilder().getBuilderList();
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
            com.google.cloud.pubsublite.proto.Topic,
            com.google.cloud.pubsublite.proto.Topic.Builder,
            com.google.cloud.pubsublite.proto.TopicOrBuilder>
        getTopicsFieldBuilder() {
      if (topicsBuilder_ == null) {
        topicsBuilder_ =
            new com.google.protobuf.RepeatedFieldBuilderV3<
                com.google.cloud.pubsublite.proto.Topic,
                com.google.cloud.pubsublite.proto.Topic.Builder,
                com.google.cloud.pubsublite.proto.TopicOrBuilder>(
                topics_, ((bitField0_ & 0x00000001) != 0), getParentForChildren(), isClean());
        topics_ = null;
      }
      return topicsBuilder_;
    }

    private java.lang.Object nextPageToken_ = "";
    /**
     *
     *
     * <pre>
     * A token that can be sent as `page_token` to retrieve the next page of
     * results. If this field is omitted, there are no more results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return The nextPageToken.
     */
    public java.lang.String getNextPageToken() {
      java.lang.Object ref = nextPageToken_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        nextPageToken_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * A token that can be sent as `page_token` to retrieve the next page of
     * results. If this field is omitted, there are no more results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return The bytes for nextPageToken.
     */
    public com.google.protobuf.ByteString getNextPageTokenBytes() {
      java.lang.Object ref = nextPageToken_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        nextPageToken_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     *
     *
     * <pre>
     * A token that can be sent as `page_token` to retrieve the next page of
     * results. If this field is omitted, there are no more results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @param value The nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageToken(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }
      nextPageToken_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A token that can be sent as `page_token` to retrieve the next page of
     * results. If this field is omitted, there are no more results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @return This builder for chaining.
     */
    public Builder clearNextPageToken() {
      nextPageToken_ = getDefaultInstance().getNextPageToken();
      bitField0_ = (bitField0_ & ~0x00000002);
      onChanged();
      return this;
    }
    /**
     *
     *
     * <pre>
     * A token that can be sent as `page_token` to retrieve the next page of
     * results. If this field is omitted, there are no more results.
     * </pre>
     *
     * <code>string next_page_token = 2;</code>
     *
     * @param value The bytes for nextPageToken to set.
     * @return This builder for chaining.
     */
    public Builder setNextPageTokenBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);
      nextPageToken_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
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

    // @@protoc_insertion_point(builder_scope:google.cloud.pubsublite.v1.ListTopicsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.cloud.pubsublite.v1.ListTopicsResponse)
  private static final com.google.cloud.pubsublite.proto.ListTopicsResponse DEFAULT_INSTANCE;

  static {
    DEFAULT_INSTANCE = new com.google.cloud.pubsublite.proto.ListTopicsResponse();
  }

  public static com.google.cloud.pubsublite.proto.ListTopicsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ListTopicsResponse> PARSER =
      new com.google.protobuf.AbstractParser<ListTopicsResponse>() {
        @java.lang.Override
        public ListTopicsResponse parsePartialFrom(
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

  public static com.google.protobuf.Parser<ListTopicsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ListTopicsResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.cloud.pubsublite.proto.ListTopicsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
