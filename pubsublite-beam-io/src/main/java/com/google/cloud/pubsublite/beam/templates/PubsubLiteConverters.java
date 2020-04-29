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

import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.TopicPath;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.ValueProvider;

public final class PubsubLiteConverters {
  private PubsubLiteConverters() {}

  /** Options for Pubsub Lite reads from a subscription. */
  public interface ReadSubscriptionOptions extends PipelineOptions {
    @Description("The Pub/Sub Lite subscription to read from.")
    ValueProvider<SubscriptionPath> getSubscription();

    void setSubscription(ValueProvider<SubscriptionPath> subscription);
  }

  /** Options for Pubsub Lite writes to a topic. */
  public interface WriteOptions extends PipelineOptions {
    @Description("The Pub/Sub Lite topic to write to.")
    ValueProvider<TopicPath> getTopic();

    void setTopic(ValueProvider<TopicPath> topic);
  }
}
