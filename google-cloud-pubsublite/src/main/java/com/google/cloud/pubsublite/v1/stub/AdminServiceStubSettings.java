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

package com.google.cloud.pubsublite.v1.stub;

import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicSubscriptionsPagedResponse;
import static com.google.cloud.pubsublite.v1.AdminServiceClient.ListTopicsPagedResponse;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiCallContext;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.PageContext;
import com.google.api.gax.rpc.PagedCallSettings;
import com.google.api.gax.rpc.PagedListDescriptor;
import com.google.api.gax.rpc.PagedListResponseFactory;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
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
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.TopicPartitions;
import com.google.cloud.pubsublite.proto.UpdateSubscriptionRequest;
import com.google.cloud.pubsublite.proto.UpdateTopicRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.protobuf.Empty;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.threeten.bp.Duration;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Settings class to configure an instance of {@link AdminServiceStub}.
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
 * <p>For example, to set the total timeout of createTopic to 30 seconds:
 *
 * <pre>{@code
 * AdminServiceStubSettings.Builder adminServiceSettingsBuilder =
 *     AdminServiceStubSettings.newBuilder();
 * adminServiceSettingsBuilder
 *     .createTopicSettings()
 *     .setRetrySettings(
 *         adminServiceSettingsBuilder
 *             .createTopicSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * AdminServiceStubSettings adminServiceSettings = adminServiceSettingsBuilder.build();
 * }</pre>
 */
@Generated("by gapic-generator-java")
public class AdminServiceStubSettings extends StubSettings<AdminServiceStubSettings> {
  /** The default scopes of the service. */
  private static final ImmutableList<String> DEFAULT_SERVICE_SCOPES =
      ImmutableList.<String>builder().add("https://www.googleapis.com/auth/cloud-platform").build();

  private final UnaryCallSettings<CreateTopicRequest, Topic> createTopicSettings;
  private final UnaryCallSettings<GetTopicRequest, Topic> getTopicSettings;
  private final UnaryCallSettings<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsSettings;
  private final PagedCallSettings<ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
      listTopicsSettings;
  private final UnaryCallSettings<UpdateTopicRequest, Topic> updateTopicSettings;
  private final UnaryCallSettings<DeleteTopicRequest, Empty> deleteTopicSettings;
  private final PagedCallSettings<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsSettings;
  private final UnaryCallSettings<CreateSubscriptionRequest, Subscription>
      createSubscriptionSettings;
  private final UnaryCallSettings<GetSubscriptionRequest, Subscription> getSubscriptionSettings;
  private final PagedCallSettings<
          ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
      listSubscriptionsSettings;
  private final UnaryCallSettings<UpdateSubscriptionRequest, Subscription>
      updateSubscriptionSettings;
  private final UnaryCallSettings<DeleteSubscriptionRequest, Empty> deleteSubscriptionSettings;

  private static final PagedListDescriptor<ListTopicsRequest, ListTopicsResponse, Topic>
      LIST_TOPICS_PAGE_STR_DESC =
          new PagedListDescriptor<ListTopicsRequest, ListTopicsResponse, Topic>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListTopicsRequest injectToken(ListTopicsRequest payload, String token) {
              return ListTopicsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListTopicsRequest injectPageSize(ListTopicsRequest payload, int pageSize) {
              return ListTopicsRequest.newBuilder(payload).setPageSize(pageSize).build();
            }

            @Override
            public Integer extractPageSize(ListTopicsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListTopicsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<Topic> extractResources(ListTopicsResponse payload) {
              return payload.getTopicsList() == null
                  ? ImmutableList.<Topic>of()
                  : payload.getTopicsList();
            }
          };

  private static final PagedListDescriptor<
          ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String>
      LIST_TOPIC_SUBSCRIPTIONS_PAGE_STR_DESC =
          new PagedListDescriptor<
              ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListTopicSubscriptionsRequest injectToken(
                ListTopicSubscriptionsRequest payload, String token) {
              return ListTopicSubscriptionsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListTopicSubscriptionsRequest injectPageSize(
                ListTopicSubscriptionsRequest payload, int pageSize) {
              return ListTopicSubscriptionsRequest.newBuilder(payload)
                  .setPageSize(pageSize)
                  .build();
            }

            @Override
            public Integer extractPageSize(ListTopicSubscriptionsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListTopicSubscriptionsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<String> extractResources(ListTopicSubscriptionsResponse payload) {
              return payload.getSubscriptionsList() == null
                  ? ImmutableList.<String>of()
                  : payload.getSubscriptionsList();
            }
          };

  private static final PagedListDescriptor<
          ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription>
      LIST_SUBSCRIPTIONS_PAGE_STR_DESC =
          new PagedListDescriptor<
              ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListSubscriptionsRequest injectToken(
                ListSubscriptionsRequest payload, String token) {
              return ListSubscriptionsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListSubscriptionsRequest injectPageSize(
                ListSubscriptionsRequest payload, int pageSize) {
              return ListSubscriptionsRequest.newBuilder(payload).setPageSize(pageSize).build();
            }

            @Override
            public Integer extractPageSize(ListSubscriptionsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListSubscriptionsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<Subscription> extractResources(ListSubscriptionsResponse payload) {
              return payload.getSubscriptionsList() == null
                  ? ImmutableList.<Subscription>of()
                  : payload.getSubscriptionsList();
            }
          };

  private static final PagedListResponseFactory<
          ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
      LIST_TOPICS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>() {
            @Override
            public ApiFuture<ListTopicsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListTopicsRequest, ListTopicsResponse> callable,
                ListTopicsRequest request,
                ApiCallContext context,
                ApiFuture<ListTopicsResponse> futureResponse) {
              PageContext<ListTopicsRequest, ListTopicsResponse, Topic> pageContext =
                  PageContext.create(callable, LIST_TOPICS_PAGE_STR_DESC, request, context);
              return ListTopicsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  private static final PagedListResponseFactory<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          ListTopicSubscriptionsPagedResponse>
      LIST_TOPIC_SUBSCRIPTIONS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListTopicSubscriptionsRequest,
              ListTopicSubscriptionsResponse,
              ListTopicSubscriptionsPagedResponse>() {
            @Override
            public ApiFuture<ListTopicSubscriptionsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse>
                    callable,
                ListTopicSubscriptionsRequest request,
                ApiCallContext context,
                ApiFuture<ListTopicSubscriptionsResponse> futureResponse) {
              PageContext<ListTopicSubscriptionsRequest, ListTopicSubscriptionsResponse, String>
                  pageContext =
                      PageContext.create(
                          callable, LIST_TOPIC_SUBSCRIPTIONS_PAGE_STR_DESC, request, context);
              return ListTopicSubscriptionsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  private static final PagedListResponseFactory<
          ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
      LIST_SUBSCRIPTIONS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListSubscriptionsRequest,
              ListSubscriptionsResponse,
              ListSubscriptionsPagedResponse>() {
            @Override
            public ApiFuture<ListSubscriptionsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListSubscriptionsRequest, ListSubscriptionsResponse> callable,
                ListSubscriptionsRequest request,
                ApiCallContext context,
                ApiFuture<ListSubscriptionsResponse> futureResponse) {
              PageContext<ListSubscriptionsRequest, ListSubscriptionsResponse, Subscription>
                  pageContext =
                      PageContext.create(
                          callable, LIST_SUBSCRIPTIONS_PAGE_STR_DESC, request, context);
              return ListSubscriptionsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  /** Returns the object with the settings used for calls to createTopic. */
  public UnaryCallSettings<CreateTopicRequest, Topic> createTopicSettings() {
    return createTopicSettings;
  }

  /** Returns the object with the settings used for calls to getTopic. */
  public UnaryCallSettings<GetTopicRequest, Topic> getTopicSettings() {
    return getTopicSettings;
  }

  /** Returns the object with the settings used for calls to getTopicPartitions. */
  public UnaryCallSettings<GetTopicPartitionsRequest, TopicPartitions>
      getTopicPartitionsSettings() {
    return getTopicPartitionsSettings;
  }

  /** Returns the object with the settings used for calls to listTopics. */
  public PagedCallSettings<ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
      listTopicsSettings() {
    return listTopicsSettings;
  }

  /** Returns the object with the settings used for calls to updateTopic. */
  public UnaryCallSettings<UpdateTopicRequest, Topic> updateTopicSettings() {
    return updateTopicSettings;
  }

  /** Returns the object with the settings used for calls to deleteTopic. */
  public UnaryCallSettings<DeleteTopicRequest, Empty> deleteTopicSettings() {
    return deleteTopicSettings;
  }

  /** Returns the object with the settings used for calls to listTopicSubscriptions. */
  public PagedCallSettings<
          ListTopicSubscriptionsRequest,
          ListTopicSubscriptionsResponse,
          ListTopicSubscriptionsPagedResponse>
      listTopicSubscriptionsSettings() {
    return listTopicSubscriptionsSettings;
  }

  /** Returns the object with the settings used for calls to createSubscription. */
  public UnaryCallSettings<CreateSubscriptionRequest, Subscription> createSubscriptionSettings() {
    return createSubscriptionSettings;
  }

  /** Returns the object with the settings used for calls to getSubscription. */
  public UnaryCallSettings<GetSubscriptionRequest, Subscription> getSubscriptionSettings() {
    return getSubscriptionSettings;
  }

  /** Returns the object with the settings used for calls to listSubscriptions. */
  public PagedCallSettings<
          ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
      listSubscriptionsSettings() {
    return listSubscriptionsSettings;
  }

  /** Returns the object with the settings used for calls to updateSubscription. */
  public UnaryCallSettings<UpdateSubscriptionRequest, Subscription> updateSubscriptionSettings() {
    return updateSubscriptionSettings;
  }

  /** Returns the object with the settings used for calls to deleteSubscription. */
  public UnaryCallSettings<DeleteSubscriptionRequest, Empty> deleteSubscriptionSettings() {
    return deleteSubscriptionSettings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public AdminServiceStub createStub() throws IOException {
    if (getTransportChannelProvider()
        .getTransportName()
        .equals(GrpcTransportChannel.getGrpcTransportName())) {
      return GrpcAdminServiceStub.create(this);
    }
    throw new UnsupportedOperationException(
        String.format(
            "Transport not supported: %s", getTransportChannelProvider().getTransportName()));
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return InstantiatingExecutorProvider.newBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return "pubsublite.googleapis.com:443";
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return DEFAULT_SERVICE_SCOPES;
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return GoogleCredentialsProvider.newBuilder().setScopesToApply(DEFAULT_SERVICE_SCOPES);
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return InstantiatingGrpcChannelProvider.newBuilder()
        .setMaxInboundMessageSize(Integer.MAX_VALUE);
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return defaultGrpcTransportProviderBuilder().build();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return ApiClientHeaderProvider.newBuilder()
        .setGeneratedLibToken(
            "gapic", GaxProperties.getLibraryVersion(AdminServiceStubSettings.class))
        .setTransportToken(
            GaxGrpcProperties.getGrpcTokenName(), GaxGrpcProperties.getGrpcVersion());
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

  protected AdminServiceStubSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);

    createTopicSettings = settingsBuilder.createTopicSettings().build();
    getTopicSettings = settingsBuilder.getTopicSettings().build();
    getTopicPartitionsSettings = settingsBuilder.getTopicPartitionsSettings().build();
    listTopicsSettings = settingsBuilder.listTopicsSettings().build();
    updateTopicSettings = settingsBuilder.updateTopicSettings().build();
    deleteTopicSettings = settingsBuilder.deleteTopicSettings().build();
    listTopicSubscriptionsSettings = settingsBuilder.listTopicSubscriptionsSettings().build();
    createSubscriptionSettings = settingsBuilder.createSubscriptionSettings().build();
    getSubscriptionSettings = settingsBuilder.getSubscriptionSettings().build();
    listSubscriptionsSettings = settingsBuilder.listSubscriptionsSettings().build();
    updateSubscriptionSettings = settingsBuilder.updateSubscriptionSettings().build();
    deleteSubscriptionSettings = settingsBuilder.deleteSubscriptionSettings().build();
  }

  /** Builder for AdminServiceStubSettings. */
  public static class Builder extends StubSettings.Builder<AdminServiceStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;
    private final UnaryCallSettings.Builder<CreateTopicRequest, Topic> createTopicSettings;
    private final UnaryCallSettings.Builder<GetTopicRequest, Topic> getTopicSettings;
    private final UnaryCallSettings.Builder<GetTopicPartitionsRequest, TopicPartitions>
        getTopicPartitionsSettings;
    private final PagedCallSettings.Builder<
            ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
        listTopicsSettings;
    private final UnaryCallSettings.Builder<UpdateTopicRequest, Topic> updateTopicSettings;
    private final UnaryCallSettings.Builder<DeleteTopicRequest, Empty> deleteTopicSettings;
    private final PagedCallSettings.Builder<
            ListTopicSubscriptionsRequest,
            ListTopicSubscriptionsResponse,
            ListTopicSubscriptionsPagedResponse>
        listTopicSubscriptionsSettings;
    private final UnaryCallSettings.Builder<CreateSubscriptionRequest, Subscription>
        createSubscriptionSettings;
    private final UnaryCallSettings.Builder<GetSubscriptionRequest, Subscription>
        getSubscriptionSettings;
    private final PagedCallSettings.Builder<
            ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
        listSubscriptionsSettings;
    private final UnaryCallSettings.Builder<UpdateSubscriptionRequest, Subscription>
        updateSubscriptionSettings;
    private final UnaryCallSettings.Builder<DeleteSubscriptionRequest, Empty>
        deleteSubscriptionSettings;
    private static final ImmutableMap<String, ImmutableSet<StatusCode.Code>>
        RETRYABLE_CODE_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, ImmutableSet<StatusCode.Code>> definitions =
          ImmutableMap.builder();
      definitions.put(
          "retry_policy_0_codes",
          ImmutableSet.copyOf(
              Lists.<StatusCode.Code>newArrayList(
                  StatusCode.Code.DEADLINE_EXCEEDED,
                  StatusCode.Code.UNAVAILABLE,
                  StatusCode.Code.ABORTED,
                  StatusCode.Code.INTERNAL,
                  StatusCode.Code.UNKNOWN)));
      RETRYABLE_CODE_DEFINITIONS = definitions.build();
    }

    private static final ImmutableMap<String, RetrySettings> RETRY_PARAM_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, RetrySettings> definitions = ImmutableMap.builder();
      RetrySettings settings = null;
      settings =
          RetrySettings.newBuilder()
              .setInitialRetryDelay(Duration.ofMillis(100L))
              .setRetryDelayMultiplier(1.3)
              .setMaxRetryDelay(Duration.ofMillis(60000L))
              .setInitialRpcTimeout(Duration.ofMillis(600000L))
              .setRpcTimeoutMultiplier(1.0)
              .setMaxRpcTimeout(Duration.ofMillis(600000L))
              .setTotalTimeout(Duration.ofMillis(600000L))
              .build();
      definitions.put("retry_policy_0_params", settings);
      RETRY_PARAM_DEFINITIONS = definitions.build();
    }

    protected Builder() {
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(clientContext);

      createTopicSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getTopicSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getTopicPartitionsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      listTopicsSettings = PagedCallSettings.newBuilder(LIST_TOPICS_PAGE_STR_FACT);
      updateTopicSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      deleteTopicSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      listTopicSubscriptionsSettings =
          PagedCallSettings.newBuilder(LIST_TOPIC_SUBSCRIPTIONS_PAGE_STR_FACT);
      createSubscriptionSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      getSubscriptionSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      listSubscriptionsSettings = PagedCallSettings.newBuilder(LIST_SUBSCRIPTIONS_PAGE_STR_FACT);
      updateSubscriptionSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      deleteSubscriptionSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              createTopicSettings,
              getTopicSettings,
              getTopicPartitionsSettings,
              listTopicsSettings,
              updateTopicSettings,
              deleteTopicSettings,
              listTopicSubscriptionsSettings,
              createSubscriptionSettings,
              getSubscriptionSettings,
              listSubscriptionsSettings,
              updateSubscriptionSettings,
              deleteSubscriptionSettings);
      initDefaults(this);
    }

    protected Builder(AdminServiceStubSettings settings) {
      super(settings);

      createTopicSettings = settings.createTopicSettings.toBuilder();
      getTopicSettings = settings.getTopicSettings.toBuilder();
      getTopicPartitionsSettings = settings.getTopicPartitionsSettings.toBuilder();
      listTopicsSettings = settings.listTopicsSettings.toBuilder();
      updateTopicSettings = settings.updateTopicSettings.toBuilder();
      deleteTopicSettings = settings.deleteTopicSettings.toBuilder();
      listTopicSubscriptionsSettings = settings.listTopicSubscriptionsSettings.toBuilder();
      createSubscriptionSettings = settings.createSubscriptionSettings.toBuilder();
      getSubscriptionSettings = settings.getSubscriptionSettings.toBuilder();
      listSubscriptionsSettings = settings.listSubscriptionsSettings.toBuilder();
      updateSubscriptionSettings = settings.updateSubscriptionSettings.toBuilder();
      deleteSubscriptionSettings = settings.deleteSubscriptionSettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              createTopicSettings,
              getTopicSettings,
              getTopicPartitionsSettings,
              listTopicsSettings,
              updateTopicSettings,
              deleteTopicSettings,
              listTopicSubscriptionsSettings,
              createSubscriptionSettings,
              getSubscriptionSettings,
              listSubscriptionsSettings,
              updateSubscriptionSettings,
              deleteSubscriptionSettings);
    }

    private static Builder createDefault() {
      Builder builder = new Builder(((ClientContext) null));

      builder.setTransportChannelProvider(defaultTransportChannelProvider());
      builder.setCredentialsProvider(defaultCredentialsProviderBuilder().build());
      builder.setInternalHeaderProvider(defaultApiClientHeaderProviderBuilder().build());
      builder.setEndpoint(getDefaultEndpoint());

      return initDefaults(builder);
    }

    private static Builder initDefaults(Builder builder) {
      builder
          .createTopicSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getTopicSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getTopicPartitionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .listTopicsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .updateTopicSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .deleteTopicSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .listTopicSubscriptionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .createSubscriptionSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .getSubscriptionSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .listSubscriptionsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .updateSubscriptionSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .deleteSubscriptionSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      return builder;
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'.
    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) throws Exception {
      super.applyToAllUnaryMethods(unaryMethodSettingsBuilders, settingsUpdater);
      return this;
    }

    public ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders() {
      return unaryMethodSettingsBuilders;
    }

    /** Returns the builder for the settings used for calls to createTopic. */
    public UnaryCallSettings.Builder<CreateTopicRequest, Topic> createTopicSettings() {
      return createTopicSettings;
    }

    /** Returns the builder for the settings used for calls to getTopic. */
    public UnaryCallSettings.Builder<GetTopicRequest, Topic> getTopicSettings() {
      return getTopicSettings;
    }

    /** Returns the builder for the settings used for calls to getTopicPartitions. */
    public UnaryCallSettings.Builder<GetTopicPartitionsRequest, TopicPartitions>
        getTopicPartitionsSettings() {
      return getTopicPartitionsSettings;
    }

    /** Returns the builder for the settings used for calls to listTopics. */
    public PagedCallSettings.Builder<ListTopicsRequest, ListTopicsResponse, ListTopicsPagedResponse>
        listTopicsSettings() {
      return listTopicsSettings;
    }

    /** Returns the builder for the settings used for calls to updateTopic. */
    public UnaryCallSettings.Builder<UpdateTopicRequest, Topic> updateTopicSettings() {
      return updateTopicSettings;
    }

    /** Returns the builder for the settings used for calls to deleteTopic. */
    public UnaryCallSettings.Builder<DeleteTopicRequest, Empty> deleteTopicSettings() {
      return deleteTopicSettings;
    }

    /** Returns the builder for the settings used for calls to listTopicSubscriptions. */
    public PagedCallSettings.Builder<
            ListTopicSubscriptionsRequest,
            ListTopicSubscriptionsResponse,
            ListTopicSubscriptionsPagedResponse>
        listTopicSubscriptionsSettings() {
      return listTopicSubscriptionsSettings;
    }

    /** Returns the builder for the settings used for calls to createSubscription. */
    public UnaryCallSettings.Builder<CreateSubscriptionRequest, Subscription>
        createSubscriptionSettings() {
      return createSubscriptionSettings;
    }

    /** Returns the builder for the settings used for calls to getSubscription. */
    public UnaryCallSettings.Builder<GetSubscriptionRequest, Subscription>
        getSubscriptionSettings() {
      return getSubscriptionSettings;
    }

    /** Returns the builder for the settings used for calls to listSubscriptions. */
    public PagedCallSettings.Builder<
            ListSubscriptionsRequest, ListSubscriptionsResponse, ListSubscriptionsPagedResponse>
        listSubscriptionsSettings() {
      return listSubscriptionsSettings;
    }

    /** Returns the builder for the settings used for calls to updateSubscription. */
    public UnaryCallSettings.Builder<UpdateSubscriptionRequest, Subscription>
        updateSubscriptionSettings() {
      return updateSubscriptionSettings;
    }

    /** Returns the builder for the settings used for calls to deleteSubscription. */
    public UnaryCallSettings.Builder<DeleteSubscriptionRequest, Empty>
        deleteSubscriptionSettings() {
      return deleteSubscriptionSettings;
    }

    @Override
    public AdminServiceStubSettings build() throws IOException {
      return new AdminServiceStubSettings(this);
    }
  }
}
