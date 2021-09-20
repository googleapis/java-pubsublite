// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/admin.proto

package com.google.cloud.pubsublite.proto;

public interface ListReservationsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.ListReservationsRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Required. The parent whose reservations are to be listed.
   * Structured like `projects/{project_number}/locations/{location}`.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The parent.
   */
  java.lang.String getParent();
  /**
   * <pre>
   * Required. The parent whose reservations are to be listed.
   * Structured like `projects/{project_number}/locations/{location}`.
   * </pre>
   *
   * <code>string parent = 1 [(.google.api.field_behavior) = REQUIRED, (.google.api.resource_reference) = { ... }</code>
   * @return The bytes for parent.
   */
  com.google.protobuf.ByteString
      getParentBytes();

  /**
   * <pre>
   * The maximum number of reservations to return. The service may return fewer
   * than this value. If unset or zero, all reservations for the parent will be
   * returned.
   * </pre>
   *
   * <code>int32 page_size = 2;</code>
   * @return The pageSize.
   */
  int getPageSize();

  /**
   * <pre>
   * A page token, received from a previous `ListReservations` call.
   * Provide this to retrieve the subsequent page.
   * When paginating, all other parameters provided to `ListReservations` must
   * match the call that provided the page token.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   * @return The pageToken.
   */
  java.lang.String getPageToken();
  /**
   * <pre>
   * A page token, received from a previous `ListReservations` call.
   * Provide this to retrieve the subsequent page.
   * When paginating, all other parameters provided to `ListReservations` must
   * match the call that provided the page token.
   * </pre>
   *
   * <code>string page_token = 3;</code>
   * @return The bytes for pageToken.
   */
  com.google.protobuf.ByteString
      getPageTokenBytes();
}
