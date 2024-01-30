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

package com.google.cloud.pubsublite.proto;

public interface ListSubscriptionsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.ListSubscriptionsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The list of subscriptions in the requested parent. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Subscription subscriptions = 1;</code>
   */
  java.util.List<com.google.cloud.pubsublite.proto.Subscription> getSubscriptionsList();
  /**
   *
   *
   * <pre>
   * The list of subscriptions in the requested parent. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Subscription subscriptions = 1;</code>
   */
  com.google.cloud.pubsublite.proto.Subscription getSubscriptions(int index);
  /**
   *
   *
   * <pre>
   * The list of subscriptions in the requested parent. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Subscription subscriptions = 1;</code>
   */
  int getSubscriptionsCount();
  /**
   *
   *
   * <pre>
   * The list of subscriptions in the requested parent. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Subscription subscriptions = 1;</code>
   */
  java.util.List<? extends com.google.cloud.pubsublite.proto.SubscriptionOrBuilder>
      getSubscriptionsOrBuilderList();
  /**
   *
   *
   * <pre>
   * The list of subscriptions in the requested parent. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated .google.cloud.pubsublite.v1.Subscription subscriptions = 1;</code>
   */
  com.google.cloud.pubsublite.proto.SubscriptionOrBuilder getSubscriptionsOrBuilder(int index);

  /**
   *
   *
   * <pre>
   * A token that can be sent as `page_token` to retrieve the next page of
   * results. If this field is omitted, there are no more results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The nextPageToken.
   */
  java.lang.String getNextPageToken();
  /**
   *
   *
   * <pre>
   * A token that can be sent as `page_token` to retrieve the next page of
   * results. If this field is omitted, there are no more results.
   * </pre>
   *
   * <code>string next_page_token = 2;</code>
   *
   * @return The bytes for nextPageToken.
   */
  com.google.protobuf.ByteString getNextPageTokenBytes();
}
