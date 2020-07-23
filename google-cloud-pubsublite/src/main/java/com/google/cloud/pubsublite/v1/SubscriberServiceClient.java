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
import com.google.cloud.pubsublite.proto.SubscribeRequest;
import com.google.cloud.pubsublite.proto.SubscribeResponse;
import com.google.cloud.pubsublite.v1.stub.SubscriberServiceStub;
import com.google.cloud.pubsublite.v1.stub.SubscriberServiceStubSettings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND SERVICE
/**
 * Service Description: The service that a subscriber client application uses to receive messages
 * from subscriptions.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>
 * <code>
 * try (SubscriberServiceClient subscriberServiceClient = SubscriberServiceClient.create()) {
 *   SubscribeRequest request = SubscribeRequest.newBuilder().build();
 *   ApiFuture&lt;SubscribeResponse&gt; future = subscriberServiceClient.subscribeCallable().futureCall(request);
 *   // Do something
 *   SubscribeResponse response = future.get();
 * }
 * </code>
 * </pre>
 *
 * <p>Note: close() needs to be called on the subscriberServiceClient object to clean up resources
 * such as threads. In the example above, try-with-resources is used, which automatically calls
 * close().
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
 * <p>This class can be customized by passing in a custom instance of SubscriberServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>
 * <code>
 * SubscriberServiceSettings subscriberServiceSettings =
 *     SubscriberServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * SubscriberServiceClient subscriberServiceClient =
 *     SubscriberServiceClient.create(subscriberServiceSettings);
 * </code>
 * </pre>
 *
 * To customize the endpoint:
 *
 * <pre>
 * <code>
 * SubscriberServiceSettings subscriberServiceSettings =
 *     SubscriberServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * SubscriberServiceClient subscriberServiceClient =
 *     SubscriberServiceClient.create(subscriberServiceSettings);
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
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
   * is for advanced usage - prefer to use SubscriberServiceSettings}.
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
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

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected SubscriberServiceClient(SubscriberServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final SubscriberServiceSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public SubscriberServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD
  /**
   * Establishes a stream with the server for receiving messages.
   *
   * <p>Sample code:
   *
   * <pre><code>
   * try (SubscriberServiceClient subscriberServiceClient = SubscriberServiceClient.create()) {
   *   BidiStream&lt;SubscribeRequest, SubscribeResponse&gt; bidiStream =
   *       subscriberServiceClient.subscribeCallable().call();
   *
   *   SubscribeRequest request = SubscribeRequest.newBuilder().build();
   *   bidiStream.send(request);
   *   for (SubscribeResponse response : bidiStream) {
   *     // Do something when receive a response
   *   }
   * }
   * </code></pre>
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
