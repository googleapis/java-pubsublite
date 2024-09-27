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
// source: google/cloud/pubsublite/v1/cursor.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.pubsublite.proto;

public interface SequencedCommitCursorResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.SequencedCommitCursorResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The number of outstanding SequencedCommitCursorRequests acknowledged by
   * this response. Note that SequencedCommitCursorRequests are acknowledged in
   * the order that they are received.
   * </pre>
   *
   * <code>int64 acknowledged_commits = 1;</code>
   *
   * @return The acknowledgedCommits.
   */
  long getAcknowledgedCommits();
}
