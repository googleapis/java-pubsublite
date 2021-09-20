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

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.v1.stub.TopicStatsServiceStub;
import com.google.cloud.pubsublite.v1.stub.TopicStatsServiceStubSettings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: This service allows users to get stats about messages in their topic.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
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
 *
 * <p>Note: close() needs to be called on the TopicStatsServiceClient object to clean up resources
 * such as threads. In the example above, try-with-resources is used, which automatically calls
 * close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li> A "flattened" method. With this type of method, the fields of the request type have been
 *       converted into function parameters. It may be the case that not all fields are available as
 *       parameters, and not every API method will have a flattened method entry point.
 *   <li> A "request object" method. This type of method only takes one parameter, a request object,
 *       which must be constructed before the call. Not every API method will have a request object
 *       method.
 *   <li> A "callable" method. This type of method takes no parameters and returns an immutable API
 *       callable object, which can be used to initiate calls to the service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of TopicStatsServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * TopicStatsServiceSettings topicStatsServiceSettings =
 *     TopicStatsServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * TopicStatsServiceClient topicStatsServiceClient =
 *     TopicStatsServiceClient.create(topicStatsServiceSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * TopicStatsServiceSettings topicStatsServiceSettings =
 *     TopicStatsServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * TopicStatsServiceClient topicStatsServiceClient =
 *     TopicStatsServiceClient.create(topicStatsServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class TopicStatsServiceClient implements BackgroundResource {
  private final TopicStatsServiceSettings settings;
  private final TopicStatsServiceStub stub;

  /** Constructs an instance of TopicStatsServiceClient with default settings. */
  public static final TopicStatsServiceClient create() throws IOException {
    return create(TopicStatsServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of TopicStatsServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final TopicStatsServiceClient create(TopicStatsServiceSettings settings)
      throws IOException {
    return new TopicStatsServiceClient(settings);
  }

  /**
   * Constructs an instance of TopicStatsServiceClient, using the given stub for making calls. This
   * is for advanced usage - prefer using create(TopicStatsServiceSettings).
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final TopicStatsServiceClient create(TopicStatsServiceStub stub) {
    return new TopicStatsServiceClient(stub);
  }

  /**
   * Constructs an instance of TopicStatsServiceClient, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected TopicStatsServiceClient(TopicStatsServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((TopicStatsServiceStubSettings) settings.getStubSettings()).createStub();
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected TopicStatsServiceClient(TopicStatsServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final TopicStatsServiceSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public TopicStatsServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute statistics about a range of messages in a given topic and partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
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
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ComputeMessageStatsResponse computeMessageStats(ComputeMessageStatsRequest request) {
    return computeMessageStatsCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute statistics about a range of messages in a given topic and partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
   *   ComputeMessageStatsRequest request =
   *       ComputeMessageStatsRequest.newBuilder()
   *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPartition(-1799810326)
   *           .setStartCursor(Cursor.newBuilder().build())
   *           .setEndCursor(Cursor.newBuilder().build())
   *           .build();
   *   ApiFuture<ComputeMessageStatsResponse> future =
   *       topicStatsServiceClient.computeMessageStatsCallable().futureCall(request);
   *   // Do something.
   *   ComputeMessageStatsResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsCallable() {
    return stub.computeMessageStatsCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute the head cursor for the partition. The head cursor's offset is guaranteed to be less
   * than or equal to all messages which have not yet been acknowledged as published, and greater
   * than the offset of any message whose publish has already been acknowledged. It is zero if there
   * have never been messages in the partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
   *   ComputeHeadCursorRequest request =
   *       ComputeHeadCursorRequest.newBuilder()
   *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPartition(-1799810326)
   *           .build();
   *   ComputeHeadCursorResponse response = topicStatsServiceClient.computeHeadCursor(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ComputeHeadCursorResponse computeHeadCursor(ComputeHeadCursorRequest request) {
    return computeHeadCursorCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute the head cursor for the partition. The head cursor's offset is guaranteed to be less
   * than or equal to all messages which have not yet been acknowledged as published, and greater
   * than the offset of any message whose publish has already been acknowledged. It is zero if there
   * have never been messages in the partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
   *   ComputeHeadCursorRequest request =
   *       ComputeHeadCursorRequest.newBuilder()
   *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPartition(-1799810326)
   *           .build();
   *   ApiFuture<ComputeHeadCursorResponse> future =
   *       topicStatsServiceClient.computeHeadCursorCallable().futureCall(request);
   *   // Do something.
   *   ComputeHeadCursorResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorCallable() {
    return stub.computeHeadCursorCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute the corresponding cursor for a publish or event time in a topic partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
   *   ComputeTimeCursorRequest request =
   *       ComputeTimeCursorRequest.newBuilder()
   *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPartition(-1799810326)
   *           .setTarget(TimeTarget.newBuilder().build())
   *           .build();
   *   ComputeTimeCursorResponse response = topicStatsServiceClient.computeTimeCursor(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ComputeTimeCursorResponse computeTimeCursor(ComputeTimeCursorRequest request) {
    return computeTimeCursorCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Compute the corresponding cursor for a publish or event time in a topic partition.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * try (TopicStatsServiceClient topicStatsServiceClient = TopicStatsServiceClient.create()) {
   *   ComputeTimeCursorRequest request =
   *       ComputeTimeCursorRequest.newBuilder()
   *           .setTopic(TopicName.of("[PROJECT]", "[LOCATION]", "[TOPIC]").toString())
   *           .setPartition(-1799810326)
   *           .setTarget(TimeTarget.newBuilder().build())
   *           .build();
   *   ApiFuture<ComputeTimeCursorResponse> future =
   *       topicStatsServiceClient.computeTimeCursorCallable().futureCall(request);
   *   // Do something.
   *   ComputeTimeCursorResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorCallable() {
    return stub.computeTimeCursorCallable();
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
}
