// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/admin.proto

package com.google.cloud.pubsublite.proto;

public interface UpdateTopicRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.UpdateTopicRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The topic to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic topic = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the topic field is set.
   */
  boolean hasTopic();
  /**
   *
   *
   * <pre>
   * The topic to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic topic = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The topic.
   */
  com.google.cloud.pubsublite.proto.Topic getTopic();
  /**
   *
   *
   * <pre>
   * The topic to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic topic = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.cloud.pubsublite.proto.TopicOrBuilder getTopicOrBuilder();

  /**
   *
   *
   * <pre>
   * A mask specifying the topic fields to change.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the updateMask field is set.
   */
  boolean hasUpdateMask();
  /**
   *
   *
   * <pre>
   * A mask specifying the topic fields to change.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The updateMask.
   */
  com.google.protobuf.FieldMask getUpdateMask();
  /**
   *
   *
   * <pre>
   * A mask specifying the topic fields to change.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.protobuf.FieldMaskOrBuilder getUpdateMaskOrBuilder();
}
