package com.google.cloud.pubsublite;

import com.google.api.core.AbstractApiService;

/**
 * Fake Pub/Sub Lite service for testing. Used like:
 *
 * <p>static abstract class SubscriberFakeService extends FakeApiService implements Subscriber
 * {} @Spy private SubscriberFakeService wireSubscriber;
 */
public abstract class FakeApiService extends AbstractApiService {
  public void fail(Throwable t) {
    notifyFailed(t);
  }

  @Override
  protected void doStart() {
    notifyStarted();
  }

  @Override
  protected void doStop() {
    notifyStopped();
  }
}
