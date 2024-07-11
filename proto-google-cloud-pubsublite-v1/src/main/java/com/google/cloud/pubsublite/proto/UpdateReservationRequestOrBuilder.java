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

// Protobuf Java Version: 3.25.3
package com.google.cloud.pubsublite.proto;

public interface UpdateReservationRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.UpdateReservationRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Required. The reservation to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.Reservation reservation = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return Whether the reservation field is set.
   */
  boolean hasReservation();
  /**
   *
   *
   * <pre>
   * Required. The reservation to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.Reservation reservation = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   *
   * @return The reservation.
   */
  com.google.cloud.pubsublite.proto.Reservation getReservation();
  /**
   *
   *
   * <pre>
   * Required. The reservation to update. Its `name` field must be populated.
   * </pre>
   *
   * <code>
   * .google.cloud.pubsublite.v1.Reservation reservation = 1 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.cloud.pubsublite.proto.ReservationOrBuilder getReservationOrBuilder();

  /**
   *
   *
   * <pre>
   * Required. A mask specifying the reservation fields to change.
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
   * Required. A mask specifying the reservation fields to change.
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
   * Required. A mask specifying the reservation fields to change.
   * </pre>
   *
   * <code>.google.protobuf.FieldMask update_mask = 2 [(.google.api.field_behavior) = REQUIRED];
   * </code>
   */
  com.google.protobuf.FieldMaskOrBuilder getUpdateMaskOrBuilder();
}
