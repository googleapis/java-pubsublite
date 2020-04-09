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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.StatusException;

// A KeyExtractor can extract the Pub/Sub Lite 'key' field used for message routing from a
// PubsubMessage. It will by default use the ordering_key field directly for this if it exists.
//
// An empty ByteString implies that the message should have no ordering key.
public interface KeyExtractor {
  KeyExtractor DEFAULT = PubsubMessage::getOrderingKeyBytes;

  ByteString extractKey(PubsubMessage message) throws StatusException;
}
