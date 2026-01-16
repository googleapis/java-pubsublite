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

package com.google.cloud.pubsublite.v1;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.cloud.pubsublite.v1.stub.SubscriberServiceStub;
import com.google.cloud.pubsublite.v1.stub.SubscriberServiceStubSettings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: The service that a subscriber client application uses to receive messages
 * from subscriptions.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
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
 * <p>Note: close() needs to be called on the SubscriberServiceClient object to clean up resources
 * such as threads. In the example above, try-with-resources is used, which automatically calls
 * close().
 *
 * <table>
 *    <caption>Methods</caption>
 *    <tr>
 *      <th>Method</th>
 *      <th>Description</th>
 *      <th>Method Variants</th>
 *    </tr>
 *    <tr>
 *      <td><p> Subscribe</td>
 *      <td><p> Establishes a stream with the server for receiving messages.</td>
 *      <td>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> subscribeCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *  </table>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of SubscriberServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * SubscriberServiceSettings subscriberServiceSettings =
 *     SubscriberServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * SubscriberServiceClient subscriberServiceClient =
 *     SubscriberServiceClient.create(subscriberServiceSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * SubscriberServiceSettings subscriberServiceSettings =
 *     SubscriberServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * SubscriberServiceClient subscriberServiceClient =
 *     SubscriberServiceClient.create(subscriberServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class SubscriberServiceClient implements BackgroundResource {
  private final SubscriberServiceSettings settings;
  private final SubscriberServiceStub stub;

  /** Constructs an instance of SubscriberServiceClient with default settings. */
  public static final SubscriberServiceClient create() throws IOException {
    return create(SubscriberServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of SubscriberServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final SubscriberServiceClient create(SubscriberServiceSettings settings)
      throws IOException {
    return new SubscriberServiceClient(settings);
  }

  /**
   * Constructs an instance of SubscriberServiceClient, using the given stub for making calls. This
   * is for advanced usage - prefer using create(SubscriberServiceSettings).
   */
  public static final SubscriberServiceClient create(SubscriberServiceStub stub) {
    return new SubscriberServiceClient(stub);
  }

  /**
   * Constructs an instance of SubscriberServiceClient, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected SubscriberServiceClient(SubscriberServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((SubscriberServiceStubSettings) settings.getStubSettings()).createStub();
  }

  protected SubscriberServiceClient(SubscriberServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final SubscriberServiceSettings getSettings() {
    return settings;
  }

  public SubscriberServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Establishes a stream with the server for receiving messages.
   *
   * <p>Sample code:
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
   */
  public final BidiStreamingCallable<SubscribeRequest, SubscribeResponse> subscribeCallable() {
    return stub.subscribeCallable();
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
