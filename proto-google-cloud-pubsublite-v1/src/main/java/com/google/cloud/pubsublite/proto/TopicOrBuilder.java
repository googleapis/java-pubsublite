/*
 * Copyright 2025 Google LLC
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
// source: google/cloud/pubsublite/v1/common.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.pubsublite.proto;

public interface TopicOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.Topic)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The name of the topic.
   * Structured like:
   * projects/{project_number}/locations/{location}/topics/{topic_id}
   * </pre>
   *
   * <code>string name = 1;</code>
   *
   * @return The name.
   */
  java.lang.String getName();

  /**
   *
   *
   * <pre>
   * The name of the topic.
   * Structured like:
   * projects/{project_number}/locations/{location}/topics/{topic_id}
   * </pre>
   *
   * <code>string name = 1;</code>
   *
   * @return The bytes for name.
   */
  com.google.protobuf.ByteString getNameBytes();

  /**
   *
   *
   * <pre>
   * The settings for this topic's partitions.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.PartitionConfig partition_config = 2;</code>
   *
   * @return Whether the partitionConfig field is set.
   */
  boolean hasPartitionConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's partitions.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.PartitionConfig partition_config = 2;</code>
   *
   * @return The partitionConfig.
   */
  com.google.cloud.pubsublite.proto.Topic.PartitionConfig getPartitionConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's partitions.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.PartitionConfig partition_config = 2;</code>
   */
  com.google.cloud.pubsublite.proto.Topic.PartitionConfigOrBuilder getPartitionConfigOrBuilder();

  /**
   *
   *
   * <pre>
   * The settings for this topic's message retention.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.RetentionConfig retention_config = 3;</code>
   *
   * @return Whether the retentionConfig field is set.
   */
  boolean hasRetentionConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's message retention.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.RetentionConfig retention_config = 3;</code>
   *
   * @return The retentionConfig.
   */
  com.google.cloud.pubsublite.proto.Topic.RetentionConfig getRetentionConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's message retention.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.RetentionConfig retention_config = 3;</code>
   */
  com.google.cloud.pubsublite.proto.Topic.RetentionConfigOrBuilder getRetentionConfigOrBuilder();

  /**
   *
   *
   * <pre>
   * The settings for this topic's Reservation usage.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.ReservationConfig reservation_config = 4;</code>
   *
   * @return Whether the reservationConfig field is set.
   */
  boolean hasReservationConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's Reservation usage.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.ReservationConfig reservation_config = 4;</code>
   *
   * @return The reservationConfig.
   */
  com.google.cloud.pubsublite.proto.Topic.ReservationConfig getReservationConfig();

  /**
   *
   *
   * <pre>
   * The settings for this topic's Reservation usage.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Topic.ReservationConfig reservation_config = 4;</code>
   */
  com.google.cloud.pubsublite.proto.Topic.ReservationConfigOrBuilder
      getReservationConfigOrBuilder();
}
