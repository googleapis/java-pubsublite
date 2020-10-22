package com.google.cloud.pubsublite.demo;

import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.SubscriptionName;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.kafka.ConsumerSettings;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.io.kafka.KafkaIO;

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
import com.google.common.flogger.GoogleLogger;
import io.grpc.StatusException;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.joda.time.Duration;

public class KafkaIOReadExample {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();

  public static void main(String[] args) throws StatusException {
    CloudZone cloudZone = CloudZone.parse("your-cloud-region-b");
    String topicId = "your-topic-id";
    String subscriptionId = "your-subscription-id";
    long projectNumber = 123456789L;
    String gcpTempLocation = "gs://my-project/temp";

    DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
    options.setRunner(DataflowRunner.class);
    options.setProject("my-project");
    options.setRegion(cloudZone.region().value());
    options.setGcpTempLocation(gcpTempLocation);
    options.setStreaming(true);
    options.setEnableStreamingEngine(true);

    Pipeline pipeline = Pipeline.create(options);

    TopicPath topic =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(cloudZone)
            .setName(TopicName.of(topicId))
            .build();
    SubscriptionPath.Builder subBuilder =
        SubscriptionPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(cloudZone);
    SubscriptionPath main_sub = subBuilder.setName(SubscriptionName.of("my-main-sub")).build();
    SubscriptionPath offset_sub = subBuilder.setName(SubscriptionName.of("my-offset-sub")).build();

    SerializableFunction<Map<String, Object>, Consumer<byte[], byte[]>> consumerFactory = config -> {
      try {
        ConsumerSettings.Builder builder = ConsumerSettings.newBuilder();
        String groupId = (String) config.get(ConsumerConfig.GROUP_ID_CONFIG);
        if (groupId != null && groupId.contains("offset_consumer")) {
          builder.setSubscriptionPath(offset_sub)
              .setPerPartitionFlowControlSettings(FlowControlSettings.builder()
                  .setMessagesOutstanding(1L)
                  .setBytesOutstanding(2L * 1024 * 1024).build());
        } else {
          builder.setSubscriptionPath(main_sub)
              .setPerPartitionFlowControlSettings(FlowControlSettings.builder()
                  .setMessagesOutstanding(Long.MAX_VALUE)
                  .setBytesOutstanding(2L * 1024 * 1024).build());
        }
        return builder.build().instantiate();
      } catch (StatusException e) {
        throw e.getStatus().asRuntimeException();
      }
    };

    PCollection<KafkaRecord<byte[], byte[]>> messages = pipeline.apply(
        "ReadKafka",
        KafkaIO.readBytes()
            .commitOffsetsInFinalize()
            .withConsumerConfigUpdates(ImmutableMap.of("group.id", main_sub.toString()))
            .withConsumerFactoryFn(consumerFactory)
            .withBootstrapServers("ignored")
            .withTopic(topic.toString()));

    PCollection<Long> bytes = messages.apply(MapElements.via(
        new SimpleFunction<KafkaRecord<byte[], byte[]>, Long>() {
          @Override
          public Long apply(KafkaRecord<byte[], byte[]> input) {
            return (long) input.getKV().getValue().length;
          }
        }));

    bytes
        .apply(Window.into(FixedWindows.of(Duration.standardSeconds(10))))
        .apply(Sum.longsGlobally().withoutDefaults())
        .apply(ParDo.of(new DoFn<Long, Void>() {
          @ProcessElement
          public void processElement(@Element Long bytes, OutputReceiver<Void> out) {
            logger.atWarning().log("Received " + bytes + " bytes.");
          }
        }));
    pipeline.run().waitUntilFinish();
  }
}

