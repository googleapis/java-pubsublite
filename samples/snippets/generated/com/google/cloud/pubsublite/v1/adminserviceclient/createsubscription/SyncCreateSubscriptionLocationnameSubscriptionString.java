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

// [START
// pubsublite_v1_generated_adminserviceclient_createsubscription_locationnamesubscriptionstring_sync]
import com.google.cloud.pubsublite.proto.LocationName;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.v1.AdminServiceClient;

public class SyncCreateSubscriptionLocationnameSubscriptionString {

  public static void main(String[] args) throws Exception {
    syncCreateSubscriptionLocationnameSubscriptionString();
  }

  public static void syncCreateSubscriptionLocationnameSubscriptionString() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
      LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
      Subscription subscription = Subscription.newBuilder().build();
      String subscriptionId = "subscriptionId1478790936";
      Subscription response =
          adminServiceClient.createSubscription(parent, subscription, subscriptionId);
    }
  }
}
// [END
// pubsublite_v1_generated_adminserviceclient_createsubscription_locationnamesubscriptionstring_sync]
