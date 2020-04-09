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

package com.google.cloud.pubsublite;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.proto.Cursor;
import com.google.common.base.Preconditions;
import com.google.protobuf.Timestamp;

/** A message with its sequencing information in the partition. */
@AutoValue
public abstract class SequencedMessage {
  public static SequencedMessage create(
      Message message, Timestamp publishTime, Offset offset, long byteSize) {
    return new AutoValue_SequencedMessage(message, publishTime, offset, byteSize);
  }

  public static SequencedMessage fromProto(
      com.google.cloud.pubsublite.proto.SequencedMessage proto) {
    Preconditions.checkArgument(proto.getSizeBytes() >= 0);
    return create(
        Message.fromProto(proto.getMessage()),
        proto.getPublishTime(),
        Offset.create(proto.getCursor().getOffset()),
        proto.getSizeBytes());
  }

  public com.google.cloud.pubsublite.proto.SequencedMessage toProto() {
    return com.google.cloud.pubsublite.proto.SequencedMessage.newBuilder()
        .setMessage(message().toProto())
        .setCursor(Cursor.newBuilder().setOffset(offset().value()))
        .setPublishTime(publishTime())
        .setSizeBytes(byteSize())
        .build();
  }

  public abstract Message message();

  public abstract Timestamp publishTime();

  public abstract Offset offset();

  public abstract long byteSize();
}
