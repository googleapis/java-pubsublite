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

/** The version number of this library.*/
final class Versions {
  private Versions() {}

  private static String[] GetVersionSplits() {
    try {
      String versionString = GaxProperties.getLibraryVersion(Versions.class);
      return versionString.split(".");
    } catch (Exception e) {
      return new String[0];
    }
  }

  private static int GetMajorVersion() {
    String[] splits = GetVersionSplits();
    if (splits.length != 3) return 0;
    try {
      return Integer.parseInt(splits[0]);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static int GetMinorVersion() {
    String[] splits = GetVersionSplits();
    if (splits.length != 3) return 0;
    try {
      return Integer.parseInt(splits[1]);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  // TODO: Do this using generation automation as opposed to maven packaging.
  static final int MAJOR_VERSION = GetMajorVersion();
  static final int MINOR_VERSION = GetMinorVersion();
}
