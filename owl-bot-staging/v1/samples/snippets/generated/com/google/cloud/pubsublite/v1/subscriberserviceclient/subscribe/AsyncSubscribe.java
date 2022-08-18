/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.v1.samples;

// [START pubsublite_v1_generated_subscriberserviceclient_subscribe_async]
import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.cloud.pubsublite.v1.SubscriberServiceClient;

public class AsyncSubscribe {

  public static void main(String[] args) throws Exception {
    asyncSubscribe();
  }

  public static void asyncSubscribe() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (SubscriberServiceClient subscriberServiceClient = SubscriberServiceClient.create()) {
      BidiStream<SubscribeRequest, SubscribeResponse> bidiStream =
          subscriberServiceClient.subscribeCallable().call();
      SubscribeRequest request = SubscribeRequest.newBuilder().build();
      bidiStream.send(request);
      for (SubscribeResponse response : bidiStream) {
        // Do something when a response is received.
      }
    }
  }
}
// [END pubsublite_v1_generated_subscriberserviceclient_subscribe_async]
