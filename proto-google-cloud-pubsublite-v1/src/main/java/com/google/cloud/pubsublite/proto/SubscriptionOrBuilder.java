// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/common.proto

package com.google.cloud.pubsublite.proto;

public interface SubscriptionOrBuilder
    extends
    // @@protoc_insertion_point(interface_extends:google.cloud.pubsublite.v1.Subscription)
    com.google.protobuf.MessageOrBuilder {

  /**
   *
   *
   * <pre>
   * The name of the subscription.
   * Structured like:
   * projects/{project_number}/locations/{location}/subscriptions/{subscription_id}
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
   * The name of the subscription.
   * Structured like:
   * projects/{project_number}/locations/{location}/subscriptions/{subscription_id}
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
   * The name of the topic this subscription is attached to.
   * Structured like:
   * projects/{project_number}/locations/{location}/topics/{topic_id}
   * </pre>
   *
   * <code>string topic = 2 [(.google.api.resource_reference) = { ... }</code>
   *
   * @return The topic.
   */
  java.lang.String getTopic();
  /**
   *
   *
   * <pre>
   * The name of the topic this subscription is attached to.
   * Structured like:
   * projects/{project_number}/locations/{location}/topics/{topic_id}
   * </pre>
   *
   * <code>string topic = 2 [(.google.api.resource_reference) = { ... }</code>
   *
   * @return The bytes for topic.
   */
  com.google.protobuf.ByteString getTopicBytes();

  /**
   *
   *
   * <pre>
   * The settings for this subscription's message delivery.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Subscription.DeliveryConfig delivery_config = 3;</code>
   *
   * @return Whether the deliveryConfig field is set.
   */
  boolean hasDeliveryConfig();
  /**
   *
   *
   * <pre>
   * The settings for this subscription's message delivery.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Subscription.DeliveryConfig delivery_config = 3;</code>
   *
   * @return The deliveryConfig.
   */
  com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig getDeliveryConfig();
  /**
   *
   *
   * <pre>
   * The settings for this subscription's message delivery.
   * </pre>
   *
   * <code>.google.cloud.pubsublite.v1.Subscription.DeliveryConfig delivery_config = 3;</code>
   */
  com.google.cloud.pubsublite.proto.Subscription.DeliveryConfigOrBuilder
      getDeliveryConfigOrBuilder();
}
