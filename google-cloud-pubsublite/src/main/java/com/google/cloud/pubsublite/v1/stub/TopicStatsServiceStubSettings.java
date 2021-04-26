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

package com.google.cloud.pubsublite.v1.stub;

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GaxProperties;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;
import org.threeten.bp.Duration;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Settings class to configure an instance of {@link TopicStatsServiceStub}.
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
 * <p>For example, to set the total timeout of computeMessageStats to 30 seconds:
 *
 * <pre>{@code
 * TopicStatsServiceStubSettings.Builder topicStatsServiceSettingsBuilder =
 *     TopicStatsServiceStubSettings.newBuilder();
 * topicStatsServiceSettingsBuilder
 *     .computeMessageStatsSettings()
 *     .setRetrySettings(
 *         topicStatsServiceSettingsBuilder
 *             .computeMessageStatsSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * TopicStatsServiceStubSettings topicStatsServiceSettings =
 *     topicStatsServiceSettingsBuilder.build();
 * }</pre>
 */
@Generated("by gapic-generator-java")
public class TopicStatsServiceStubSettings extends StubSettings<TopicStatsServiceStubSettings> {
  /** The default scopes of the service. */
  private static final ImmutableList<String> DEFAULT_SERVICE_SCOPES =
      ImmutableList.<String>builder().add("https://www.googleapis.com/auth/cloud-platform").build();

  private final UnaryCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsSettings;
  private final UnaryCallSettings<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorSettings;
  private final UnaryCallSettings<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorSettings;

  /** Returns the object with the settings used for calls to computeMessageStats. */
  public UnaryCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsSettings() {
    return computeMessageStatsSettings;
  }

  /** Returns the object with the settings used for calls to computeHeadCursor. */
  public UnaryCallSettings<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorSettings() {
    return computeHeadCursorSettings;
  }

  /** Returns the object with the settings used for calls to computeTimeCursor. */
  public UnaryCallSettings<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorSettings() {
    return computeTimeCursorSettings;
  }

  @BetaApi("A restructuring of stub classes is planned, so this may break in the future")
  public TopicStatsServiceStub createStub() throws IOException {
    if (getTransportChannelProvider()
        .getTransportName()
        .equals(GrpcTransportChannel.getGrpcTransportName())) {
      return GrpcTopicStatsServiceStub.create(this);
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
            "gapic", GaxProperties.getLibraryVersion(TopicStatsServiceStubSettings.class))
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

  protected TopicStatsServiceStubSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);

    computeMessageStatsSettings = settingsBuilder.computeMessageStatsSettings().build();
    computeHeadCursorSettings = settingsBuilder.computeHeadCursorSettings().build();
    computeTimeCursorSettings = settingsBuilder.computeTimeCursorSettings().build();
  }

  /** Builder for TopicStatsServiceStubSettings. */
  public static class Builder extends StubSettings.Builder<TopicStatsServiceStubSettings, Builder> {
    private final ImmutableList<UnaryCallSettings.Builder<?, ?>> unaryMethodSettingsBuilders;
    private final UnaryCallSettings.Builder<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsSettings;
    private final UnaryCallSettings.Builder<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
        computeHeadCursorSettings;
    private final UnaryCallSettings.Builder<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
        computeTimeCursorSettings;
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

      computeMessageStatsSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      computeHeadCursorSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();
      computeTimeCursorSettings = UnaryCallSettings.newUnaryCallSettingsBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              computeMessageStatsSettings, computeHeadCursorSettings, computeTimeCursorSettings);
      initDefaults(this);
    }

    protected Builder(TopicStatsServiceStubSettings settings) {
      super(settings);

      computeMessageStatsSettings = settings.computeMessageStatsSettings.toBuilder();
      computeHeadCursorSettings = settings.computeHeadCursorSettings.toBuilder();
      computeTimeCursorSettings = settings.computeTimeCursorSettings.toBuilder();

      unaryMethodSettingsBuilders =
          ImmutableList.<UnaryCallSettings.Builder<?, ?>>of(
              computeMessageStatsSettings, computeHeadCursorSettings, computeTimeCursorSettings);
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
          .computeMessageStatsSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .computeHeadCursorSettings()
          .setRetryableCodes(RETRYABLE_CODE_DEFINITIONS.get("retry_policy_0_codes"))
          .setRetrySettings(RETRY_PARAM_DEFINITIONS.get("retry_policy_0_params"));

      builder
          .computeTimeCursorSettings()
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

    /** Returns the builder for the settings used for calls to computeMessageStats. */
    public UnaryCallSettings.Builder<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsSettings() {
      return computeMessageStatsSettings;
    }

    /** Returns the builder for the settings used for calls to computeHeadCursor. */
    public UnaryCallSettings.Builder<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
        computeHeadCursorSettings() {
      return computeHeadCursorSettings;
    }

    /** Returns the builder for the settings used for calls to computeTimeCursor. */
    public UnaryCallSettings.Builder<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
        computeTimeCursorSettings() {
      return computeTimeCursorSettings;
    }

    @Override
    public TopicStatsServiceStubSettings build() throws IOException {
      return new TopicStatsServiceStubSettings(this);
    }
  }
}
