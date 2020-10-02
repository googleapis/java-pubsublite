package com.google.cloud.pubsublite.kafka;

interface ConsumerFactory {
  SingleSubscriptionConsumer newConsumer();
}
