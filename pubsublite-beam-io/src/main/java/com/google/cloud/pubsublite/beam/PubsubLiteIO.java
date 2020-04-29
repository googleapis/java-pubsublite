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

package com.google.cloud.pubsublite.beam;

import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.SequencedMessage;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.PInput;
import org.apache.beam.sdk.values.POutput;

public final class PubsubLiteIO {
  private PubsubLiteIO() {}

  private static <InT extends PInput, OutT extends POutput> PTransform<InT, OutT> toTransform(
      SerializableFunction<InT, OutT> fn, String name) {
    return new PTransform<InT, OutT>(name) {
      @Override
      public OutT expand(InT input) {
        return fn.apply(input);
      }
    };
  }

  // Read messages from Pub/Sub Lite. These messages may contain duplicates if the publisher
  // retried, which the PubsubLiteIO write method will do. Use the dedupe transform to remove these
  // duplicates.
  public static Read.Unbounded<SequencedMessage> read(SubscriberOptions options) {
    return Read.from(new PubsubLiteUnboundedSource(options));
  }

  // Remove duplicates from the PTransform from a read. Assumes by default that the uuids were
  // added by a call to PubsubLiteIO.addUuids().
  public static PTransform<PCollection<SequencedMessage>, PCollection<SequencedMessage>>
      deduplicate(UuidDeduplicationOptions options) {
    return new UuidDeduplicationTransform(options);
  }

  // Add Uuids to to-be-published messages that ensures that uniqueness is maintained.
  public static PTransform<PCollection<Message>, PCollection<Message>> addUuids() {
    return new AddUuidsTransform();
  }

  // Write messages to Pub/Sub Lite.
  public static PTransform<PCollection<Message>, PDone> write(PublisherOptions options) {
    return toTransform(
        input -> {
          PubsubLiteSink sink = new PubsubLiteSink(options);
          input.apply("Write", ParDo.of(sink));
          return PDone.in(input.getPipeline());
        },
        "PubsubLiteIO");
  }
}
