package com.google.cloud.pubsublite.beam.templates;

import static com.google.cloud.pubsublite.cloudpubsub.MessageTransforms.toCpsPublishTransformer;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.TopicPath;
import com.google.cloud.pubsublite.beam.PublisherOptions;
import com.google.cloud.pubsublite.beam.PubsubLiteIO;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.protobuf.ByteString;
import io.grpc.StatusException;
import java.io.IOException;
import java.util.Arrays;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.TypeDescriptors;

public class PubsubLiteToPubsubLite {

  static PublisherOptions publisherOptions() throws StatusException {
    return PublisherOptions.newBuilder().setTopicPath(TopicPath.parse("/yolo")).build();
  }

  static Pipeline publishPipeline() throws StatusException {
    PipelineOptions options = PipelineOptionsFactory.create();


    Pipeline pipeline = Pipeline.create(options);

    pipeline
        .apply("ReadLines", TextIO.read().from("gs://apache-beam-samples/shakespeare/kinglear.txt"))
        .apply("ExtractWords", FlatMapElements
            .into(TypeDescriptors.strings())
            .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))
        .apply(ParDo.of(
            new DoFn<String, Message>() {
              @ProcessElement
              public void processElement(@Element String word, OutputReceiver<Message> output)
                  throws StatusException {
                output.output(Message.builder().setData(ByteString.copyFromUtf8(word)).build());
              }
            }))
        .apply(PubsubLiteIO.addUuids())
        .apply(PubsubLiteIO.write(publisherOptions()));
    return pipeline;

  }

  static Pipeline subscribePipeline() throws StatusException {
    PipelineOptions options = PipelineOptionsFactory.create();


    Pipeline pipeline = Pipeline.create(options);

    pipeline
        .apply("ReadLines", TextIO.read().from("gs://apache-beam-samples/shakespeare/kinglear.txt"))
        .apply("ExtractWords", FlatMapElements
            .into(TypeDescriptors.strings())
            .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))
        .apply(ParDo.of(
            new DoFn<String, Message>() {
              @ProcessElement
              public void processElement(@Element String word, OutputReceiver<Message> output)
                  throws StatusException {
                output.output(Message.builder().setData(ByteString.copyFromUtf8(word)).build());
              }
            }))
        .apply(PubsubLiteIO.addUuids())
        .apply(PubsubLiteIO.write(publisherOptions()));
    return pipeline;

  }

  public static void main(String[] args) throws IOException, StatusException {
    /**
     * Sets up and starts streaming pipeline.
     *
     * @throws IOException if there is a problem setting up resources
     */
      PipelineOptions options = PipelineOptionsFactory.create();


      Pipeline pipeline = Pipeline.create(options);

      pipeline
          .apply("ReadLines", TextIO.read().from("gs://apache-beam-samples/shakespeare/kinglear.txt"))
          .apply("ExtractWords", FlatMapElements
              .into(TypeDescriptors.strings())
              .via((String line) -> Arrays.asList(line.split("[^\\p{L}]+"))))
          .apply(ParDo.of(
              new DoFn<String, Message>() {
                @ProcessElement
                public void processElement(@Element String word, OutputReceiver<Message> output)
                    throws StatusException {
                  output.output(Message.builder().setData(ByteString.copyFromUtf8(word)).build());
                }
              }))
          .apply(PubsubLiteIO.addUuids())
          .apply(PubsubLiteIO.write(publisherOptions()));


      PipelineResult result = pipeline.run();
      result.waitUntilFinish();

    }
  }

}
