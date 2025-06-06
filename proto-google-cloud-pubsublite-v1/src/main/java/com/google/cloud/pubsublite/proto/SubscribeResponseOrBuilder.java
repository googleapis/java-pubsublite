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

public interface SubscribeResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.SubscribeResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Initial response on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialSubscribeResponse initial = 1;</code>
   *
   * @return Whether the initial field is set.
   */
  boolean hasInitial();

  /**
   *
   *
   * <pre>
   * Initial response on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialSubscribeResponse initial = 1;</code>
   *
   * @return The initial.
   */
  com.google.cloud.pubsublite.proto.InitialSubscribeResponse getInitial();

  /**
   *
   *
   * <pre>
   * Initial response on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialSubscribeResponse initial = 1;</code>
   */
  com.google.cloud.pubsublite.proto.InitialSubscribeResponseOrBuilder getInitialOrBuilder();

  /**
   *
   *
   * <pre>
   * Response to a Seek operation.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekResponse seek = 2;</code>
   *
   * @return Whether the seek field is set.
   */
  boolean hasSeek();

  /**
   *
   *
   * <pre>
   * Response to a Seek operation.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekResponse seek = 2;</code>
   *
   * @return The seek.
   */
  com.google.cloud.pubsublite.proto.SeekResponse getSeek();

  /**
   *
   *
   * <pre>
   * Response to a Seek operation.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.SeekResponse seek = 2;</code>
   */
  com.google.cloud.pubsublite.proto.SeekResponseOrBuilder getSeekOrBuilder();

  /**
   *
   *
   * <pre>
   * Response containing messages from the topic partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessageResponse messages = 3;</code>
   *
   * @return Whether the messages field is set.
   */
  boolean hasMessages();

  /**
   *
   *
   * <pre>
   * Response containing messages from the topic partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessageResponse messages = 3;</code>
   *
   * @return The messages.
   */
  com.google.cloud.pubsublite.proto.MessageResponse getMessages();

  /**
   *
   *
   * <pre>
   * Response containing messages from the topic partition.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessageResponse messages = 3;</code>
   */
  com.google.cloud.pubsublite.proto.MessageResponseOrBuilder getMessagesOrBuilder();

  com.google.cloud.pubsublite.proto.SubscribeResponse.ResponseCase getResponseCase();
}
