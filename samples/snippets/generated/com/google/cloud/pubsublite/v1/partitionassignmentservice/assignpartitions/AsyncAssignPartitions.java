/*
 * Copyright 2026 Google LLC
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

// [START pubsublite_v1_generated_PartitionAssignmentService_AssignPartitions_async]
import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.v1.PartitionAssignmentServiceClient;

public class AsyncAssignPartitions {

  public static void main(String[] args) throws Exception {
    asyncAssignPartitions();
  }

  public static void asyncAssignPartitions() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (PartitionAssignmentServiceClient partitionAssignmentServiceClient =
        PartitionAssignmentServiceClient.create()) {
      BidiStream<PartitionAssignmentRequest, PartitionAssignment> bidiStream =
          partitionAssignmentServiceClient.assignPartitionsCallable().call();
      PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();
      bidiStream.send(request);
      for (PartitionAssignment response : bidiStream) {
        // Do something when a response is received.
      }
    }
  }
}
// [END pubsublite_v1_generated_PartitionAssignmentService_AssignPartitions_async]
