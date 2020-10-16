/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.beam.templates;

// import com.google.cloud.teleport.templates.common.PubsubConverters;
import org.apache.beam.sdk.options.PipelineOptions;

/**
 * Dataflow template which copies messages from Pub/Sub Lite to Cloud Pub/Sub.
 *
 * <p>NOTE: Will not work without being included in the
 * https://github.com/GoogleCloudPlatform/DataflowTemplates repo.
 */
public final class PubsubLiteToPubsub {
  private PubsubLiteToPubsub() {}

  interface Options
      extends PipelineOptions,
          // PubsubConverters.PubsubWriteOptions,
          PubsubLiteConverters.ReadSubscriptionOptions {}

  /**
   * Runs a pipeline which reads in Pub/Sub Lite messages and writes to Cloud Pub/Sub
   *
   * @param args arguments to the pipeline
   */
  /*
  public static void main(String[] args) {
    Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

    Pipeline pipeline = Pipeline.create(options);
    try {
      pipeline
          .apply(
              PubsubLiteIO.read(
                  SubscriberOptions.newBuilder()
                      .setSubscriptionPath(options.getSubscription().get())
                      .build()))
          .apply(MapElements.into(new TypeDescriptor<Message>() {}).via(SequencedMessage::message))
          .apply(CloudPubsubTransforms.toCpsPublishTransform())
          .apply(FakePubsubIO.writeRawPubsubProtos(options.getPubsubWriteTopic().get()));
    } catch (CheckedApiException e) {
      throw e.getStatus().asRuntimeException();
    }

    pipeline.run();
  }*/
}
