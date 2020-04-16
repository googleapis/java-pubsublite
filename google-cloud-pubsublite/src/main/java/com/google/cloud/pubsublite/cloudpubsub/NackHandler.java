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

package com.google.cloud.pubsublite.cloudpubsub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.pubsub.v1.PubsubMessage;
import io.grpc.Status;

/**
 * A NackHandler handles when nack() is called in a user's AckReplyConsumer. Pub/Sub Lite does not
 * have a concept of 'nack'. When nack() is called in an AckReplyConsumerr, nack(message) is called
 * with the nacked message.
 *
 * <p>If the returned future is successful, the message is acknowledged. If the future fails, the
 * subscriber client will be failed. The default behavior is to fail the client
 */
public interface NackHandler {
  default ApiFuture<Void> nack(PubsubMessage message) {
    return ApiFutures.immediateFailedFuture(
        Status.UNIMPLEMENTED
            .withDescription(
                "You may not nack messages by default when using a PubSub Lite client. See NackHandler for how to customize this.")
            .asException());
  }
}
