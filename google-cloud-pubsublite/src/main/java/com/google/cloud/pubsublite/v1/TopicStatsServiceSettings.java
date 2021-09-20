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

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.StubSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeHeadCursorResponse;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsRequest;
import com.google.cloud.pubsublite.proto.ComputeMessageStatsResponse;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorRequest;
import com.google.cloud.pubsublite.proto.ComputeTimeCursorResponse;
import com.google.cloud.pubsublite.v1.stub.TopicStatsServiceStubSettings;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Settings class to configure an instance of {@link TopicStatsServiceClient}.
 *
 * <p>The default instance has everything set to sensible defaults:
 *
 * <ul>
 *   <li> The default service address (pubsublite.googleapis.com) and default port (443) are used.
 *   <li> Credentials are acquired automatically through Application Default Credentials.
 *   <li> Retries are configured for idempotent methods but not for non-idempotent methods.
 * </ul>
 *
 * <p>The builder of this class is recursive, so contained classes are themselves builders. When
 * build() is called, the tree of builders is called to create the complete settings object.
 *
 * <p>For example, to set the total timeout of computeMessageStats to 30 seconds:
 *
 * <pre>{@code
 * TopicStatsServiceSettings.Builder topicStatsServiceSettingsBuilder =
 *     TopicStatsServiceSettings.newBuilder();
 * topicStatsServiceSettingsBuilder
 *     .computeMessageStatsSettings()
 *     .setRetrySettings(
 *         topicStatsServiceSettingsBuilder
 *             .computeMessageStatsSettings()
 *             .getRetrySettings()
 *             .toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * TopicStatsServiceSettings topicStatsServiceSettings = topicStatsServiceSettingsBuilder.build();
 * }</pre>
 */
@Generated("by gapic-generator-java")
public class TopicStatsServiceSettings extends ClientSettings<TopicStatsServiceSettings> {

  /** Returns the object with the settings used for calls to computeMessageStats. */
  public UnaryCallSettings<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
      computeMessageStatsSettings() {
    return ((TopicStatsServiceStubSettings) getStubSettings()).computeMessageStatsSettings();
  }

  /** Returns the object with the settings used for calls to computeHeadCursor. */
  public UnaryCallSettings<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
      computeHeadCursorSettings() {
    return ((TopicStatsServiceStubSettings) getStubSettings()).computeHeadCursorSettings();
  }

  /** Returns the object with the settings used for calls to computeTimeCursor. */
  public UnaryCallSettings<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
      computeTimeCursorSettings() {
    return ((TopicStatsServiceStubSettings) getStubSettings()).computeTimeCursorSettings();
  }

  public static final TopicStatsServiceSettings create(TopicStatsServiceStubSettings stub)
      throws IOException {
    return new TopicStatsServiceSettings.Builder(stub.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return TopicStatsServiceStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return TopicStatsServiceStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return TopicStatsServiceStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return TopicStatsServiceStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return TopicStatsServiceStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return TopicStatsServiceStubSettings.defaultTransportChannelProvider();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return TopicStatsServiceStubSettings.defaultApiClientHeaderProviderBuilder();
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

  protected TopicStatsServiceSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for TopicStatsServiceSettings. */
  public static class Builder extends ClientSettings.Builder<TopicStatsServiceSettings, Builder> {

    protected Builder() throws IOException {
      this(((ClientContext) null));
    }

    protected Builder(ClientContext clientContext) {
      super(TopicStatsServiceStubSettings.newBuilder(clientContext));
    }

    protected Builder(TopicStatsServiceSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(TopicStatsServiceStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    private static Builder createDefault() {
      return new Builder(TopicStatsServiceStubSettings.newBuilder());
    }

    public TopicStatsServiceStubSettings.Builder getStubSettingsBuilder() {
      return ((TopicStatsServiceStubSettings.Builder) getStubSettings());
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

    /** Returns the builder for the settings used for calls to computeMessageStats. */
    public UnaryCallSettings.Builder<ComputeMessageStatsRequest, ComputeMessageStatsResponse>
        computeMessageStatsSettings() {
      return getStubSettingsBuilder().computeMessageStatsSettings();
    }

    /** Returns the builder for the settings used for calls to computeHeadCursor. */
    public UnaryCallSettings.Builder<ComputeHeadCursorRequest, ComputeHeadCursorResponse>
        computeHeadCursorSettings() {
      return getStubSettingsBuilder().computeHeadCursorSettings();
    }

    /** Returns the builder for the settings used for calls to computeTimeCursor. */
    public UnaryCallSettings.Builder<ComputeTimeCursorRequest, ComputeTimeCursorResponse>
        computeTimeCursorSettings() {
      return getStubSettingsBuilder().computeTimeCursorSettings();
    }

    @Override
    public TopicStatsServiceSettings build() throws IOException {
      return new TopicStatsServiceSettings(this);
    }
  }
}
