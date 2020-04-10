package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import io.grpc.Metadata;
import io.grpc.Status;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

final class RoutingMetadata {
  private RoutingMetadata() {}

  static final String PARAMS_HEADER = "x-goog-request-params";
  static final Metadata.Key<String> PARAMS_KEY =
      Metadata.Key.of(PARAMS_HEADER, Metadata.ASCII_STRING_MARSHALLER);

  static Metadata of(TopicPath topic, Partition partition) {
    try {
      Metadata metadata = new Metadata();
      String topic_value = URLEncoder.encode(topic.value(), StandardCharsets.UTF_8.toString());
      String params = String.format("partition=%s&topic=%s", partition.value(), topic_value);
      metadata.put(PARAMS_KEY, params);
      return metadata;
    } catch (UnsupportedEncodingException e) {
      throw Status.INVALID_ARGUMENT.withCause(e).asRuntimeException();
    }
  }

  static Metadata of(SubscriptionPath subscription, Partition partition) {
    try {
      Metadata metadata = new Metadata();
      String subscription_value =
          URLEncoder.encode(subscription.value(), StandardCharsets.UTF_8.toString());
      String params =
          String.format("partition=%s&subscription=%s", partition.value(), subscription_value);
      metadata.put(PARAMS_KEY, params);
      return metadata;
    } catch (UnsupportedEncodingException e) {
      throw Status.INVALID_ARGUMENT.withCause(e).asRuntimeException();
    }
  }
}
