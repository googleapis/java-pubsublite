package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import com.google.cloud.pubsublite.internal.wire.SubscriberBuilder;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.cloud.pubsublite.proto.SeekRequest;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.reader.InputPartition;
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader;

public class PslMicroBatchInputPartition implements InputPartition<InternalRow> {

    private final SparkPartitionOffset startOffset;
    private final SparkPartitionOffset endOffset;
    private final SubscriptionPath subscriptionPath;
    private final FlowControlSettings flowControlSettings;

    public PslMicroBatchInputPartition(
            SubscriptionPath subscriptionPath,
            FlowControlSettings flowControlSettings,
            SparkPartitionOffset startOffset,
            SparkPartitionOffset endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.subscriptionPath = subscriptionPath;
        this.flowControlSettings = flowControlSettings;
    }


    @Override
    public InputPartitionReader createPartitionReader() {
        PslPartitionOffset pslPartitionOffset =
                PslSparkUtils.toPslPartitionOffset(startOffset);

        RangedBlockingPullSubscriber subscriber;
        try {
            subscriber =
                    new RangedBlockingPullSubscriber(
                            // TODO(jiangmichael): Pass credentials settings here.
                            (consumer) ->
                                    SubscriberBuilder.newBuilder()
                                            .setSubscriptionPath(subscriptionPath)
                                            .setPartition(pslPartitionOffset.partition())
                                            .setContext(PubsubContext.of(Constants.FRAMEWORK))
                                            .setMessageConsumer(consumer)
                                            .build(),
                            flowControlSettings,
                            SeekRequest.newBuilder()
                                    .setCursor(
                                            Cursor.newBuilder().setOffset(pslPartitionOffset.offset().value()).build())
                                    .build(), endOffset);
        } catch (CheckedApiException e) {
            throw new IllegalStateException(
                    "Unable to create PSL subscriber for " + startOffset.toString(), e);
        }
        return new PslMicroBatchInputPartitionReader(
                subscriptionPath, startOffset.partition(), subscriber);
    }
}
