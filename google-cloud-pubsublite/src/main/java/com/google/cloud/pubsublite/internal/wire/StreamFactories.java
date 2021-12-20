/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;

public final class StreamFactories {
  public interface PublishStreamFactory extends StreamFactory<PublishRequest, PublishResponse> {}

  public interface SubscribeStreamFactory
      extends StreamFactory<SubscribeRequest, SubscribeResponse> {}

  public interface CursorStreamFactory
      extends StreamFactory<StreamingCommitCursorRequest, StreamingCommitCursorResponse> {}
}
