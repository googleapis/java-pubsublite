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
import com.google.cloud.pubsublite.CloudRegionOrZone.Kind;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A string wrapper representing a reservation. Should be structured like:
 *
 * <p>projects/&lt;project number&gt;/locations/&lt;cloud region&gt;/reservations/&lt;id&gt;
 */
@AutoValue
public abstract class ReservationPath implements Serializable {
  public abstract ProjectIdOrNumber project();

  public abstract CloudRegion location();

  public abstract ReservationName name();

  public LocationPath locationPath() {
    return LocationPath.newBuilder().setProject(project()).setLocation(location()).build();
  }

  @Override
  public String toString() {
    return locationPath() + "/reservations/" + name();
  }

  /** Create a new ReservationPath builder. */
  public static Builder newBuilder() {
    return new AutoValue_ReservationPath.Builder();
  }

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder extends ProjectBuilderHelper<Builder> {
    public abstract Builder setLocation(CloudRegion region);

    public abstract Builder setName(ReservationName name);

    /** Build a new ReservationPath. */
    public abstract ReservationPath build();
  }

  public static ReservationPath parse(String path) throws ApiException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 6, "Not a valid reservation path: " + path);
    checkArgument(splits[4].equals("reservations"), "Not a valid reservation path: " + path);
    LocationPath location = LocationPath.parse(String.join("/", Arrays.copyOf(splits, 4)));
    checkArgument(
        location.location().getKind() == Kind.REGION,
        "Reservation location must be a valid cloud region.");
    return ReservationPath.newBuilder()
        .setProject(location.project())
        .setLocation(location.location().region())
        .setName(ReservationName.of(splits[5]))
        .build();
  }
}
