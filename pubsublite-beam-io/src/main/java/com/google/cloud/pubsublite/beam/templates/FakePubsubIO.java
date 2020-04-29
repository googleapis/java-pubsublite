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

import com.google.pubsub.v1.PubsubMessage;
import javax.annotation.Nullable;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

// A mock up of a future PubsubIO state.
//
// A different approach to writing messages with pubsub lite ordering keys needs to be explored.
public class FakePubsubIO {
  private FakePubsubIO() {}

  @Nullable
  public static PTransform<PBegin, PCollection<PubsubMessage>> readRawPubsubProtos(String topic) {
    return null;
  }

  @Nullable
  public static PTransform<PCollection<PubsubMessage>, PDone> writeRawPubsubProtos(
      String subscription) {
    return null;
  }
}
