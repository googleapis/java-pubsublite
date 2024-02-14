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
// source: google/cloud/pubsublite/v1/publisher.proto

package com.google.cloud.pubsublite.proto;

public interface PublishRequestOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.PublishRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPublishRequest initial_request = 1;</code>
   *
   * @return Whether the initialRequest field is set.
   */
  boolean hasInitialRequest();
  /**
   *
   *
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPublishRequest initial_request = 1;</code>
   *
   * @return The initialRequest.
   */
  com.google.cloud.pubsublite.proto.InitialPublishRequest getInitialRequest();
  /**
   *
   *
   * <pre>
   * Initial request on the stream.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.InitialPublishRequest initial_request = 1;</code>
   */
  com.google.cloud.pubsublite.proto.InitialPublishRequestOrBuilder getInitialRequestOrBuilder();

  /**
   *
   *
   * <pre>
   * Request to publish messages.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessagePublishRequest message_publish_request = 2;</code>
   *
   * @return Whether the messagePublishRequest field is set.
   */
  boolean hasMessagePublishRequest();
  /**
   *
   *
   * <pre>
   * Request to publish messages.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessagePublishRequest message_publish_request = 2;</code>
   *
   * @return The messagePublishRequest.
   */
  com.google.cloud.pubsublite.proto.MessagePublishRequest getMessagePublishRequest();
  /**
   *
   *
   * <pre>
   * Request to publish messages.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.MessagePublishRequest message_publish_request = 2;</code>
   */
  com.google.cloud.pubsublite.proto.MessagePublishRequestOrBuilder
      getMessagePublishRequestOrBuilder();

  com.google.cloud.pubsublite.proto.PublishRequest.RequestTypeCase getRequestTypeCase();
}
