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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A string wrapper representing a subscription. Should be structured like:
 *
 * <p>projects/&lt;project number&gt;/locations/&lt;cloud zone&gt;/subscriptions/&lt;id&gt;
 */
@AutoValue
public abstract class SubscriptionPath implements Serializable {
  public abstract ProjectIdOrNumber project();

  public abstract CloudZone location();

  public abstract SubscriptionName name();

  public LocationPath locationPath() {
    return LocationPath.newBuilder().setProject(project()).setLocation(location()).build();
  }

  @Override
  public String toString() {
    return locationPath() + "/subscriptions/" + name();
  }

  /** Create a new SubscriptionPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_SubscriptionPath.Builder();
  }

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder extends ProjectBuilderHelper<Builder> {
    public abstract Builder setLocation(CloudZone zone);

    public abstract Builder setName(SubscriptionName name);

    /** Build a new SubscriptionPath. */
    public abstract SubscriptionPath build();
  }

  public static SubscriptionPath parse(String path) throws ApiException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 6);
    checkArgument(splits[4].equals("subscriptions"));
    LocationPath location = LocationPath.parse(String.join("/", Arrays.copyOf(splits, 4)));
    return SubscriptionPath.newBuilder()
        .setProject(location.project())
        .setLocation(location.location())
        .setName(SubscriptionName.of(splits[5]))
        .build();
  }
}
