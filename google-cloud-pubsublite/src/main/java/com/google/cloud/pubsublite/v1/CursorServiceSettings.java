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

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.core.ApiFunction;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.ClientSettings;
import com.google.api.gax.rpc.PagedCallSettings;
import com.google.api.gax.rpc.StreamingCallSettings;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.api.gax.rpc.UnaryCallSettings;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import com.google.cloud.pubsublite.v1.stub.CursorServiceStubSettings;
import java.io.IOException;
import java.util.List;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS
/**
 * Settings class to configure an instance of {@link CursorServiceClient}.
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
 * CursorServiceSettings.Builder cursorServiceSettingsBuilder =
 *     CursorServiceSettings.newBuilder();
 * cursorServiceSettingsBuilder
 *     .listPartitionCursorsSettings()
 *     .setRetrySettings(
 *         cursorServiceSettingsBuilder.listPartitionCursorsSettings().getRetrySettings().toBuilder()
 *             .setTotalTimeout(Duration.ofSeconds(30))
 *             .build());
 * CursorServiceSettings cursorServiceSettings = cursorServiceSettingsBuilder.build();
 * </code>
 * </pre>
 */
@Generated("by gapic-generator")
@BetaApi
public class CursorServiceSettings extends ClientSettings<CursorServiceSettings> {
  /** Returns the object with the settings used for calls to streamingCommitCursor. */
  public StreamingCallSettings<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorSettings() {
    return ((CursorServiceStubSettings) getStubSettings()).streamingCommitCursorSettings();
  }

  /** Returns the object with the settings used for calls to commitCursor. */
  public UnaryCallSettings<CommitCursorRequest, CommitCursorResponse> commitCursorSettings() {
    return ((CursorServiceStubSettings) getStubSettings()).commitCursorSettings();
  }

  /** Returns the object with the settings used for calls to listPartitionCursors. */
  public PagedCallSettings<
          ListPartitionCursorsRequest,
          ListPartitionCursorsResponse,
          ListPartitionCursorsPagedResponse>
      listPartitionCursorsSettings() {
    return ((CursorServiceStubSettings) getStubSettings()).listPartitionCursorsSettings();
  }

  public static final CursorServiceSettings create(CursorServiceStubSettings stub)
      throws IOException {
    return new CursorServiceSettings.Builder(stub.toBuilder()).build();
  }

  /** Returns a builder for the default ExecutorProvider for this service. */
  public static InstantiatingExecutorProvider.Builder defaultExecutorProviderBuilder() {
    return CursorServiceStubSettings.defaultExecutorProviderBuilder();
  }

  /** Returns the default service endpoint. */
  public static String getDefaultEndpoint() {
    return CursorServiceStubSettings.getDefaultEndpoint();
  }

  /** Returns the default service scopes. */
  public static List<String> getDefaultServiceScopes() {
    return CursorServiceStubSettings.getDefaultServiceScopes();
  }

  /** Returns a builder for the default credentials for this service. */
  public static GoogleCredentialsProvider.Builder defaultCredentialsProviderBuilder() {
    return CursorServiceStubSettings.defaultCredentialsProviderBuilder();
  }

  /** Returns a builder for the default ChannelProvider for this service. */
  public static InstantiatingGrpcChannelProvider.Builder defaultGrpcTransportProviderBuilder() {
    return CursorServiceStubSettings.defaultGrpcTransportProviderBuilder();
  }

  public static TransportChannelProvider defaultTransportChannelProvider() {
    return CursorServiceStubSettings.defaultTransportChannelProvider();
  }

  @BetaApi("The surface for customizing headers is not stable yet and may change in the future.")
  public static ApiClientHeaderProvider.Builder defaultApiClientHeaderProviderBuilder() {
    return CursorServiceStubSettings.defaultApiClientHeaderProviderBuilder();
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

  protected CursorServiceSettings(Builder settingsBuilder) throws IOException {
    super(settingsBuilder);
  }

  /** Builder for CursorServiceSettings. */
  public static class Builder extends ClientSettings.Builder<CursorServiceSettings, Builder> {
    protected Builder() throws IOException {
      this((ClientContext) null);
    }

    protected Builder(ClientContext clientContext) {
      super(CursorServiceStubSettings.newBuilder(clientContext));
    }

    private static Builder createDefault() {
      return new Builder(CursorServiceStubSettings.newBuilder());
    }

    protected Builder(CursorServiceSettings settings) {
      super(settings.getStubSettings().toBuilder());
    }

    protected Builder(CursorServiceStubSettings.Builder stubSettings) {
      super(stubSettings);
    }

    public CursorServiceStubSettings.Builder getStubSettingsBuilder() {
      return ((CursorServiceStubSettings.Builder) getStubSettings());
    }

    // NEXT_MAJOR_VER: remove 'throws Exception'
    /**
     * Applies the given settings updater function to all of the unary API methods in this service.
     *
     * <p>Note: This method does not support applying settings to streaming methods.
     */
    public Builder applyToAllUnaryMethods(
        ApiFunction<UnaryCallSettings.Builder<?, ?>, Void> settingsUpdater) throws Exception {
      super.applyToAllUnaryMethods(
          getStubSettingsBuilder().unaryMethodSettingsBuilders(), settingsUpdater);
      return this;
    }

    /** Returns the builder for the settings used for calls to streamingCommitCursor. */
    public StreamingCallSettings.Builder<
            StreamingCommitCursorRequest, StreamingCommitCursorResponse>
        streamingCommitCursorSettings() {
      return getStubSettingsBuilder().streamingCommitCursorSettings();
    }

    /** Returns the builder for the settings used for calls to commitCursor. */
    public UnaryCallSettings.Builder<CommitCursorRequest, CommitCursorResponse>
        commitCursorSettings() {
      return getStubSettingsBuilder().commitCursorSettings();
    }

    /** Returns the builder for the settings used for calls to listPartitionCursors. */
    public PagedCallSettings.Builder<
            ListPartitionCursorsRequest,
            ListPartitionCursorsResponse,
            ListPartitionCursorsPagedResponse>
        listPartitionCursorsSettings() {
      return getStubSettingsBuilder().listPartitionCursorsSettings();
    }

    @Override
    public CursorServiceSettings build() throws IOException {
      return new CursorServiceSettings(this);
    }
  }
}
