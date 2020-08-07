/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A client to Pub/Sub Lite API.
 *
 * <p>The interfaces provided are listed below, along with usage samples.
 *
 * <p>================== AdminServiceClient ==================
 *
 * <p>Service Description: The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 *
 * <p>Sample for AdminServiceClient:
 *
 * <pre>
 * <code>
 * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
 *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
 *   Topic topic = Topic.newBuilder().build();
 *   String topicId = "";
 *   Topic response = adminServiceClient.createTopic(parent, topic, topicId);
 * }
 * </code>
 * </pre>
 *
 * =================== CursorServiceClient ===================
 *
 * <p>Service Description: The service that a subscriber client application uses to manage committed
 * cursors while receiving messsages. A cursor represents a subscriber's progress within a topic
 * partition for a given subscription.
 *
 * <p>Sample for CursorServiceClient:
 *
 * <pre>
 * <code>
 * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
 *   SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
 *   ListPartitionCursorsPagedResponse response = cursorServiceClient.listPartitionCursors(parent);
 * }
 * </code>
 * </pre>
 *
 * ====================== PublisherServiceClient ======================
 *
 * <p>Service Description: The service that a publisher client application uses to publish messages
 * to topics. Published messages are retained by the service for the duration of the retention
 * period configured for the respective topic, and are delivered to subscriber clients upon request
 * (via the `SubscriberService`).
 *
 * <p>Sample for PublisherServiceClient:
 *
 * <pre>
 * <code>
 * try (PublisherServiceClient publisherServiceClient = PublisherServiceClient.create()) {
 *   PublishRequest request = PublishRequest.newBuilder().build();
 *   PublishResponse response = publisherServiceClient.publishCallable(request);
 * }
 * </code>
 * </pre>
 *
 * ======================= SubscriberServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to receive messages
 * from subscriptions.
 *
 * <p>Sample for SubscriberServiceClient:
 *
 * <pre>
 * <code>
 * try (SubscriberServiceClient subscriberServiceClient = SubscriberServiceClient.create()) {
 *   SubscribeRequest request = SubscribeRequest.newBuilder().build();
 *   SubscribeResponse response = subscriberServiceClient.subscribeCallable(request);
 * }
 * </code>
 * </pre>
 *
 * ================================ PartitionAssignmentServiceClient
 * ================================
 *
 * <p>Service Description: The service that a subscriber client application uses to determine which
 * partitions it should connect to.
 *
 * <p>Sample for PartitionAssignmentServiceClient:
 *
 * <pre>
 * <code>
 * try (PartitionAssignmentServiceClient partitionAssignmentServiceClient = PartitionAssignmentServiceClient.create()) {
 *   PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();
 *   PartitionAssignment response = partitionAssignmentServiceClient.assignPartitionsCallable(request);
 * }
 * </code>
 * </pre>
 *
 * ======================= TopicStatsServiceClient =======================
 *
 * <p>Service Description: This service allows users to get stats about messages in their topic.
 *
 * <p>Sample for TopicStatsServiceClient:
 *
 * <pre>
 * <code>
 * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
 *   TopicName topic = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
 *   long partition = 0L;
 *   ComputeMessageStatsRequest request = ComputeMessageStatsRequest.newBuilder()
 *     .setTopic(topic.toString())
 *     .setPartition(partition)
 *     .build();
 *   ComputeMessageStatsResponse response = topicStatsServiceClient.computeMessageStats(request);
 * }
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
package com.google.cloud.pubsublite.v1;

import javax.annotation.Generated;
