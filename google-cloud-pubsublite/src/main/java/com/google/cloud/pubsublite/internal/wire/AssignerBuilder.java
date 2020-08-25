/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal.wire;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.ProjectLookupUtils;
import com.google.cloud.pubsublite.Stubs;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.proto.InitialPartitionAssignmentRequest;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc;
import com.google.cloud.pubsublite.proto.PartitionAssignmentServiceGrpc.PartitionAssignmentServiceStub;
import com.google.common.flogger.GoogleLogger;
import com.google.protobuf.ByteString;
import io.grpc.StatusException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

@AutoValue
public abstract class AssignerBuilder {
  private static final GoogleLogger logger = GoogleLogger.forEnclosingClass();
  // Required parameters.
  abstract SubscriptionPath subscriptionPath();

  abstract PartitionAssignmentReceiver receiver();

  // Optional parameters.
  abstract Optional<PartitionAssignmentServiceStub> assignmentStub();

  public static Builder newBuilder() {
    return new AutoValue_AssignerBuilder.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    // Required parameters.
    public abstract Builder setSubscriptionPath(SubscriptionPath path);

    public abstract Builder setReceiver(PartitionAssignmentReceiver receiver);

    // Optional parameters.
    public abstract Builder setAssignmentStub(PartitionAssignmentServiceStub stub);

    abstract AssignerBuilder autoBuild();

    @SuppressWarnings("CheckReturnValue")
    public Assigner build() throws StatusException {
      AssignerBuilder builder = autoBuild();

      PartitionAssignmentServiceStub stub;
      if (builder.assignmentStub().isPresent()) {
        stub = builder.assignmentStub().get();
      } else {
        stub =
            Stubs.defaultStub(
                builder.subscriptionPath().location().location().region(),
                PartitionAssignmentServiceGrpc::newStub);
      }

      UUID uuid = UUID.randomUUID();
      ByteBuffer uuidBuffer = ByteBuffer.allocate(16);
      uuidBuffer.putLong(uuid.getMostSignificantBits());
      uuidBuffer.putLong(uuid.getLeastSignificantBits());
      logger.atInfo().log(
          "Subscription %s using UUID %s for assignment.", builder.subscriptionPath(), uuid);

      InitialPartitionAssignmentRequest initial =
          InitialPartitionAssignmentRequest.newBuilder()
              .setSubscription(
                  ProjectLookupUtils.toCannonical(builder.subscriptionPath()).toString())
              .setClientId(ByteString.copyFrom(uuidBuffer.array()))
              .build();
      return new AssignerImpl(
          stub, new ConnectedAssignerImpl.Factory(), initial, builder.receiver());
    }
  }
}
