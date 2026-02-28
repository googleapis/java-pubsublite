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

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationTopicsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListReservationsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.core.ApiFunction;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.OperationCallSettings;
import com.google.api.gax.rpc.PagedCallSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.pubsublite.proto.CreateReservationRequest;
import com.google.cloud.pubsublite.proto.CreateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.CreateTopicRequest;
import com.google.cloud.pubsublite.proto.DeleteReservationRequest;
import com.google.cloud.pubsublite.proto.DeleteSubscriptionRequest;
import com.google.cloud.pubsublite.proto.DeleteTopicRequest;
import com.google.cloud.pubsublite.proto.GetReservationRequest;
import com.google.cloud.pubsublite.proto.GetSubscriptionRequest;
import com.google.cloud.pubsublite.proto.GetTopicPartitionsRequest;
import com.google.cloud.pubsublite.proto.GetTopicRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsRequest;
import com.google.cloud.pubsublite.proto.ListReservationTopicsResponse;
import com.google.cloud.pubsublite.proto.ListReservationsRequest;
import com.google.cloud.pubsublite.proto.ListReservationsResponse;
import com.google.cloud.pubsublite.proto.ListSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsRequest;
import com.google.cloud.pubsublite.proto.ListTopicSubscriptionsResponse;
import com.google.cloud.pubsublite.proto.ListTopicsRequest;
import com.google.cloud.pubsublite.proto.ListTopicsResponse;
import com.google.cloud.pubsublite.proto.OperationMetadata;
import com.google.cloud.pubsublite.proto.Reservation;
import com.google.cloud.pubsublite.proto.SeekSubscriptionRequest;
import com.google.cloud.pubsublite.proto.SeekSubscriptionResponse;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateReservationRequest;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.cloud.pubsublite.v1.stub.AdminServiceStubSettings;
import com.google.longrunning.Operation;
import com.google.protobuf.Empty;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Settings class to configure an instance of {@link AdminServiceClient}.
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li>The default service address (pubsublite.googleapis.com) and default port (443) are used.
 *   <li>Credentials are acquired automatically through Application Default Credentials.
 *   <li>Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 *
 * <p>The builder of this class is recursive, so contained classes are themselves builders. When
 * build() is called, the tree of builders is called to create the complete settings object.
 *
 * <p>For example, to set the
 * [RetrySettings](https://cloud.google.com/java/docs/reference/gax/latest/com.google.api.gax.retrying.RetrySettings)
 * of createTopic:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * AdminServiceSettings.Builder adminServiceSettingsBuilder = AdminServiceSettings.newBuilder();
 * adminServiceSettingsBuilder
 *     .createTopicSettings()
 *     .setRetrySettings(
 *         adminServiceSettingsBuilder
 *             .createTopicSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setInitialRetryDelayDuration(Duration.ofSeconds(1))
 *             .setInitialRpcTimeoutDuration(Duration.ofSeconds(5))
 *             .setMaxAttempts(5)
 *             .setMaxRetryDelayDuration(Duration.ofSeconds(30))
 *             .setMaxRpcTimeoutDuration(Duration.ofSeconds(60))
 *             .setRetryDelayMultiplier(1.3)
 *             .setRpcTimeoutMultiplier(1.5)
 *             .setTotalTimeoutDuration(Duration.ofSeconds(300))
 *             .build());
 * AdminServiceSettings adminServiceSettings = adminServiceSettingsBuilder.build();
 * }</pre>
 *
 * Please refer to the [Client Side Retry
 * Guide](https://docs.cloud.google.com/java/docs/client-retries) for additional support in setting
 * retries.
 *
 * <p>To configure the RetrySettings of a Long Running Operation method, create an
 * OperationTimedPollAlgorithm object and update the RPC's polling algorithm. For example, to
 * configure the RetrySettings for seekSubscription:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * AdminServiceSettings.Builder adminServiceSettingsBuilder = AdminServiceSettings.newBuilder();
 * TimedRetryAlgorithm timedRetryAlgorithm =
 *     OperationalTimedPollAlgorithm.create(
 *         RetrySettings.newBuilder()
 *             .setInitialRetryDelayDuration(Duration.ofMillis(500))
 *             .setRetryDelayMultiplier(1.5)
 *             .setMaxRetryDelayDuration(Duration.ofMillis(5000))
 *             .setTotalTimeoutDuration(Duration.ofHours(24))
 *             .build());
 * adminServiceSettingsBuilder
 *     .createClusterOperationSettings()
 *     .setPollingAlgorithm(timedRetryAlgorithm)
 *     .build();
 * }</pre>
 */
