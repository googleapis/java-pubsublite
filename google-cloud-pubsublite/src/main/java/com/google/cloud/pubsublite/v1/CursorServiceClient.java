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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.paging.AbstractFixedSizeCollection;
import com.google.api.gax.paging.AbstractPage;
import com.google.api.gax.paging.AbstractPagedListResponse;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.cloud.pubsublite.proto.SubscriptionName;
import com.google.cloud.pubsublite.v1.stub.CursorServiceStub;
import com.google.cloud.pubsublite.v1.stub.CursorServiceStubSettings;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: The service that a subscriber client application uses to manage committed
 * cursors while receiving messsages. A cursor represents a subscriber's progress within a topic
 * partition for a given subscription.
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
 * <p>Note: close() needs to be called on the CursorServiceClient object to clean up resources such
 * as threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <table>
 *    <caption>Methods</caption>
 *    <tr>
 *      <th>Method</th>
 *      <th>Description</th>
 *      <th>Method Variants</th>
 *    </tr>
 *    <tr>
 *      <td><p> StreamingCommitCursor</td>
 *      <td><p> Establishes a stream with the server for managing committed cursors.</td>
 *      <td>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> streamingCommitCursorCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> CommitCursor</td>
 *      <td><p> Updates the committed cursor.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> commitCursor(CommitCursorRequest request)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> commitCursorCallable()
 *      </ul>
 *       </td>
 *    </tr>
 *    <tr>
 *      <td><p> ListPartitionCursors</td>
 *      <td><p> Returns all committed cursor information for a subscription.</td>
 *      <td>
 *      <p>Request object method variants only take one parameter, a request object, which must be constructed before the call.</p>
 *      <ul>
 *           <li><p> listPartitionCursors(ListPartitionCursorsRequest request)
 *      </ul>
 *      <p>"Flattened" method variants have converted the fields of the request object into function parameters to enable multiple ways to call the same method.</p>
 *      <ul>
 *           <li><p> listPartitionCursors(SubscriptionName parent)
 *           <li><p> listPartitionCursors(String parent)
 *      </ul>
 *      <p>Callable method variants take no parameters and return an immutable API callable object, which can be used to initiate calls to the service.</p>
 *      <ul>
 *           <li><p> listPartitionCursorsPagedCallable()
 *           <li><p> listPartitionCursorsCallable()
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
 * <p>This class can be customized by passing in a custom instance of CursorServiceSettings to
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
 * CursorServiceSettings cursorServiceSettings =
 *     CursorServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * CursorServiceClient cursorServiceClient = CursorServiceClient.create(cursorServiceSettings);
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
 * CursorServiceSettings cursorServiceSettings =
 *     CursorServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * CursorServiceClient cursorServiceClient = CursorServiceClient.create(cursorServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@Generated("by gapic-generator-java")
public class CursorServiceClient implements BackgroundResource {
  private final CursorServiceSettings settings;
  private final CursorServiceStub stub;

  /** Constructs an instance of CursorServiceClient with default settings. */
  public static final CursorServiceClient create() throws IOException {
    return create(CursorServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of CursorServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final CursorServiceClient create(CursorServiceSettings settings)
      throws IOException {
    return new CursorServiceClient(settings);
  }

  /**
   * Constructs an instance of CursorServiceClient, using the given stub for making calls. This is
   * for advanced usage - prefer using create(CursorServiceSettings).
   */
  public static final CursorServiceClient create(CursorServiceStub stub) {
    return new CursorServiceClient(stub);
  }

  /**
   * Constructs an instance of CursorServiceClient, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected CursorServiceClient(CursorServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((CursorServiceStubSettings) settings.getStubSettings()).createStub();
  }

  protected CursorServiceClient(CursorServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final CursorServiceSettings getSettings() {
    return settings;
  }

  public CursorServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Establishes a stream with the server for managing committed cursors.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   BidiStream<StreamingCommitCursorRequest, StreamingCommitCursorResponse> bidiStream =
   *       cursorServiceClient.streamingCommitCursorCallable().call();
   *   StreamingCommitCursorRequest request = StreamingCommitCursorRequest.newBuilder().build();
   *   bidiStream.send(request);
   *   for (StreamingCommitCursorResponse response : bidiStream) {
   *     // Do something when a response is received.
   *   }
   * }
   * }</pre>
   */
  public final BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorCallable() {
    return stub.streamingCommitCursorCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates the committed cursor.
   *
   * <p>Sample code:
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
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final CommitCursorResponse commitCursor(CommitCursorRequest request) {
    return commitCursorCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Updates the committed cursor.
   *
   * <p>Sample code:
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
   *   ApiFuture<CommitCursorResponse> future =
   *       cursorServiceClient.commitCursorCallable().futureCall(request);
   *   // Do something.
   *   CommitCursorResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable() {
    return stub.commitCursorCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns all committed cursor information for a subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   SubscriptionName parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]");
   *   for (PartitionCursor element :
   *       cursorServiceClient.listPartitionCursors(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The subscription for which to retrieve cursors. Structured like
   *     `projects/{project_number}/locations/{location}/subscriptions/{subscription_id}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListPartitionCursorsPagedResponse listPartitionCursors(SubscriptionName parent) {
    ListPartitionCursorsRequest request =
        ListPartitionCursorsRequest.newBuilder()
            .setParent(parent == null ? null : parent.toString())
            .build();
    return listPartitionCursors(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns all committed cursor information for a subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   String parent = SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString();
   *   for (PartitionCursor element :
   *       cursorServiceClient.listPartitionCursors(parent).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param parent Required. The subscription for which to retrieve cursors. Structured like
   *     `projects/{project_number}/locations/{location}/subscriptions/{subscription_id}`.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListPartitionCursorsPagedResponse listPartitionCursors(String parent) {
    ListPartitionCursorsRequest request =
        ListPartitionCursorsRequest.newBuilder().setParent(parent).build();
    return listPartitionCursors(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns all committed cursor information for a subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   ListPartitionCursorsRequest request =
   *       ListPartitionCursorsRequest.newBuilder()
   *           .setParent(
   *               SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   for (PartitionCursor element :
   *       cursorServiceClient.listPartitionCursors(request).iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final ListPartitionCursorsPagedResponse listPartitionCursors(
      ListPartitionCursorsRequest request) {
    return listPartitionCursorsPagedCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns all committed cursor information for a subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   ListPartitionCursorsRequest request =
   *       ListPartitionCursorsRequest.newBuilder()
   *           .setParent(
   *               SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   ApiFuture<PartitionCursor> future =
   *       cursorServiceClient.listPartitionCursorsPagedCallable().futureCall(request);
   *   // Do something.
   *   for (PartitionCursor element : future.get().iterateAll()) {
   *     // doThingsWith(element);
   *   }
   * }
   * }</pre>
   */
  public final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable() {
    return stub.listPartitionCursorsPagedCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Returns all committed cursor information for a subscription.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (CursorServiceClient cursorServiceClient = CursorServiceClient.create()) {
   *   ListPartitionCursorsRequest request =
   *       ListPartitionCursorsRequest.newBuilder()
   *           .setParent(
   *               SubscriptionName.of("[PROJECT]", "[LOCATION]", "[SUBSCRIPTION]").toString())
   *           .setPageSize(883849137)
   *           .setPageToken("pageToken873572522")
   *           .build();
   *   while (true) {
   *     ListPartitionCursorsResponse response =
   *         cursorServiceClient.listPartitionCursorsCallable().call(request);
   *     for (PartitionCursor element : response.getPartitionCursorsList()) {
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
  public final UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable() {
    return stub.listPartitionCursorsCallable();
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

  public static class ListPartitionCursorsPagedResponse
      extends AbstractPagedListResponse<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          PartitionCursor,
          ListPartitionCursorsPage,
          ListPartitionCursorsFixedSizeCollection> {

    public static ApiFuture<ListPartitionCursorsPagedResponse> createAsync(
        PageContext<ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
            context,
        ApiFuture<ListPartitionCursorsResponse> futureResponse) {
      ApiFuture<ListPartitionCursorsPage> futurePage =
          ListPartitionCursorsPage.createEmptyPage().createPageAsync(context, futureResponse);
      return ApiFutures.transform(
          futurePage,
          input -> new ListPartitionCursorsPagedResponse(input),
          MoreExecutors.directExecutor());
    }

    private ListPartitionCursorsPagedResponse(ListPartitionCursorsPage page) {
      super(page, ListPartitionCursorsFixedSizeCollection.createEmptyCollection());
    }
  }

  public static class ListPartitionCursorsPage
      extends AbstractPage<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          PartitionCursor,
          ListPartitionCursorsPage> {

    private ListPartitionCursorsPage(
        PageContext<ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
            context,
        ListPartitionCursorsResponse response) {
      super(context, response);
    }

    private static ListPartitionCursorsPage createEmptyPage() {
      return new ListPartitionCursorsPage(null, null);
    }

    @Override
    protected ListPartitionCursorsPage createPage(
        PageContext<ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
            context,
        ListPartitionCursorsResponse response) {
      return new ListPartitionCursorsPage(context, response);
    }

    @Override
    public ApiFuture<ListPartitionCursorsPage> createPageAsync(
        PageContext<ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
            context,
        ApiFuture<ListPartitionCursorsResponse> futureResponse) {
      return super.createPageAsync(context, futureResponse);
    }
  }

  public static class ListPartitionCursorsFixedSizeCollection
      extends AbstractFixedSizeCollection<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          PartitionCursor,
          ListPartitionCursorsPage,
          ListPartitionCursorsFixedSizeCollection> {

    private ListPartitionCursorsFixedSizeCollection(
        List<ListPartitionCursorsPage> pages, int collectionSize) {
      super(pages, collectionSize);
    }

    private static ListPartitionCursorsFixedSizeCollection createEmptyCollection() {
      return new ListPartitionCursorsFixedSizeCollection(null, 0);
    }

    @Override
    protected ListPartitionCursorsFixedSizeCollection createCollection(
        List<ListPartitionCursorsPage> pages, int collectionSize) {
      return new ListPartitionCursorsFixedSizeCollection(pages, collectionSize);
    }
  }
}
