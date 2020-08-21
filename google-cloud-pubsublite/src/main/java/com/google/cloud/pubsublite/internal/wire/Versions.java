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

import com.google.api.gax.core.GaxProperties;
import com.google.common.annotations.VisibleForTesting;

/** The version number of this library. */
final class Versions {
  private final String versionString;

  @VisibleForTesting
  Versions(String versionString) {
    this.versionString = versionString;
  }

  private Versions() {
    this(GaxProperties.getLibraryVersion(Versions.class));
  }

  private String[] getVersionSplits() {
    try {
      return versionString.split("\\.");
    } catch (Exception e) {
      return new String[0];
    }
  }

  @VisibleForTesting
  int getMajorVersion() {
    String[] splits = getVersionSplits();
    if (splits.length != 3) return 0;
    try {
      return Integer.parseInt(splits[0]);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  @VisibleForTesting
  int getMinorVersion() {
    String[] splits = getVersionSplits();
    if (splits.length != 3) return 0;
    try {
      return Integer.parseInt(splits[1]);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static final Versions VERSIONS = new Versions();
  // TODO: Do this using generation automation as opposed to maven packaging.
  static final int MAJOR_VERSION = VERSIONS.getMajorVersion();
  static final int MINOR_VERSION = VERSIONS.getMinorVersion();
}
