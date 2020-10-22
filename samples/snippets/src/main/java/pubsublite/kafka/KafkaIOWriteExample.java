package com.google.cloud.pubsublite.demo;

import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.kafka.ProducerSettings;
import io.grpc.StatusException;
import java.util.Random;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.PeriodicImpulse;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.serialization.Serializer;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class KafkaIOWriteExample {
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

    PCollection<Instant> impulse = PeriodicImpulse.create().withInterval(Duration.millis(1)).expand(pipeline.begin());

    PCollection<KV<byte[], byte[]>> messages = impulse.apply(ParDo.of(new DoFn<Instant, KV<byte[], byte[]>>() {
      private final Random random = new Random();

      @ProcessElement
      public void processElement(@Element Instant instant, OutputReceiver<KV<byte[], byte[]>> out) {
        for (int i = 0; i < 50; i++) {
          byte[] someBytes = new byte[2 * 1024];
          random.nextBytes(someBytes);
          out.output(KV.of("".getBytes(), someBytes));
        }
      }
    }));

    TopicPath topic =
        TopicPath.newBuilder()
            .setProject(ProjectNumber.of(projectNumber))
            .setLocation(cloudZone)
            .setName(TopicName.of(topicId))
            .build();

    messages.apply(KafkaIO.<byte[], byte[]>write().withProducerFactoryFn(map -> {
      try {
        return ProducerSettings.newBuilder().setTopicPath(topic).build().instantiate();
      } catch (StatusException e) {
        throw e.getStatus().asRuntimeException();
      }
    }).withTopic(topic.toString()).withBootstrapServers("ignored")
        .withKeySerializer(UnusedSerializer.class)
        .withValueSerializer(UnusedSerializer.class));

    pipeline.run().waitUntilFinish();
  }

  private static class UnusedSerializer implements Serializer<byte[]> {
    @Override
    public byte[] serialize(String topic, byte[] data) {
      return data;
    }
  }
}
