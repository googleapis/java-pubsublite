/*
 * Copyright 2020 Google LLC
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
 * The interfaces provided are listed below, along with usage samples.
 *
 * <p>======================= AdminServiceClient =======================
 *
 * <p>Service Description: The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 *
 * <p>Sample for AdminServiceClient:
 *
 * <p>======================= CursorServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to manage committed
 * cursors while receiving messsages. A cursor represents a subscriber's progress within a topic
 * partition for a given subscription.
 *
 * <p>Sample for CursorServiceClient:
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
 * <p>======================= SubscriberServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to receive messages
 * from subscriptions.
 *
 * <p>Sample for SubscriberServiceClient:
 *
 * <p>======================= PartitionAssignmentServiceClient =======================
 *
 * <p>Service Description: The service that a subscriber client application uses to determine which
 * partitions it should connect to.
 *
 * <p>Sample for PartitionAssignmentServiceClient:
 *
 * <p>======================= TopicStatsServiceClient =======================
 *
 * <p>Service Description: This service allows users to get stats about messages in their topic.
 *
 * <p>Sample for TopicStatsServiceClient:
 */
@Generated("by gapic-generator-java")
package com.google.cloud.pubsublite.v1;

import javax.annotation.Generated;
