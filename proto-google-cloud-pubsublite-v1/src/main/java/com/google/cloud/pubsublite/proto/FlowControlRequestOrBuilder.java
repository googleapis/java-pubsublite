// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/subscriber.proto

package com.google.cloud.pubsublite.proto;

public interface FlowControlRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.FlowControlRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The number of message tokens to grant. Must be greater than or equal to 0.
   * </pre>
   *
   * <code>int64 allowed_messages = 1;</code>
   *
   * @return The allowedMessages.
   */
  long getAllowedMessages();

  /**
   *
   *
   * <pre>
   * The number of byte tokens to grant. Must be greater than or equal to 0.
   * </pre>
   *
   * <code>int64 allowed_bytes = 2;</code>
   *
   * @return The allowedBytes.
   */
  long getAllowedBytes();
}
