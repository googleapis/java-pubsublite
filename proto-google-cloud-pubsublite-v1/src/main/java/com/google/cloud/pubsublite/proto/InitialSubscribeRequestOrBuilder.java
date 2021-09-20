// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/subscriber.proto

package com.google.cloud.pubsublite.proto;

public interface InitialSubscribeRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.InitialSubscribeRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The subscription from which to receive messages.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   * @return The subscription.
   */
  java.lang.String getSubscription();
  /**
   * <pre>
   * The subscription from which to receive messages.
   * </pre>
   *
   * <code>string subscription = 1;</code>
   * @return The bytes for subscription.
   */
  com.google.protobuf.ByteString
      getSubscriptionBytes();

  /**
   * <pre>
   * The partition from which to receive messages. Partitions are zero indexed,
   * so `partition` must be in the range [0, topic.num_partitions).
   * </pre>
   *
   * <code>int64 partition = 2;</code>
   * @return The partition.
   */
  long getPartition();

  /**
   * <pre>
   * Optional. Initial target location within the message backlog. If not set, messages
   * will be delivered from the commit cursor for the given subscription and
   * partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];</code>
   * @return Whether the initialLocation field is set.
   */
  boolean hasInitialLocation();
  /**
   * <pre>
   * Optional. Initial target location within the message backlog. If not set, messages
   * will be delivered from the commit cursor for the given subscription and
   * partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];</code>
   * @return The initialLocation.
   */
  com.google.cloud.pubsublite.proto.SeekRequest getInitialLocation();
  /**
   * <pre>
   * Optional. Initial target location within the message backlog. If not set, messages
   * will be delivered from the commit cursor for the given subscription and
   * partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekRequest initial_location = 4 [(.google.api.field_behavior) = OPTIONAL];</code>
   */
  com.google.cloud.pubsublite.proto.SeekRequestOrBuilder getInitialLocationOrBuilder();
}
