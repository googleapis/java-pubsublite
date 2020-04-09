// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal.wire;

import com.google.auto.value.AutoValue;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.Struct;
import com.google.protobuf.util.Values;
import io.grpc.Metadata;
import java.util.Optional;

/** A context for identifying clients based on basic properties. */
@AutoValue
public abstract class PubsubContext {
  static final String HEADER_NAME = "x-goog-pubsub-context";

  static final String LANGUAGE_KEY = "language";
  static final String FRAMEWORK_KEY = "framework";
  static final String MAJOR_VERSION_KEY = "major_version";
  static final String MINOR_VERSION_KEY = "minor_version";

  static final String LANGUAGE_VALUE = "JAVA";

  /**
   * A unique string representing the unambiguous name of a framework in SCREAMING_SNAKE_CASE.
   *
   * <p>Setting this value will be used to track framework popularity in internal metrics.
   */
  @AutoValue
  public abstract static class Framework {
    public abstract String value();

    public static Framework of(String value) {
      return new AutoValue_PubsubContext_Framework(value);
    }
  }

  public abstract Optional<Framework> framework();

  public static PubsubContext of(Framework framework) {
    return new AutoValue_PubsubContext(Optional.of(framework));
  }

  public static PubsubContext of() {
    return new AutoValue_PubsubContext(Optional.empty());
  }

  Struct getStruct() {
    Struct.Builder builder = Struct.newBuilder();
    builder.putFields(LANGUAGE_KEY, Values.of(LANGUAGE_VALUE));
    builder.putFields(MAJOR_VERSION_KEY, Values.of(Versions.MAJOR_VERSION));
    builder.putFields(MINOR_VERSION_KEY, Values.of(Versions.MINOR_VERSION));
    framework()
        .ifPresent(framework -> builder.putFields(FRAMEWORK_KEY, Values.of(framework.value())));
    return builder.build();
  }

  Metadata getMetadata() {
    Metadata metadata = new Metadata();
    Metadata.Key<String> key = Metadata.Key.of(HEADER_NAME, Metadata.ASCII_STRING_MARSHALLER);
    metadata.put(key, BaseEncoding.base64().encode(getStruct().toByteArray()));
    return metadata;
  }
}
