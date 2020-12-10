package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PslMicroBatchReader implements MicroBatchReader {

    private final PslDataSourceOptions options;
    private final CursorClient cursorClient;
    private final HeadOffsetReader headOffsetReader;
    private final MultiPartitionCommitter committer;
    private final Subscription subscription;
    private final long topicPartitionCount;
    private SparkSourceOffset startOffset;
    private SparkSourceOffset endOffset;

    public PslMicroBatchReader(PslDataSourceOptions options) {
        this.options = options;
        this.cursorClient = options.newCursorClient();
        AdminClient adminClient = options.newAdminClient();
        try {
            this.subscription = adminClient.getSubscription(options.subscriptionPath()).get();
            this.topicPartitionCount =
                    adminClient.getTopicPartitionCount(TopicPath.parse(subscription.getTopic())).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to get information of subscription " + options.subscriptionPath(), e);
        }
        this.committer =
                new MultiPartitionCommitter(
                        topicPartitionCount,
                        (partition) ->
                                CommitterBuilder.newBuilder()
                                        .setSubscriptionPath(options.subscriptionPath())
                                        .setPartition(partition)
                                        .setServiceClient(options.newCursorServiceClient())
                                        .build());
        // TODO(jiangmichael): Replace it with real implementation.
        this.headOffsetReader = new HeadOffsetReader() {
            @Override
            public PslSourceOffset getHeadOffset(TopicPath topic) {
                return PslSourceOffset.builder().partitionOffsetMap(ImmutableMap.of(
                        Partition.of(0), com.google.cloud.pubsublite.Offset.of(0),
                        Partition.of(1), com.google.cloud.pubsublite.Offset.of(0)
                )).build();
            }
            @Override
            public void close() {
            }
        };
    }

    @Override
    public void setOffsetRange(Optional<Offset> start, Optional<Offset> end) {
        if (start.isPresent()) {
            assert SparkSourceOffset.class.isAssignableFrom(start.get().getClass())
                    : "start offset is not assignable to PslSourceOffset.";
            startOffset = (SparkSourceOffset) start.get();
        } else {
            try {
                Map<Partition, com.google.cloud.pubsublite.Offset> pslSourceOffsetMap = new HashMap<>();
                for (int i = 0; i < topicPartitionCount; i++) {
                    pslSourceOffsetMap.put(Partition.of(i), com.google.cloud.pubsublite.Offset.of(0));
                }
                cursorClient
                        .listPartitionCursors(options.subscriptionPath())
                        .get()
                        .entrySet()
                        .forEach((e) -> pslSourceOffsetMap.replace(e.getKey(), e.getValue()));
                startOffset =
                        PslSparkUtils.toSparkSourceOffset(
                                PslSourceOffset.builder().partitionOffsetMap(pslSourceOffsetMap).build());
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException(
                        "Failed to get information from PSL and construct startOffset", e);
            }
        }
        if (end.isPresent()) {
            assert SparkSourceOffset.class.isAssignableFrom(end.get().getClass())
                    : "start offset is not assignable to PslSourceOffset.";
            endOffset = (SparkSourceOffset) end.get();
        } else {
            try {
                endOffset = PslSparkUtils.toSparkSourceOffset(headOffsetReader.getHeadOffset(
                        TopicPath.parse(subscription.getTopic())));
            } catch (CheckedApiException e) {
                throw new IllegalStateException("Unable to get head offsets.", e);
            }
        }
    }

    @Override
    public Offset getStartOffset() {
        return startOffset;
    }

    @Override
    public Offset getEndOffset() {
        return endOffset;
    }

    @Override
    public Offset deserializeOffset(String json) {
        return SparkSourceOffset.fromJson(json);
    }

    @Override
    public void commit(Offset end) {
        assert SparkSourceOffset.class.isAssignableFrom(end.getClass())
                : "end offset is not assignable to SparkSourceOffset.";
        committer.commit(PslSparkUtils.toPslSourceOffset((SparkSourceOffset) end));
    }

    @Override
    public void stop() {
        committer.close();
        headOffsetReader.close();
    }

    @Override
    public StructType readSchema() {
        return Constants.DEFAULT_SCHEMA;
    }

    @Override
    public List<InputPartition<InternalRow>> planInputPartitions() {
        return startOffset.getPartitionOffsetMap().entrySet().stream()
                .map(
                        e -> {
                            Partition partition = e.getKey();
                            SparkPartitionOffset startPartitionOffset = e.getValue();
                            SparkPartitionOffset endPartitionOfffset = endOffset.getPartitionOffsetMap()
                                    .get(partition);
                            return new PslMicroBatchInputPartition(
                                    options.subscriptionPath(),
                                    Objects.requireNonNull(options.flowControlSettings()),
                                    startPartitionOffset, endPartitionOfffset);
                        })
                .collect(Collectors.toList());
    }
}
