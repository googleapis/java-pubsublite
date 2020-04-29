// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.beam.templates;

// import com.google.cloud.teleport.templates.common.PubsubConverters;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

/**
 * Dataflow template which copies messages from Cloud Pub/Sub to Pub/Sub Lite.
 *
 * <p>NOTE: Will not work without being included in the
 * https://github.com/GoogleCloudPlatform/DataflowTemplates repo.
 */
public final class PubsubToPubsubLite {
  private PubsubToPubsubLite() {}

  interface Options
      extends PipelineOptions,
          // PubsubConverters.PubsubReadSubscriptionOptions,
          PubsubLiteConverters.WriteOptions {}

  /**
   * Runs a pipeline which reads in Cloud Pub/Sub messages and writes to Pub/Sub Lite.
   *
   * @param args arguments to the pipeline
   */
  public static void main(String[] args) {
    Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

    Pipeline pipeline = Pipeline.create(options); /*
    pipeline
        .apply(FakePubsubIO.readRawPubsubProtos(options.getInputSubscription().get()))
        .apply(CloudPubsubTransforms.fromCpsPublishTransform())
        .apply(
            PubsubLiteIO.write(
                PublisherOptions.newBuilder().setTopicPath(options.getTopic().get()).build()));*/

    pipeline.run();
  }
}
