package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;

import java.io.Closeable;
import java.io.IOException;

public interface HeadOffsetReader extends Closeable {

    // Gets the head offsets for all partitions in a topic. Blocks.
    PslSourceOffset getHeadOffset(TopicPath topic) throws CheckedApiException;

    @Override
    void close();
}
