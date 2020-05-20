// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/admin.proto

package com.google.cloud.pubsublite.proto;

public interface ListTopicSubscriptionsResponseOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.ListTopicSubscriptionsResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The names of subscriptions attached to the topic. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated string subscriptions = 1;</code>
   *
   * @return A list containing the subscriptions.
   */
  java.util.List<java.lang.String> getSubscriptionsList();
  /**
   *
   *
   * <pre>
   * The names of subscriptions attached to the topic. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated string subscriptions = 1;</code>
   *
   * @return The count of subscriptions.
   */
  int getSubscriptionsCount();
  /**
   *
   *
   * <pre>
   * The names of subscriptions attached to the topic. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated string subscriptions = 1;</code>
   *
   * @param index The index of the element to return.
   * @return The subscriptions at the given index.
   */
  java.lang.String getSubscriptions(int index);
  /**
   *
   *
   * <pre>
   * The names of subscriptions attached to the topic. The order of the
   * subscriptions is unspecified.
   * </pre>
   *
   * <code>repeated string subscriptions = 1;</code>
   *
   * @param index The index of the value to return.
   * @return The bytes of the subscriptions at the given index.
   */
  com.google.protobuf.ByteString getSubscriptionsBytes(int index);

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
