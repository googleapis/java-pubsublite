/*
 * Copyright 2021 Google LLC
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

package com.google.cloud.pubsublite.v1;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.paging.AbstractFixedSizeCollection;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.AbstractPagedListResponse;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.LocationName;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicName;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.cloud.pubsublite.v1.stub.AdminServiceStub;
import com.google.cloud.pubsublite.v1.stub.AdminServiceStubSettings;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.google.protobuf.FieldMask;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: The service that a client application uses to manage topics and
 * subscriptions, such creating, listing, and deleting topics and subscriptions.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
 * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
 *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
 *   Topic topic = Topic.newBuilder().build();
 *   String topicId = "topicId-1139259734";
 *   Topic response = adminServiceClient.createTopic(parent, topic, topicId);
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the AdminServiceClient object to clean up resources such
 * as threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li>A "flattened" method. With this type of method, the fields of the request type have been
 *       converted into function parameters. It may be the case that not all fields are available as
 *       parameters, and not every API method will have a flattened method entry point.
 *   <li>A "request object" method. This type of method only takes one parameter, a request object,
 *       which must be constructed before the call. Not every API method will have a request object
 *       method.
 *   <li>A "callable" method. This type of method takes no parameters and returns an immutable API
 *       callable object, which can be used to initiate calls to the service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of AdminServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * AdminServiceSettings adminServiceSettings =
 *     AdminServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * AdminServiceClient adminServiceClient = AdminServiceClient.create(adminServiceSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * AdminServiceSettings adminServiceSettings =
 *     AdminServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * AdminServiceClient adminServiceClient = AdminServiceClient.create(adminServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class AdminServiceClient implements BackgroundResource {
  private final AdminServiceSettings settings;
  private final AdminServiceStub stub;

  /** Constructs an instance of AdminServiceClient with default settings. */
  public static final AdminServiceClient create() throws IOException {
    return create(AdminServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of AdminServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final AdminServiceClient create(AdminServiceSettings settings) throws IOException {
    return new AdminServiceClient(settings);
  }

  /**
   * Constructs an instance of AdminServiceClient, using the given stub for making calls. This is
   * for advanced usage - prefer using create(AdminServiceSettings).
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final AdminServiceClient create(AdminServiceStub stub) {
    return new AdminServiceClient(stub);
  }

  /**
   * Constructs an instance of AdminServiceClient, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected AdminServiceClient(AdminServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((AdminServiceStubSettings) settings.getStubSettings()).createStub();
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected AdminServiceClient(AdminServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final AdminServiceSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public AdminServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
   *   Topic topic = Topic.newBuilder().build();
   *   String topicId = "topicId-1139259734";
   *   Topic response = adminServiceClient.createTopic(parent, topic, topicId);
   * }
   * }</pre>
   *
   * @param parent Required. The parent location in which to create the topic. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @param topic Required. Configuration of the topic to create. Its `name` field is ignored.
   * @param topicId Required. The ID to use for the topic, which will become the final component of
   *     the topic's name.
   *     <p>This value is structured like: `my-topic-name`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic createTopic(LocationName parent, Topic topic, String topicId) {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .setTopic(topic)
            .setTopicId(topicId)
            .build();
    return createTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String parent = LocationName.of("[PROJECT]", "[LOCATION]").toString();
   *   Topic topic = Topic.newBuilder().build();
   *   String topicId = "topicId-1139259734";
   *   Topic response = adminServiceClient.createTopic(parent, topic, topicId);
   * }
   * }</pre>
   *
   * @param parent Required. The parent location in which to create the topic. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @param topic Required. Configuration of the topic to create. Its `name` field is ignored.
   * @param topicId Required. The ID to use for the topic, which will become the final component of
   *     the topic's name.
   *     <p>This value is structured like: `my-topic-name`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic createTopic(String parent, Topic topic, String topicId) {
    CreateTopicRequest request =
        CreateTopicRequest.newBuilder()
            .setParent(parent)
            .setTopic(topic)
            .setTopicId(topicId)
            .build();
    return createTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   CreateTopicRequest request =
   *       CreateTopicRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setTopic(Topic.newBuilder().build())
   *           .setTopicId("topicId-1139259734")
   *           .build();
   *   Topic response = adminServiceClient.createTopic(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic createTopic(CreateTopicRequest request) {
    return createTopicCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   CreateTopicRequest request =
   *       CreateTopicRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setTopic(Topic.newBuilder().build())
   *           .setTopicId("topicId-1139259734")
   *           .build();
   *   ApiFuture<Topic> future = adminServiceClient.createTopicCallable().futureCall(request);
   *   // Do something.
   *   Topic response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CreateTopicRequest, Topic> createTopicCallable() {
    return stub.createTopicCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the topic configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
   *   Topic response = adminServiceClient.getTopic(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic whose configuration to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic getTopic(TopicName name) {
    GetTopicRequest request =
        GetTopicRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    return getTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the topic configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString();
   *   Topic response = adminServiceClient.getTopic(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic whose configuration to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic getTopic(String name) {
    GetTopicRequest request = GetTopicRequest.newBuilder().setName(name).build();
    return getTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the topic configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetTopicRequest request =
   *       GetTopicRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   Topic response = adminServiceClient.getTopic(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic getTopic(GetTopicRequest request) {
    return getTopicCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the topic configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetTopicRequest request =
   *       GetTopicRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   ApiFuture<Topic> future = adminServiceClient.getTopicCallable().futureCall(request);
   *   // Do something.
   *   Topic response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetTopicRequest, Topic> getTopicCallable() {
    return stub.getTopicCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the partition information for the requested topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
   *   TopicPartitions response = adminServiceClient.getTopicPartitions(name);
   * }
   * }</pre>
   *
   * @param name Required. The topic whose partition information to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final TopicPartitions getTopicPartitions(TopicName name) {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder()
            .setName(name == null ? null : name.toString())
            .build();
    return getTopicPartitions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the partition information for the requested topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString();
   *   TopicPartitions response = adminServiceClient.getTopicPartitions(name);
   * }
   * }</pre>
   *
   * @param name Required. The topic whose partition information to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final TopicPartitions getTopicPartitions(String name) {
    GetTopicPartitionsRequest request =
        GetTopicPartitionsRequest.newBuilder().setName(name).build();
    return getTopicPartitions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the partition information for the requested topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetTopicPartitionsRequest request =
   *       GetTopicPartitionsRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   TopicPartitions response = adminServiceClient.getTopicPartitions(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final TopicPartitions getTopicPartitions(GetTopicPartitionsRequest request) {
    return getTopicPartitionsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the partition information for the requested topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetTopicPartitionsRequest request =
   *       GetTopicPartitionsRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   ApiFuture<TopicPartitions> future =
   *       adminServiceClient.getTopicPartitionsCallable().futureCall(request);
   *   // Do something.
   *   TopicPartitions response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsCallable() {
    return stub.getTopicPartitionsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of topics for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
   *   for (Topic element : adminServiceClient.listTopics(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The parent whose topics are to be listed. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicsPagedResponse listTopics(LocationName parent) {
    ListTopicsRequest request =
        ListTopicsRequest.newBuilder().setParent(parent == null ? null : parent.toString()).build();
    return listTopics(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of topics for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String parent = LocationName.of("[PROJECT]", "[LOCATION]").toString();
   *   for (Topic element : adminServiceClient.listTopics(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The parent whose topics are to be listed. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicsPagedResponse listTopics(String parent) {
    ListTopicsRequest request = ListTopicsRequest.newBuilder().setParent(parent).build();
    return listTopics(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of topics for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicsRequest request =
   *       ListTopicsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   for (Topic element : adminServiceClient.listTopics(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicsPagedResponse listTopics(ListTopicsRequest request) {
    return listTopicsPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of topics for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicsRequest request =
   *       ListTopicsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   ApiFuture<Topic> future = adminServiceClient.listTopicsPagedCallable().futureCall(request);
   *   // Do something.
   *   for (Topic element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListTopicsRequest, ListTopicsPagedResponse> listTopicsPagedCallable() {
    return stub.listTopicsPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of topics for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicsRequest request =
   *       ListTopicsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   while (true) {
   *     ListTopicsResponse response = adminServiceClient.listTopicsCallable().call(request);
   *     for (Topic element : response.getResponsesList()) {
   *       // doThingsWith(element);
   *     }
   *     String nextPageToken = response.getNextPageToken();
   *     if (!Strings.isNullOrEmpty(nextPageToken)) {
   *       request = request.toBuilder().setPageToken(nextPageToken).build();
   *     } else {
   *       break;
   *     }
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListTopicsRequest, ListTopicsResponse> listTopicsCallable() {
    return stub.listTopicsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   Topic topic = Topic.newBuilder().build();
   *   FieldMask updateMask = FieldMask.newBuilder().build();
   *   Topic response = adminServiceClient.updateTopic(topic, updateMask);
   * }
   * }</pre>
   *
   * @param topic Required. The topic to update. Its `name` field must be populated.
   * @param updateMask Required. A mask specifying the topic fields to change.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic updateTopic(Topic topic, FieldMask updateMask) {
    UpdateTopicRequest request =
        UpdateTopicRequest.newBuilder().setTopic(topic).setUpdateMask(updateMask).build();
    return updateTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   UpdateTopicRequest request =
   *       UpdateTopicRequest.newBuilder()
   *           .setTopic(Topic.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   Topic response = adminServiceClient.updateTopic(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Topic updateTopic(UpdateTopicRequest request) {
    return updateTopicCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   UpdateTopicRequest request =
   *       UpdateTopicRequest.newBuilder()
   *           .setTopic(Topic.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ApiFuture<Topic> future = adminServiceClient.updateTopicCallable().futureCall(request);
   *   // Do something.
   *   Topic response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateTopicRequest, Topic> updateTopicCallable() {
    return stub.updateTopicCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
   *   adminServiceClient.deleteTopic(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic to delete.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteTopic(TopicName name) {
    DeleteTopicRequest request =
        DeleteTopicRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    deleteTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString();
   *   adminServiceClient.deleteTopic(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic to delete.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteTopic(String name) {
    DeleteTopicRequest request = DeleteTopicRequest.newBuilder().setName(name).build();
    deleteTopic(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   DeleteTopicRequest request =
   *       DeleteTopicRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   adminServiceClient.deleteTopic(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteTopic(DeleteTopicRequest request) {
    deleteTopicCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   DeleteTopicRequest request =
   *       DeleteTopicRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .build();
   *   ApiFuture<Empty> future = adminServiceClient.deleteTopicCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteTopicRequest, Empty> deleteTopicCallable() {
    return stub.deleteTopicCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists the subscriptions attached to the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   TopicName name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]");
   *   for (String element : adminServiceClient.listTopicSubscriptions(name).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic whose subscriptions to list.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicSubscriptionsPagedResponse listTopicSubscriptions(TopicName name) {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder()
            .setName(name == null ? null : name.toString())
            .build();
    return listTopicSubscriptions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists the subscriptions attached to the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString();
   *   for (String element : adminServiceClient.listTopicSubscriptions(name).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param name Required. The name of the topic whose subscriptions to list.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicSubscriptionsPagedResponse listTopicSubscriptions(String name) {
    ListTopicSubscriptionsRequest request =
        ListTopicSubscriptionsRequest.newBuilder().setName(name).build();
    return listTopicSubscriptions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists the subscriptions attached to the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicSubscriptionsRequest request =
   *       ListTopicSubscriptionsRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   for (String element : adminServiceClient.listTopicSubscriptions(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListTopicSubscriptionsPagedResponse listTopicSubscriptions(
      ListTopicSubscriptionsRequest request) {
    return listTopicSubscriptionsPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists the subscriptions attached to the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicSubscriptionsRequest request =
   *       ListTopicSubscriptionsRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   ApiFuture<String> future =
   *       adminServiceClient.listTopicSubscriptionsPagedCallable().futureCall(request);
   *   // Do something.
   *   for (String element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsPagedCallable() {
    return stub.listTopicSubscriptionsPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Lists the subscriptions attached to the specified topic.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListTopicSubscriptionsRequest request =
   *       ListTopicSubscriptionsRequest.newBuilder()
   *           .setName(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   while (true) {
   *     ListTopicSubscriptionsResponse response =
   *         adminServiceClient.listTopicSubscriptionsCallable().call(request);
   *     for (String element : response.getResponsesList()) {
   *       // doThingsWith(element);
   *     }
   *     String nextPageToken = response.getNextPageToken();
   *     if (!Strings.isNullOrEmpty(nextPageToken)) {
   *       request = request.toBuilder().setPageToken(nextPageToken).build();
   *     } else {
   *       break;
   *     }
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
      listTopicSubscriptionsCallable() {
    return stub.listTopicSubscriptionsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
   *   Subscription subscription = Subscription.newBuilder().build();
   *   String subscriptionId = "subscriptionId1478790936";
   *   Subscription response =
   *       adminServiceClient.createSubscription(parent, subscription, subscriptionId);
   * }
   * }</pre>
   *
   * @param parent Required. The parent location in which to create the subscription. Structured
   *     like `projects/{project_number}/locations/{location}`.
   * @param subscription Required. Configuration of the subscription to create. Its `name` field is
   *     ignored.
   * @param subscriptionId Required. The ID to use for the subscription, which will become the final
   *     component of the subscription's name.
   *     <p>This value is structured like: `my-sub-name`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription createSubscription(
      LocationName parent, Subscription subscription, String subscriptionId) {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .setSubscription(subscription)
            .setSubscriptionId(subscriptionId)
            .build();
    return createSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String parent = LocationName.of("[PROJECT]", "[LOCATION]").toString();
   *   Subscription subscription = Subscription.newBuilder().build();
   *   String subscriptionId = "subscriptionId1478790936";
   *   Subscription response =
   *       adminServiceClient.createSubscription(parent, subscription, subscriptionId);
   * }
   * }</pre>
   *
   * @param parent Required. The parent location in which to create the subscription. Structured
   *     like `projects/{project_number}/locations/{location}`.
   * @param subscription Required. Configuration of the subscription to create. Its `name` field is
   *     ignored.
   * @param subscriptionId Required. The ID to use for the subscription, which will become the final
   *     component of the subscription's name.
   *     <p>This value is structured like: `my-sub-name`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription createSubscription(
      String parent, Subscription subscription, String subscriptionId) {
    CreateSubscriptionRequest request =
        CreateSubscriptionRequest.newBuilder()
            .setParent(parent)
            .setSubscription(subscription)
            .setSubscriptionId(subscriptionId)
            .build();
    return createSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   CreateSubscriptionRequest request =
   *       CreateSubscriptionRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setSubscription(Subscription.newBuilder().build())
   *           .setSubscriptionId("subscriptionId1478790936")
   *           .setSkipBacklog(true)
   *           .build();
   *   Subscription response = adminServiceClient.createSubscription(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription createSubscription(CreateSubscriptionRequest request) {
    return createSubscriptionCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Creates a new subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   CreateSubscriptionRequest request =
   *       CreateSubscriptionRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setSubscription(Subscription.newBuilder().build())
   *           .setSubscriptionId("subscriptionId1478790936")
   *           .setSkipBacklog(true)
   *           .build();
   *   ApiFuture<Subscription> future =
   *       adminServiceClient.createSubscriptionCallable().futureCall(request);
   *   // Do something.
   *   Subscription response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CreateSubscriptionRequest, Subscription> createSubscriptionCallable() {
    return stub.createSubscriptionCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the subscription configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
   *   Subscription response = adminServiceClient.getSubscription(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the subscription whose configuration to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription getSubscription(SubscriptionName name) {
    GetSubscriptionRequest request =
        GetSubscriptionRequest.newBuilder().setName(name == null ? null : name.toString()).build();
    return getSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the subscription configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString();
   *   Subscription response = adminServiceClient.getSubscription(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the subscription whose configuration to return.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription getSubscription(String name) {
    GetSubscriptionRequest request = GetSubscriptionRequest.newBuilder().setName(name).build();
    return getSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the subscription configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetSubscriptionRequest request =
   *       GetSubscriptionRequest.newBuilder()
   *           .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .build();
   *   Subscription response = adminServiceClient.getSubscription(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription getSubscription(GetSubscriptionRequest request) {
    return getSubscriptionCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the subscription configuration.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   GetSubscriptionRequest request =
   *       GetSubscriptionRequest.newBuilder()
   *           .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .build();
   *   ApiFuture<Subscription> future =
   *       adminServiceClient.getSubscriptionCallable().futureCall(request);
   *   // Do something.
   *   Subscription response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GetSubscriptionRequest, Subscription> getSubscriptionCallable() {
    return stub.getSubscriptionCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of subscriptions for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   LocationName parent = LocationName.of("[PROJECT]", "[LOCATION]");
   *   for (Subscription element : adminServiceClient.listSubscriptions(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The parent whose subscriptions are to be listed. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListSubscriptionsPagedResponse listSubscriptions(LocationName parent) {
    ListSubscriptionsRequest request =
        ListSubscriptionsRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .build();
    return listSubscriptions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of subscriptions for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String parent = LocationName.of("[PROJECT]", "[LOCATION]").toString();
   *   for (Subscription element : adminServiceClient.listSubscriptions(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The parent whose subscriptions are to be listed. Structured like
   *     `projects/{project_number}/locations/{location}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListSubscriptionsPagedResponse listSubscriptions(String parent) {
    ListSubscriptionsRequest request =
        ListSubscriptionsRequest.newBuilder().setParent(parent).build();
    return listSubscriptions(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of subscriptions for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListSubscriptionsRequest request =
   *       ListSubscriptionsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   for (Subscription element : adminServiceClient.listSubscriptions(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListSubscriptionsPagedResponse listSubscriptions(ListSubscriptionsRequest request) {
    return listSubscriptionsPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of subscriptions for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListSubscriptionsRequest request =
   *       ListSubscriptionsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   ApiFuture<Subscription> future =
   *       adminServiceClient.listSubscriptionsPagedCallable().futureCall(request);
   *   // Do something.
   *   for (Subscription element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsPagedResponse>
      listSubscriptionsPagedCallable() {
    return stub.listSubscriptionsPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns the list of subscriptions for the given project.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   ListSubscriptionsRequest request =
   *       ListSubscriptionsRequest.newBuilder()
   *           .setParent(LocationName.of("[PROJECT]", "[LOCATION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   while (true) {
   *     ListSubscriptionsResponse response =
   *         adminServiceClient.listSubscriptionsCallable().call(request);
   *     for (Subscription element : response.getResponsesList()) {
   *       // doThingsWith(element);
   *     }
   *     String nextPageToken = response.getNextPageToken();
   *     if (!Strings.isNullOrEmpty(nextPageToken)) {
   *       request = request.toBuilder().setPageToken(nextPageToken).build();
   *     } else {
   *       break;
   *     }
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse>
      listSubscriptionsCallable() {
    return stub.listSubscriptionsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   Subscription subscription = Subscription.newBuilder().build();
   *   FieldMask updateMask = FieldMask.newBuilder().build();
   *   Subscription response = adminServiceClient.updateSubscription(subscription, updateMask);
   * }
   * }</pre>
   *
   * @param subscription Required. The subscription to update. Its `name` field must be populated.
   *     Topic field must not be populated.
   * @param updateMask Required. A mask specifying the subscription fields to change.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription updateSubscription(Subscription subscription, FieldMask updateMask) {
    UpdateSubscriptionRequest request =
        UpdateSubscriptionRequest.newBuilder()
            .setSubscription(subscription)
            .setUpdateMask(updateMask)
            .build();
    return updateSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   UpdateSubscriptionRequest request =
   *       UpdateSubscriptionRequest.newBuilder()
   *           .setSubscription(Subscription.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   Subscription response = adminServiceClient.updateSubscription(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final Subscription updateSubscription(UpdateSubscriptionRequest request) {
    return updateSubscriptionCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates properties of the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   UpdateSubscriptionRequest request =
   *       UpdateSubscriptionRequest.newBuilder()
   *           .setSubscription(Subscription.newBuilder().build())
   *           .setUpdateMask(FieldMask.newBuilder().build())
   *           .build();
   *   ApiFuture<Subscription> future =
   *       adminServiceClient.updateSubscriptionCallable().futureCall(request);
   *   // Do something.
   *   Subscription response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<UpdateSubscriptionRequest, Subscription> updateSubscriptionCallable() {
    return stub.updateSubscriptionCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   SubscriptionName name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
   *   adminServiceClient.deleteSubscription(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the subscription to delete.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteSubscription(SubscriptionName name) {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder()
            .setName(name == null ? null : name.toString())
            .build();
    deleteSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   String name = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString();
   *   adminServiceClient.deleteSubscription(name);
   * }
   * }</pre>
   *
   * @param name Required. The name of the subscription to delete.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteSubscription(String name) {
    DeleteSubscriptionRequest request =
        DeleteSubscriptionRequest.newBuilder().setName(name).build();
    deleteSubscription(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   DeleteSubscriptionRequest request =
   *       DeleteSubscriptionRequest.newBuilder()
   *           .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .build();
   *   adminServiceClient.deleteSubscription(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final void deleteSubscription(DeleteSubscriptionRequest request) {
    deleteSubscriptionCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Deletes the specified subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (AdminServiceClient adminServiceClient = AdminServiceClient.create()) {
   *   DeleteSubscriptionRequest request =
   *       DeleteSubscriptionRequest.newBuilder()
   *           .setName(SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .build();
   *   ApiFuture<Empty> future = adminServiceClient.deleteSubscriptionCallable().futureCall(request);
   *   // Do something.
   *   future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<DeleteSubscriptionRequest, Empty> deleteSubscriptionCallable() {
    return stub.deleteSubscriptionCallable();
  }

  @Override
  public final void close() {
    stub.close();
  }

  @Override
  public void shutdown() {
    stub.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return stub.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return stub.isTerminated();
  }

  @Override
  public void shutdownNow() {
    stub.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return stub.awaitTermination(duration, unit);
  }

  public static class ListTopicsPagedResponse
      extends AbstractPagedListResponse<
          ListTopicsRequest,
          ListTopicsResponse,
          Topic,
          ListTopicsPage,
          ListTopicsFixedSizeCollection> {

    public static ApiFuture<ListTopicsPagedResponse> createAsync(
        PageContext<ListTopicsRequest, ListTopicsResponse, Topic> context,
        ApiFuture<ListTopicsResponse> futureResponse) {
      ApiFuture<ListTopicsPage> futurePage =
          ListTopicsPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage,
          new ApiFunction<ListTopicsPage, ListTopicsPagedResponse>() {
            @Override
            public ListTopicsPagedResponse apply(ListTopicsPage input) {
              return new ListTopicsPagedResponse(input);
            }
          },
          MoreExecutors.directExecutor());
    }

    private ListTopicsPagedResponse(ListTopicsPage page) {
      super(page, ListTopicsFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListTopicsPage
      extends AbstractPage<ListTopicsRequest, ListTopicsResponse, Topic, ListTopicsPage> {

    private ListTopicsPage(
        PageContext<ListTopicsRequest, ListTopicsResponse, Topic> context,
        ListTopicsResponse response) {
      super(context, response);
    }

    private static ListTopicsPage createEmptyPage() {
      return new ListTopicsPage(null, null);
    }

    @Override
    protected ListTopicsPage createPage(
        PageContext<ListTopicsRequest, ListTopicsResponse, Topic> context,
        ListTopicsResponse response) {
      return new ListTopicsPage(context, response);
    }

    @Override
    public ApiFuture<ListTopicsPage> createPageAsync(
        PageContext<ListTopicsRequest, ListTopicsResponse, Topic> context,
        ApiFuture<ListTopicsResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListTopicsFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListTopicsRequest,
          ListTopicsResponse,
          Topic,
          ListTopicsPage,
          ListTopicsFixedSizeCollection> {

    private ListTopicsFixedSizeCollection(List<ListTopicsPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListTopicsFixedSizeCollection createEmptyCollection() {
      return new ListTopicsFixedSizeCollection(null, 0);
    }

    @Override
    protected ListTopicsFixedSizeCollection createCollection(
        List<ListTopicsPage> pages, int collectionSize) {
      return new ListTopicsFixedSizeCollection(pages, collectionSize);
    }
  }

  public static class ListTopicSubscriptionsPagedResponse
      extends AbstractPagedListResponse<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          String,
          ListTopicSubscriptionsPage,
          ListTopicSubscriptionsFixedSizeCollection> {

    public static ApiFuture<ListTopicSubscriptionsPagedResponse> createAsync(
        PageContext<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String> context,
        ApiFuture<ListTopicSubscriptionsResponse> futureResponse) {
      ApiFuture<ListTopicSubscriptionsPage> futurePage =
          ListTopicSubscriptionsPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage,
          new ApiFunction<ListTopicSubscriptionsPage, ListTopicSubscriptionsPagedResponse>() {
            @Override
            public ListTopicSubscriptionsPagedResponse apply(ListTopicSubscriptionsPage input) {
              return new ListTopicSubscriptionsPagedResponse(input);
            }
          },
          MoreExecutors.directExecutor());
    }

    private ListTopicSubscriptionsPagedResponse(ListTopicSubscriptionsPage page) {
      super(page, ListTopicSubscriptionsFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListTopicSubscriptionsPage
      extends AbstractPage<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          String,
          ListTopicSubscriptionsPage> {

    private ListTopicSubscriptionsPage(
        PageContext<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String> context,
        ListTopicSubscriptionsResponse response) {
      super(context, response);
    }

    private static ListTopicSubscriptionsPage createEmptyPage() {
      return new ListTopicSubscriptionsPage(null, null);
    }

    @Override
    protected ListTopicSubscriptionsPage createPage(
        PageContext<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String> context,
        ListTopicSubscriptionsResponse response) {
      return new ListTopicSubscriptionsPage(context, response);
    }

    @Override
    public ApiFuture<ListTopicSubscriptionsPage> createPageAsync(
        PageContext<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String> context,
        ApiFuture<ListTopicSubscriptionsResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListTopicSubscriptionsFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          String,
          ListTopicSubscriptionsPage,
          ListTopicSubscriptionsFixedSizeCollection> {

    private ListTopicSubscriptionsFixedSizeCollection(
        List<ListTopicSubscriptionsPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListTopicSubscriptionsFixedSizeCollection createEmptyCollection() {
      return new ListTopicSubscriptionsFixedSizeCollection(null, 0);
    }

    @Override
    protected ListTopicSubscriptionsFixedSizeCollection createCollection(
        List<ListTopicSubscriptionsPage> pages, int collectionSize) {
      return new ListTopicSubscriptionsFixedSizeCollection(pages, collectionSize);
    }
  }

  public static class ListSubscriptionsPagedResponse
      extends AbstractPagedListResponse<
          ListSubscriptionsRequest,
          ListSubscriptionsResponse,
          Subscription,
          ListSubscriptionsPage,
          ListSubscriptionsFixedSizeCollection> {

    public static ApiFuture<ListSubscriptionsPagedResponse> createAsync(
        PageContext<ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription> context,
        ApiFuture<ListSubscriptionsResponse> futureResponse) {
      ApiFuture<ListSubscriptionsPage> futurePage =
          ListSubscriptionsPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage,
          new ApiFunction<ListSubscriptionsPage, ListSubscriptionsPagedResponse>() {
            @Override
            public ListSubscriptionsPagedResponse apply(ListSubscriptionsPage input) {
              return new ListSubscriptionsPagedResponse(input);
            }
          },
          MoreExecutors.directExecutor());
    }

    private ListSubscriptionsPagedResponse(ListSubscriptionsPage page) {
      super(page, ListSubscriptionsFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListSubscriptionsPage
      extends AbstractPage<
          ListSubscriptionsRequest,
          ListSubscriptionsResponse,
          Subscription,
          ListSubscriptionsPage> {

    private ListSubscriptionsPage(
        PageContext<ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription> context,
        ListSubscriptionsResponse response) {
      super(context, response);
    }

    private static ListSubscriptionsPage createEmptyPage() {
      return new ListSubscriptionsPage(null, null);
    }

    @Override
    protected ListSubscriptionsPage createPage(
        PageContext<ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription> context,
        ListSubscriptionsResponse response) {
      return new ListSubscriptionsPage(context, response);
    }

    @Override
    public ApiFuture<ListSubscriptionsPage> createPageAsync(
        PageContext<ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription> context,
        ApiFuture<ListSubscriptionsResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListSubscriptionsFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListSubscriptionsRequest,
          ListSubscriptionsResponse,
          Subscription,
          ListSubscriptionsPage,
          ListSubscriptionsFixedSizeCollection> {

    private ListSubscriptionsFixedSizeCollection(
        List<ListSubscriptionsPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListSubscriptionsFixedSizeCollection createEmptyCollection() {
      return new ListSubscriptionsFixedSizeCollection(null, 0);
    }

    @Override
    protected ListSubscriptionsFixedSizeCollection createCollection(
        List<ListSubscriptionsPage> pages, int collectionSize) {
      return new ListSubscriptionsFixedSizeCollection(pages, collectionSize);
    }
  }
}
