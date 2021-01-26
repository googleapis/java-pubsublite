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

import java.util.Objects;

/** Constructs regional endpoints from a CloudRegion. */
public final class Endpoints {
  /** Construct a regional endpoint from a CloudRegion. */
  public static String regionalEndpoint(CloudRegion region) {
//    String strFail = "us-central1-staging-pubsublite.sandbox.googleapis.com:443";
//    String strSuccess = region.value() + "-staging-pubsublite.sandbox.googleapis.com:443";
//    System.out.println("equal? " + Objects.equals(strSuccess, strFail));
//    return region.value() + "-staging-pubsublite.sandbox.googleapis.com:443";
//    return strFail;
    return "us-central1-staging-pubsublite.sandbox.googleapis.com:443";
//    return region.value() + "-pubsublite.googleapis.com:443";
  }

  private Endpoints() {}
}
