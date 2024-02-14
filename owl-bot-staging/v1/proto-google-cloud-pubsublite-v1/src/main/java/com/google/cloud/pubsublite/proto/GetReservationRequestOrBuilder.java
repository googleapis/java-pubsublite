// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/admin.proto

// Protobuf Java Version: 3.25.2
package com.google.cloud.pubsublite.proto;

public interface GetReservationRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.GetReservationRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. The name of the reservation whose configuration to return.
   * Structured like:
   * projects/{project_number}/locations/{location}/reservations/{reservation_id}
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The name.
   */
  java.lang.String getName();
  /**
   * <pre>
   * Required. The name of the reservation whose configuration to return.
   * Structured like:
   * projects/{project_number}/locations/{location}/reservations/{reservation_id}
   * </pre>
   *
   * <code>string name = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString
      getNameBytes();
}
