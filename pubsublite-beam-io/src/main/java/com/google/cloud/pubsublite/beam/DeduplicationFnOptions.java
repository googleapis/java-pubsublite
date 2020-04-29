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

package com.google.cloud.pubsublite.beam;

import com.google.auto.value.AutoValue;
import java.io.Serializable;
import org.apache.beam.sdk.coders.Coder;
import org.joda.time.Duration;

@AutoValue
public abstract class DeduplicationFnOptions<KeyT> implements Serializable {
  private static final long serialVersionUID = 9837489720893L;

  public static final Duration DEFAULT_GC_DELAY = Duration.standardSeconds(60);

  // A coder for the key type. Required.
  public abstract Coder<KeyT> keyCoder();

  // The minimum amount in event time to delay garbage collection of old UUIDs.
  public abstract Duration gcDelay();

  @SuppressWarnings("CheckReturnValue")
  public static <KeyT> Builder<KeyT> newBuilder() {
    Builder<KeyT> builder = new AutoValue_DeduplicationFnOptions.Builder<KeyT>();
    builder.setGcDelay(DEFAULT_GC_DELAY);
    return builder;
  }

  @AutoValue.Builder
  public abstract static class Builder<KeyT> {
    public abstract Builder<KeyT> setGcDelay(Duration windowDelay);

    public abstract Builder<KeyT> setKeyCoder(Coder<KeyT> coder);

    public abstract DeduplicationFnOptions<KeyT> build();
  }
}
