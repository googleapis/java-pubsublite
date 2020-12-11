package com.google.cloud.pubsublite.internal;

import com.google.cloud.pubsublite.SequencedMessage;

import java.io.Closeable;

public interface BlockingPullSubscriber extends Closeable {

    // Pull one message, blocking. Only one call may be outstanding at any time.
    SequencedMessage pull() throws InterruptedException, CheckedApiException;

    void close();

}
