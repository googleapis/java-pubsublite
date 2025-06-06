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
// source: google/cloud/pubsublite/v1/subscriber.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.pubsublite.proto;

public interface PartitionAssignmentOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.PartitionAssignment)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The list of partition numbers this subscriber is assigned to.
   * </pre>
   *
   * <code>repeated int64 partitions = 1;</code>
   *
   * @return A list containing the partitions.
   */
  java.util.List<java.lang.Long> getPartitionsList();

  /**
   *
   *
   * <pre>
   * The list of partition numbers this subscriber is assigned to.
   * </pre>
   *
   * <code>repeated int64 partitions = 1;</code>
   *
   * @return The count of partitions.
   */
  int getPartitionsCount();

  /**
   *
   *
   * <pre>
   * The list of partition numbers this subscriber is assigned to.
   * </pre>
   *
   * <code>repeated int64 partitions = 1;</code>
   *
   * @param index The index of the element to return.
   * @return The partitions at the given index.
   */
  long getPartitions(int index);
}
