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

package com.google.cloud.pubsublite.v1.stub;

import static com.google.cloud.pubsublite.v1.CursorServiceClient.ListPartitionCursorsPagedResponse;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.pubsublite.proto.CommitCursorRequest;
import com.google.cloud.pubsublite.proto.CommitCursorResponse;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsRequest;
import com.google.cloud.pubsublite.proto.ListPartitionCursorsResponse;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorRequest;
import com.google.cloud.pubsublite.proto.StreamingCommitCursorResponse;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the CursorService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@Generated("by gapic-generator-java")
public abstract class CursorServiceStub implements BackgroundResource {

  public BidiStreamingCallable<StreamingCommitCursorRequest, StreamingCommitCursorResponse>
      streamingCommitCursorCallable() {
    throw new UnsupportedOperationException("Not implemented: streamingCommitCursorCallable()");
  }

  public UnaryCallable<CommitCursorRequest, CommitCursorResponse> commitCursorCallable() {
    throw new UnsupportedOperationException("Not implemented: commitCursorCallable()");
  }

  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsPagedResponse>
      listPartitionCursorsPagedCallable() {
    throw new UnsupportedOperationException("Not implemented: listPartitionCursorsPagedCallable()");
  }

  public UnaryCallable<ListPartitionCursorsRequest, ListPartitionCursorsResponse>
      listPartitionCursorsCallable() {
    throw new UnsupportedOperationException("Not implemented: listPartitionCursorsCallable()");
  }

  @Override
  public abstract void close();
}
