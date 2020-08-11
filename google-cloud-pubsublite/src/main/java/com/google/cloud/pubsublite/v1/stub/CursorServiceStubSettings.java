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
package com.google.cloud.pubsublite.v1.stub;

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

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
import com.google.api.gax.rpc.StreamingCallSettings;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.PartitionCursor;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.threeten.bp.Duration;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Settings class to configure an instance of {@link CursorServiceStub}.
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
 * <p>For example, to set the total timeout of listPartitionCursors to 30 seconds:
 *
 * <pre>
 * <code>
 * CursorServiceStubSettings.Builder cursorServiceSettingsBuilder =
 *     CursorServiceStubSettings.newBuilder();
 * cursorServiceSettingsBuilder
 *     .listPartitionCursorsSettings()
 *     .setRetrySettings(
 *         cursorServiceSettingsBuilder.listPartitionCursorsSettings().getRetrySettings().toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * CursorServiceStubSettings cursorServiceSettings = cursorServiceSettingsBuilder.build();
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class CursorServiceStubSettings extends StubSettings<CursorServiceStubSettings> {
  /** The default scopes of the service. */
  private static final ImmutableList<String> DEFAULT_SERVICE_SCOPES =
      ImmutableList.<String>builder().add("https://www.googleapis.com/auth/cloud-platform").build();

  private final StreamingCallSettings<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorSettings;
  private final UnaryCallSettings<CommitCursorRequest, CommitCursorResponse> commitCursorSettings;
  private final PagedCallSettings<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          ListPartitionCursorsPagedResponse>
      listPartitionCursorsSettings;

  /** Returns the object with the settings used for calls to streamingCommitCursor. */
  public StreamingCallSettings<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorSettings() {
    return streamingCommitCursorSettings;
  }

  /** Returns the object with the settings used for calls to commitCursor. */
  public UnaryCallSettings<CommitCursorRequest, CommitCursorResponse> commitCursorSettings() {
    return commitCursorSettings;
  }

  /** Returns the object with the settings used for calls to listPartitionCursors. */
  public PagedCallSettings<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          ListPartitionCursorsPagedResponse>
      listPartitionCursorsSettings() {
    return listPartitionCursorsSettings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public CursorServiceStub createStub() throws IOException {
    if (getTransportChannelProvider()
        .getTransportName()
        .equals(GrpcTransportChannel.getGrpcTransportName())) {
      return GrpcCursorServiceStub.create(this);
    } else {
      throw new UnsupportedOperationException(
          "Transport not supported: " + getTransportChannelProvider().getTransportName());
    }
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
            "gapic", GaxProperties.getLibraryVersion(CursorServiceStubSettings.class))
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

  protected CursorServiceStubSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);

    streamingCommitCursorSettings = settingsBuilder.streamingCommitCursorSettings().build();
    commitCursorSettings = settingsBuilder.commitCursorSettings().build();
    listPartitionCursorsSettings = settingsBuilder.listPartitionCursorsSettings().build();
  }

  private static final PagedListDescriptor<
          ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
      LIST_PARTITION_CURSORS_PAGE_STR_DESC =
          new PagedListDescriptor<
              ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>() {
            @Override
            public String emptyToken() {
              return "";
            }

            @Override
            public ListPartitionCursorsRequest injectToken(
                ListPartitionCursorsRequest payload, String token) {
              return ListPartitionCursorsRequest.newBuilder(payload).setPageToken(token).build();
            }

            @Override
            public ListPartitionCursorsRequest injectPageSize(
                ListPartitionCursorsRequest payload, int pageSize) {
              return ListPartitionCursorsRequest.newBuilder(payload).setPageSize(pageSize).build();
            }

            @Override
            public Integer extractPageSize(ListPartitionCursorsRequest payload) {
              return payload.getPageSize();
            }

            @Override
            public String extractNextToken(ListPartitionCursorsResponse payload) {
              return payload.getNextPageToken();
            }

            @Override
            public Iterable<PartitionCursor> extractResources(
                ListPartitionCursorsResponse payload) {
              return payload.getPartitionCursorsList() != null
                  ? payload.getPartitionCursorsList()
                  : ImmutableList.<PartitionCursor>of();
            }
          };

  private static final PagedListResponseFactory<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          ListPartitionCursorsPagedResponse>
      LIST_PARTITION_CURSORS_PAGE_STR_FACT =
          new PagedListResponseFactory<
              ListPartitionCursorsRequest,
              ListPartitionCursorsResponse,
              ListPartitionCursorsPagedResponse>() {
            @Override
            public ApiFuture<ListPartitionCursorsPagedResponse> getFuturePagedResponse(
                UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse> callable,
                ListPartitionCursorsRequest request,
                ApiCallContext context,
                ApiFuture<ListPartitionCursorsResponse> futureResponse) {
              PageContext<
                      ListPartitionCursorsRequest, ListPartitionCursorsResponse, PartitionCursor>
                  pageContext =
                      PageContext.create(
                          callable, LIST_PARTITION_CURSORS_PAGE_STR_DESC, request, context);
              return ListPartitionCursorsPagedResponse.createAsync(pageContext, futureResponse);
            }
          };

  /** Builder for CursorServiceStubSettings. */
  public static class Builder extends StubSettings.Builder<CursorServiceStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;

    private final StreamingCallSettings.Builder<
            StreamingCommitCursorRequest, StreamingCommitCursorResponse>
        streamingCommitCursorSettings;
    private final UnaryCallSettings.Builder<CommitCursorRequest, CommitCursorResponse>
        commitCursorSettings;
    private final PagedCallSettings.Builder<
            ListPartitionCursorsRequest,
            ListPartitionCursorsResponse,
            ListPartitionCursorsPagedResponse>
        listPartitionCursorsSettings;

    private static final ImmutableMap<String, ImmutableSet<StatusCode.Code>>
        RETRYABLE_CODE_DEFINITIONS;

    static {
      ImmutableMap.Builder<String, ImmutableSet<StatusCode.Code>> definitions =
          ImmutableMap.builder();
      definitions.put(
          "retry_policy_1_codes",
          ImmutableSet.copyOf(
              Lists.<StatusCode.Code>newArrayList(
                  StatusCode.Code.DEADLINE_EXCEEDED, StatusCode.Code.UNAVAILABLE)));
      definitions.put("no_retry_codes", ImmutableSet.copyOf(Lists.<StatusCode.Code>newArrayList()));
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
      definitions.put("retry_policy_1_params", settings);
      settings = RetrySettings.newBuilder().setRpcTimeoutMultiplier(1.0).build();
      definitions.put("no_retry_params", settings);
      RETRY_PARAM_DEFINITIONS = definitions.build();
    }

    protected Builder() {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(clientContext);

      streamingCommitCursorSettings = StreamingCallSettings.newBuilder();

      commitCursorSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      listPartitionCursorsSettings =
          PagedCallSettings.newBuilder(LIST_PARTITION_CURSORS_PAGE_STR_FACT);

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              commitCursorSettings, listPartitionCursorsSettings);

      initDefaults(this);
    }

    private static Builder createDefault() {
      Builder builder = new Builder((ClientContext) null);
      builder.setTransportChannelProvider(defaultTransportChannelProvider());
      builder.setCredentialsProvider(defaultCredentialsProviderBuilder().build());
      builder.setInternalHeaderProvider(defaultApiClientHeaderProviderBuilder().build());
      builder.setEndpoint(getDefaultEndpoint());
      return initDefaults(builder);
    }

    private static Builder initDefaults(Builder builder) {

      builder
          .commitCursorSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_1_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_1_params"));

      builder
          .listPartitionCursorsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_1_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_1_params"));

      return builder;
    }

    protected Builder(CursorServiceStubSettings settings) {
      super(settings);

      streamingCommitCursorSettings = settings.streamingCommitCursorSettings.toBuilder();
      commitCursorSettings = settings.commitCursorSettings.toBuilder();
      listPartitionCursorsSettings = settings.listPartitionCursorsSettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              commitCursorSettings, listPartitionCursorsSettings);
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'
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

    /** Returns the builder for the settings used for calls to streamingCommitCursor. */
    public StreamingCallSettings.Builder<
            StreamingCommitCursorRequest, StreamingCommitCursorResponse>
        streamingCommitCursorSettings() {
      return streamingCommitCursorSettings;
    }

    /** Returns the builder for the settings used for calls to commitCursor. */
    public UnaryCallSettings.Builder<CommitCursorRequest, CommitCursorResponse>
        commitCursorSettings() {
      return commitCursorSettings;
    }

    /** Returns the builder for the settings used for calls to listPartitionCursors. */
    public PagedCallSettings.Builder<
            ListPartitionCursorsRequest,
            ListPartitionCursorsResponse,
            ListPartitionCursorsPagedResponse>
        listPartitionCursorsSettings() {
      return listPartitionCursorsSettings;
    }

    @Override
    public CursorServiceStubSettings build() throws IOException {
      return new CursorServiceStubSettings(this);
    }
  }
}
