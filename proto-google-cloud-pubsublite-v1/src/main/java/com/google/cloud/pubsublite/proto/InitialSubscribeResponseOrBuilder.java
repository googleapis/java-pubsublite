// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/subscriber.proto

package com.google.cloud.pubsublite.proto;

public interface InitialSubscribeResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.InitialSubscribeResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The cursor from which the subscriber will start receiving messages once
   * flow control tokens become available.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor cursor = 1;</code>
   *
   * @return Whether the cursor field is set.
   */
  boolean hasCursor();
  /**
   *
   *
   * <pre>
   * The cursor from which the subscriber will start receiving messages once
   * flow control tokens become available.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor cursor = 1;</code>
   *
   * @return The cursor.
   */
  com.google.cloud.pubsublite.proto.Cursor getCursor();
  /**
   *
   *
   * <pre>
   * The cursor from which the subscriber will start receiving messages once
   * flow control tokens become available.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Cursor cursor = 1;</code>
   */
  com.google.cloud.pubsublite.proto.CursorOrBuilder getCursorOrBuilder();
}
