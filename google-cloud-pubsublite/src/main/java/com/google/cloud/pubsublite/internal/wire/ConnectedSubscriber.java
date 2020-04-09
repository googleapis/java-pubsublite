// Copyright 2019 Google LLC
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

package com.google.cloud.pubsublite.internal.wire;

import com.google.auto.value.AutoOneOf;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.proto.FlowControlRequest;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.ImmutableList;

interface ConnectedSubscriber extends AutoCloseable {
  // seek may not be called while another seek is outstanding.
  void seek(SeekRequest request);

  void allowFlow(FlowControlRequest request);

  // A Response either holds messages or a seek offset. getKind() must be checked before using.
  @AutoOneOf(Response.Kind.class)
  abstract class Response {
    enum Kind {
      MESSAGES,
      SEEK_OFFSET
    }

    abstract Kind getKind();

    // messages() is guranteed to be sorted.
    abstract ImmutableList<SequencedMessage> messages();

    abstract Offset seekOffset();

    static Response ofMessages(ImmutableList<SequencedMessage> messages) {
      return AutoOneOf_ConnectedSubscriber_Response.messages(messages);
    }

    static Response ofSeekOffset(Offset seekOffset) {
      return AutoOneOf_ConnectedSubscriber_Response.seekOffset(seekOffset);
    }
  }
}
