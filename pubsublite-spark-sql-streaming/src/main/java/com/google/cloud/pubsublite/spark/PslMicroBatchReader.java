package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.internal.CursorClient;
import com.google.cloud.pubsublite.internal.wire.CommitterBuilder;
import com.google.common.annotations.VisibleForTesting;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.streaming.MicroBatchReader;
import org.apache.spark.sql.sources.v2.reader.streaming.Offset;
import org.apache.spark.sql.types.StructType;

import java.util.List;
import java.util.Optional;

public class PslMicroBatchReader implements MicroBatchReader {

    public PslMicroBatchReader(PslDataSourceOptions options) {
        this(
                options,
                options.newAdminClient(),
                options.newCursorClient(),
                new MultiPartitionCommitter(
                        (partition) ->
                                CommitterBuilder.newBuilder()
                                        .setSubscriptionPath(options.subscriptionPath())
                                        .setPartition(partition)
                                        .setServiceClient(options.newCursorServiceClient())
                                        .build()));
    }

    @VisibleForTesting
    public PslMicroBatchReader(
            PslDataSourceOptions options,
            AdminClient adminClient,
            CursorClient cursorClient,
            MultiPartitionCommitter committer) {
        this.options = options;
        this.adminClient = adminClient;
        this.cursorClient = cursorClient;
        this.committer = committer;
    }

    @Override
    public void setOffsetRange(Optional<Offset> start, Optional<Offset> end) {

    }

    @Override
    public Offset getStartOffset() {
        return null;
    }

    @Override
    public Offset getEndOffset() {
        return null;
    }

    @Override
    public Offset deserializeOffset(String json) {
        return null;
    }

    @Override
    public void commit(Offset end) {

    }

    @Override
    public void stop() {

    }

    @Override
    public StructType readSchema() {
        return null;
    }

    @Override
    public List<InputPartition<InternalRow>> planInputPartitions() {
        return null;
    }
}
