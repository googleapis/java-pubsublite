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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.api.gax.rpc.ApiException;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

/**
 * A KeyExtractor can extract the Pub/Sub Lite 'key' field used for message routing from a
 * PubsubMessage. It will by default use the ordering_key field directly for this if it exists.
 *
 * <p>An empty ByteString implies that the message should have no ordering key.
 */
public interface KeyExtractor {
  /** An extractor that gets the routing key from the ordering key field. */
  KeyExtractor DEFAULT = PubsubMessage::getOrderingKeyBytes;

  /** Extract the ByteString routing key from a PubsubMessage. */
  ByteString extractKey(PubsubMessage message) throws ApiException;
}