@Generated("by gapic-generator-java")
public class AdminServiceSettings extends ClientSettings<AdminServiceSettings> {

  /** Returns the object with the settings used for calls to createTopic. */
  public UnaryCallSettings<CreateTopicRequest, Topic> createTopicSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).createTopicSettings();
  }

  /** Returns the object with the settings used for calls to getTopic. */
  public UnaryCallSettings<GetTopicRequest, Topic> getTopicSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).getTopicSettings();
  }

  /** Returns the object with the settings used for calls to getTopicPartitions. */
  public UnaryCallSettings<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).getTopicPartitionsSettings();
  }

  /** Returns the object with the settings used for calls to listTopics. */
  public PagedCallSettings<ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
      listTopicsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).listTopicsSettings();
  }

  /** Returns the object with the settings used for calls to updateTopic. */
  public UnaryCallSettings<UpdateTopicRequest, Topic> updateTopicSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).updateTopicSettings();
  }

  /** Returns the object with the settings used for calls to deleteTopic. */
  public UnaryCallSettings<DeleteTopicRequest, Empty> deleteTopicSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).deleteTopicSettings();
  }

  /** Returns the object with the settings used for calls to listTopicSubscriptions. */
  public PagedCallSettings<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).listTopicSubscriptionsSettings();
  }

  /** Returns the object with the settings used for calls to createSubscription. */
  public UnaryCallSettings<CreateSubscriptionRequest, Subscription> createSubscriptionSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).createSubscriptionSettings();
  }

  /** Returns the object with the settings used for calls to getSubscription. */
  public UnaryCallSettings<GetSubscriptionRequest, Subscription> getSubscriptionSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).getSubscriptionSettings();
  }

  /** Returns the object with the settings used for calls to listSubscriptions. */
  public PagedCallSettings<
          ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
      listSubscriptionsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).listSubscriptionsSettings();
  }

  /** Returns the object with the settings used for calls to updateSubscription. */
  public UnaryCallSettings<UpdateSubscriptionRequest, Subscription> updateSubscriptionSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).updateSubscriptionSettings();
  }

  /** Returns the object with the settings used for calls to deleteSubscription. */
  public UnaryCallSettings<DeleteSubscriptionRequest, Empty> deleteSubscriptionSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).deleteSubscriptionSettings();
  }

  /** Returns the object with the settings used for calls to seekSubscription. */
  public UnaryCallSettings<SeekSubscriptionRequest, Operation> seekSubscriptionSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).seekSubscriptionSettings();
  }

  /** Returns the object with the settings used for calls to seekSubscription. */
  public OperationCallSettings<SeekSubscriptionRequest, SeekSubscriptionResponse, OperationMetadata>
      seekSubscriptionOperationSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).seekSubscriptionOperationSettings();
  }

  /** Returns the object with the settings used for calls to createReservation. */
  public UnaryCallSettings<CreateReservationRequest, Reservation> createReservationSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).createReservationSettings();
  }

  /** Returns the object with the settings used for calls to getReservation. */
  public UnaryCallSettings<GetReservationRequest, Reservation> getReservationSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).getReservationSettings();
  }

  /** Returns the object with the settings used for calls to listReservations. */
  public PagedCallSettings<
          ListReservationsRequest, ListReservationsResponse, ListReservationsPagedResponse>
      listReservationsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).listReservationsSettings();
  }

  /** Returns the object with the settings used for calls to updateReservation. */
  public UnaryCallSettings<UpdateReservationRequest, Reservation> updateReservationSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).updateReservationSettings();
  }

  /** Returns the object with the settings used for calls to deleteReservation. */
  public UnaryCallSettings<DeleteReservationRequest, Empty> deleteReservationSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).deleteReservationSettings();
  }

  /** Returns the object with the settings used for calls to listReservationTopics. */
  public PagedCallSettings<
          ListReservationTopicsRequest,
          ListReservationTopicsResponse,
          ListReservationTopicsPagedResponse>
      listReservationTopicsSettings() {
    return ((AdminServiceStubSettings) getStubSettings()).listReservationTopicsSettings();
  }

  public static final AdminServiceSettings create(AdminServiceStubSettings stub)
      throws IOException {
    return new AdminServiceSettings.Builder(stub.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return AdminServiceStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return AdminServiceStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return AdminServiceStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return AdminServiceStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return AdminServiceStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return AdminServiceStubSettings.defaultTransportChannelProvider();
  }

  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return AdminServiceStubSettings.defaultApiClientHeaderProviderBuilder();
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder() {
    return Builder.createDefault();
  }

  /** Returns a new builder for this class. */
  public static Builder newBuilder(ClientContext clientContext) {
    return new Builder(clientContext);
  }

  /** Returns a builder containing all the values of this settings class. */
  public Builder toBuilder() {
    return new Builder(this);
  }

  protected AdminServiceSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for AdminServiceSettings. */
  public static class Builder extends ClientSettings.Builder<AdminServiceSettings, Builder> {

    protected Builder() throws IOException {
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(AdminServiceStubSettings.newBuilder(clientContext));
    }

    protected Builder(AdminServiceSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(AdminServiceStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    private static Builder createDefault() {
      return new Builder(AdminServiceStubSettings.newBuilder());
    }

    public AdminServiceStubSettings.Builder getStubSettingsBuilder() {
      return ((AdminServiceStubSettings.Builder) getStubSettings());
    }

    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) {
      super.applyToAllUnaryMethods(
          getStubSettingsBuilder().unaryMethodSettingsBuilders(), settingsUpdater);
      return this;
    }

    /** Returns the builder for the settings used for calls to createTopic. */
    public UnaryCallSettings.Builder<CreateTopicRequest, Topic> createTopicSettings() {
      return getStubSettingsBuilder().createTopicSettings();
    }

    /** Returns the builder for the settings used for calls to getTopic. */
    public UnaryCallSettings.Builder<GetTopicRequest, Topic> getTopicSettings() {
      return getStubSettingsBuilder().getTopicSettings();
    }

    /** Returns the builder for the settings used for calls to getTopicPartitions. */
    public UnaryCallSettings.Builder<GetTopicPartitionsRequest, TopicPartitions>
        getTopicPartitionsSettings() {
      return getStubSettingsBuilder().getTopicPartitionsSettings();
    }

    /** Returns the builder for the settings used for calls to listTopics. */
    public PagedCallSettings.Builder<ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
        listTopicsSettings() {
      return getStubSettingsBuilder().listTopicsSettings();
    }

    /** Returns the builder for the settings used for calls to updateTopic. */
    public UnaryCallSettings.Builder<UpdateTopicRequest, Topic> updateTopicSettings() {
      return getStubSettingsBuilder().updateTopicSettings();
    }

    /** Returns the builder for the settings used for calls to deleteTopic. */
    public UnaryCallSettings.Builder<DeleteTopicRequest, Empty> deleteTopicSettings() {
      return getStubSettingsBuilder().deleteTopicSettings();
    }

    /** Returns the builder for the settings used for calls to listTopicSubscriptions. */
    public PagedCallSettings.Builder<
            ListTopicSubscriptionsRequest,
            ListTopicSubscriptionsResponse,
            ListTopicSubscriptionsPagedResponse>
        listTopicSubscriptionsSettings() {
      return getStubSettingsBuilder().listTopicSubscriptionsSettings();
    }

    /** Returns the builder for the settings used for calls to createSubscription. */
    public UnaryCallSettings.Builder<CreateSubscriptionRequest, Subscription>
        createSubscriptionSettings() {
      return getStubSettingsBuilder().createSubscriptionSettings();
    }

    /** Returns the builder for the settings used for calls to getSubscription. */
    public UnaryCallSettings.Builder<GetSubscriptionRequest, Subscription>
        getSubscriptionSettings() {
      return getStubSettingsBuilder().getSubscriptionSettings();
    }

    /** Returns the builder for the settings used for calls to listSubscriptions. */
    public PagedCallSettings.Builder<
            ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
        listSubscriptionsSettings() {
      return getStubSettingsBuilder().listSubscriptionsSettings();
    }

    /** Returns the builder for the settings used for calls to updateSubscription. */
    public UnaryCallSettings.Builder<UpdateSubscriptionRequest, Subscription>
        updateSubscriptionSettings() {
      return getStubSettingsBuilder().updateSubscriptionSettings();
    }

    /** Returns the builder for the settings used for calls to deleteSubscription. */
    public UnaryCallSettings.Builder<DeleteSubscriptionRequest, Empty>
        deleteSubscriptionSettings() {
      return getStubSettingsBuilder().deleteSubscriptionSettings();
    }

    /** Returns the builder for the settings used for calls to seekSubscription. */
    public UnaryCallSettings.Builder<SeekSubscriptionRequest, Operation>
        seekSubscriptionSettings() {
      return getStubSettingsBuilder().seekSubscriptionSettings();
    }

    /** Returns the builder for the settings used for calls to seekSubscription. */
    public OperationCallSettings.Builder<
            SeekSubscriptionRequest, SeekSubscriptionResponse, OperationMetadata>
        seekSubscriptionOperationSettings() {
      return getStubSettingsBuilder().seekSubscriptionOperationSettings();
    }

    /** Returns the builder for the settings used for calls to createReservation. */
    public UnaryCallSettings.Builder<CreateReservationRequest, Reservation>
        createReservationSettings() {
      return getStubSettingsBuilder().createReservationSettings();
    }

    /** Returns the builder for the settings used for calls to getReservation. */
    public UnaryCallSettings.Builder<GetReservationRequest, Reservation> getReservationSettings() {
      return getStubSettingsBuilder().getReservationSettings();
    }

    /** Returns the builder for the settings used for calls to listReservations. */
    public PagedCallSettings.Builder<
            ListReservationsRequest, ListReservationsResponse, ListReservationsPagedResponse>
        listReservationsSettings() {
      return getStubSettingsBuilder().listReservationsSettings();
    }

    /** Returns the builder for the settings used for calls to updateReservation. */
    public UnaryCallSettings.Builder<UpdateReservationRequest, Reservation>
        updateReservationSettings() {
      return getStubSettingsBuilder().updateReservationSettings();
    }

    /** Returns the builder for the settings used for calls to deleteReservation. */
    public UnaryCallSettings.Builder<DeleteReservationRequest, Empty> deleteReservationSettings() {
      return getStubSettingsBuilder().deleteReservationSettings();
    }

    /** Returns the builder for the settings used for calls to listReservationTopics. */
    public PagedCallSettings.Builder<
            ListReservationTopicsRequest,
            ListReservationTopicsResponse,
            ListReservationTopicsPagedResponse>
        listReservationTopicsSettings() {
      return getStubSettingsBuilder().listReservationTopicsSettings();
    }

    @Override
    public AdminServiceSettings build() throws IOException {
      return new AdminServiceSettings(this);
    }
  }
}
