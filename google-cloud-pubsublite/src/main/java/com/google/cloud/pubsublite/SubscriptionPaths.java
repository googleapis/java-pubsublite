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

package com.google.cloud.pubsublite;

import static com.google.cloud.pubsublite.internal.ExtractStatus.toCanonical;
import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;

/** Helpers for constructing valid SubscriptionPaths. */
@AutoValue
public abstract class SubscriptionPaths {
  abstract ProjectNumber projectNumber();

  abstract CloudZone zone();

  abstract SubscriptionName subscriptionName();

  /** Create a new SubscriptionPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_SubscriptionPaths.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    /** The project number. */
    public abstract Builder setProjectNumber(ProjectNumber number);

    /** The Google Cloud zone. */
    public abstract Builder setZone(CloudZone zone);

    /** The subscription name. */
    public abstract Builder setSubscriptionName(SubscriptionName name);

    abstract SubscriptionPaths autoBuild();

    /** Build a new SubscriptionPath. */
    public SubscriptionPath build() {
      SubscriptionPaths built = autoBuild();
      return SubscriptionPath.of(
          String.format(
              "projects/%s/locations/%s/subscriptions/%s",
              built.projectNumber().value(),
              built.zone().toString(),
              built.subscriptionName().value()));
    }
  }

  private static void checkSplits(String[] splits) throws StatusException {
    checkArgument(splits.length == 6);
    checkArgument(splits[0].equals("projects"));
    checkArgument(splits[2].equals("locations"));
    checkArgument(splits[4].equals("subscriptions"));
  }

  /** Check that the provided SubscriptionPath is valid. */
  public static void check(SubscriptionPath path) throws StatusException {
    ProjectNumber unusedProjectNumber = getProjectNumber(path);
    CloudZone unusedZone = getZone(path);
    SubscriptionName unusedName = getSubscriptionName(path);
  }

  /** Get the ProjectNumber from a SubscriptionPath. */
  public static ProjectNumber getProjectNumber(SubscriptionPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    try {
      return ProjectNumber.of(Long.parseLong(splits[1]));
    } catch (NumberFormatException e) {
      throw toCanonical(e);
    }
  }

  /** Get the CloudZone from a SubscriptionPath. */
  public static CloudZone getZone(SubscriptionPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    return CloudZone.parse(splits[3]);
  }

  /** Get the SubscriptionName from a SubscriptionPath. */
  public static SubscriptionName getSubscriptionName(SubscriptionPath path) throws StatusException {
    String[] splits = path.value().split("/");
    checkSplits(splits);
    return SubscriptionName.of(splits[5]);
  }

  /** Get the LocationPath from a SubscriptionPath. */
  public static LocationPath getLocationPath(SubscriptionPath path) throws StatusException {
    return LocationPaths.newBuilder()
        .setProjectNumber(getProjectNumber(path))
        .setZone(getZone(path))
        .build();
  }
}
