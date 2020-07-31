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
package com.google.cloud.pubsublite.v1;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.pubsublite.proto.PartitionAssignment;
import com.google.cloud.pubsublite.proto.PartitionAssignmentRequest;
import com.google.cloud.pubsublite.v1.stub.PartitionAssignmentServiceStub;
import com.google.cloud.pubsublite.v1.stub.PartitionAssignmentServiceStubSettings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND SERVICE
/**
 * Service Description: The service that a subscriber client application uses to determine which
 * partitions it should connect to.
 *
 * <p>This is an under development API being published to build client libraries. Users will not be
 * able to access it until fully launched.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>
 * <code>
 * try (PartitionAssignmentServiceClient partitionAssignmentServiceClient = PartitionAssignmentServiceClient.create()) {
 *   PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();
 *   ApiFuture&lt;PartitionAssignment&gt; future = partitionAssignmentServiceClient.assignPartitionsCallable().futureCall(request);
 *   // Do something
 *   PartitionAssignment response = future.get();
 * }
 * </code>
 * </pre>
 *
 * <p>Note: close() needs to be called on the partitionAssignmentServiceClient object to clean up
 * resources such as threads. In the example above, try-with-resources is used, which automatically
 * calls close().
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
 * <p>This class can be customized by passing in a custom instance of
 * PartitionAssignmentServiceSettings to create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>
 * <code>
 * PartitionAssignmentServiceSettings partitionAssignmentServiceSettings =
 *     PartitionAssignmentServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * PartitionAssignmentServiceClient partitionAssignmentServiceClient =
 *     PartitionAssignmentServiceClient.create(partitionAssignmentServiceSettings);
 * </code>
 * </pre>
 *
 * To customize the endpoint:
 *
 * <pre>
 * <code>
 * PartitionAssignmentServiceSettings partitionAssignmentServiceSettings =
 *     PartitionAssignmentServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * PartitionAssignmentServiceClient partitionAssignmentServiceClient =
 *     PartitionAssignmentServiceClient.create(partitionAssignmentServiceSettings);
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class PartitionAssignmentServiceClient implements BackgroundResource {
  private final PartitionAssignmentServiceSettings settings;
  private final PartitionAssignmentServiceStub stub;

  /** Constructs an instance of PartitionAssignmentServiceClient with default settings. */
  public static final PartitionAssignmentServiceClient create() throws IOException {
    return create(PartitionAssignmentServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of PartitionAssignmentServiceClient, using the given settings. The
   * channels are created based on the settings passed in, or defaults for any settings that are not
   * set.
   */
  public static final PartitionAssignmentServiceClient create(
      PartitionAssignmentServiceSettings settings) throws IOException {
    return new PartitionAssignmentServiceClient(settings);
  }

  /**
   * Constructs an instance of PartitionAssignmentServiceClient, using the given stub for making
   * calls. This is for advanced usage - prefer to use PartitionAssignmentServiceSettings}.
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final PartitionAssignmentServiceClient create(PartitionAssignmentServiceStub stub) {
    return new PartitionAssignmentServiceClient(stub);
  }

  /**
   * Constructs an instance of PartitionAssignmentServiceClient, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected PartitionAssignmentServiceClient(PartitionAssignmentServiceSettings settings)
      throws IOException {
    this.settings = settings;
    this.stub = ((PartitionAssignmentServiceStubSettings) settings.getStubSettings()).createStub();
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected PartitionAssignmentServiceClient(PartitionAssignmentServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final PartitionAssignmentServiceSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public PartitionAssignmentServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Assign partitions for this client to handle for the specified subscription.
   *
   * <p>The client must send an InitialPartitionAssignmentRequest first. The server will then send
   * at most one unacknowledged PartitionAssignment outstanding on the stream at a time. The client
   * should send a PartitionAssignmentAck after updating the partitions it is connected to to
   * reflect the new assignment.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (PartitionAssignmentServiceClient partitionAssignmentServiceClient = PartitionAssignmentServiceClient.create()) {
   *   BidiStream&lt;PartitionAssignmentRequest, PartitionAssignment&gt; bidiStream =
   *       partitionAssignmentServiceClient.assignPartitionsCallable().call();
   *
   *   PartitionAssignmentRequest request = PartitionAssignmentRequest.newBuilder().build();
   *   bidiStream.send(request);
   *   for (PartitionAssignment response : bidiStream) {
   *     // Do something when receive a response
   *   }
   * }
   * </code></pre>
   */
  public final BidiStreamingCallable<PartitionAssignmentRequest, PartitionAssignment>
      assignPartitionsCallable() {
    return stub.assignPartitionsCallable();
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
