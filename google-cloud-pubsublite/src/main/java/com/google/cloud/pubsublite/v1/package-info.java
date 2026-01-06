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

/**
 * A client to Pub/Sub Lite API
 *
 * <p>The interfaces provided are listed below, along with usage samples.
 *
 * <p>======================= AdminServiceClient =======================
 *
 * <p>Service Description: The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 *
 * <p>Sample for AdminServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
 *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
 *   Topic topic = Topic.newBuilder().build();
 *   String topicId = "topicId-1139259734";
 *   Topic response = adminServiceClient.createTopic(parent, topic, topicId);
 * }
 * }</pre>
 *
 * <p>======================= CursorServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to manage committed
 * cursors while receiving messsages. A cursor represents a subscriber's progress within a topic
 * partition for a given subscription.
 *
 * <p>Sample for CursorServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
 *   CommitCursorRequest request =
 *       CommitCursorRequest.newBuilder()
 *           .setSubscription("subscription341203229")
 *           .setPartition(-1799810326)
 *           .setCursor(Cursor.newBuilder().build())
 *           .build();
 *   CommitCursorResponse response = cursorServiceClient.commitCursor(request);
 * }
 * }</pre>
 *
 * <p>======================= PublisherServiceClient =======================
 *
 * <p>Service Description: The service that a publisher client application uses to publish messages
 * to topics. Published messages are retained by the service for the duration of the retention
 * period configured for the respective topic, and are delivered to subscriber clients upon request
 * (via the `SubscriberService`).
 *
 * <p>Sample for PublisherServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (PublisherServiceClient publisherServiceClient = PublisherServiceClient.create()) {
 *   BidiStream<PublishRequest, PublishResponse> bidiStream =
 *       publisherServiceClient.publishCallable().call();
 *   PublishRequest request = PublishRequest.newBuilder().build();
 *   bidiStream.send(request);
 *   for (PublishResponse response : bidiStream) {
 *     // Do something when a response is received.
 *   }
 * }
 * }</pre>
 *
 * <p>======================= SubscriberServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to receive messages
 * from subscriptions.
 *
 * <p>Sample for SubscriberServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (SubscriberServiceClient subscriberServiceClient = SubscriberServiceClient.create()) {
 *   BidiStream<SubscribeRequest, SubscribeResponse> bidiStream =
 *       subscriberServiceClient.subscribeCallable().call();
 *   SubscribeRequest request = SubscribeRequest.newBuilder().build();
 *   bidiStream.send(request);
 *   for (SubscribeResponse response : bidiStream) {
 *     // Do something when a response is received.
 *   }
 * }
 * }</pre>
 *
 * <p>======================= PartitionAssignmentServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to determine which
 * partitions it should connect to.
 *
 * <p>Sample for PartitionAssignmentServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (PartitionAssignmentServiceClient partitionAssignmentServiceClient =
 *     PartitionAssignmentServiceClient.create()) {
 *   BidiStream<PartitionAssignmentRequest, PartitionAssignment> bidiStream =
 *       partitionAssignmentServiceClient.assignPartitionsCallable().call();
 *   PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();
 *   bidiStream.send(request);
 *   for (PartitionAssignment response : bidiStream) {
 *     // Do something when a response is received.
 *   }
 * }
 * }</pre>
 *
 * <p>======================= TopicStatsServiceClient =======================
 *
 * <p>Service Description: This service allows users to get stats about messages in their topic.
 *
 * <p>Sample for TopicStatsServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
 *   ComputeMessageStatsRequest request =
 *       ComputeMessageStatsRequest.newBuilder()
 *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
 *           .setPartition(-1799810326)
 *           .setStartCursor(Cursor.newBuilder().build())
 *           .setEndCursor(Cursor.newBuilder().build())
 *           .build();
 *   ComputeMessageStatsResponse response = topicStatsServiceClient.computeMessageStats(request);
 * }
 * }</pre>
 */
@Generated("by gapic-generator-java")
package com.google.cloud.pubsublite.v1;

import javax.annotation.Generated;
