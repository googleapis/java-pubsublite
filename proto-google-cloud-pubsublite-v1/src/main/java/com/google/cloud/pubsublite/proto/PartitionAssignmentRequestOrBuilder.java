// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/subscriber.proto

package com.google.cloud.pubsublite.proto;

public interface PartitionAssignmentRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.PartitionAssignmentRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPartitionAssignmentRequest initial = 1;</code>
   * @return Whether the initial field is set.
   */
  boolean hasInitial();
  /**
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPartitionAssignmentRequest initial = 1;</code>
   * @return The initial.
   */
  com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest getInitial();
  /**
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPartitionAssignmentRequest initial = 1;</code>
   */
  com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequestOrBuilder getInitialOrBuilder();

  /**
   * <pre>
   * Acknowledgement of a partition assignment.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.PartitionAssignmentAck ack = 2;</code>
   * @return Whether the ack field is set.
   */
  boolean hasAck();
  /**
   * <pre>
   * Acknowledgement of a partition assignment.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.PartitionAssignmentAck ack = 2;</code>
   * @return The ack.
   */
  com.google.cloud.pubsublite.proto.PartitionAssignmentAck getAck();
  /**
   * <pre>
   * Acknowledgement of a partition assignment.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.PartitionAssignmentAck ack = 2;</code>
   */
  com.google.cloud.pubsublite.proto.PartitionAssignmentAckOrBuilder getAckOrBuilder();

  public com.google.cloud.pubsublite.proto.PartitionAssignmentRequest.RequestCase getRequestCase();
}
