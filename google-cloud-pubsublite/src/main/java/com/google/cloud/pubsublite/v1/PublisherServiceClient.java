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

package com.google.cloud.pubsublite.v1;

import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.pubsublite.proto.PublishRequest;
import com.google.cloud.pubsublite.proto.PublishResponse;
import com.google.cloud.pubsublite.v1.stub.PublisherServiceStub;
import com.google.cloud.pubsublite.v1.stub.PublisherServiceStubSettings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: The service that a publisher client application uses to publish messages to
 * topics. Published messages are retained by the service for the duration of the retention period
 * configured for the respective topic, and are delivered to subscriber clients upon request (via
 * the `SubscriberService`).
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
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
 * <p>Note: close() needs to be called on the PublisherServiceClient object to clean up resources
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
 * <p>This class can be customized by passing in a custom instance of PublisherServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * PublisherServiceSettings publisherServiceSettings =
 *     PublisherServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * PublisherServiceClient publisherServiceClient =
 *     PublisherServiceClient.create(publisherServiceSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * PublisherServiceSettings publisherServiceSettings =
 *     PublisherServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * PublisherServiceClient publisherServiceClient =
 *     PublisherServiceClient.create(publisherServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class PublisherServiceClient implements BackgroundResource {
  private final PublisherServiceSettings settings;
  private final PublisherServiceStub stub;

  /** Constructs an instance of PublisherServiceClient with default settings. */
  public static final PublisherServiceClient create() throws IOException {
    return create(PublisherServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of PublisherServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final PublisherServiceClient create(PublisherServiceSettings settings)
      throws IOException {
    return new PublisherServiceClient(settings);
  }

  /**
   * Constructs an instance of PublisherServiceClient, using the given stub for making calls. This
   * is for advanced usage - prefer using create(PublisherServiceSettings).
   */
  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public static final PublisherServiceClient create(PublisherServiceStub stub) {
    return new PublisherServiceClient(stub);
  }

  /**
   * Constructs an instance of PublisherServiceClient, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected PublisherServiceClient(PublisherServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((PublisherServiceStubSettings) settings.getStubSettings()).createStub();
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  protected PublisherServiceClient(PublisherServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final PublisherServiceSettings getSettings() {
    return settings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public PublisherServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Establishes a stream with the server for publishing messages. Once the stream is initialized,
   * the client publishes messages by sending publish requests on the stream. The server responds
   * with a PublishResponse for each PublishRequest sent by the client, in the same order that the
   * requests were sent. Note that multiple PublishRequests can be in flight simultaneously, but
   * they will be processed by the server in the order that they are sent by the client on a given
   * stream.
   *
   * <p>Sample code:
   *
   * <pre>{@code
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
   */
  public final BidiStreamingCallable<PublishRequest, PublishResponse> publishCallable() {
    return stub.publishCallable();
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
